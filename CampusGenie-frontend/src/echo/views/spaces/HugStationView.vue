<script setup lang="ts">
import { Coffee } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="咖啡室"
    subtitle="一杯咖啡，一段专注时刻"
    description="这里适合放下输入、开始感受。你可以写下最近的一点灵感、一次停顿，或者一段还没有整理好的心情。"
    :icon="Coffee"
    tone="#8b5e3c"
    accent="#f7eee4"
    island-slug="coffee-room"
  />
</template>
