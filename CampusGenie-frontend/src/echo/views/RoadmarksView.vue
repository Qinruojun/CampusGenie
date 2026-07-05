<script setup lang="ts">
import { ChevronRight } from "lucide-vue-next";
import type { RoadmarkItem } from "../data/mock";

defineProps<{
  filteredRoadmarks: RoadmarkItem[];
  roadmarkFilter: string;
}>();

const emit = defineEmits<{
  "update:roadmarkFilter": [value: string];
}>();

const roadmarkTabs = ["全部", "帖子", "资源", "音乐", "书籍", "模板", "经验"] as const;
</script>

<template>
  <section class="roadmark-view">
    <div class="section-heading">
      <p class="eyebrow">我的路标</p>
      <h1>把帖子、资源、音乐、书籍、模板和经验放在一个地方。</h1>
    </div>

    <div class="roadmark-tabs">
      <button
        v-for="tab in roadmarkTabs"
        :key="tab"
        type="button"
        :class="{ active: roadmarkFilter === tab }"
        @click="emit('update:roadmarkFilter', tab)"
      >
        {{ tab }}
      </button>
    </div>

    <div class="roadmark-list">
      <article v-for="item in filteredRoadmarks" :key="item.id">
        <span>{{ item.category }}</span>
        <h3>{{ item.title }}</h3>
        <p>{{ item.island }} · {{ item.note }}</p>
        <button type="button">
          <ChevronRight :size="16" />
        </button>
      </article>
    </div>
  </section>
</template>
