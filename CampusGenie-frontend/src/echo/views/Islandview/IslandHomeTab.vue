<script setup lang="ts">
import { ChevronRight } from "lucide-vue-next";
import type { Island, IslandTab, PostItem } from "../../data/mock";

defineProps<{
  selectedIsland: Island;
  islandPosts: PostItem[];
  isJazzIsland: boolean;
}>();

const emit = defineEmits<{
  "update:islandTab": [value: IslandTab];
}>();

const jazzFeatureCards = [
  ["Call & Response", "把回应当作对话，不抢走别人的独奏。"],
  ["Improvisation", "允许一次不完美的开始，在现场里慢慢长出方向。"],
  ["Blue Notes", "把低落、迟疑和松弛也放进声音里。"]
] as const;
</script>

<template>
  <div class="island-grid">
    <article class="stat-band">
      <div>
        <strong>{{ selectedIsland.population.toLocaleString() }}</strong>
        <span>岛民</span>
      </div>
      <div>
        <strong>{{ selectedIsland.resources }}</strong>
        <span>资源</span>
      </div>
      <div>
        <strong>{{ islandPosts.length }}</strong>
        <span>回声</span>
      </div>
    </article>

    <article v-if="isJazzIsland" class="jazz-listening-room">
      <div>
        <p class="eyebrow">爵士听歌房</p>
        <h2>把岛屿从“音乐”收束成爵士现场</h2>
        <p>
          这里更像一间深夜唱片室：有人分享黑胶、有人记下一段小号独奏，也有人只是在 Play Jazz
          按钮旁坐一会儿。
        </p>
      </div>
      <div class="jazz-feature-grid">
        <section v-for="[title, copy] in jazzFeatureCards" :key="title">
          <strong>{{ title }}</strong>
          <span>{{ copy }}</span>
        </section>
      </div>
    </article>

    <article class="season-panel">
      <p class="eyebrow">岛屿四季</p>
      <h2>{{ selectedIsland.name }} · {{ selectedIsland.season }}</h2>
      <p>{{ selectedIsland.seasonSummary }}</p>
    </article>

    <article class="emotion-card">
      <p class="eyebrow">情绪天气</p>
      <h2>{{ selectedIsland.mood }}</h2>
      <p>数据会以更温柔的语言进入岛屿首页，而不是只剩下冷冰冰的统计。</p>
    </article>

    <article class="preview-list">
      <p class="eyebrow">最新回声</p>
      <button v-for="post in islandPosts.slice(0, 3)" :key="post.id" type="button" @click="emit('update:islandTab', 'posts')">
        <span>{{ post.title }}</span>
        <ChevronRight :size="16" />
      </button>
    </article>
  </div>
</template>
