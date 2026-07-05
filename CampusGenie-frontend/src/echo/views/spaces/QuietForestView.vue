<script setup lang="ts">
import { computed, ref } from "vue";
import { RouterLink } from "vue-router";
import { ArrowLeft, CirclePlay, Heart, MessageCircle, Plus, TreePine } from "lucide-vue-next";
import type { PostItem } from "../../data/mock";
import { reactionLabel, statusCardLabel } from "../../utils/labels";

const isMeditating = ref(false);

const props = defineProps<{
  islandPosts?: PostItem[];
  actionMessage?: string;
}>();

const emit = defineEmits<{
  reactToPost: [post: PostItem, reaction: string];
}>();

const visiblePosts = computed(() => (props.islandPosts ?? []).filter((post) => post.islandSlug === "low-energy"));

function startMeditation() {
  isMeditating.value = true;
}
</script>

<template>
  <section class="meditation-view">
    <RouterLink class="meditation-back" :to="{ name: 'home' }">
      <ArrowLeft :size="17" />
      <span>返回精神空间</span>
    </RouterLink>

    <div class="meditation-hero">
      <div class="meditation-copy">
        <span class="meditation-kicker">
          <TreePine :size="18" />
          冥想社区
        </span>
        <h1>冥想社区</h1>
        <p class="subtitle">静下来，听听自己</p>
        <p>
          这里适合低能量、疲惫或者想短暂停靠的人。你可以跟着一段很短的呼吸练习，
          先把注意力放回身体，再慢慢整理今天的感受。
        </p>

        <button class="meditation-start" type="button" @click="startMeditation">
          <CirclePlay :size="19" />
          <span>{{ isMeditating ? "冥想进行中" : "开始冥想" }}</span>
        </button>
      </div>

      <div class="meditation-orb" :class="{ active: isMeditating }" aria-hidden="true">
        <TreePine :size="86" />
      </div>
    </div>

    <p v-if="isMeditating" class="breath-line">吸气 4 秒，停留 2 秒，呼气 6 秒。重复三轮就好。</p>

    <section id="community-posts" class="meditation-posts">
      <div class="posts-head">
        <div>
          <p class="eyebrow">回声广场</p>
          <h2>冥想社区的回声</h2>
          <p>发到冥想社区的帖子会出现在这里。</p>
        </div>
        <RouterLink class="post-action" :to="{ name: 'create-post', query: { island: 'low-energy' } }">
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
        <span>发布第一条帖子后，它会显示在冥想社区的回声广场。</span>
      </article>
    </section>
  </section>
</template>

<style scoped>
.meditation-view {
  display: grid;
  gap: 18px;
  min-height: calc(100vh - 160px);
}

.meditation-back {
  display: inline-flex;
  align-items: center;
  justify-self: start;
  gap: 8px;
  color: #26362f;
  text-decoration: none;
  font-weight: 900;
}

.meditation-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(240px, 0.55fr);
  gap: 32px;
  align-items: center;
  min-height: 560px;
  padding: clamp(28px, 6vw, 72px);
  border: 1px solid rgba(38, 49, 45, 0.12);
  border-radius: 8px;
  background:
    radial-gradient(circle at 74% 28%, rgba(255, 255, 255, 0.82), transparent 34%),
    linear-gradient(135deg, #eff6d7, rgba(255, 252, 246, 0.92)),
    #fffaf2;
  box-shadow: 0 24px 64px rgba(53, 61, 55, 0.12);
}

.meditation-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #729646;
  font-size: 14px;
  font-weight: 900;
}

.meditation-copy h1 {
  max-width: 720px;
  margin: 22px 0 0;
  color: #1f342b;
  font-size: clamp(42px, 7vw, 78px);
}

.subtitle {
  margin: 18px 0 0;
  color: #729646;
  font-size: 22px;
  font-weight: 900;
}

.meditation-copy > p:not(.subtitle) {
  max-width: 680px;
  color: #53615a;
  font-size: 17px;
  line-height: 1.8;
}

.meditation-start {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  min-height: 46px;
  margin-top: 26px;
  padding: 10px 18px;
  color: #fff;
  border: 0;
  border-radius: 8px;
  background: #729646;
  font-weight: 900;
}

.meditation-orb {
  display: grid;
  width: min(300px, 100%);
  aspect-ratio: 1;
  place-items: center;
  justify-self: center;
  color: #729646;
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(255, 255, 255, 0.28)),
    #eff6d7;
  box-shadow:
    0 28px 80px rgba(53, 61, 55, 0.14),
    inset 0 0 34px rgba(255, 255, 255, 0.58);
}

.meditation-orb.active {
  animation: breathe 5s ease-in-out infinite;
}

.breath-line {
  justify-self: start;
  margin: 0;
  padding: 12px 16px;
  color: #4d6840;
  border: 1px solid rgba(114, 150, 70, 0.2);
  border-radius: 8px;
  background: rgba(239, 246, 215, 0.72);
  font-weight: 800;
}

.meditation-posts {
  display: grid;
  gap: 18px;
  padding: clamp(24px, 4vw, 42px);
  border: 1px solid rgba(114, 150, 70, 0.16);
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
  color: #729646;
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
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  padding: 9px 14px;
  color: #fff;
  border-radius: 8px;
  background: #729646;
  text-decoration: none;
  font-weight: 900;
}

.inline-message {
  margin: 0;
  color: #729646;
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
  color: #729646;
  border: 1px solid rgba(114, 150, 70, 0.22);
  border-radius: 999px;
  background: #eff6d7;
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

@media (max-width: 1440px) and (min-width: 761px) {
  .meditation-view {
    gap: 14px;
  }

  .meditation-hero {
    grid-template-columns: minmax(0, 1fr) minmax(200px, 0.48fr);
    gap: 24px;
    min-height: 440px;
    padding: 42px;
  }

  .meditation-copy h1 {
    font-size: 58px;
  }

  .subtitle {
    margin-top: 12px;
    font-size: 19px;
  }

  .meditation-copy > p:not(.subtitle) {
    font-size: 15px;
    line-height: 1.65;
  }

  .meditation-start {
    margin-top: 22px;
  }

  .meditation-orb {
    width: min(230px, 100%);
  }

  .meditation-posts {
    gap: 14px;
    padding: 28px;
  }

  .posts-head h2 {
    font-size: 32px;
  }
}

@media (max-height: 760px) and (min-width: 761px) {
  .meditation-hero {
    min-height: 380px;
    padding: 34px;
  }

  .meditation-orb {
    width: min(200px, 100%);
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(0.96);
  }

  50% {
    transform: scale(1.05);
  }
}

@media (max-width: 760px) {
  .meditation-hero {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .meditation-orb {
    width: min(220px, 100%);
    order: -1;
  }

  .posts-head {
    display: grid;
  }
}
</style>
