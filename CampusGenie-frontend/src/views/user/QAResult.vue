<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
const question = route.query.q
const answer = route.query.answer
const isFromAdminHot = route.query.from === 'admin-hot'
const showBackToHot = route.query.from === 'hot' || isFromAdminHot
const hotListPath = isFromAdminHot ? '/admin/hotQuestion' : '/user/hot'
const homePath = isFromAdminHot ? '/admin/knowledge' : '/user/home'



</script>

<template>
  <main class="page result-page">
    <div class="back-actions" :class="{ center: !showBackToHot }">
      <RouterLink
        v-if="showBackToHot"
        class="btn ghost"
        :to="hotListPath"
      >
        返回热点列表
      </RouterLink>
      <RouterLink class="btn primary" :to="homePath">返回首页</RouterLink>
    </div>

    <p class="eyebrow">问答结果</p>
    <h1>{{ question }}</h1>

    <section class="answer card panel">
      <div class="check">✓</div>
      <div>
        <p class="muted">系统匹配到的答案</p>
        <h2>{{ answer }}</h2>

      </div>
    </section>

    <section class="card panel related">
      <h3>相关问题</h3>
      <ul class="simple-list">
        <li v-for="item in relatedQuestions" :key="item" class="simple-row">
          <span>{{ item }}</span>
          <span>›</span>
        </li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.result-page {
  margin: 0 auto;
  max-width: 900px;
  text-align: center;
}
.eyebrow {
  margin: 0 auto;
}
.back-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
}

.back-actions .btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 44px;
  padding: 0 24px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
}

.back-actions .btn.primary {
  color: #fff;
  background: #16a34a;
  border-color: #16a34a;
}

.back-actions .btn.primary:hover {
  background: #15803d;
}

.back-actions .btn.ghost:hover {
  border-color: #16a34a;
  color: #16a34a;
}

h1 {
  margin: 0 0 26px;
  font-size: clamp(28px, 5vw, 48px);
  letter-spacing: -0.04em;
}

.answer {
  display: grid;
  grid-template-columns: 42px 1fr;
  gap: 18px;
  margin-bottom: 18px;
  text-align: left;
}

.check {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: var(--green);
  border: 1px solid rgba(35, 157, 83, 0.35);
}

.answer h2 {
  margin: 4px 0 8px;
  font-size: 22px;
}

.related h3 {
  margin: 0 0 16px;
}

.related {
  text-align: left;
}

.simple-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.simple-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}

.simple-row:hover {
  color: #16a34a;
}
</style>
