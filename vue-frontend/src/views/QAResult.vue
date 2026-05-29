<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { relatedQuestions } from '../data/mockData'

const route = useRoute()
const question = computed(() => route.query.q || '图书馆的开放时间是多少？')
</script>

<template>
  <main class="page result-page">
    <RouterLink class="back-link" to="/">← 返回首页</RouterLink>

    <p class="eyebrow">问答结果</p>
    <h1>{{ question }}</h1>

    <section class="answer card panel">
      <div class="check">✓</div>
      <div>
        <p class="muted">系统匹配到的答案</p>
        <h2>图书馆开放时间为：周一至周日 8:00 - 22:00</h2>
        <p>寒暑假期间开放时间可能有所调整，请以学校最新通知为准。</p>
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
  max-width: 760px;
}

.back-link {
  display: inline-block;
  margin-bottom: 40px;
  color: var(--muted);
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
</style>
