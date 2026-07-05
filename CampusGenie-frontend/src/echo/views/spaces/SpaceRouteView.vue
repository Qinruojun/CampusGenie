<script setup lang="ts">
import { computed, type Component } from "vue";
import { RouterLink, type RouteLocationRaw } from "vue-router";
import { ArrowLeft, Compass, Heart, MessageCircle, PenLine, Plus } from "lucide-vue-next";
import type { Island, PostItem, ViewKey } from "../../data/mock";
import { reactionLabel, statusCardLabel } from "../../utils/labels";

const props = withDefaults(defineProps<{
  title: string;
  subtitle: string;
  description: string;
  icon: Component;
  tone: string;
  accent: string;
  primaryTo?: RouteLocationRaw;
  primaryLabel?: string;
  selectedIsland?: Island;
  islandSlug?: string;
  islandPosts?: PostItem[];
  actionMessage?: string;
}>(), {
  primaryLabel: "发布一条回声",
  islandPosts: () => []
});

const emit = defineEmits<{
  navigate: [view: ViewKey];
  reactToPost: [post: PostItem, reaction: string];
}>();

const postIslandSlug = computed(() => props.islandSlug || props.selectedIsland?.slug || "");
const visiblePosts = computed(() => {
  if (!postIslandSlug.value) {
    return props.islandPosts;
  }

  return props.islandPosts.filter((post) => post.islandSlug === postIslandSlug.value);
});
const primaryTarget = computed<RouteLocationRaw>(() => {
  if (props.primaryTo) {
    return props.primaryTo;
  }

  return {
    name: "create-post",
    query: postIslandSlug.value ? { island: postIslandSlug.value } : undefined
  };
});
</script>

<template>
  <section class="space-route" :style="{ '--tone': tone, '--accent': accent }">
    <RouterLink class="back-link" :to="{ name: 'home' }">
      <ArrowLeft :size="17" />
      <span>返回精神空间</span>
    </RouterLink>

    <div class="space-hero">
      <div class="space-copy">
        <span class="route-kicker">
          <Compass :size="18" />
          精神空间入口
        </span>
        <h1>{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
        <p>{{ description }}</p>

        <div class="route-actions">
          <RouterLink class="primary-action" :to="primaryTarget">
            <PenLine :size="18" />
            <span>{{ primaryLabel }}</span>
          </RouterLink>
          <RouterLink class="ghost-action" :to="{ name: 'map' }">看看全部岛屿</RouterLink>
        </div>
      </div>

      <div class="route-orb" aria-hidden="true">
        <component :is="icon" :size="86" />
      </div>
    </div>

    <section id="community-posts" class="community-posts">
      <div class="posts-head">
        <div>
          <p class="eyebrow">回声广场</p>
          <h2>{{ title }}的社区回声</h2>
          <p>发到这个社区的帖子会出现在这里。</p>
        </div>
        <RouterLink class="post-action" :to="primaryTarget">
          <Plus :size="18" />
          <span>发布回声</span>
        </RouterLink>
      </div>

      <p v-if="actionMessage" class="inline-message">{{ actionMessage }}</p>

      <div v-if="visiblePosts.length" class="post-list">
        <article v-for="post in visiblePosts" :key="post.id" class="post-card">
          <div class="post-meta">
            <span>{{ post.author }}</span>
            <span>{{ post.createdAt }}</span>
            <span>{{ statusCardLabel(post.statusCard) }}</span>
          </div>
          <h3>{{ post.title }}</h3>
          <p>{{ post.content }}</p>
          <footer>
            <button
              v-for="(count, reaction) in post.reactions"
              :key="reaction"
              type="button"
              @click="emit('reactToPost', post, String(reaction))"
            >
              <Heart :size="15" />
              {{ reactionLabel(String(reaction)) }} {{ count }}
            </button>
            <button type="button">
              <MessageCircle :size="15" />
              {{ post.comments }} 回声
            </button>
          </footer>
        </article>
      </div>

      <article v-else class="empty-posts">
        <strong>这里还没有回声</strong>
        <span>发布第一条帖子后，它会显示在这个社区的回声广场。</span>
      </article>
    </section>
  </section>
</template>

<style scoped>
.space-route {
  display: grid;
  gap: 24px;
  min-height: calc(100vh - 160px);
}

.back-link,
.primary-action,
.ghost-action,
.post-action {
  display: inline-flex;
  align-items: center;
  justify-self: start;
  gap: 8px;
  color: #26362f;
  text-decoration: none;
  font-weight: 900;
}

.space-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(240px, 0.55fr);
  gap: 32px;
  align-items: center;
  overflow: hidden;
  min-height: 560px;
  padding: clamp(28px, 6vw, 72px);
  border: 1px solid rgba(38, 49, 45, 0.12);
  border-radius: 8px;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--accent) 82%, #fff), rgba(255, 252, 246, 0.9)),
    #fffaf2;
  box-shadow: 0 24px 64px rgba(53, 61, 55, 0.12);
}

.route-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--tone);
  font-size: 14px;
  font-weight: 900;
}

h1 {
  max-width: 720px;
  margin-top: 22px;
  color: #1f342b;
  font-size: clamp(42px, 7vw, 78px);
}

.subtitle {
  margin-top: 18px;
  color: var(--tone);
  font-size: 22px;
  font-weight: 900;
}

.space-copy > p:not(.subtitle) {
  max-width: 680px;
  color: #53615a;
  font-size: 17px;
  line-height: 1.8;
}

.route-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 32px;
}

.primary-action,
.ghost-action {
  min-height: 44px;
  padding: 10px 16px;
  border-radius: 8px;
}

.primary-action {
  color: #fff;
  background: var(--tone);
}

.ghost-action {
  color: var(--tone);
  border: 1px solid color-mix(in srgb, var(--tone) 28%, transparent);
  background: rgba(255, 255, 255, 0.66);
}

.route-orb {
  display: grid;
  width: min(300px, 100%);
  aspect-ratio: 1;
  place-items: center;
  justify-self: center;
  color: var(--tone);
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(255, 255, 255, 0.28)),
    var(--accent);
  box-shadow:
    0 28px 80px rgba(53, 61, 55, 0.14),
    inset 0 0 34px rgba(255, 255, 255, 0.58);
}

.community-posts {
  display: grid;
  gap: 18px;
  padding: clamp(24px, 4vw, 42px);
  border: 1px solid rgba(38, 49, 45, 0.1);
  border-radius: 8px;
  background: rgba(255, 252, 246, 0.82);
  box-shadow: 0 18px 48px rgba(53, 61, 55, 0.1);
}

.posts-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.posts-head .eyebrow {
  margin: 0;
  color: var(--tone);
  font-size: 13px;
  font-weight: 900;
}

.posts-head h2 {
  margin: 8px 0 0;
  color: #1f342b;
  font-size: clamp(28px, 4vw, 42px);
}

.posts-head p:not(.eyebrow) {
  margin: 8px 0 0;
  color: #647067;
  font-weight: 700;
}

.post-action {
  min-height: 42px;
  padding: 9px 14px;
  color: #fff;
  border-radius: 8px;
  background: var(--tone);
}

.inline-message {
  margin: 0;
  color: var(--tone);
  font-weight: 900;
}

.post-list {
  display: grid;
  gap: 14px;
}

.post-card,
.empty-posts {
  padding: 18px;
  border: 1px solid rgba(38, 49, 45, 0.1);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.72);
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: #6f7872;
  font-size: 13px;
  font-weight: 800;
}

.post-card h3 {
  margin: 10px 0 0;
  color: #22372d;
  font-size: 22px;
}

.post-card p {
  margin: 10px 0 0;
  color: #4f5d55;
  line-height: 1.7;
}

.post-card footer {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.post-card footer button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 32px;
  padding: 6px 10px;
  color: var(--tone);
  border: 1px solid color-mix(in srgb, var(--tone) 20%, transparent);
  border-radius: 999px;
  background: color-mix(in srgb, var(--accent) 70%, #fff);
  font-weight: 800;
}

.empty-posts {
  display: grid;
  gap: 6px;
  color: #53615a;
}

.empty-posts strong {
  color: #1f342b;
  font-size: 18px;
}

@media (max-width: 760px) {
  .space-hero {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .route-orb {
    width: min(220px, 100%);
    order: -1;
  }

  .posts-head {
    display: grid;
  }
}
</style>
