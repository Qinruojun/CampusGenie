<script setup>
import { onBeforeUnmount, ref } from "vue";
import {
  ArrowLeft,
  BookOpen,
  Disc3,
  Heart,
  MessageCircle,
  Music2,
  Pause,
  Play
} from "lucide-vue-next";

const isPlaying = ref(false);

const books = [
  {
    title: "The History of Jazz",
    author: "Ted Gioia",
    quote: "To understand the past is to better hear the present.",
    tags: ["History", "Culture"],
    likes: 42,
    comments: 8
  },
  {
    title: "The Wisdom of Improvisation",
    author: "Gary Kernfeld",
    quote: "Improvisation is not the absence of rules.",
    tags: ["Improvisation", "Thinking"],
    likes: 35,
    comments: 6
  },
  {
    title: "Attending to Music",
    author: "William Claxton",
    quote: "A musician's life is a life of deep attention.",
    tags: ["Writing", "Listening"],
    likes: 28,
    comments: 5
  }
];

const audio = typeof Audio !== "undefined" ? new Audio("/music/jazz2.mp3") : null;

if (audio) {
  audio.loop = true;
  audio.volume = 0.55;
}

const toggleMusic = async () => {
  if (!audio) return;

  try {
    if (isPlaying.value) {
      audio.pause();
      isPlaying.value = false;
    } else {
      await audio.play();
      isPlaying.value = true;
    }
  } catch (error) {
    console.error("音乐播放失败：", error);
  }
};

onBeforeUnmount(() => {
  if (!audio) return;
  audio.pause();
  audio.currentTime = 0;
});
</script>

<template>
  <section class="jazz-library-page">
    <div class="library-shell">
      <header class="library-nav">
        <RouterLink class="return-link" :to="{ name: 'jazz' }">
          <ArrowLeft :size="18" />
          <span>返回爵士岛</span>
        </RouterLink>

        <nav aria-label="爵士岛资源库导航">
          <RouterLink :to="{ name: 'jazz' }">首页</RouterLink>
          <RouterLink :to="{ name: 'create-post' }">回声广场</RouterLink>
          <RouterLink class="active" :to="{ name: 'jazz-library' }">资源库</RouterLink>
          <RouterLink :to="{ name: 'jazz-library' }">岛屿书房</RouterLink>
        </nav>

        <button class="play-button" type="button" :aria-pressed="isPlaying" @click="toggleMusic">
          <component :is="isPlaying ? Pause : Play" :size="18" />
          <span>{{ isPlaying ? "Pause Jazz" : "Play Jazz" }}</span>
        </button>
      </header>

      <main class="library-layout">
        <figure class="photo-panel">
          <img src="/assets/jazz/jazz-room.jpg" alt="Jazz record room" />
          <figcaption>
            <Disc3 :size="22" />
            <span>Jazz Island Library</span>
          </figcaption>
        </figure>

        <section class="resource-panel" aria-label="爵士资源书单">
          <div class="section-head">
            <p class="eyebrow">
              <BookOpen :size="18" />
              <span>Jazz Reading Room</span>
            </p>
            <h1>Books that speak in blue.</h1>
            <p class="lead">精选爵士书单、入门路线与聆听笔记，放在同一个安静的紫色书房里。</p>
          </div>

          <article v-for="book in books" :key="book.title" class="resource-card">
            <div class="book-cover">
              <Music2 :size="36" />
            </div>

            <div class="book-main">
              <h2>{{ book.title }}</h2>
              <p class="author">{{ book.author }}</p>
              <p class="quote">"{{ book.quote }}"</p>

              <div class="tags">
                <span v-for="tag in book.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>

            <div class="book-side">
              <button type="button">Details →</button>
              <div class="meta">
                <span><Heart :size="14" />{{ book.likes }}</span>
                <span><MessageCircle :size="14" />{{ book.comments }}</span>
              </div>
            </div>
          </article>
        </section>
      </main>
    </div>
  </section>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Shrikhand&family=Bitter:ital,wght@0,400;0,500;0,600;1,700&display=swap");

.jazz-library-page {
  width: 100vw;
  min-height: 100vh;
  margin-top: -34px;
  margin-left: calc(50% - 50vw);
  overflow-x: hidden;
  color: #2f0f3e;
  font-family: "Bitter", "Microsoft YaHei", serif;
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.34), transparent 24%),
    radial-gradient(circle at 18% 74%, rgba(99, 55, 156, 0.28), transparent 26%),
    linear-gradient(135deg, #bba7ed 0%, #d4c6fb 46%, #a98adf 100%);
}

.library-shell {
  width: min(1540px, calc(100vw - 72px));
  min-height: 100vh;
  margin: 0 auto;
  padding: 22px 0 56px;
  box-sizing: border-box;
}

.library-nav {
  display: grid;
  grid-template-columns: auto minmax(320px, 1fr) auto;
  gap: 22px;
  align-items: center;
  min-height: 72px;
}

.return-link,
.library-nav a,
.play-button {
  color: inherit;
  text-decoration: none;
}

.return-link,
.play-button {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 42px;
  border-radius: 999px;
  font-weight: 900;
}

.return-link {
  padding: 0 18px;
  border: 1px solid rgba(75, 27, 94, 0.24);
  background: rgba(229, 215, 255, 0.5);
  box-shadow: 0 12px 26px rgba(58, 29, 87, 0.12);
}

.library-nav nav {
  display: flex;
  justify-content: center;
  gap: clamp(18px, 3vw, 42px);
  min-width: 0;
}

.library-nav nav a {
  position: relative;
  padding: 10px 0;
  color: rgba(47, 15, 62, 0.82);
  font-size: 17px;
  font-weight: 900;
}

.library-nav nav a.active::after,
.library-nav nav a:hover::after {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  height: 2px;
  content: "";
  background: #6f1d65;
}

.play-button {
  justify-self: end;
  padding: 0 24px;
  color: #5c1760;
  border: 0;
  background: rgba(247, 239, 255, 0.82);
  font-family: "Shrikhand", cursive;
  font-size: 20px;
  box-shadow: 0 14px 30px rgba(59, 25, 83, 0.16);
  cursor: pointer;
}

.library-layout {
  display: grid;
  grid-template-columns: minmax(560px, 0.98fr) minmax(520px, 0.82fr);
  gap: clamp(48px, 6vw, 96px);
  align-items: center;
  min-height: calc(100vh - 150px);
  padding-top: 18px;
}

.photo-panel {
  position: relative;
  min-height: 520px;
  height: min(680px, calc(100vh - 190px));
  margin: 0;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.32);
  border-radius: 22px;
  background: rgba(92, 50, 139, 0.32);
  box-shadow: 0 30px 72px rgba(47, 20, 74, 0.24);
}

.photo-panel img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-panel::after {
  position: absolute;
  inset: 0;
  content: "";
  background: linear-gradient(180deg, transparent 52%, rgba(39, 12, 58, 0.52));
}

.photo-panel figcaption {
  position: absolute;
  right: 24px;
  bottom: 22px;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  color: #fbf6ff;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 999px;
  background: rgba(51, 18, 73, 0.48);
  font-weight: 900;
  backdrop-filter: blur(12px);
}

.resource-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 650px;
}

.section-head {
  margin-bottom: 8px;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  margin: 0 0 10px;
  color: rgba(63, 24, 78, 0.72);
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.section-head h1 {
  margin: 0;
  color: #4c155a;
  font-family: "Shrikhand", cursive;
  font-size: clamp(34px, 3.2vw, 52px);
  line-height: 1.02;
}

.lead {
  max-width: 560px;
  margin: 14px 0 0;
  color: rgba(47, 15, 62, 0.74);
  font-size: 15px;
  font-weight: 700;
  line-height: 1.7;
}

.resource-card {
  display: grid;
  grid-template-columns: 92px 1fr 104px;
  gap: 18px;
  align-items: center;
  min-height: 134px;
  padding: 16px 18px;
  border: 1px solid rgba(255, 255, 255, 0.42);
  border-radius: 18px;
  background: rgba(224, 207, 252, 0.68);
  box-shadow:
    0 16px 34px rgba(62, 31, 82, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.48);
  backdrop-filter: blur(14px);
}

.book-cover {
  display: grid;
  width: 82px;
  height: 104px;
  place-items: center;
  color: #35123f;
  border-radius: 10px;
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.24), transparent 26%),
    linear-gradient(145deg, #b58cf0 0%, #8a60cb 100%);
  box-shadow:
    8px 10px 18px rgba(67, 34, 98, 0.22),
    inset 8px 0 10px rgba(45, 20, 68, 0.18);
}

.book-main {
  min-width: 0;
}

.book-main h2 {
  margin: 0 0 4px;
  color: #2f103c;
  font-size: 20px;
  line-height: 1.15;
  font-weight: 800;
}

.author {
  margin: 0 0 9px;
  color: rgba(65, 31, 76, 0.72);
  font-size: 13px;
  font-weight: 700;
}

.quote {
  margin: 0 0 11px;
  color: #3b1747;
  font-size: 13px;
  line-height: 1.4;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.tags span {
  padding: 5px 9px;
  color: #4c2860;
  border-radius: 999px;
  background: rgba(191, 168, 231, 0.78);
  font-size: 11px;
  font-weight: 800;
  line-height: 1;
}

.book-side {
  display: flex;
  height: 100%;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
}

.book-side button {
  height: 31px;
  padding: 0 13px;
  color: #43204f;
  border: 1px solid rgba(107, 65, 145, 0.36);
  border-radius: 999px;
  background: rgba(234, 221, 255, 0.58);
  font-family: "Bitter", serif;
  font-size: 12px;
  font-weight: 800;
  cursor: pointer;
}

.meta {
  display: flex;
  gap: 11px;
  color: rgba(67, 32, 79, 0.86);
  font-size: 12px;
  font-weight: 800;
  white-space: nowrap;
}

.meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 1180px) {
  .library-shell {
    width: min(980px, calc(100vw - 44px));
  }

  .library-nav {
    grid-template-columns: 1fr;
  }

  .library-nav nav {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .play-button {
    justify-self: start;
  }

  .library-layout {
    grid-template-columns: 1fr;
    gap: 34px;
  }

  .photo-panel {
    min-height: 0;
    height: 440px;
  }

  .resource-panel {
    max-width: none;
  }
}

@media (max-width: 640px) {
  .jazz-library-page {
    margin-top: -20px;
  }

  .library-shell {
    width: calc(100vw - 28px);
    padding-top: 18px;
  }

  .photo-panel {
    height: 320px;
  }

  .resource-card {
    grid-template-columns: 78px 1fr;
  }

  .book-cover {
    width: 70px;
    height: 92px;
  }

  .book-side {
    grid-column: 2;
    flex-direction: row;
    align-items: center;
  }
}
</style>
