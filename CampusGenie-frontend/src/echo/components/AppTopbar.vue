<script setup lang="ts">
import type { Component } from "vue";
import { ArrowLeft, Bookmark, Home, Map, PenLine, Search } from "lucide-vue-next";
import type { ViewKey } from "../data/mock";

defineProps<{
  currentView: ViewKey;
  dataSourceLabel: string;
}>();

const emit = defineEmits<{
  navigate: [view: ViewKey];
}>();

const navItems: Array<{ key: ViewKey; label: string; icon: Component }> = [
  { key: "home", label: "精神空间", icon: Home },
  { key: "map", label: "社区探索", icon: Map },
  { key: "create-post", label: "发布感受", icon: PenLine },
  { key: "roadmarks", label: "我的路标", icon: Bookmark }

];
</script>

<template>
  <header class="topbar">
    <RouterLink class="campus-back-button" to="/user/home">
      <ArrowLeft :size="17" />
      <span>返回主页</span>
    </RouterLink>

    <button class="brand" type="button" @click="emit('navigate', 'home')">
      <span class="brand-mark">E</span>
      <span>
        <strong>回声岛</strong>
        <small>Echo Island</small>
      </span>
    </button>

    <nav class="main-nav" aria-label="主导航">
      <button
        v-for="item in navItems"
        :key="item.key"
        type="button"
        :class="{ active: currentView === item.key }"
        @click="emit('navigate', item.key)"
      >
        <component :is="item.icon" :size="17" />
        <span>{{ item.label }}</span>
      </button>
    </nav>

    <span class="data-pill">{{ dataSourceLabel }}</span>

    <button class="icon-button" type="button" aria-label="搜索">
      <Search :size="19" />
    </button>
  </header>
</template>
