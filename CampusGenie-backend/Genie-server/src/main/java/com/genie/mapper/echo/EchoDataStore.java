package com.genie.mapper.echo;

import com.genie.exception.BizException;
import com.genie.dto.echo.EchoModels.AuthResponse;
import com.genie.dto.echo.EchoModels.Comment;
import com.genie.dto.echo.EchoModels.CommentRequest;
import com.genie.dto.echo.EchoModels.EmotionCheckin;
import com.genie.dto.echo.EchoModels.EmotionCheckinRequest;
import com.genie.dto.echo.EchoModels.EmotionWeather;
import com.genie.dto.echo.EchoModels.EmotionWeatherItem;
import com.genie.dto.echo.EchoModels.Island;
import com.genie.dto.echo.EchoModels.IslandMember;
import com.genie.dto.echo.EchoModels.IslandRequest;
import com.genie.dto.echo.EchoModels.LoginRequest;
import com.genie.dto.echo.EchoModels.MemberRole;
import com.genie.dto.echo.EchoModels.Post;
import com.genie.dto.echo.EchoModels.PostRequest;
import com.genie.dto.echo.EchoModels.Reaction;
import com.genie.dto.echo.EchoModels.ReactionRequest;
import com.genie.dto.echo.EchoModels.RegisterRequest;
import com.genie.dto.echo.EchoModels.ResourceItem;
import com.genie.dto.echo.EchoModels.ResourceSubmission;
import com.genie.dto.echo.EchoModels.ResourceSubmissionRequest;
import com.genie.dto.echo.EchoModels.ReviewRequest;
import com.genie.dto.echo.EchoModels.Roadmark;
import com.genie.dto.echo.EchoModels.RoadmarkRequest;
import com.genie.dto.echo.EchoModels.User;
import com.genie.dto.echo.EchoModels.WikiEditRequest;
import com.genie.dto.echo.EchoModels.WikiEditRequestPayload;
import com.genie.dto.echo.EchoModels.WikiPage;
import com.genie.dto.echo.EchoModels.WikiPageRequest;
import com.genie.dto.echo.EchoModels.WikiRevision;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class EchoDataStore {
  private final AtomicLong userIds = new AtomicLong(1);
  private final AtomicLong islandIds = new AtomicLong(1);
  private final AtomicLong memberIds = new AtomicLong(1);
  private final AtomicLong postIds = new AtomicLong(1);
  private final AtomicLong commentIds = new AtomicLong(1);
  private final AtomicLong reactionIds = new AtomicLong(1);
  private final AtomicLong roadmarkIds = new AtomicLong(1);
  private final AtomicLong wikiPageIds = new AtomicLong(1);
  private final AtomicLong wikiRevisionIds = new AtomicLong(1);
  private final AtomicLong wikiEditRequestIds = new AtomicLong(1);
  private final AtomicLong resourceIds = new AtomicLong(1);
  private final AtomicLong resourceSubmissionIds = new AtomicLong(1);
  private final AtomicLong emotionIds = new AtomicLong(1);

  private final Map<Long, User> users = new ConcurrentHashMap<>();
  private final Map<String, Long> userIdsByUsername = new ConcurrentHashMap<>();
  private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();
  private final Map<Long, Island> islands = new ConcurrentHashMap<>();
  private final Map<Long, IslandMember> islandMembers = new ConcurrentHashMap<>();
  private final Map<Long, Post> posts = new ConcurrentHashMap<>();
  private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
  private final Map<Long, Reaction> reactions = new ConcurrentHashMap<>();
  private final Map<Long, Roadmark> roadmarks = new ConcurrentHashMap<>();
  private final Map<Long, WikiPage> wikiPages = new ConcurrentHashMap<>();
  private final Map<Long, WikiRevision> wikiRevisions = new ConcurrentHashMap<>();
  private final Map<Long, WikiEditRequest> wikiEditRequests = new ConcurrentHashMap<>();
  private final Map<Long, ResourceItem> resources = new ConcurrentHashMap<>();
  private final Map<Long, ResourceSubmission> resourceSubmissions = new ConcurrentHashMap<>();
  private final Map<Long, EmotionCheckin> emotionCheckins = new ConcurrentHashMap<>();

  public EchoDataStore() {
    seed();
  }

  public AuthResponse register(RegisterRequest request) {
    var username = normalize(request.username());
    if (userIdsByUsername.containsKey(username)) {
      throw new BizException("用户名已存在");
    }

    var user = new User();
    user.id = userIds.getAndIncrement();
    user.username = username;
    user.nickname = blankToDefault(request.nickname(), username);
    user.email = request.email();
    user.passwordHash = hashPassword(request.password());
    user.bio = "刚刚抵达回声岛。";
    user.createdAt = now();
    user.updatedAt = user.createdAt;
    users.put(user.id, user);
    userIdsByUsername.put(username, user.id);
    return new AuthResponse(issueToken(user), copyUser(user));
  }

  public AuthResponse login(LoginRequest request) {
    var user = findUserByUsername(request.username());
    if (!Objects.equals(user.passwordHash, hashPassword(request.password()))) {
      throw new BizException(HttpStatus.UNAUTHORIZED, "用户名或密码不正确");
    }
    return new AuthResponse(issueToken(user), copyUser(user));
  }

  public User me(String authorization) {
    return copyUser(requireUser(authorization));
  }

  public void logout(String authorization) {
    var token = extractToken(authorization);
    if (token != null) {
      tokenToUserId.remove(token);
    }
  }

  public User requireUser(String authorization) {
    var token = extractToken(authorization);
    if (token == null || !tokenToUserId.containsKey(token)) {
      throw new BizException(HttpStatus.UNAUTHORIZED, "请先登录");
    }
    return users.get(tokenToUserId.get(token));
  }

  public List<Island> listIslands() {
    return islands.values().stream()
        .filter(item -> item.deletedAt == null)
        .sorted(Comparator.comparing(item -> item.id))
        .toList();
  }

  public Island getIslandBySlug(String slug) {
    return islands.values().stream()
        .filter(item -> item.deletedAt == null && item.slug.equals(slug))
        .findFirst()
        .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "岛屿不存在"));
  }

  public Island getIslandById(Long id) {
    var island = islands.get(id);
    if (island == null || island.deletedAt != null) {
      throw new BizException(HttpStatus.NOT_FOUND, "岛屿不存在");
    }
    return island;
  }

  public Island createIsland(IslandRequest request, User user) {
    if (islands.values().stream().anyMatch(item -> item.slug.equals(request.slug()))) {
      throw new BizException("岛屿 slug 已存在");
    }

    var island = new Island();
    island.id = islandIds.getAndIncrement();
    island.name = request.name();
    island.slug = request.slug();
    island.description = request.description();
    island.slogan = request.slogan();
    island.coverUrl = request.coverUrl();
    island.iconUrl = request.iconUrl();
    island.themeColor = blankToDefault(request.themeColor(), "#1f4f3c");
    island.creatorId = user.id;
    island.createdAt = now();
    island.updatedAt = island.createdAt;
    islands.put(island.id, island);
    joinIslandAs(island.id, user.id, MemberRole.OWNER);
    return island;
  }

  public Island updateIsland(Long id, IslandRequest request) {
    var island = getIslandById(id);
    island.name = request.name();
    island.slug = request.slug();
    island.description = request.description();
    island.slogan = request.slogan();
    island.coverUrl = request.coverUrl();
    island.iconUrl = request.iconUrl();
    island.themeColor = request.themeColor();
    island.updatedAt = now();
    return island;
  }

  public IslandMember joinIsland(Long id, User user) {
    getIslandById(id);
    return joinIslandAs(id, user.id, MemberRole.MEMBER);
  }

  public void leaveIsland(Long id, User user) {
    islandMembers.values().stream()
        .filter(member -> Objects.equals(member.islandId, id) && Objects.equals(member.userId, user.id))
        .findFirst()
        .ifPresent(member -> islandMembers.remove(member.id));
  }

  public List<IslandMember> listMembers(Long islandId) {
    getIslandById(islandId);
    return islandMembers.values().stream()
        .filter(member -> Objects.equals(member.islandId, islandId))
        .sorted(Comparator.comparing(member -> member.joinedAt))
        .toList();
  }

  public List<Post> listPosts(Long islandId) {
    getIslandById(islandId);
    return posts.values().stream()
        .filter(post -> post.deletedAt == null && Objects.equals(post.islandId, islandId))
        .sorted(Comparator.comparing((Post post) -> post.createdAt).reversed())
        .toList();
  }

  public Post getPost(Long postId) {
    var post = posts.get(postId);
    if (post == null || post.deletedAt != null) {
      throw new BizException(HttpStatus.NOT_FOUND, "帖子不存在");
    }
    return post;
  }

  public Post createPost(Long islandId, PostRequest request, User user) {
    getIslandById(islandId);
    var post = new Post();
    post.id = postIds.getAndIncrement();
    post.islandId = islandId;
    post.authorId = user.id;
    post.authorName = request.anonymous() ? "匿名岛民" : user.nickname;
    post.title = request.title();
    post.content = request.content();
    post.postType = blankToDefault(request.postType(), "NORMAL");
    post.statusCard = request.statusCard();
    post.replyPreference = request.replyPreference();
    post.anonymous = request.anonymous();
    post.createdAt = now();
    post.updatedAt = post.createdAt;
    posts.put(post.id, post);
    return post;
  }

  public Post updatePost(Long postId, PostRequest request) {
    var post = getPost(postId);
    post.title = request.title();
    post.content = request.content();
    post.postType = blankToDefault(request.postType(), post.postType);
    post.statusCard = request.statusCard();
    post.replyPreference = request.replyPreference();
    post.anonymous = request.anonymous();
    post.updatedAt = now();
    return post;
  }

  public void deletePost(Long postId) {
    var post = getPost(postId);
    post.deletedAt = now();
  }

  public List<Comment> listComments(Long postId) {
    getPost(postId);
    return comments.values().stream()
        .filter(comment -> comment.deletedAt == null && Objects.equals(comment.postId, postId))
        .sorted(Comparator.comparing(comment -> comment.createdAt))
        .toList();
  }

  public Comment createComment(Long postId, CommentRequest request, User user) {
    getPost(postId);
    var comment = new Comment();
    comment.id = commentIds.getAndIncrement();
    comment.postId = postId;
    comment.authorId = user.id;
    comment.authorName = request.anonymous() ? "匿名岛民" : user.nickname;
    comment.parentId = request.parentId();
    comment.content = request.content();
    comment.anonymous = request.anonymous();
    comment.createdAt = now();
    comment.updatedAt = comment.createdAt;
    comments.put(comment.id, comment);
    return comment;
  }

  public void deleteComment(Long commentId) {
    var comment = comments.get(commentId);
    if (comment == null || comment.deletedAt != null) {
      throw new BizException(HttpStatus.NOT_FOUND, "评论不存在");
    }
    comment.deletedAt = now();
  }

  public List<Reaction> listReactionsForPost(Long postId) {
    getPost(postId);
    return reactions.values().stream()
        .filter(reaction -> "POST".equals(reaction.targetType) && Objects.equals(reaction.targetId, postId))
        .sorted(Comparator.comparing(reaction -> reaction.createdAt))
        .toList();
  }

  public Reaction createReaction(ReactionRequest request, User user) {
    reactions.values().stream()
        .filter(reaction -> Objects.equals(reaction.targetType, request.targetType())
            && Objects.equals(reaction.targetId, request.targetId())
            && Objects.equals(reaction.userId, user.id)
            && Objects.equals(reaction.reactionType, request.reactionType()))
        .findFirst()
        .ifPresent(reaction -> {
          throw new BizException("已经回应过了");
        });

    var reaction = new Reaction();
    reaction.id = reactionIds.getAndIncrement();
    reaction.targetType = request.targetType();
    reaction.targetId = request.targetId();
    reaction.userId = user.id;
    reaction.reactionType = request.reactionType();
    reaction.createdAt = now();
    reactions.put(reaction.id, reaction);
    return reaction;
  }

  public void deleteReaction(Long id, User user) {
    var reaction = reactions.get(id);
    if (reaction == null || !Objects.equals(reaction.userId, user.id)) {
      throw new BizException(HttpStatus.NOT_FOUND, "回应不存在");
    }
    reactions.remove(id);
  }

  public Roadmark createRoadmark(RoadmarkRequest request, User user) {
    roadmarks.values().stream()
        .filter(item -> Objects.equals(item.userId, user.id)
            && Objects.equals(item.targetType, request.targetType())
            && Objects.equals(item.targetId, request.targetId()))
        .findFirst()
        .ifPresent(item -> {
          throw new BizException("已经收进路标");
        });

    var roadmark = new Roadmark();
    roadmark.id = roadmarkIds.getAndIncrement();
    roadmark.userId = user.id;
    roadmark.targetType = request.targetType();
    roadmark.targetId = request.targetId();
    roadmark.islandId = request.islandId();
    roadmark.note = request.note();
    roadmark.createdAt = now();
    roadmarks.put(roadmark.id, roadmark);
    return roadmark;
  }

  public List<Roadmark> listMyRoadmarks(User user) {
    return roadmarks.values().stream()
        .filter(item -> Objects.equals(item.userId, user.id))
        .sorted(Comparator.comparing((Roadmark item) -> item.createdAt).reversed())
        .toList();
  }

  public void deleteRoadmark(Long id, User user) {
    var roadmark = roadmarks.get(id);
    if (roadmark == null || !Objects.equals(roadmark.userId, user.id)) {
      throw new BizException(HttpStatus.NOT_FOUND, "路标不存在");
    }
    roadmarks.remove(id);
  }

  public List<WikiPage> listWikiPages(Long islandId) {
    getIslandById(islandId);
    return wikiPages.values().stream()
        .filter(page -> page.deletedAt == null && Objects.equals(page.islandId, islandId))
        .sorted(Comparator.comparing(page -> page.title))
        .toList();
  }

  public WikiPage getWikiPage(Long pageId) {
    var page = wikiPages.get(pageId);
    if (page == null || page.deletedAt != null) {
      throw new BizException(HttpStatus.NOT_FOUND, "Wiki 页面不存在");
    }
    return page;
  }

  public WikiPage createWikiPage(WikiPageRequest request, User user) {
    getIslandById(request.islandId());
    var page = new WikiPage();
    page.id = wikiPageIds.getAndIncrement();
    page.islandId = request.islandId();
    page.title = request.title();
    page.slug = request.slug();
    page.contentJson = request.contentJson();
    page.contentHtml = request.contentHtml();
    page.version = 1;
    page.createdBy = user.id;
    page.updatedBy = user.id;
    page.createdAt = now();
    page.updatedAt = page.createdAt;
    wikiPages.put(page.id, page);
    createRevision(page, user.id, null, blankToDefault(request.changeSummary(), "创建页面"));
    return page;
  }

  public WikiPage updateWikiPage(Long pageId, WikiPageRequest request, User user) {
    var page = getWikiPage(pageId);
    page.title = request.title();
    page.slug = request.slug();
    page.contentJson = request.contentJson();
    page.contentHtml = request.contentHtml();
    page.version += 1;
    page.updatedBy = user.id;
    page.updatedAt = now();
    createRevision(page, user.id, null, blankToDefault(request.changeSummary(), "更新页面"));
    return page;
  }

  public List<WikiRevision> listWikiRevisions(Long pageId) {
    getWikiPage(pageId);
    return wikiRevisions.values().stream()
        .filter(revision -> Objects.equals(revision.pageId, pageId))
        .sorted(Comparator.comparing((WikiRevision revision) -> revision.revisionNo).reversed())
        .toList();
  }

  public WikiPage rollbackWikiPage(Long pageId, Long revisionId, User user) {
    var page = getWikiPage(pageId);
    var revision = revisionId == null
        ? listWikiRevisions(pageId).stream().skip(1).findFirst().orElseThrow(() -> new BizException("没有可回滚版本"))
        : wikiRevisions.get(revisionId);
    if (revision == null || !Objects.equals(revision.pageId, pageId)) {
      throw new BizException(HttpStatus.NOT_FOUND, "历史版本不存在");
    }
    page.contentJson = revision.contentJson;
    page.contentHtml = revision.contentHtml;
    page.version += 1;
    page.updatedBy = user.id;
    page.updatedAt = now();
    createRevision(page, user.id, null, "回滚到版本 " + revision.revisionNo);
    return page;
  }

  public WikiEditRequest createWikiEditRequest(Long pageId, WikiEditRequestPayload request, User user) {
    var page = getWikiPage(pageId);
    var editRequest = new WikiEditRequest();
    editRequest.id = wikiEditRequestIds.getAndIncrement();
    editRequest.pageId = page.id;
    editRequest.islandId = page.islandId;
    editRequest.proposerId = user.id;
    editRequest.baseRevisionId = page.currentRevisionId;
    editRequest.baseVersion = page.version;
    editRequest.proposedContentJson = request.proposedContentJson();
    editRequest.proposedContentHtml = request.proposedContentHtml();
    editRequest.changeSummary = request.changeSummary();
    editRequest.createdAt = now();
    editRequest.updatedAt = editRequest.createdAt;
    wikiEditRequests.put(editRequest.id, editRequest);
    return editRequest;
  }

  public WikiEditRequest reviewWikiEditRequest(Long requestId, ReviewRequest request, User reviewer) {
    var editRequest = wikiEditRequests.get(requestId);
    if (editRequest == null) {
      throw new BizException(HttpStatus.NOT_FOUND, "修改申请不存在");
    }
    if (!"PENDING".equals(editRequest.status)) {
      throw new BizException("修改申请已处理");
    }

    editRequest.status = request.approved() ? "APPROVED" : "REJECTED";
    editRequest.reviewerId = reviewer.id;
    editRequest.reviewComment = request.reviewComment();
    editRequest.reviewedAt = now();
    editRequest.updatedAt = editRequest.reviewedAt;

    if (request.approved()) {
      var page = getWikiPage(editRequest.pageId);
      page.contentJson = editRequest.proposedContentJson;
      page.contentHtml = editRequest.proposedContentHtml;
      page.version += 1;
      page.updatedBy = reviewer.id;
      page.updatedAt = now();
      createRevision(page, reviewer.id, editRequest.id, blankToDefault(editRequest.changeSummary, "通过修改申请"));
    }

    return editRequest;
  }

  public List<ResourceItem> listResources(Long islandId) {
    getIslandById(islandId);
    return resources.values().stream()
        .filter(resource -> resource.deletedAt == null
            && Objects.equals(resource.islandId, islandId)
            && "PUBLISHED".equals(resource.status))
        .sorted(Comparator.comparing((ResourceItem item) -> item.createdAt).reversed())
        .toList();
  }

  public ResourceSubmission submitResource(ResourceSubmissionRequest request, User user) {
    getIslandById(request.islandId());
    var submission = new ResourceSubmission();
    submission.id = resourceSubmissionIds.getAndIncrement();
    submission.islandId = request.islandId();
    submission.submitterId = user.id;
    submission.title = request.title();
    submission.description = request.description();
    submission.resourceType = request.resourceType();
    submission.url = request.url();
    submission.coverUrl = request.coverUrl();
    submission.content = request.content();
    submission.contentJson = request.contentJson();
    submission.reason = request.reason();
    submission.createdAt = now();
    submission.updatedAt = submission.createdAt;
    resourceSubmissions.put(submission.id, submission);
    return submission;
  }

  public ResourceSubmission reviewResourceSubmission(Long submissionId, ReviewRequest request, User reviewer) {
    var submission = resourceSubmissions.get(submissionId);
    if (submission == null) {
      throw new BizException(HttpStatus.NOT_FOUND, "资源投稿不存在");
    }
    if (!"PENDING".equals(submission.status)) {
      throw new BizException("资源投稿已处理");
    }

    submission.status = request.approved() ? "APPROVED" : "REJECTED";
    submission.reviewerId = reviewer.id;
    submission.reviewComment = request.reviewComment();
    submission.reviewedAt = now();
    submission.updatedAt = submission.reviewedAt;

    if (request.approved()) {
      var resource = new ResourceItem();
      resource.id = resourceIds.getAndIncrement();
      resource.islandId = submission.islandId;
      resource.title = submission.title;
      resource.description = submission.description;
      resource.resourceType = submission.resourceType;
      resource.url = submission.url;
      resource.coverUrl = submission.coverUrl;
      resource.content = submission.content;
      resource.contentJson = submission.contentJson;
      resource.submitterId = submission.submitterId;
      resource.status = "PUBLISHED";
      resource.createdAt = now();
      resource.updatedAt = resource.createdAt;
      resources.put(resource.id, resource);
    }

    return submission;
  }

  public EmotionCheckin checkinEmotion(EmotionCheckinRequest request, User user) {
    if (request.islandId() != null) {
      getIslandById(request.islandId());
    }
    var checkin = new EmotionCheckin();
    checkin.id = emotionIds.getAndIncrement();
    checkin.userId = user.id;
    checkin.islandId = request.islandId();
    checkin.emotion = request.emotion();
    checkin.anonymous = request.anonymous();
    checkin.createdAt = now();
    emotionCheckins.put(checkin.id, checkin);
    return checkin;
  }

  public EmotionWeather emotionWeather(Long islandId) {
    getIslandById(islandId);
    var today = LocalDate.now();
    var counts = new LinkedHashMap<String, Long>();
    emotionCheckins.values().stream()
        .filter(item -> Objects.equals(item.islandId, islandId))
        .filter(item -> item.createdAt.toLocalDate().equals(today))
        .forEach(item -> counts.merge(item.emotion, 1L, Long::sum));

    var total = counts.values().stream().mapToLong(Long::longValue).sum();
    var items = counts.entrySet().stream()
        .map(entry -> new EmotionWeatherItem(entry.getKey(), emotionLabel(entry.getKey()),
            total == 0 ? 0 : Math.round(entry.getValue() * 100f / total)))
        .sorted(Comparator.comparing(EmotionWeatherItem::percent).reversed())
        .toList();
    return new EmotionWeather(today, total, items);
  }

  private void seed() {
    var demo = seedUser("demo", "demo123", "青禾", "demo@echo.local");
    var keeper = seedUser("keeper", "keeper123", "守岛人", "keeper@echo.local");

    var treeHole = seedIsland("树洞岛", "tree-hole", "匿名倾诉、被听见和温柔回应的地方。", "不用马上变好，先被听见。", "#1f4f3c", demo.id);
    var graduate = seedIsland("保研岛", "graduate", "保研经验、材料清单、导师邮件模板与避坑提醒。", "慢一点也可以，方向比速度更重要。", "#2f7d55", demo.id);
    var music = seedIsland("爵士岛", "music", "爵士乐、唱片、即兴练习与深夜聆听的岛屿。", "让即兴成为一种生活方式。", "#6f4fc4", keeper.id);
    var lowEnergy = seedIsland("低精力岛", "low-energy", "低精力自救、温柔清单、休息方法和小步恢复。", "今天只做一件很小的事，也算抵达。", "#7c8490", demo.id);
    var running = seedIsland("跑步岛", "running", "路线、装备、节奏，以及重新启动身体的方式。", "先出门，再谈配速。", "#dd7a2f", demo.id);

    joinIslandAs(treeHole.id, demo.id, MemberRole.OWNER);
    joinIslandAs(graduate.id, demo.id, MemberRole.OWNER);
    joinIslandAs(music.id, keeper.id, MemberRole.OWNER);
    joinIslandAs(lowEnergy.id, demo.id, MemberRole.OWNER);
    joinIslandAs(running.id, demo.id, MemberRole.OWNER);

    seedPost(treeHole.id, demo, "我只是想被听见，不想马上被建议", "今天已经很努力解释自己了。来到这里，是想暂时不用证明什么。", "JUST_BE_HEARD", "LISTEN_ONLY", true);
    seedPost(graduate.id, demo, "双非保研边缘人上岸复盘", "如果你也觉得自己站在边缘，我想把这段经历留下来，给后面的人一盏灯。", "SHARE_EXPERIENCE", "OPEN_DISCUSSION", false);
    seedPost(lowEnergy.id, demo, "把洗澡也算作今天的重要任务，可以吗", "我知道它很小，但对今天的我来说已经很大了。", "NEED_COMFORT", "COMFORT_ONLY", true);
    seedPost(music.id, keeper, "深夜听到一段小号，突然觉得生活还在流动", "不是被治愈，就是有一瞬间愿意继续待着。", "RECORD_MOMENT", "SIMILAR_STORY", false);

    seedResource(graduate.id, "保研导师邮件模板", "适合第一次联系导师，包含自我介绍、研究兴趣和附件清单。", "TEMPLATE", demo.id);
    seedResource(graduate.id, "常见骗局和广告避坑", "整理推免季常见付费坑、虚假内推和焦虑营销话术。", "WARNING", demo.id);
    seedResource(lowEnergy.id, "低精力自救歌单", "不催促、不打鸡血，只适合慢慢恢复的一组歌。", "MUSIC", demo.id);
    seedResource(music.id, "焦虑时可以听的 Jazz", "从 Bill Evans 到 Chet Baker，适合阴雨背景板的一小时。", "MUSIC", keeper.id);
    seedResource(music.id, "爵士乐的历史", "了解过去，才能听懂斑驳的即兴。", "BOOK", keeper.id);
    seedResource(running.id, "城市晨跑安全清单", "路线、补水、反光装备和低风险启动建议。", "CHECKLIST", demo.id);

    seedWiki(graduate.id, demo.id, "温柔守则", "gentle-rules", "回应之前，先确认对方想被怎样回应。");
    seedWiki(music.id, keeper.id, "爵士岛灵魂", "jazz-spirit", "Improvisation, Dialogue & Community, Boundary-breaking, Rebellion.");

    seedEmotion(treeHole.id, "CALM", 21);
    seedEmotion(treeHole.id, "TIRED", 27);
    seedEmotion(treeHole.id, "LOST", 12);
    seedEmotion(graduate.id, "ANXIOUS", 34);
    seedEmotion(graduate.id, "TIRED", 29);
    seedEmotion(graduate.id, "HOPEFUL", 16);
    seedEmotion(music.id, "HAPPY", 31);
    seedEmotion(music.id, "CALM", 24);
    seedEmotion(lowEnergy.id, "TIRED", 41);
    seedEmotion(lowEnergy.id, "NUMB", 18);
    seedEmotion(running.id, "HOPEFUL", 28);
  }

  private User seedUser(String username, String password, String nickname, String email) {
    var user = new User();
    user.id = userIds.getAndIncrement();
    user.username = username;
    user.nickname = nickname;
    user.email = email;
    user.passwordHash = hashPassword(password);
    user.bio = "回声岛初始用户";
    user.createdAt = now();
    user.updatedAt = user.createdAt;
    users.put(user.id, user);
    userIdsByUsername.put(username, user.id);
    return user;
  }

  private Island seedIsland(String name, String slug, String description, String slogan, String color, Long creatorId) {
    var island = new Island();
    island.id = islandIds.getAndIncrement();
    island.name = name;
    island.slug = slug;
    island.description = description;
    island.slogan = slogan;
    island.themeColor = color;
    island.creatorId = creatorId;
    island.createdAt = now();
    island.updatedAt = island.createdAt;
    islands.put(island.id, island);
    return island;
  }

  private Post seedPost(Long islandId, User author, String title, String content, String statusCard, String replyPreference, boolean anonymous) {
    var post = new Post();
    post.id = postIds.getAndIncrement();
    post.islandId = islandId;
    post.authorId = author.id;
    post.authorName = anonymous ? "匿名岛民" : author.nickname;
    post.title = title;
    post.content = content;
    post.statusCard = statusCard;
    post.replyPreference = replyPreference;
    post.anonymous = anonymous;
    post.createdAt = now();
    post.updatedAt = post.createdAt;
    posts.put(post.id, post);
    return post;
  }

  private void seedResource(Long islandId, String title, String description, String type, Long submitterId) {
    var resource = new ResourceItem();
    resource.id = resourceIds.getAndIncrement();
    resource.islandId = islandId;
    resource.title = title;
    resource.description = description;
    resource.resourceType = type;
    resource.submitterId = submitterId;
    resource.status = "PUBLISHED";
    resource.createdAt = now();
    resource.updatedAt = resource.createdAt;
    resources.put(resource.id, resource);
  }

  private void seedWiki(Long islandId, Long userId, String title, String slug, String content) {
    var page = new WikiPage();
    page.id = wikiPageIds.getAndIncrement();
    page.islandId = islandId;
    page.title = title;
    page.slug = slug;
    page.contentJson = Map.of("blocks", List.of(Map.of("type", "paragraph", "text", content)));
    page.contentHtml = "<p>" + content + "</p>";
    page.version = 1;
    page.createdBy = userId;
    page.updatedBy = userId;
    page.createdAt = now();
    page.updatedAt = page.createdAt;
    wikiPages.put(page.id, page);
    createRevision(page, userId, null, "初始版本");
  }

  private void seedEmotion(Long islandId, String emotion, int count) {
    for (var i = 0; i < count; i++) {
      var checkin = new EmotionCheckin();
      checkin.id = emotionIds.getAndIncrement();
      checkin.userId = null;
      checkin.islandId = islandId;
      checkin.emotion = emotion;
      checkin.anonymous = true;
      checkin.createdAt = now();
      emotionCheckins.put(checkin.id, checkin);
    }
  }

  private IslandMember joinIslandAs(Long islandId, Long userId, MemberRole role) {
    var existing = islandMembers.values().stream()
        .filter(member -> Objects.equals(member.islandId, islandId) && Objects.equals(member.userId, userId))
        .findFirst();
    if (existing.isPresent()) {
      return existing.get();
    }

    var member = new IslandMember();
    member.id = memberIds.getAndIncrement();
    member.islandId = islandId;
    member.userId = userId;
    member.role = role;
    member.joinedAt = now();
    islandMembers.put(member.id, member);
    return member;
  }

  private WikiRevision createRevision(WikiPage page, Long editorId, Long sourceRequestId, String summary) {
    var revision = new WikiRevision();
    revision.id = wikiRevisionIds.getAndIncrement();
    revision.pageId = page.id;
    revision.islandId = page.islandId;
    revision.revisionNo = page.version;
    revision.contentJson = page.contentJson == null ? null : new HashMap<>(page.contentJson);
    revision.contentHtml = page.contentHtml;
    revision.editorId = editorId;
    revision.sourceRequestId = sourceRequestId;
    revision.changeSummary = summary;
    revision.createdAt = now();
    wikiRevisions.put(revision.id, revision);
    page.currentRevisionId = revision.id;
    return revision;
  }

  private User findUserByUsername(String username) {
    var userId = userIdsByUsername.get(normalize(username));
    if (userId == null) {
      throw new BizException(HttpStatus.UNAUTHORIZED, "用户名或密码不正确");
    }
    return users.get(userId);
  }

  private User copyUser(User source) {
    var user = new User();
    user.id = source.id;
    user.username = source.username;
    user.nickname = source.nickname;
    user.avatarUrl = source.avatarUrl;
    user.email = source.email;
    user.bio = source.bio;
    user.status = source.status;
    user.createdAt = source.createdAt;
    user.updatedAt = source.updatedAt;
    user.deletedAt = source.deletedAt;
    return user;
  }

  private String issueToken(User user) {
    var token = Base64.getUrlEncoder().withoutPadding()
        .encodeToString((user.id + ":" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8));
    tokenToUserId.put(token, user.id);
    return token;
  }

  private String extractToken(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return null;
    }
    return authorization.substring("Bearer ".length()).trim();
  }

  private String hashPassword(String password) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      return Base64.getEncoder().encodeToString(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception exception) {
      throw new IllegalStateException("Cannot hash password", exception);
    }
  }

  private OffsetDateTime now() {
    return OffsetDateTime.now();
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase();
  }

  private String blankToDefault(String value, String defaultValue) {
    return value == null || value.isBlank() ? defaultValue : value;
  }

  private String emotionLabel(String emotion) {
    return switch (emotion) {
      case "TIRED" -> "疲惫";
      case "CALM" -> "平静";
      case "ANXIOUS" -> "焦虑";
      case "LOST" -> "迷茫";
      case "HAPPY" -> "开心";
      case "NUMB" -> "麻木";
      case "HOPEFUL" -> "有希望";
      default -> emotion;
    };
  }
}

