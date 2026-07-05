<script setup lang="ts">
import { HeartHandshake } from "lucide-vue-next";
import SpaceRouteView from "./SpaceRouteView.vue";
</script>

<template>
  <SpaceRouteView
    title="拥抱站"
    subtitle="给自己，也给别人"
    description="当你需要一点安慰，或者想给别人留下一句支持，可以来到这里。"
    :icon="HeartHandshake"
    tone="#d98585"
    accent="#fff0ef"
  />
</template>
