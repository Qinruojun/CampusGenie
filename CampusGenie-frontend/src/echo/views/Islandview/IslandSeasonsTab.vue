<script setup lang="ts">
import { Sprout } from "lucide-vue-next";
import { seasons } from "../../data/mock";
import type { Island } from "../../data/mock";

defineProps<{
  selectedIsland: Island;
}>();
</script>

<template>
  <div class="season-grid">
    <article v-for="[name, copy] in seasons" :key="name" :class="{ active: selectedIsland.season === name }">
      <Sprout :size="20" />
      <h3>{{ name }}</h3>
      <p>{{ copy }}</p>
    </article>
  </div>
</template>
