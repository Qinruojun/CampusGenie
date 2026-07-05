<script setup lang="ts">
import { Cloud } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="想象岛"
    subtitle="放飞你的思绪"
    description="这里留给灵感、幻想、创作和暂时不需要结论的念头。"
    :icon="Cloud"
    tone="#9176c8"
    accent="#f1ecfb"
  />
</template>
