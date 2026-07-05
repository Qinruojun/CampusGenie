<script setup lang="ts">
import { computed } from "vue";
import { ArrowRight, Bookmark, Plus, Star } from "lucide-vue-next";
import { useRouter } from "vue-router";
import { customIslands } from "../stores/customIslands";

const router = useRouter();

interface ExploreCard {
  id: string;
  badge: string;
  title: string;
  subtitle: string;
  description: string;
  tags: string[];
  theme: "academic" | "lifestyle" | "movement" | "mood" | "music" | "movie" | "custom";
  layout?: "wide";
  targetSlug?: string;
  targetCustomId?: string;
  image: string;
}

const emit = defineEmits<{
  openIsland: [slug: string];
}>();

const baseCards: ExploreCard[] = [
  {
    id: "research",
    badge: "Academic",
    title: "保研岛 / Research Atlas",
    subtitle: "Find my next step",
    description: "聚焦保研与科研，分享经验、资料与方法，找到属于你的学术方向。",
    tags: ["学术成长", "科研经验", "资料分享", "下一步"],
    theme: "academic",
    targetSlug: "graduate",
    image: "/assets/explore/research.jpg"
  },
  {
    id: "vegetarian",
    badge: "Lifestyle",
    title: "轻食角 / Vegetarian Corner",
    subtitle: "Try to be a vegetarian",
    description: "分享素食灵感、健康食谱与生活方式，一起吃得更好。",
    tags: ["素食生活", "健康食谱", "生活方式", "灵感分享"],
    theme: "lifestyle",
    image: "/assets/explore/vegetarian.jpg"
  },
  {
    id: "running",
    badge: "Movement",
    title: "跑步岛 / Running Club",
    subtitle: "Become a runner",
    description: "跑步不仅是运动，更是习惯与自我突破的旅程。",
    tags: ["跑步", "习惯养成", "自我突破", "运动生活"],
    theme: "movement",
    targetSlug: "running",
    image: "/assets/explore/runner.jpg"
  },
  {
    id: "tree-hole",
    badge: "Mood",
    title: "树洞岛 / Tree Hollow",
    subtitle: "Say it quietly",
    description: "在夜晚的树洞里，安心倾诉，温柔陪伴，彼此接住情绪。",
    tags: ["倾诉陪伴", "情绪支持", "温暖治愈", "心灵树洞"],
    theme: "mood",
    layout: "wide",
    targetSlug: "tree-hole",
    image: "/assets/explore/tree-hole.png"
  },
  {
    id: "jazz",
    badge: "Music",
    title: "爵士岛 / Jazz Corner",
    subtitle: "Listen to vinyl",
    description: "黑胶、爵士与灵感乐，一起分享好音乐与松弛氛围。",
    tags: ["黑胶唱片", "爵士乐", "灵魂乐", "音乐氛围"],
    theme: "music",
    layout: "wide",
    targetSlug: "music",
    image: "/assets/explore/jazz-vinyl.jpg"
  },
  {
    id: "movie",
    badge: "Cinema",
    title: "电影岛 / Movie Island",
    subtitle: "Find a story to stay",
    description: "分享电影片单、银幕瞬间与观影心情，在光影里找到自己的方向。",
    tags: ["电影片单", "观影心情", "光影美学", "故事讨论"],
    theme: "movie",
    image: "/assets/explore/movie.jfif"
  }
];

const cards = computed<ExploreCard[]>(() => [
  ...baseCards,
  ...customIslands.value.map((island) => ({
    id: `custom-${island.id}`,
    badge: island.isChildIsland ? "Sub Community" : "Community",
    title: `${island.name} / 自建社区`,
    subtitle: island.motto || "find your own way",
    description: "这是用户创建的社区，已经加入社区推荐，也会同步出现在精神空间首页节点里。",
    tags: [
      "用户创建",
      island.visibility === "PUBLIC" ? "公开可见" : "仅自己可见",
      island.isChildIsland ? "二级社区" : "一级社区"
    ],
    theme: "custom" as const,
    targetCustomId: island.id,
    image: island.coverUrl || "/assets/create-island/lighthouse.jpg"
  }))
]);

function canOpenCard(card: ExploreCard) {
  return Boolean(card.targetSlug || card.targetCustomId);
}

function getExploreActionLabel(card: ExploreCard) {
  if (card.targetCustomId) {
    return "查看社区";
  }

  return card.targetSlug ? "探索社区" : "即将开放";
}

function handleExplore(card: ExploreCard) {
  if (card.targetCustomId) {
    void router.push({ name: "custom-island", params: { id: card.targetCustomId } });
    return;
  }

  if (card.targetSlug) {
    emit("openIsland", card.targetSlug);
  }
}

function handleCreateCommunity() {
  router.push({ name: "create-island" });
}
</script>

<template>
  <section class="explore-view">
    <header class="explore-header">
      <div>
        <h1>Community Picks</h1>
        <h2>社区推荐</h2>
        <p>探索感兴趣的社区，找到属于你的热爱与方向。</p>
      </div>
      <div class="explore-actions">
        <button class="create-community-button" type="button" @click="handleCreateCommunity">
          <Plus :size="20" />
          <span>
            <strong>创建社区</strong>
            <small>find your own way</small>
          </span>
        </button>
        <div class="explore-note">
          <span>找到同频的人</span>
          <strong>一起成长与热爱</strong>
        </div>
      </div>
    </header>

    <div class="explore-grid">
      <article
          v-for="card in cards"
          :key="card.id"
          class="explore-card"
          :class="[`theme-${card.theme}`, { wide: card.layout === 'wide', clickable: canOpenCard(card) }]"
          :tabindex="canOpenCard(card) ? 0 : undefined"
          :role="canOpenCard(card) ? 'button' : undefined"
          @click="handleExplore(card)"
          @keydown.enter.prevent="handleExplore(card)"
          @keydown.space.prevent="handleExplore(card)"
      >
        <div class="card-copy">
          <div class="card-topline">
            <span class="category-pill">{{ card.badge }}</span>
            <button class="save-button" type="button" :aria-label="`收藏 ${card.title}`" @click.stop>
              <Bookmark :size="22" />
            </button>
          </div>

          <h3>{{ card.title }}</h3>
          <p class="card-subtitle">{{ card.subtitle }}</p>
          <span class="title-mark" aria-hidden="true"></span>
          <p class="card-description">{{ card.description }}</p>
        </div>

        <figure class="card-visual" :class="`visual-${card.theme}`">
          <img :src="card.image" :alt="`${card.title} 社区图片`" />
        </figure>

        <footer>
          <div class="tag-list">
            <span v-for="tag in card.tags" :key="tag">{{ tag }}</span>
          </div>
          <button
              class="explore-link"
              type="button"
              :disabled="!canOpenCard(card)"
              @click.stop="handleExplore(card)"
          >
            {{ getExploreActionLabel(card) }}
            <ArrowRight :size="16" />
          </button>
        </footer>
      </article>
    </div>

    <p class="explore-footer-note">
      <Star :size="20" />
      <span>探索更多社区，发现更多可能</span>
    </p>
  </section>
</template>

<style scoped>
.explore-view {
  display: grid;
  gap: 20px;
  color: #111315;
}

.explore-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  min-height: 120px;
}

.explore-header h1 {
  color: #07090a;
  font-size: clamp(44px, 6vw, 64px);
  line-height: 0.96;
  font-weight: 900;
}

.explore-header h2 {
  margin-top: 10px;
  color: #171a1c;
  font-size: 22px;
}

.explore-header p {
  margin: 10px 0 0;
  color: #6b7076;
  font-size: 16px;
}

.explore-note {
  position: relative;
  display: grid;
  gap: 4px;
  margin-top: 34px;
  padding-right: 42px;
  color: #303238;
  text-align: center;
  transform: rotate(-6deg);
}

.explore-note::before {
  position: absolute;
  right: 18px;
  bottom: -8px;
  width: 118px;
  height: 12px;
  border-radius: 999px;
  content: "";
  background: rgba(245, 190, 78, 0.42);
  transform: rotate(-8deg);
}

.explore-note::after {
  position: absolute;
  right: 0;
  bottom: -18px;
  color: #efb13e;
  content: "♡";
  font-size: 28px;
  line-height: 1;
  transform: rotate(18deg);
}

.explore-note span,
.explore-note strong {
  position: relative;
  z-index: 1;
}

.explore-actions {
  display: grid;
  justify-items: end;
  gap: 18px;
}

.create-community-button {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 48px;
  padding: 8px 14px;
  color: #161a1e;
  border: 1px solid rgba(20, 24, 28, 0.16);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 34px rgba(33, 36, 41, 0.1);
}

.create-community-button svg {
  flex: 0 0 auto;
}

.create-community-button span {
  display: grid;
  gap: 2px;
  text-align: left;
}

.create-community-button strong {
  font-size: 15px;
  line-height: 1.1;
}

.create-community-button small {
  color: #6f747b;
  font-size: 12px;
  font-weight: 700;
  line-height: 1.1;
}

.explore-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.explore-card {
  display: grid;
  grid-template-rows: auto minmax(190px, 1fr) auto;
  gap: 14px;
  min-width: 0;
  min-height: 420px;
  padding: 16px;
  border: 1px solid rgba(22, 26, 31, 0.1);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 18px 42px rgba(33, 36, 41, 0.08);
}

.explore-card.clickable {
  cursor: pointer;
  transition:
    transform 180ms ease,
    box-shadow 180ms ease,
    border-color 180ms ease;
}

.explore-card.clickable:hover {
  border-color: color-mix(in srgb, currentColor 32%, transparent);
  box-shadow: 0 24px 52px rgba(33, 36, 41, 0.12);
  transform: translateY(-3px);
}

.explore-card.clickable:focus-visible {
  outline: 3px solid color-mix(in srgb, currentColor 36%, transparent);
  outline-offset: 4px;
}

.explore-card.wide {
  grid-template-columns: minmax(220px, 0.72fr) minmax(300px, 1fr);
  grid-template-rows: auto auto;
  min-height: 260px;
}

.explore-card.wide .card-copy {
  align-self: start;
}

.explore-card.wide .card-visual {
  grid-column: 2;
  grid-row: 1 / span 2;
  min-height: 205px;
}

.explore-card.wide footer {
  grid-column: 1 / -1;
}

.card-copy {
  display: grid;
  align-content: start;
  min-width: 0;
}

.card-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-height: 34px;
}

.category-pill,
.tag-list span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 5px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 800;
}

.save-button {
  display: grid;
  width: 32px;
  height: 32px;
  place-items: center;
  color: #171a1c;
  border: 0;
  background: transparent;
}

.explore-card h3 {
  margin-top: 10px;
  color: #101214;
  font-size: 25px;
  font-weight: 900;
  line-height: 1.16;
}

.card-subtitle {
  margin: 4px 0 0;
  color: #3d4147;
  font-size: 16px;
}

.title-mark {
  width: 26px;
  height: 2px;
  margin-top: 10px;
  background: currentColor;
}

.card-description {
  margin: 12px 0 0;
  color: #596068;
  font-size: 15px;
  line-height: 1.7;
}

.card-visual {
  position: relative;
  overflow: hidden;
  min-width: 0;
  min-height: 190px;
  margin: 0;
  border-radius: 8px;
  background: #eef3eb;
}

.card-visual img {
  width: 100%;
  height: 100%;
  min-height: 100%;
  object-fit: cover;
}

.visual-academic img,
.visual-lifestyle img,
.visual-movement img {
  object-position: center;
}

.visual-mood img {
  object-position: 64% center;
}

.visual-music img {
  object-position: center;
}

.visual-movie img {
  object-position: center;
}

footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.explore-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
  color: currentColor;
  border: 0;
  background: transparent;
  font-weight: 800;
  white-space: nowrap;
  cursor: pointer;
}

.explore-link:disabled {
  color: #8b9098;
  cursor: not-allowed;
}

.theme-academic {
  color: #a46624;
}

.theme-academic .category-pill,
.theme-academic .tag-list span {
  background: #f7ead8;
}

.theme-lifestyle {
  color: #43752f;
}

.theme-lifestyle .category-pill,
.theme-lifestyle .tag-list span {
  background: #e8f3df;
}

.theme-movement {
  color: #145eb6;
}

.theme-movement .category-pill,
.theme-movement .tag-list span {
  background: #e4effd;
}

.theme-mood {
  color: #7c4aa5;
}

.theme-mood .category-pill,
.theme-mood .tag-list span {
  background: #f0e7f8;
}

.theme-music {
  color: #9a6738;
}

.theme-music .category-pill,
.theme-music .tag-list span {
  background: #f4eadc;
}

.theme-movie {
  color: #597f86;
}

.theme-movie .category-pill,
.theme-movie .tag-list span {
  background: #e2f0f1;
}

.theme-custom {
  color: #2f5f4f;
}

.theme-custom .category-pill,
.theme-custom .tag-list span {
  background: #e4f1ea;
}

.explore-footer-note {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 6px 0 0;
  color: #4b5058;
  font-size: 16px;
  font-weight: 700;
}

.explore-footer-note svg {
  color: #e1a42d;
}

@media (max-width: 1440px) and (min-width: 721px) {
  .explore-view {
    gap: 16px;
  }

  .explore-header {
    min-height: 92px;
  }

  .explore-header h1 {
    font-size: 46px;
  }

  .explore-header h2 {
    margin-top: 6px;
    font-size: 19px;
  }

  .explore-header p {
    margin-top: 6px;
    font-size: 14px;
  }

  .explore-actions {
    gap: 12px;
  }

  .explore-note {
    margin-top: 18px;
    padding-right: 24px;
  }

  .explore-note::before {
    width: 88px;
  }

  .explore-grid {
    gap: 16px;
  }

  .explore-card {
    grid-template-rows: auto minmax(148px, 1fr) auto;
    gap: 12px;
    min-height: 340px;
    padding: 14px;
  }

  .explore-card.wide {
    min-height: 224px;
  }

  .explore-card.wide .card-visual {
    min-height: 170px;
  }

  .explore-card h3 {
    margin-top: 8px;
    font-size: 21px;
  }

  .card-subtitle {
    font-size: 14px;
  }

  .card-description {
    margin-top: 10px;
    font-size: 14px;
    line-height: 1.55;
  }

  .card-visual {
    min-height: 150px;
  }

  .category-pill,
  .tag-list span {
    min-height: 25px;
    padding: 4px 10px;
    font-size: 12px;
  }

  .explore-footer-note {
    font-size: 14px;
  }
}

@media (max-width: 1200px) {
  .explore-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .explore-header {
    display: grid;
  }

  .explore-note {
    justify-self: start;
    margin-top: 0;
  }

  .explore-actions {
    justify-items: start;
  }

  .explore-grid,
  .explore-card.wide {
    grid-template-columns: 1fr;
  }

  .explore-card {
    grid-column: 1 / -1;
  }

  .explore-card.wide .card-visual {
    grid-column: auto;
    grid-row: auto;
  }

  footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .explore-link {
    margin-left: 0;
  }
}
</style>
