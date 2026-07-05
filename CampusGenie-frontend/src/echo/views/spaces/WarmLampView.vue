<script setup lang="ts">
import { Lamp } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="暖灯岛"
    subtitle="治愈与陪伴"
    description="这里适合放下一盏小灯，给正在夜里走路的人一点陪伴。"
    :icon="Lamp"
    tone="#d99b48"
    accent="#fff3df"
  />
</template>
