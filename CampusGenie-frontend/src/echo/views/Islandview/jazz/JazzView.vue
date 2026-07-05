<script setup>
import { computed } from "vue";
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  CalendarDays,
  CloudRain,
  Disc3,
  Heart,
  MessageCircle,
  Mic2,
  Plus
} from "lucide-vue-next";
import { reactionLabel, statusCardLabel } from "../../../utils/labels.ts";

const props = defineProps({
  islandPosts: {
    type: Array,
    default: () => []
  },
  actionMessage: {
    type: String,
    default: ""
  }
});

const emit = defineEmits(["reactToPost"]);
const visiblePosts = computed(() => props.islandPosts.filter((post) => post.islandSlug === "music"));

const featureCards = [
  {
    title: "岛屿资源库",
    copy: "歌单 / 入门路线 / 经典专辑",
    icon: Disc3,
    to: { name: "jazz-library" }
  },
  {
    title: "岛屿书房",
    copy: "阅读与聆听",
    icon: BookOpen,
    to: { name: "jazz-library" }
  },
  {
    title: "回声广场",
    copy: "分享最近在听的歌",
    icon: Mic2,
    to: { name: "jazz", query: { section: "posts" } }
  },
  {
    title: "岛屿四季",
    copy: "本季岛屿氛围",
    icon: CalendarDays,
    to: { name: "jazz" }
  },
  {
    title: "情绪天气",
    copy: "焦虑 · 雨夜蓝调",
    icon: CloudRain,
    to: { name: "jazz" },
    dark: true
  }
];
</script>

<template>
  <section class="jazz-home">
    <RouterLink class="back-home" :to="{ name: 'home' }">
      <ArrowLeft :size="18" />
      <span>返回首页</span>
    </RouterLink>

    <main class="jazz-main">
      <section class="hero-grid">
        <figure class="stage-card">
          <img src="/assets/jazz/jazz-stage.jpg" alt="爵士舞台上的低音提琴、鼓和钢琴" />
        </figure>

        <article class="hero-copy">
          <h1>即兴、对话、陪伴</h1>
          <span class="hero-underline" aria-hidden="true"></span>
          <h2>边界突破、反叛与生命力</h2>
          <p>欢迎来到爵士岛。这里是爵士乐的栖息地，也是我们共同的精神角落。一起聆听、探索、分享，让音乐自由流动。</p>
        </article>

        <aside class="listening-card">
          <span class="vinyl"><Disc3 :size="34" /></span>
          <img src="/assets/jazz/jazz-room.jpg" alt="黑胶唱机和唱片收藏" />
        </aside>
      </section>

      <section class="feature-row" aria-label="爵士岛入口">
        <RouterLink
          v-for="item in featureCards"
          :key="item.title"
          class="feature-card"
          :class="{ dark: item.dark }"
          :to="item.to"
        >
          <component :is="item.icon" :size="50" />
          <span>
            <strong>{{ item.title }}</strong>
            <small>{{ item.copy }}</small>
          </span>
          <ArrowRight :size="24" />
        </RouterLink>
      </section>

      <section id="community-posts" class="jazz-posts">
        <div class="posts-head">
          <div>
            <p class="eyebrow">回声广场</p>
            <h2>爵士岛的社区回声</h2>
            <p>发到爵士岛的帖子会出现在这里。</p>
          </div>
          <RouterLink class="post-action" :to="{ name: 'create-post', query: { island: 'music' } }">
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
          <span>发布第一条帖子后，它会显示在爵士岛的回声广场。</span>
        </article>
      </section>

      <p class="jazz-signature">不必成为专家，只需保持听的好奇。 — 爵士岛</p>
    </main>
  </section>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Shrikhand&family=Bitter:ital,wght@0,400;0,600;1,700;1,800&display=swap");

.jazz-home {
  width: 100vw;
  min-height: 100vh;
  margin-left: calc(50% - 50vw);
  margin-top: -34px;
  overflow: hidden;
  color: #5b164d;
  font-family: "Bitter", "Microsoft YaHei", serif;
  background:
    radial-gradient(circle at 77% 23%, rgba(255, 255, 255, 0.44), transparent 23%),
    radial-gradient(circle at 18% 68%, rgba(255, 255, 255, 0.34), transparent 24%),
    linear-gradient(135deg, #d9cdf8 0%, #cbbcf0 48%, #e5dcff 100%);
}

.jazz-nav {
  display: grid;
  grid-template-columns: auto minmax(360px, 1fr) auto;
  gap: 22px;
  align-items: center;
  width: min(1560px, calc(100vw - 72px));
  min-height: 88px;
  margin: 0 auto;
  padding: 18px 0 10px;
}

.back-home {
  color: inherit;
  text-decoration: none;
}

.back-home {
  position: absolute;
  top: 30px;
  left: 48px;
  z-index: 3;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  padding: 0 16px;
  border: 1px solid rgba(91, 22, 77, 0.26);
  border-radius: 999px;
  background: rgba(255, 247, 252, 0.56);
  box-shadow: 0 10px 22px rgba(87, 26, 86, 0.08);
  font-size: 14px;
  font-weight: 900;
  white-space: nowrap;
}

.jazz-main {
  width: min(1560px, calc(100vw - 72px));
  margin: 0 auto;
  padding: 92px 0 64px;
}

.hero-grid {
  display: grid;
  grid-template-columns: minmax(520px, 1.08fr) minmax(330px, 0.58fr) minmax(260px, 0.36fr);
  gap: 44px;
  align-items: center;
}

.stage-card {
  position: relative;
  min-height: 500px;
  margin: 0;
}

.stage-card img {
  display: block;
  width: 100%;
  height: 500px;
  object-fit: cover;
  border-radius: 20px;
  box-shadow: 0 22px 52px rgba(55, 20, 63, 0.18);
}

.hero-copy {
  position: relative;
}

.hero-copy::before,
.hero-copy::after {
  position: absolute;
  color: #7b1e63;
  content: "✦";
  font-size: 44px;
}

.hero-copy::before {
  top: -22px;
  left: -94px;
}

.hero-copy::after {
  right: -56px;
  bottom: 120px;
}

.hero-copy h1 {
  margin: 0;
  color: #7b1e63;
  font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
  font-size: clamp(44px, 3.8vw, 66px);
  font-weight: 900;
  line-height: 1.12;
  text-shadow: 0 2px 0 rgba(255, 255, 255, 0.34);
}

.hero-underline {
  display: block;
  width: min(420px, 86%);
  height: 28px;
  margin: 10px 0 24px;
  border-bottom: 3px solid rgba(174, 62, 84, 0.72);
  border-radius: 50%;
  transform: skewX(-12deg);
}

.hero-copy h2 {
  margin: 0;
  color: #76265e;
  font-size: 28px;
  line-height: 1.35;
}

.hero-copy p {
  max-width: 560px;
  margin: 22px 0 0;
  color: rgba(73, 25, 70, 0.82);
  font-size: 17px;
  font-weight: 700;
  line-height: 1.75;
}

.listening-card {
  position: relative;
  justify-self: end;
  width: min(380px, 100%);
  margin: 0;
}

.listening-card img {
  display: block;
  width: 100%;
  height: 282px;
  object-fit: cover;
  object-position: 58% center;
  border-radius: 50% 50% 8px 8px;
  box-shadow: 0 22px 46px rgba(58, 21, 59, 0.18);
}

.vinyl {
  position: absolute;
  top: -96px;
  right: 22px;
  display: grid;
  width: 120px;
  height: 120px;
  place-items: center;
  color: #f0c2df;
  border: 9px solid #21161d;
  border-radius: 999px;
  background: radial-gradient(circle, #bd557a 0 18%, #1e151c 19% 100%);
  box-shadow: 0 16px 32px rgba(51, 18, 50, 0.28);
}

.feature-row {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 20px;
  margin-top: 56px;
}

.feature-card {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 18px;
  align-items: center;
  min-height: 132px;
  padding: 24px 28px;
  color: #5b164d;
  text-decoration: none;
  border: 1px solid rgba(91, 22, 77, 0.16);
  border-radius: 16px;
  background: rgba(255, 248, 238, 0.58);
  box-shadow: 0 14px 34px rgba(70, 28, 82, 0.1);
}

.feature-card.dark {
  color: #fff0fb;
  background: linear-gradient(135deg, #6b538f, #382652);
}

.feature-card strong,
.feature-card small {
  display: block;
}

.feature-card strong {
  font-size: 27px;
  line-height: 1.1;
}

.feature-card small {
  margin-top: 8px;
  color: currentColor;
  opacity: 0.78;
  font-size: 14px;
  font-weight: 800;
}

.jazz-signature {
  margin: 28px 0 0;
  color: rgba(91, 22, 77, 0.78);
  text-align: center;
  font-size: 16px;
  font-style: italic;
  font-weight: 800;
}

.jazz-posts {
  display: grid;
  gap: 18px;
  margin-top: 44px;
  padding: clamp(24px, 4vw, 42px);
  border: 1px solid rgba(91, 22, 77, 0.14);
  border-radius: 16px;
  background: rgba(255, 248, 238, 0.5);
  box-shadow: 0 18px 48px rgba(70, 28, 82, 0.1);
}

.posts-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.posts-head .eyebrow {
  margin: 0;
  color: #7b1e63;
  font-size: 13px;
  font-weight: 900;
}

.posts-head h2 {
  margin: 8px 0 0;
  color: #5b164d;
  font-size: clamp(28px, 4vw, 42px);
}

.posts-head p:not(.eyebrow) {
  margin: 8px 0 0;
  color: rgba(73, 25, 70, 0.72);
  font-weight: 800;
}

.post-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  padding: 9px 14px;
  color: #fff4fb;
  border-radius: 8px;
  background: #7b1e63;
  text-decoration: none;
  font-weight: 900;
}

.inline-message {
  margin: 0;
  color: #7b1e63;
  font-weight: 900;
}

.post-list {
  display: grid;
  gap: 14px;
}

.post-card,
.empty-posts {
  padding: 18px;
  border: 1px solid rgba(91, 22, 77, 0.12);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.54);
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: rgba(73, 25, 70, 0.64);
  font-size: 13px;
  font-weight: 800;
}

.post-card h3 {
  margin: 10px 0 0;
  color: #4b1745;
  font-size: 22px;
}

.post-card p {
  margin: 10px 0 0;
  color: rgba(73, 25, 70, 0.82);
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
  color: #7b1e63;
  border: 1px solid rgba(91, 22, 77, 0.16);
  border-radius: 999px;
  background: rgba(255, 248, 238, 0.68);
  font-weight: 800;
}

.empty-posts {
  display: grid;
  gap: 6px;
  color: rgba(73, 25, 70, 0.72);
}

.empty-posts strong {
  color: #5b164d;
  font-size: 18px;
}

@media (max-width: 1180px) {
  .jazz-main {
    width: min(1080px, calc(100vw - 40px));
  }

  .hero-grid {
    grid-template-columns: 1fr;
  }

  .hero-copy::before,
  .hero-copy::after,
  .vinyl {
    display: none;
  }

  .listening-card {
    justify-self: stretch;
    width: 100%;
  }

  .listening-card img {
    height: 260px;
    border-radius: 18px;
  }

  .feature-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .posts-head {
    display: grid;
  }
}

@media (max-width: 760px) {
  .jazz-home {
    margin-top: -20px;
  }

  .jazz-main {
    width: calc(100vw - 28px);
  }

  .back-home {
    top: 18px;
    left: 18px;
  }

  .stage-card,
  .stage-card img {
    min-height: 0;
    height: 260px;
  }

  .feature-row {
    grid-template-columns: 1fr;
  }
}
</style>
