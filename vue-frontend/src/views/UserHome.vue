<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const question = ref('')

function search() {
  const query = question.value.trim() || '图书馆的开放时间是多少？'
  router.push({ path: '/qa-result', query: { q: query } })
}
</script>

<template>
  <main class="home page">
    <section class="hero-center">
      <p class="eyebrow">CampusGenie</p>
      <h1 class="page-title">你好，有什么可以帮助你？</h1>
      <p class="page-desc">智能问答 · 校园知识 · 快速解决</p>

      <form class="search-box" @submit.prevent="search">
        <input v-model="question" class="input" placeholder="请输入你的问题，例如：图书馆几点关门？" />
        <button class="primary-btn" type="submit">搜索</button>
      </form>

      <div class="shortcut-grid">
        <RouterLink class="shortcut card" to="/hot">
          <span class="shortcut-icon">□</span>
          <strong>热点问题</strong>
          <small>查看大家常问的问题</small>
        </RouterLink>
        <RouterLink class="shortcut card" to="/qa-result?q=校园卡如何补办？">
          <span class="shortcut-icon">◇</span>
          <strong>快速问答</strong>
          <small>输入问题获得答案</small>
        </RouterLink>
        <RouterLink class="shortcut card" to="/contribute">
          <span class="shortcut-icon">△</span>
          <strong>我要贡献</strong>
          <small>补充新的校园知识</small>
        </RouterLink>
      </div>
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
