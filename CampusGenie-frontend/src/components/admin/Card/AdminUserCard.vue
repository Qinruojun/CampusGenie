<template>
  <button class="user-card" type="button" @click="handleClick">
    <div class="avatar" aria-hidden="true">
      <AdminAvatar />
    </div>

    <div class="user-text">
      <strong>{{ username }}</strong>
      <span>{{ roleName }}</span>
    </div>

    <svg class="user-caret" viewBox="0 0 24 24" aria-hidden="true">
      <path d="m8 10 4 4 4-4" />
    </svg>
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AdminAvatar from '@/components/icons/AdminAvatar.vue'

const props = defineProps({
  username: {
    type: String,
    default: '管理员'
  },
  roleName: {
    type: String,
    default: '超级管理员'
  },
   to: {
     type: String,
     default: '/admin/profile'
  }
})

const router = useRouter()

function handleClick() {
  router.push(props.to)
}
</script>

<style scoped>
.user-card {
  display: flex;
  align-items: center;
  gap: 9px;
  min-width: 134px;
  border: 0;
  background: transparent;
  color: #152033;
  cursor: pointer;
  padding: 4px 6px;
  border-radius: 10px;
}

.user-card:hover {
  background: #f5f7fb;
}

.avatar {
  width: 38px;
  height: 38px;
  overflow: hidden;
  border-radius: 50%;
  flex-shrink: 0;
}

.avatar :deep(svg) {
  width: 38px;
  height: 38px;
  stroke: none;
}

.user-text {
  display: grid;
  gap: 2px;
  line-height: 1.15;
  text-align: left;
}

.user-text strong {
  font-size: 13px;
  font-weight: 800;
}

.user-text span {
  color: #6b7688;
  font-size: 12px;
  font-weight: 600;
}

.user-caret {
  width: 14px;
  height: 14px;
  color: #9aa4b2;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}
</style>