package com.genie.dto.echo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class EchoModels {
  private EchoModels() {
  }

  public enum MemberRole {
    MEMBER, CONTRIBUTOR, KEEPER, OWNER
  }

  public record RegisterRequest(
      @NotBlank String username,
      @NotBlank String password,
      String nickname,
      String email
  ) {
  }

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {
  }

  public record AuthResponse(String token, User user) {
  }

  public record IslandRequest(
      @NotBlank String name,
      @NotBlank String slug,
      String description,
      String slogan,
      String coverUrl,
      String iconUrl,
      String themeColor
  ) {
  }

  public record PostRequest(
      @NotBlank String title,
      @NotBlank String content,
      String postType,
      String statusCard,
      String replyPreference,
      boolean anonymous
  ) {
  }

  public record CommentRequest(@NotBlank String content, Long parentId, boolean anonymous) {
  }

  public record ReactionRequest(
      @NotBlank String targetType,
      @NotNull Long targetId,
      @NotBlank String reactionType
  ) {
  }

  public record RoadmarkRequest(
      @NotBlank String targetType,
      @NotNull Long targetId,
      Long islandId,
      String note
  ) {
  }

  public record WikiPageRequest(
      @NotNull Long islandId,
      @NotBlank String title,
      @NotBlank String slug,
      Map<String, Object> contentJson,
      String contentHtml,
      String changeSummary
  ) {
  }

  public record WikiEditRequestPayload(
      Map<String, Object> proposedContentJson,
      String proposedContentHtml,
      String changeSummary
  ) {
  }

  public record ReviewRequest(boolean approved, String reviewComment) {
  }

  public record EmotionCheckinRequest(
      Long islandId,
      @NotBlank String emotion,
      boolean anonymous
  ) {
  }

  public record ResourceSubmissionRequest(
      @NotNull Long islandId,
      @NotBlank String title,
      String description,
      @NotBlank String resourceType,
      String url,
      String coverUrl,
      String content,
      Map<String, Object> contentJson,
      String reason
  ) {
  }

  public record EmotionWeather(LocalDate date, long total, List<EmotionWeatherItem> items) {
  }

  public record EmotionWeatherItem(String emotion, String label, int percent) {
  }

  public static class User {
    public Long id;
    public String username;
    public String nickname;
    public String avatarUrl;
    public String email;
    public String passwordHash;
    public String bio;
    public String status = "ACTIVE";
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class Island {
    public Long id;
    public String name;
    public String slug;
    public String description;
    public String slogan;
    public String coverUrl;
    public String iconUrl;
    public String themeColor;
    public String visibility = "PUBLIC";
    public String joinMode = "FREE";
    public Long creatorId;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class IslandMember {
    public Long id;
    public Long islandId;
    public Long userId;
    public MemberRole role = MemberRole.MEMBER;
    public String status = "ACTIVE";
    public OffsetDateTime joinedAt;
  }

  public static class Post {
    public Long id;
    public Long islandId;
    public Long authorId;
    public String authorName;
    public String title;
    public String content;
    public Map<String, Object> contentJson;
    public String postType = "NORMAL";
    public String statusCard;
    public String replyPreference;
    public boolean anonymous;
    public String visibility = "PUBLIC";
    public String auditStatus = "NORMAL";
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class Comment {
    public Long id;
    public Long postId;
    public Long authorId;
    public String authorName;
    public Long parentId;
    public String content;
    public boolean anonymous;
    public String auditStatus = "NORMAL";
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class Reaction {
    public Long id;
    public String targetType;
    public Long targetId;
    public Long userId;
    public String reactionType;
    public OffsetDateTime createdAt;
  }

  public static class Roadmark {
    public Long id;
    public Long userId;
    public String targetType;
    public Long targetId;
    public Long islandId;
    public String note;
    public OffsetDateTime createdAt;
  }

  public static class WikiPage {
    public Long id;
    public Long islandId;
    public String title;
    public String slug;
    public Map<String, Object> contentJson;
    public String contentHtml;
    public Long currentRevisionId;
    public int version;
    public Long createdBy;
    public Long updatedBy;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class WikiRevision {
    public Long id;
    public Long pageId;
    public Long islandId;
    public int revisionNo;
    public Map<String, Object> contentJson;
    public String contentHtml;
    public Long editorId;
    public Long sourceRequestId;
    public String changeSummary;
    public OffsetDateTime createdAt;
  }

  public static class WikiEditRequest {
    public Long id;
    public Long pageId;
    public Long islandId;
    public Long proposerId;
    public Long baseRevisionId;
    public int baseVersion;
    public Map<String, Object> proposedContentJson;
    public String proposedContentHtml;
    public String changeSummary;
    public String status = "PENDING";
    public Long reviewerId;
    public String reviewComment;
    public OffsetDateTime reviewedAt;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
  }

  public static class ResourceItem {
    public Long id;
    public Long islandId;
    public String title;
    public String description;
    public String resourceType;
    public String url;
    public String coverUrl;
    public String content;
    public Map<String, Object> contentJson;
    public String sourceType;
    public Long sourcePostId;
    public Long submitterId;
    public String status = "PUBLISHED";
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
  }

  public static class ResourceSubmission {
    public Long id;
    public Long islandId;
    public Long submitterId;
    public String title;
    public String description;
    public String resourceType;
    public String url;
    public String coverUrl;
    public String content;
    public Map<String, Object> contentJson;
    public String reason;
    public String status = "PENDING";
    public Long reviewerId;
    public String reviewComment;
    public OffsetDateTime reviewedAt;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
  }

  public static class EmotionCheckin {
    public Long id;
    public Long userId;
    public Long islandId;
    public String emotion;
    public boolean anonymous = true;
    public OffsetDateTime createdAt;
  }
}
