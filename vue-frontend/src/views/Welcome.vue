<!--欢迎页面，提示用户要注册-->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const question = ref('')
</script>
<template>
  <main class="home page">
    <RouterView />
    <section class="hero-center">
      <p class="eyebrow">CampusGenie</p>
      <h1 class="page-title">Welcome to CampusGenie</h1>
      <p class="page-desc">智能问答 · 校园知识 · 快速解决</p>
               <nav class="nav-links">
        <RouterLink to="/admin/login">管理员</RouterLink>
        <RouterLink to="/user/login">用户登录</RouterLink>
      </nav>
    </section>
  </main>
</template>

<style scoped>
.hero-center {
  min-height: calc(100vh - 238px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.search-box {
  width: min(640px, 100%);
  margin: 44px auto 28px;
  display: grid;
  grid-template-columns: 1fr 110px;
  gap: 12px;
}

.shortcut-grid {
  width: min(640px, 100%);
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.shortcut {
  padding: 24px 16px;
  display: grid;
  gap: 8px;
  place-items: center;
  color: var(--deep);
  transition: 0.18s ease;
}

.shortcut:hover {
  transform: translateY(-3px);
  border-color: rgba(35, 157, 83, 0.28);
}

.shortcut-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: var(--green);
  border: 1px solid rgba(35, 157, 83, 0.22);
  background: rgba(35, 157, 83, 0.06);
}

.shortcut small {
  color: var(--muted);
}

@media (max-width: 640px) {
  .search-box,
  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}
</style>
