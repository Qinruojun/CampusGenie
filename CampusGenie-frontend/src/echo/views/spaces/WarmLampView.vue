<script setup lang="ts">
import { CupSoda } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="茶室"
    subtitle="放下输入，开始感受"
    description="这里适合慢一点说话。把一段不急着被回应的心情放下来，让它像热茶一样先停在手边。"
    :icon="CupSoda"
    tone="#b7833f"
    accent="#fff3df"
    island-slug="tea-room"
  />
</template>
