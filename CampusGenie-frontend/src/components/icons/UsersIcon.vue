<template>
  <svg
      class="icon"
      viewBox="0 0 24 24"
      aria-hidden="true"
      fill="none"
      stroke="currentColor"
      :stroke-width="strokeWidth"
      stroke-linecap="round"
      stroke-linejoin="round"
      :style="{ width: size + 'px', height: size + 'px' }"
  >
    <path d="M16 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2" />
    <circle cx="9.5" cy="7" r="4" />
    <path d="M22 21v-2a4 4 0 0 0-3-3.8" />
    <path d="M16 3.2a4 4 0 0 1 0 7.6" />
  </svg>
</template>

<script setup>
defineProps({
  size: {
    type: Number,
    default: 18
  },
  strokeWidth: {
    type: Number,
    default: 2
  }
})
</script>

<style scoped>
.icon {
  display: inline-block;
  flex-shrink: 0;
}
</style>