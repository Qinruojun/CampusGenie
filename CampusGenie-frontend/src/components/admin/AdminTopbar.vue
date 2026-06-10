<template>
  <header class="admin-topbar">
    <div class="topbar-left">
      <button class="menu-btn" type="button" @click="emit('toggle-sidebar')">
        ☰
      </button>

      <div class="title-box">
        <div class="title">{{ title }}</div>
        <div class="subtitle">{{ subtitle }}</div>
      </div>
    </div>

    <div class="topbar-right">
      <button class="notice-btn" type="button">
        <span class="bell-icon">🔔</span>
        <span v-if="noticeCount > 0" class="notice-count">
          {{ noticeCount }}
        </span>
      </button>

      <AdminUserCard
          :username="username"
          :role-name="roleName"
      />
    </div>
  </header>
</template>

<script setup>
import AdminUserCard from '@/components/admin/Card/AdminUserCard.vue'

defineProps({
  title: {
    type: String,
    default: '后台管理'
  },
  subtitle: {
    type: String,
    default: 'CampusGenie 管理控制台'
  },
  username: {
    type: String,
    default: '管理员'
  },
  roleName: {
    type: String,
    default: '超级管理员'
  },
  noticeCount: {
    type: Number,
    default: 3
  }
})

const emit = defineEmits(['toggle-sidebar'])
</script>

<style scoped>
.admin-topbar {
  height: 72px;
  padding: 0 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e5e9f2;
  box-sizing: border-box;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.menu-btn {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border: 0;
  border-radius: 10px;
  background: #f4f7fb;
  color: #152033;
  font-size: 22px;
  line-height: 40px;
  text-align: center;
  cursor: pointer;
}

.menu-btn:hover {
  color: #16834a;
  background: #effaf4;
}

.title-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  min-width: 0;
  height: 44px;
}

.title {
  color: #152033;
  font-size: 18px;
  font-weight: 800;
  line-height: 22px;
  white-space: nowrap;
}

.subtitle {
  color: #6b7688;
  font-size: 13px;
  font-weight: 600;
  line-height: 16px;
  white-space: nowrap;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-shrink: 0;
}

.notice-btn {
  position: relative;
  width: 40px;
  height: 40px;
  border: 0;
  border-radius: 10px;
  background: #f4f7fb;
  cursor: pointer;
}

.bell-icon {
  font-size: 18px;
  line-height: 40px;
}

.notice-count {
  position: absolute;
  top: -5px;
  right: -5px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background: #ef4444;
  color: #ffffff;
  font-size: 11px;
  font-weight: 800;
  line-height: 18px;
  box-sizing: border-box;
}
</style>