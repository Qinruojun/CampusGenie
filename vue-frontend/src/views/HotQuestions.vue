<script setup>
import { computed, ref } from 'vue'
import { hotQuestions, categories } from '../data/mockData'

const selectedCategory = ref('全部')
const filtered = computed(() => {
  if (selectedCategory.value === '全部') return hotQuestions
  return hotQuestions.filter(item => item.category === selectedCategory.value)
})
const maxCount = computed(() => Math.max(...hotQuestions.map(item => item.count)))
</script>

<template>
  <main class="page-shell hot-page">
    <section class="hot-hero card">
      <div>
        <span class="badge">高频查询词</span>
        <h1>热点问题页</h1>
        <p>根据用户搜索次数生成常见问题排行榜，帮助新生快速了解高频校园问题。</p>
      </div>
      <strong>TOP {{ filtered.length }}</strong>
    </section>

    <section class="category-tabs">
      <button
        v-for="category in categories"
        :key="category"
        :class="{ active: selectedCategory === category }"
        @click="selectedCategory = category"
      >
        {{ category }}
      </button>
    </section>

    <section class="rank-grid">
      <div class="card ranking-card">
        <h2>🔥 问题排行榜</h2>
        <div v-for="(item, index) in filtered" :key="item.id" class="rank-item">
          <span class="rank-no">{{ index + 1 }}</span>
          <div class="rank-content">
            <div class="rank-title">
              <strong>{{ item.title }}</strong>
              <em>{{ item.count }} 次</em>
            </div>
            <div class="bar"><i :style="{ width: `${(item.count / maxCount) * 100}%` }"></i></div>
          </div>
        </div>
      </div>

      <aside class="card insight-card">
        <h2>运营建议</h2>
        <p>对高频问题应优先维护答案准确性，并设置推荐入口。</p>
        <ul>
          <li>教务类问题集中在考试、放假、成绩查询。</li>
          <li>生活类问题适合加入快捷入口。</li>
          <li>长期未命中的热词可转入待补充知识。</li>
        </ul>
      </aside>
    </section>
  </main>
</template>

<style scoped>
.hot-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hot-hero {
  padding: 34px;
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: center;
  background: linear-gradient(135deg, #ffffff, #eef2ff);
}

.hot-hero h1 {
  margin: 14px 0 10px;
  font-size: 36px;
  font-weight: 900;
}

.hot-hero p {
  margin: 0;
  color: var(--muted);
  line-height: 1.8;
}

.hot-hero > strong {
  color: var(--primary);
  font-size: 42px;
  font-weight: 900;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-tabs button {
  padding: 11px 18px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: white;
  color: #475569;
  font-weight: 800;
}

.category-tabs button.active {
  color: white;
  background: var(--primary);
}

.rank-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 24px;
}

.ranking-card,
.insight-card {
  padding: 28px;
}

.ranking-card h2,
.insight-card h2 {
  margin: 0 0 22px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--line);
}

.rank-no {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: #fff7ed;
  color: var(--warning);
  font-weight: 900;
}

.rank-content {
  flex: 1;
}

.rank-title {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.rank-title strong {
  font-weight: 900;
}

.rank-title em {
  color: var(--muted);
  font-style: normal;
}

.bar {
  height: 10px;
  margin-top: 10px;
  border-radius: 999px;
  overflow: hidden;
  background: #edf1f7;
}

.bar i {
  display: block;
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--primary), var(--purple));
}

.insight-card p,
.insight-card li {
  color: var(--muted);
  line-height: 1.8;
}

@media (max-width: 980px) {
  .rank-grid {
    grid-template-columns: 1fr;
  }
}
</style>
