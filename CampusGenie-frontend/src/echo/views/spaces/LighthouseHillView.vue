<script setup lang="ts">
import { TowerControl } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="灯塔丘"
    subtitle="寻找一点方向"
    description="这里用于整理目标、复盘选择和寻找下一步。适合学习、升学、计划和长期方向相关的讨论。"
    :icon="TowerControl"
    tone="#4d8fbd"
    accent="#e9f5fb"
  />
</template>
