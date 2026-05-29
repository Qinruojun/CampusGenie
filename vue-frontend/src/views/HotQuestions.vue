<script setup>
import { hotQuestions } from '../data/mockData'
</script>

<template>
  <main class="page hot-page">
    <p class="eyebrow">热点问题</p>
    <h1 class="page-title">大家都在问什么？</h1>
    <p class="page-desc">根据近期查询频次整理，帮助你快速找到常见答案。</p>

    <section class="card panel hot-card">
      <ol class="hot-list">
        <li v-for="item in hotQuestions" :key="item.id">
          <RouterLink :to="`/qa-result?q=${encodeURIComponent(item.title)}`">
            <span class="rank">{{ item.id }}</span>
            <strong>{{ item.title }}</strong>
            <small>{{ item.views }} 次浏览</small>
          </RouterLink>
        </li>
      </ol>
    </section>
  </main>
</template>

<style scoped>
.hot-page {
  max-width: 720px;
  text-align: center;
}

.hot-card {
  margin-top: 42px;
  text-align: left;
}

.hot-list {
  display: grid;
  gap: 4px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.hot-list a {
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 16px 12px;
  border-radius: 12px;
}

.hot-list a:hover {
  background: var(--soft-gray);
}

.rank {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(35, 157, 83, 0.1);
  color: var(--green);
  font-weight: 800;
}

small {
  color: var(--muted);
}

@media (max-width: 560px) {
  .hot-list a {
    grid-template-columns: 34px 1fr;
  }

  small {
    grid-column: 2;
  }
}
</style>
