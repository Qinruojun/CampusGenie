<template>
  <div class="avatar" :style="{ width: size + 'px', height: size + 'px' }">
    <svg viewBox="0 0 44 44" aria-hidden="true">
      <circle cx="22" cy="22" r="22" :fill="backgroundColor" />
      <circle cx="22" cy="17" r="8" :fill="skinColor" />
      <path
          d="M8 40c2.4-8 8-12 14-12s11.6 4 14 12"
          :fill="bodyColor"
      />
      <path
          d="M14 15c1-5 5-8 10-7 4 .7 6 4 6 8-4-1-8-2.5-12.5-4.8A9.5 9.5 0 0 1 14 15Z"
          :fill="hairColor"
      />
    </svg>
  </div>
</template>

<script setup>
defineProps({
  size: {
    type: Number,
    default: 44
  },
  backgroundColor: {
    type: String,
    default: '#d8ecff'
  },
  skinColor: {
    type: String,
    default: '#f2b181'
  },
  bodyColor: {
    type: String,
    default: '#58a8e6'
  },
  hairColor: {
    type: String,
    default: '#26364f'
  }
})
</script>

<style scoped>
.avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar svg {
  width: 100%;
  height: 100%;
  display: block;
}
</style>