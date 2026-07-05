<script setup lang="ts">
import { BookOpen, Home, Library, MessageCircle, ShieldCheck, Sprout } from "lucide-vue-next";
import type { IslandTab } from "../../data/mock";

defineProps<{
  islandTab: IslandTab;
}>();

const emit = defineEmits<{
  "update:islandTab": [value: IslandTab];
}>();

const islandTabs: Array<{ key: IslandTab; label: string; icon: unknown }> = [
  { key: "home", label: "首页", icon: Home },
  { key: "posts", label: "回声广场", icon: MessageCircle },
  { key: "resources", label: "资源库", icon: Library },
  { key: "library", label: "岛屿书房", icon: BookOpen },
  { key: "rules", label: "温柔守则", icon: ShieldCheck },
  { key: "seasons", label: "岛屿四季", icon: Sprout }
];
</script>

<template>
  <nav class="tabbar" aria-label="岛屿栏目">
    <button
      v-for="tab in islandTabs"
      :key="tab.key"
      type="button"
      :class="{ active: islandTab === tab.key }"
      @click="emit('update:islandTab', tab.key)"
    >
      <component :is="tab.icon" :size="16" />
      <span>{{ tab.label }}</span>
    </button>
  </nav>
</template>
