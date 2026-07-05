<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import AppTopbar from "./components/AppTopbar.vue";
import { getAuthToken } from "./components/client.ts";
import "./styles/main.css";
import {
  type ApiIsland,
  type ApiPost,
  type ApiResource,
  type ApiRoadmark,
  createPost as createApiPost,
  createReaction as createApiReaction,
  createRoadmark as createApiRoadmark,
  createWikiEditRequest,
  emotionWeather,
  listIslands,
  listPosts,
  listResources,
  listRoadmarks,
  listWikiPages,
  loginAsDemo,
  me,
  submitResource as submitResourceApi
} from "./api/echo.ts";
import {
  type Island,
  type IslandTab,
  type PostItem,
  type ResourceItem,
  type RoadmarkItem,
  type ViewKey,
  islands as mockIslands,
  posts as mockPosts,
  resources as mockResources,
  roadmarks as mockRoadmarks
} from "./data/mock";
import { normalizeResourceType, typeLabel } from "./utils/labels.ts";

const route = useRoute();
const router = useRouter();

const currentView = computed<ViewKey>(() => {
  const routeName = String(route.name ?? "home");
  if (routeName === "map" || routeName === "create-post" || routeName === "roadmarks") {
    return routeName;
  }
  if (routeName === "create-island") {
    return "map";
  }
  if (routeName !== "home") {
    return "map";
  }
  return "home";
});

const showAppTopbar = computed(() => !["create-island", "jazz", "jazz-library"].includes(String(route.name ?? "")));

const routeNameBySlug: Record<string, string> = {
  "low-energy": "quiet-forest",
  graduate: "lighthouse-hill",
  "tree-hole": "heard-bay",
  music: "jazz",
  running: "flowing-coast"
};
const selectedIslandSlug = ref("music");
const islandTab = ref<IslandTab>("home");
const islands = ref<Island[]>([...mockIslands]);
const resources = ref<ResourceItem[]>([...mockResources]);
const feedPosts = ref<PostItem[]>([...mockPosts]);
const savedRoadmarks = ref<RoadmarkItem[]>([...mockRoadmarks]);
const loading = ref(true);
const apiOnline = ref(false);
const currentNickname = ref("演示岛民");
const actionMessage = ref("");
const isSubmittingPost = ref(false);
const showResourceForm = ref(false);
const resourceMessage = ref("");
const rulesMessage = ref("");

const resourceDraft = ref({
  resourceType: "ARTICLE",
  title: "",
  description: "",
  url: "",
  tags: "",
  reason: ""
});

const draftPost = ref({
  islandSlug: "tree-hole",
  title: "",
  content: "",
  statusCard: "JUST_BE_HEARD",
  replyPreference: "LISTEN_ONLY",
  anonymous: true
});

const roadmarkFilter = ref("全部");

watch(
  () => route.meta.slug,
  (slug) => {
    if (typeof slug === "string" && slug) {
      selectedIslandSlug.value = slug;
    }
  },
  { immediate: true }
);

const selectedIsland = computed(() => {
  return islands.value.find((island) => island.slug === selectedIslandSlug.value) ?? islands.value[0] ?? mockIslands[0];
});

const islandPosts = computed(() => {
  return feedPosts.value.filter((post) => post.islandSlug === selectedIsland.value.slug);
});

const islandResources = computed(() => {
  return resources.value.filter((resource) => resource.islandSlug === selectedIsland.value.slug);
});

const filteredRoadmarks = computed(() => {
  if (roadmarkFilter.value === "全部") {
    return savedRoadmarks.value;
  }
  if (roadmarkFilter.value === "帖子") {
    return savedRoadmarks.value.filter((item) => item.type === "POST");
  }
  if (roadmarkFilter.value === "资源") {
    return savedRoadmarks.value.filter((item) => item.type === "RESOURCE");
  }
  return savedRoadmarks.value.filter((item) => item.category === roadmarkFilter.value);
});

const dataSourceLabel = computed(() => {
  if (loading.value) {
    return "加载中";
  }
  return apiOnline.value ? `${currentNickname.value} · API` : "";
});

function navigate(view: ViewKey) {
  actionMessage.value = "";
  if (view === "island") {
    void router.push({ name: routeNameBySlug[selectedIslandSlug.value] ?? "heard-bay" });
  } else {
    void router.push({ name: view });
  }
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function openIsland(slug: string) {
  selectedIslandSlug.value = slug;
  islandTab.value = "home";
  actionMessage.value = "";
  void router.push({ name: routeNameBySlug[slug] ?? "heard-bay" });
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function handleIntent(target: string) {
  if (target === "map") {
    navigate("map");
    return;
  }
  if (target === "create-island") {
    void router.push({ name: "create-island" });
    return;
  }
  openIsland(target);
}

async function ensureSession() {
  if (getAuthToken()) {
    try {
      const user = await me();
      currentNickname.value = user.nickname || user.username;
      return user;
    } catch {
      // Fall through to the seeded demo account.
    }
  }

  const user = await loginAsDemo();
  currentNickname.value = user.nickname || user.username;
  return user;
}

async function loadRemoteData() {
  loading.value = true;
  try {
    const apiIslands = await listIslands();
    apiOnline.value = true;
    islands.value = apiIslands.map(mapApiIsland);
    if (!islands.value.some((island) => island.slug === selectedIslandSlug.value)) {
      selectedIslandSlug.value = islands.value[0]?.slug ?? "tree-hole";
    }

    const islandPayloads = await Promise.all(
      apiIslands.map(async (apiIsland) => {
        const [apiPosts, apiResources, weather] = await Promise.all([
          listPosts(apiIsland.id).catch(() => []),
          listResources(apiIsland.id).catch(() => []),
          emotionWeather(apiIsland.id).catch(() => null)
        ]);
        return { apiIsland, apiPosts, apiResources, weather };
      })
    );

    const mappedPosts = islandPayloads.flatMap((payload) => payload.apiPosts.map(mapApiPost));
    const mappedResources = islandPayloads.flatMap((payload) => payload.apiResources.map(mapApiResource));
    feedPosts.value = mappedPosts.length > 0 ? mappedPosts : [...mockPosts];
    resources.value = mappedResources.length > 0 ? mappedResources : [...mockResources];

    for (const payload of islandPayloads) {
      const mood = payload.weather ? weatherToText(payload.weather.items) : "";
      const island = islands.value.find((item) => item.id === payload.apiIsland.id);
      if (island && mood) {
        island.mood = mood;
      }
    }

    await ensureSession().catch(() => undefined);
    const apiRoadmarks = getAuthToken() ? await listRoadmarks().catch(() => []) : [];
    const mappedRoadmarks = apiRoadmarks.map(mapApiRoadmark).filter(Boolean) as RoadmarkItem[];
    savedRoadmarks.value = mappedRoadmarks.length > 0 ? mappedRoadmarks : [...mockRoadmarks];
  } catch (error) {
    console.warn("Using local prototype data because API is unavailable.", error);
    apiOnline.value = false;
    islands.value = [...mockIslands];
    resources.value = [...mockResources];
    feedPosts.value = [...mockPosts];
    savedRoadmarks.value = [...mockRoadmarks];
  } finally {
    loading.value = false;
  }
}

async function publishPost() {
  if (!draftPost.value.title.trim() || !draftPost.value.content.trim()) {
    return;
  }

  isSubmittingPost.value = true;
  const targetIsland = islands.value.find((island) => island.slug === draftPost.value.islandSlug) ?? selectedIsland.value;
  let createdPost: PostItem | null = null;

  if (apiOnline.value) {
    try {
      await ensureSession();
      const apiPost = await createApiPost(targetIsland.id, {
        title: draftPost.value.title.trim(),
        content: draftPost.value.content.trim(),
        statusCard: draftPost.value.statusCard,
        replyPreference: draftPost.value.replyPreference,
        anonymous: draftPost.value.anonymous
      });
      createdPost = mapApiPost(apiPost);
      actionMessage.value = "回声已发布到后端。";
    } catch (error) {
      console.warn("Post API failed, falling back to local state.", error);
      actionMessage.value = "后端暂不可用，已先保存到本地演示数据。";
    }
  }

  feedPosts.value.unshift(
    createdPost ?? {
      id: Date.now(),
      islandSlug: draftPost.value.islandSlug,
      title: draftPost.value.title.trim(),
      content: draftPost.value.content.trim(),
      author: draftPost.value.anonymous ? "匿名岛民" : currentNickname.value,
      anonymous: draftPost.value.anonymous,
      statusCard: draftPost.value.statusCard,
      replyPreference: draftPost.value.replyPreference,
      createdAt: "刚刚",
      reactions: { HUG: 0, UNDERSTOOD: 0, ME_TOO: 0 },
      comments: 0
    }
  );

  selectedIslandSlug.value = draftPost.value.islandSlug;
  draftPost.value.title = "";
  draftPost.value.content = "";
  islandTab.value = "posts";
  void router.push({ name: routeNameBySlug[selectedIslandSlug.value] ?? "heard-bay" });
  isSubmittingPost.value = false;
}

async function saveResource(resourceId: number) {
  const resource = resources.value.find((item) => item.id === resourceId);
  if (!resource || savedRoadmarks.value.some((item) => item.title === resource.title)) {
    return;
  }

  let roadmark: RoadmarkItem | null = null;
  if (apiOnline.value) {
    try {
      await ensureSession();
      const apiRoadmark = await createApiRoadmark({
        targetType: "RESOURCE",
        targetId: resource.id,
        islandId: selectedIsland.value.id,
        note: "从资源库收藏。"
      });
      roadmark = mapApiRoadmark(apiRoadmark);
    } catch (error) {
      console.warn("Roadmark API failed, falling back to local state.", error);
    }
  }

  savedRoadmarks.value.unshift(
    roadmark ?? {
      id: Date.now(),
      category: typeLabel(resource.type),
      title: resource.title,
      island: islands.value.find((item) => item.slug === resource.islandSlug)?.name ?? "回声岛",
      note: "刚刚加入路标。",
      type: "RESOURCE"
    }
  );
  resource.saves += 1;
  actionMessage.value = "已收进我的路标。";
}

async function reactToPost(post: PostItem, reaction: string) {
  if (apiOnline.value) {
    try {
      await ensureSession();
      await createApiReaction({
        targetType: "POST",
        targetId: post.id,
        reactionType: reaction
      });
    } catch (error) {
      console.warn("Reaction API failed, keeping optimistic UI update.", error);
    }
  }
  post.reactions = {
    ...post.reactions,
    [reaction]: (post.reactions[reaction] ?? 0) + 1
  };
}

async function submitResourceDraft() {
  if (!resourceDraft.value.title.trim() || !resourceDraft.value.description.trim()) {
    resourceMessage.value = "请填写资源标题和简介。";
    return;
  }

  const tagsLine = resourceDraft.value.tags.trim() ? `标签：${resourceDraft.value.tags.trim()}` : "";
  const reason = [resourceDraft.value.reason.trim(), tagsLine].filter(Boolean).join("\n");

  if (apiOnline.value) {
    try {
      await ensureSession();
      await submitResourceApi({
        islandId: selectedIsland.value.id,
        title: resourceDraft.value.title.trim(),
        description: resourceDraft.value.description.trim(),
        resourceType: resourceDraft.value.resourceType,
        url: resourceDraft.value.url.trim() || undefined,
        reason
      });
      resourceMessage.value = "投稿已进入守岛人审核。";
      resourceDraft.value = {
        resourceType: "ARTICLE",
        title: "",
        description: "",
        url: "",
        tags: "",
        reason: ""
      };
      return;
    } catch (error) {
      console.warn("Resource submission API failed.", error);
    }
  }

  resourceMessage.value = "已记录为本地投稿草稿，启动后端后可提交审核。";
}

async function submitRulesEdit(payload: { proposedContentHtml: string; proposedText: string }) {
  if (!payload.proposedText) {
    rulesMessage.value = "请先填写守则内容。";
    return;
  }

  if (apiOnline.value) {
    try {
      await ensureSession();
      const pages = await listWikiPages(selectedIsland.value.id);
      const page = pages.find((item) => item.slug === "gentle-rules" || item.title.includes("温柔守则"));
      if (!page) {
        rulesMessage.value = "当前岛屿还没有正式守则页，已保留为本地草稿。";
        return;
      }
      await createWikiEditRequest(page.id, {
        proposedContentHtml: payload.proposedContentHtml,
        proposedContentJson: {
          blocks: [{ type: "paragraph", text: payload.proposedText }]
        },
        changeSummary: "更新温柔守则"
      });
      rulesMessage.value = "修改申请已提交，等待守岛人审核。";
      return;
    } catch (error) {
      console.warn("Wiki edit request API failed.", error);
    }
  }

  rulesMessage.value = "后端暂不可用，已保留为本地草稿。";
}

function isSavedResource(resourceId: number) {
  const resource = resources.value.find((item) => item.id === resourceId);
  return Boolean(resource && savedRoadmarks.value.some((item) => item.title === resource.title));
}

function mapApiIsland(apiIsland: ApiIsland): Island {
  const local = mockIslands.find((item) => item.slug === apiIsland.slug);
  return {
    id: apiIsland.id,
    slug: apiIsland.slug,
    name: local?.name ?? apiIsland.name,
    subtitle: local?.subtitle ?? apiIsland.description ?? "",
    description: local?.description ?? apiIsland.description ?? "",
    slogan: local?.slogan ?? apiIsland.slogan ?? "",
    themeColor: local?.themeColor ?? apiIsland.themeColor ?? "#1f4f3c",
    accentColor: local?.accentColor ?? "#eef3eb",
    season: local?.season ?? "冬灯期",
    seasonSummary: local?.seasonSummary ?? "最近很多岛民在这里慢慢抵达。",
    mood: local?.mood ?? "平静 100%",
    population: local?.population ?? 1,
    resources: local?.resources ?? 0,
    position: local?.position ?? { x: 50, y: 50 }
  };
}

function mapApiPost(apiPost: ApiPost): PostItem {
  const island = islands.value.find((item) => item.id === apiPost.islandId);
  const local = mockPosts.find((item) => item.title === apiPost.title);
  return {
    id: apiPost.id,
    islandSlug: island?.slug ?? local?.islandSlug ?? "tree-hole",
    title: apiPost.title,
    content: apiPost.content,
    author: apiPost.authorName ?? (apiPost.anonymous ? "匿名岛民" : "岛民"),
    anonymous: apiPost.anonymous,
    statusCard: apiPost.statusCard ?? local?.statusCard ?? "JUST_BE_HEARD",
    replyPreference: apiPost.replyPreference ?? local?.replyPreference ?? "LISTEN_ONLY",
    createdAt: apiPost.createdAt ? formatRelativeTime(apiPost.createdAt) : local?.createdAt ?? "刚刚",
    reactions: local?.reactions ?? { HUG: 0, UNDERSTOOD: 0, ME_TOO: 0 },
    comments: local?.comments ?? 0
  };
}

function mapApiResource(apiResource: ApiResource): ResourceItem {
  const island = islands.value.find((item) => item.id === apiResource.islandId);
  const local = mockResources.find((item) => item.title === apiResource.title);
  const type = normalizeResourceType(apiResource.resourceType);
  return {
    id: apiResource.id,
    islandSlug: island?.slug ?? local?.islandSlug ?? "tree-hole",
    type,
    title: apiResource.title,
    description: apiResource.description ?? local?.description ?? "",
    tags: local?.tags ?? [typeLabel(type)],
    saves: local?.saves ?? 0,
    comments: local?.comments ?? 0,
    url: apiResource.url
  };
}

function mapApiRoadmark(apiRoadmark: ApiRoadmark): RoadmarkItem | null {
  if (apiRoadmark.targetType === "RESOURCE") {
    const resource = resources.value.find((item) => item.id === apiRoadmark.targetId);
    if (!resource) {
      return null;
    }
    return {
      id: apiRoadmark.id,
      category: typeLabel(resource.type),
      title: resource.title,
      island: islands.value.find((item) => item.slug === resource.islandSlug)?.name ?? "回声岛",
      note: apiRoadmark.note || "从资源库收藏。",
      type: "RESOURCE"
    };
  }

  const post = feedPosts.value.find((item) => item.id === apiRoadmark.targetId);
  if (!post) {
    return null;
  }
  return {
    id: apiRoadmark.id,
    category: "帖子",
    title: post.title,
    island: islands.value.find((item) => item.slug === post.islandSlug)?.name ?? "回声岛",
    note: apiRoadmark.note || "收藏的回声。",
    type: "POST"
  };
}

function weatherToText(items: Array<{ label: string; percent: number }>) {
  return items.slice(0, 3).map((item) => `${item.label} ${item.percent}%`).join(" · ");
}

function formatRelativeTime(value: string) {
  const timestamp = new Date(value).getTime();
  if (Number.isNaN(timestamp)) {
    return value;
  }
  const diffSeconds = Math.max(0, Math.floor((Date.now() - timestamp) / 1000));
  if (diffSeconds < 60) {
    return "刚刚";
  }
  if (diffSeconds < 3600) {
    return `${Math.floor(diffSeconds / 60)} 分钟前`;
  }
  if (diffSeconds < 86400) {
    return `${Math.floor(diffSeconds / 3600)} 小时前`;
  }
  return `${Math.floor(diffSeconds / 86400)} 天前`;
}

onMounted(() => {
  void loadRemoteData();
});
</script>

<template>
  <div class="app-shell">
    <AppTopbar
      v-if="showAppTopbar"
      :current-view="currentView"
      :data-source-label="dataSourceLabel"
      @navigate="navigate"
    />

    <main>
      <RouterView v-slot="{ Component }">
        <component
          :is="Component"
          :islands="islands"
          :draft-post="draftPost"
          :is-submitting-post="isSubmittingPost"
          :action-message="actionMessage"
          :filtered-roadmarks="filteredRoadmarks"
          :roadmark-filter="roadmarkFilter"
          @intent="handleIntent"
          @open-island="openIsland"
          @publish-post="publishPost"
          @update:roadmark-filter="roadmarkFilter = $event"
        />
      </RouterView>
    </main>
  </div>
</template>
