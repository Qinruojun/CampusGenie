<script setup lang="ts">
import { Ear } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="被听见湾"
    subtitle="说说你的故事"
    description="这里不是急着给建议的地方。你可以把没能说出口的故事放下，让它先被温柔地听见。"
    :icon="Ear"
    tone="#7ca64a"
    accent="#eef6df"
  />
</template>
