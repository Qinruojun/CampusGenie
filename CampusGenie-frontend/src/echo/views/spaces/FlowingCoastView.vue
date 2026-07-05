<script setup lang="ts">
import { Waves } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="流动海岸"
    subtitle="释放情绪"
    description="情绪不需要马上被解释。你可以把它写下来，让它像潮水一样先流过。"
    :icon="Waves"
    tone="#679fd0"
    accent="#edf8ff"
  />
</template>
