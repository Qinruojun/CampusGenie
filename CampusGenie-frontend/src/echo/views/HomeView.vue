<script setup lang="ts">
import { computed, type Component } from "vue";
import { RouterLink, type RouteLocationRaw } from "vue-router";
import {
  ChevronRight,
  CircleDot,
  Clock3,
  Cloud,
  Compass,
  Coffee,
  CupSoda,
  Ear,
  Leaf,
  MoonStar,
  Plus,
  ScrollText,
  Star,
  TentTree,
  TowerControl,
  TreePine,
  UserRound,
  Waves
} from "lucide-vue-next";
import { customIslands } from "../stores/customIslands";

type SpaceNode = {
  id: string;
  title: string;
  subtitle: string;
  icon: Component;
  to: RouteLocationRaw;
  x: number;
  y: number;
  tone: string;
  bg: string;
  size?: "large" | "medium" | "small";
};

const filters: Array<{ label: string; icon: Component; to: RouteLocationRaw; active?: boolean }> = [
  { label: "所有岛屿", icon: CircleDot, to: { name: "map" }, active: true },
  { label: "我的岛屿", icon: UserRound, to: { name: "my-map", query: { islands: ["保研岛", "树洞岛", "爵士岛", "跑步岛"] } } },
  { label: "最近访问", icon: Clock3, to: { name: "quiet-forest" } },

];

const nodes: SpaceNode[] = [
  {
    id: "quiet-forest",
    title: "冥想社区",
    subtitle: "静下来，听听自己",
    icon: TreePine,
    to: { name: "quiet-forest" },
    x: 48,
    y: 22,
    tone: "#9dbc61",
    bg: "#eff6d7",
    size: "large"
  },
  {
    id: "lighthouse",
    title: "灯塔丘",
    subtitle: "寻找一点方向",
    icon: TowerControl,
    to: { name: "lighthouse-hill" },
    x: 70,
    y: 30,
    tone: "#7aaed6",
    bg: "#e9f5fb",
    size: "large"
  },
  {
    id: "heard-bay",
    title: "被听见湾",
    subtitle: "说说你的故事",
    icon: Ear,
    to: { name: "heard-bay" },
    x: 50,
    y: 52,
    tone: "#93b85a",
    bg: "#eef6df",
    size: "large"
  },
  {
    id: "hug-station",
    title: "咖啡室",
    subtitle: "一杯咖啡，一段专注时刻",
    icon: Coffee,
    to: { name: "hug-station" },
    x: 88,
    y: 48,
    tone: "#eaa0a0",
    bg: "#fff0ef",
    size: "large"
  },
  {
    id: "memory-dock",
    title: "记忆码头",
    subtitle: "记录此刻的你",
    icon: ScrollText,
    to: { name: "create-post" },
    x: 73,
    y: 66,
    tone: "#e7b25b",
    bg: "#fff4df",
    size: "medium"
  },
  {
    id: "dream-island",
    title: "想象岛",
    subtitle: "放飞你的思绪",
    icon: Cloud,
    to: { name: "dream-island" },
    x: 60,
    y: 79,
    tone: "#a891d3",
    bg: "#f1ecfb",
    size: "medium"
  },
  {
    id: "new-island",
    title: "新岛屿",
    subtitle: "探索更多可能",
    icon: Compass,
    to: { name: "map" },
    x: 42,
    y: 78,
    tone: "#70b8ab",
    bg: "#e6f5f1",
    size: "small"
  },
  {
    id: "flowing-coast",
    title: "扔一个漂流瓶",
    subtitle: "释放情绪",
    icon: Waves,
    to: { name: "flowing-coast" },
    x: 23,
    y: 66,
    tone: "#89bee5",
    bg: "#edf8ff",
    size: "medium"
  },
  {
    id: "warm-lamp",
    title: "茶室",
    subtitle: "放下输入，开始感受",
    icon: CupSoda,
    to: { name: "warm-lamp" },
    x: 32,
    y: 45,
    tone: "#e7b56b",
    bg: "#fff3df",
    size: "medium"
  }
];

const connectionPaths = [
  "M37 31 L48 22 L58 38",
  "M58 38 L70 30 L85 38 L88 48",
  "M58 38 L50 52 L68 52 L73 66",
  "M37 61 L50 52",
  "M27 64 L37 61 L42 78 L50 72 L60 79 L73 66",
  "M68 52 L82 50 L88 48",
  "M32 45 L23 66"
];

const allNodes = computed<SpaceNode[]>(() => [
  ...nodes,
  ...customIslands.value.map((island, index) => ({
    id: island.id,
    title: island.name,
    subtitle: island.motto || "新创建的岛屿",
    icon: Leaf,
    to: { name: "custom-island", params: { id: island.id } },
    x: 82 + (index % 2) * 6,
    y: 76 - Math.floor(index / 2) * 10,
    tone: island.themeColor,
    bg: "#eef5e8",
    size: "medium" as const
  }))
]);
</script>

<template>
  <section class="spirit-space">
    <div class="sky-wash" aria-hidden="true"></div>

    <header class="space-header">
      <RouterLink class="brand-kicker" :to="{ name: 'home' }">
        <Leaf :size="22" />
        <span>精神空间</span>
      </RouterLink>

      <RouterLink class="season-card" :to="{ name: 'quiet-forest' }">
        <span class="season-icon"><MoonStar :size="30" /></span>
        <span>
          <strong>冬灯期</strong>
          <small>适合慢慢前行</small>
        </span>
        <ChevronRight :size="24" />
      </RouterLink>
    </header>

    <section class="hero-copy" aria-labelledby="space-title">
      <h1 id="space-title">你今天想去哪里？</h1>
      <p>每一个岛屿，都是一段正在发生的故事。</p>
      <p>选择一个入口，开始你的探索。</p>
      <span class="handline" aria-hidden="true"></span>
    </section>

    <nav class="filter-panel" aria-label="岛屿筛选">
      <RouterLink
        v-for="item in filters"
        :key="item.label"
        :class="{ active: item.active }"
        :to="item.to"
      >
        <component :is="item.icon" :size="23" />
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>

    <div class="map-field" aria-label="精神空间地图">
      <svg class="route-lines" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true">
        <path v-for="path in connectionPaths" :key="path" :d="path" />
        <circle cx="37" cy="31" r="0.75" />
        <circle cx="58" cy="38" r="0.75" />
        <circle cx="85" cy="38" r="0.75" />
        <circle cx="68" cy="52" r="0.75" />
        <circle cx="37" cy="61" r="0.75" />
        <circle cx="50" cy="72" r="0.75" />
        <circle cx="82" cy="50" r="0.75" />
      </svg>

      <RouterLink
        v-for="node in allNodes"
        :key="node.id"
        class="space-node"
        :class="[`node-${node.size ?? 'medium'}`]"
        :to="node.to"
        :style="{ '--x': `${node.x}%`, '--y': `${node.y}%`, '--tone': node.tone, '--node-bg': node.bg }"
      >
        <span class="node-icon">
          <component :is="node.icon" :size="node.size === 'large' ? 40 : 32" />
        </span>
        <strong>{{ node.title }}</strong>
        <small>{{ node.subtitle }}</small>
      </RouterLink>
    </div>

    <RouterLink class="status-pill" :to="{ name: 'map' }">
      <Leaf :size="18" />
      <strong>今日状态</strong>
      <span><i class="dot tired"></i>疲惫 37%</span>
      <span><i class="dot calm"></i>平静 21%</span>
      <span><i class="dot lost"></i>迷茫 12%</span>
      <ChevronRight :size="20" />
    </RouterLink>

    <RouterLink class="create-island" :to="{ name: 'create-island' }">
      <span><Plus :size="36" /></span>
      <strong>创建岛屿</strong>
    </RouterLink>

    <div class="forest foreground-left" aria-hidden="true">
      <TentTree :size="190" />
    </div>
    <div class="forest foreground-right" aria-hidden="true">
      <TreePine :size="112" />
      <TreePine :size="86" />
      <TreePine :size="132" />
    </div>
  </section>
</template>

<style scoped>
.spirit-space {
  position: relative;
  width: 100vw;
  min-height: 860px;
  margin-left: calc(50% - 50vw);
  margin-top: -34px;
  overflow: hidden;
  padding: clamp(28px, 4vw, 56px) clamp(22px, 4vw, 64px) 96px;
  color: #142d21;
  background:
    linear-gradient(180deg, rgba(249, 248, 238, 0.94), rgba(236, 240, 226, 0.86) 58%, rgba(218, 229, 215, 0.92)),
    #f6f3e7;
}

.sky-wash {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 52% 8%, rgba(255, 255, 255, 0.86) 0 2px, transparent 3px),
    radial-gradient(circle at 24% 42%, rgba(255, 255, 255, 0.7) 0 2px, transparent 3px),
    linear-gradient(115deg, rgba(255, 255, 255, 0.58), transparent 45%);
  pointer-events: none;
}

.space-header,
.hero-copy,
.filter-panel,
.map-field,
.status-pill,
.create-island {
  position: relative;
  z-index: 2;
}

.space-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.brand-kicker,
.season-card,
.status-pill,
.create-island,
.filter-panel a,
.space-node {
  color: inherit;
  text-decoration: none;
}

.brand-kicker {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #173724;
  font-size: 18px;
  font-weight: 900;
}

.brand-kicker svg {
  color: #8aad58;
}

.season-card {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  min-width: 238px;
  min-height: 84px;
  padding: 14px 18px;
  border: 1px solid rgba(30, 42, 35, 0.1);
  border-radius: 999px;
  background: rgba(246, 244, 232, 0.72);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.64);
}

.season-card strong,
.season-card small {
  display: block;
}

.season-card strong {
  font-size: 18px;
}

.season-card small {
  margin-top: 3px;
  color: #657064;
  font-size: 13px;
  font-weight: 700;
}

.season-icon {
  display: grid;
  width: 56px;
  height: 56px;
  place-items: center;
  color: #2f3429;
  border-radius: 50%;
  background: rgba(231, 230, 210, 0.86);
}

.hero-copy {
  max-width: 520px;
  margin-top: 62px;
}

.hero-copy h1 {
  color: #132d20;
  font-family: "Songti SC", "STSong", "SimSun", serif;
  font-size: clamp(44px, 6vw, 72px);
  font-weight: 900;
  line-height: 1.08;
}

.hero-copy p {
  margin: 22px 0 0;
  color: #4b554d;
  font-size: 17px;
  line-height: 1.45;
}

.hero-copy p + p {
  margin-top: 12px;
}

.handline {
  display: block;
  width: 160px;
  height: 18px;
  margin-top: 22px;
  border-bottom: 2px solid #9ebf73;
  border-radius: 50%;
  transform: skewX(-16deg);
}

.filter-panel {
  display: grid;
  gap: 20px;
  width: 146px;
  margin-top: 84px;
  padding: 20px 16px;
  border: 1px solid rgba(31, 49, 38, 0.12);
  border-radius: 32px;
  background: rgba(248, 247, 237, 0.72);
  backdrop-filter: blur(12px);
}

.filter-panel a {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-height: 34px;
  color: #60675f;
  font-size: 14px;
  font-weight: 800;
}

.filter-panel a.active {
  color: #243326;
}

.filter-panel a.active svg {
  color: #9cbd6f;
  fill: rgba(156, 189, 111, 0.28);
}

.map-field {
  position: absolute;
  inset: 114px 90px 124px 236px;
}

.route-lines {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.route-lines path {
  fill: none;
  stroke: rgba(71, 80, 71, 0.18);
  stroke-width: 0.22;
}

.route-lines circle {
  fill: #7e8387;
  opacity: 0.9;
}

.space-node {
  position: absolute;
  left: var(--x);
  top: var(--y);
  display: grid;
  justify-items: center;
  gap: 8px;
  width: 136px;
  color: #1b2f25;
  text-align: center;
  transform: translate(-50%, -50%);
}

.space-node:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--tone) 58%, transparent);
  outline-offset: 8px;
}

.node-icon {
  display: grid;
  width: 86px;
  height: 86px;
  place-items: center;
  color: var(--tone);
  border: 1px solid rgba(255, 255, 255, 0.86);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.36)),
    var(--node-bg);
  box-shadow:
    0 12px 34px rgba(55, 65, 58, 0.08),
    inset 0 0 18px rgba(255, 255, 255, 0.62);
  transition:
    transform 180ms ease,
    box-shadow 180ms ease;
}

.space-node:hover .node-icon {
  transform: translateY(-4px) scale(1.03);
  box-shadow:
    0 18px 38px rgba(55, 65, 58, 0.13),
    inset 0 0 18px rgba(255, 255, 255, 0.66);
}

.node-large .node-icon {
  width: 110px;
  height: 110px;
}

.node-small .node-icon {
  width: 78px;
  height: 78px;
}

.space-node strong {
  font-size: 17px;
  line-height: 1.1;
}

.space-node small {
  color: #5a625b;
  font-size: 13px;
  font-weight: 700;
}

.status-pill {
  position: absolute;
  left: 50%;
  bottom: 40px;
  display: inline-flex;
  align-items: center;
  gap: 22px;
  min-height: 58px;
  padding: 0 30px;
  border: 1px solid rgba(31, 49, 38, 0.11);
  border-radius: 999px;
  background: rgba(248, 247, 237, 0.76);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.52);
  transform: translateX(-50%);
}

.status-pill > svg:first-child {
  color: #94b968;
}

.status-pill span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #3f4b43;
  font-weight: 800;
  white-space: nowrap;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.tired {
  background: #a8c978;
}

.dot.calm {
  background: #9cc9e6;
}

.dot.lost {
  background: #f1a2a4;
}

.create-island {
  position: absolute;
  right: 52px;
  bottom: 38px;
  display: grid;
  justify-items: center;
  gap: 10px;
  color: #1f2e25;
  font-size: 14px;
  font-weight: 900;
}

.create-island span {
  display: grid;
  width: 68px;
  height: 68px;
  place-items: center;
  border-radius: 50%;
  background: rgba(231, 233, 217, 0.86);
  box-shadow: 0 18px 34px rgba(52, 63, 54, 0.12);
}

.forest {
  position: absolute;
  z-index: 1;
  color: #6f815d;
  opacity: 0.58;
  pointer-events: none;
}

.foreground-left {
  left: 18px;
  bottom: 8px;
  color: #89a24e;
}

.foreground-right {
  right: 118px;
  bottom: 42px;
  display: inline-flex;
  align-items: end;
  gap: 8px;
  color: #8a9b82;
}

@media (max-width: 1440px) and (min-width: 821px) {
  .spirit-space {
    min-height: 760px;
    padding: 30px 42px 72px;
  }

  .season-card {
    min-width: 210px;
    min-height: 68px;
    padding: 10px 14px;
  }

  .season-icon {
    width: 46px;
    height: 46px;
  }

  .hero-copy {
    max-width: 430px;
    margin-top: 42px;
  }

  .hero-copy h1 {
    font-size: 56px;
  }

  .hero-copy p {
    margin-top: 16px;
    font-size: 15px;
  }

  .handline {
    width: 132px;
    margin-top: 16px;
  }

  .filter-panel {
    gap: 14px;
    width: 132px;
    margin-top: 56px;
    padding: 16px 14px;
  }

  .filter-panel a {
    min-height: 30px;
    font-size: 13px;
  }

  .map-field {
    inset: 88px 56px 98px 190px;
  }

  .space-node {
    gap: 6px;
    width: 118px;
  }

  .node-icon {
    width: 72px;
    height: 72px;
  }

  .node-large .node-icon {
    width: 92px;
    height: 92px;
  }

  .node-small .node-icon {
    width: 66px;
    height: 66px;
  }

  .space-node strong {
    font-size: 15px;
  }

  .space-node small {
    font-size: 12px;
  }

  .status-pill {
    bottom: 28px;
    gap: 16px;
    min-height: 50px;
    padding: 0 22px;
  }

  .create-island {
    right: 38px;
    bottom: 26px;
  }

  .create-island span {
    width: 56px;
    height: 56px;
  }

  .foreground-left svg {
    width: 150px;
    height: 150px;
  }

  .foreground-right {
    right: 92px;
    bottom: 30px;
  }

  .foreground-right svg:nth-child(1) {
    width: 92px;
    height: 92px;
  }

  .foreground-right svg:nth-child(2) {
    width: 70px;
    height: 70px;
  }

  .foreground-right svg:nth-child(3) {
    width: 108px;
    height: 108px;
  }
}

@media (max-height: 760px) and (min-width: 821px) {
  .spirit-space {
    min-height: 700px;
    padding-top: 24px;
    padding-bottom: 62px;
  }

  .hero-copy {
    margin-top: 30px;
  }

  .filter-panel {
    margin-top: 42px;
  }

  .map-field {
    inset: 74px 52px 86px 180px;
  }

  .node-icon {
    width: 66px;
    height: 66px;
  }

  .node-large .node-icon {
    width: 84px;
    height: 84px;
  }
}

@media (max-width: 1120px) {
  .spirit-space {
    min-height: 900px;
  }

  .map-field {
    inset: 252px 24px 116px 190px;
  }

  .filter-panel {
    margin-top: 52px;
  }

  .season-card {
    min-width: 218px;
  }
}

@media (max-width: 820px) {
  .spirit-space {
    display: grid;
    gap: 24px;
    min-height: 0;
    padding-bottom: 42px;
  }

  .space-header {
    display: grid;
  }

  .season-card {
    justify-self: start;
  }

  .hero-copy {
    margin-top: 10px;
  }

  .filter-panel,
  .map-field,
  .status-pill,
  .create-island {
    position: relative;
    inset: auto;
    left: auto;
    right: auto;
    bottom: auto;
    transform: none;
  }

  .filter-panel {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    width: auto;
    margin-top: 0;
    border-radius: 8px;
  }

  .map-field {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 20px;
  }

  .route-lines,
  .forest {
    display: none;
  }

  .space-node {
    position: relative;
    left: auto;
    top: auto;
    width: auto;
    min-height: 180px;
    padding: 18px 12px;
    border: 1px solid rgba(31, 49, 38, 0.1);
    border-radius: 8px;
    background: rgba(255, 252, 246, 0.68);
    transform: none;
  }

  .status-pill {
    justify-content: center;
    flex-wrap: wrap;
    border-radius: 8px;
    padding: 16px;
  }

  .create-island {
    justify-self: center;
  }
}

@media (max-width: 560px) {
  .map-field,
  .filter-panel {
    grid-template-columns: 1fr;
  }

  .hero-copy h1 {
    font-size: 42px;
  }

  .season-card {
    width: 100%;
  }

  .status-pill {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
