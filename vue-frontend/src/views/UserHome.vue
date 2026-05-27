<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { quickActions, hotQuestions, categories } from '../data/mockData'

const router = useRouter()
const question = ref('')
const selectedCategory = ref('全部')

function ask(text) {
  question.value = text
  submitQuestion()
}

function submitQuestion() {
  const q = question.value.trim() || '图书馆周末开放吗？'
  router.push({ path: '/qa-result', query: { q } })
}
</script>

<template>
  <main class="page-shell home-page">
    <section class="hero card">
      <div class="hero-content">
        <span class="badge">智能问答知识库</span>
        <h1>校园生活百事通</h1>
        <p>集中查询校历、食堂、图书馆、报修、社团等校园生活信息，输入自然语言即可获得答案。</p>

        <form class="search-bar" @submit.prevent="submitQuestion">
          <input v-model="question" placeholder="试试：食堂几点关门？在哪里打印？校车时刻表？" />
          <button class="btn btn-primary" type="submit">立即提问</button>
        </form>
      </div>

      <div class="quick-card">
        <button v-for="item in quickActions" :key="item.label" @click="ask(item.question)">
          <span>{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="home-grid">
      <div class="card hot-card">
        <div class="section-head">
          <h2>🔥 热门问题</h2>
          <RouterLink to="/hot">查看全部</RouterLink>
        </div>
        <div v-for="(item, index) in hotQuestions.slice(0, 5)" :key="item.id" class="hot-row" @click="ask(item.title)">
          <span class="rank">{{ index + 1 }}</span>
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ (item.count / 1000).toFixed(1) }}k 次提问 · {{ item.category }}</p>
          </div>
        </div>
      </div>

      <div class="right-stack">
        <div class="card category-card">
          <h2>📁 分类浏览</h2>
          <div class="category-list">
            <button
              v-for="category in categories"
              :key="category"
              :class="{ active: selectedCategory === category }"
              @click="selectedCategory = category"
            >
              {{ category }}
            </button>
          </div>
        </div>

        <div class="card stats-card">
          <h2>📊 平台数据</h2>
          <div class="stats-grid">
            <div><strong>12,680</strong><span>知识条目</span></div>
            <div><strong>5,421</strong><span>今日提问</span></div>
            <div><strong>328</strong><span>用户贡献</span></div>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.hero {
  min-height: 360px;
  padding: 38px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 28px;
  color: white;
  background: linear-gradient(135deg, #536dfe, #7c3aed);
  overflow: hidden;
}

.hero h1 {
  margin: 18px 0 12px;
  font-size: 46px;
  font-weight: 900;
}

.hero p {
  margin: 0;
  max-width: 740px;
  line-height: 1.9;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.92);
}

.search-bar {
  margin-top: 34px;
  max-width: 760px;
  padding: 10px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  gap: 10px;
}

.search-bar input {
  flex: 1;
  height: 52px;
  border: 0;
  outline: none;
  border-radius: 12px;
  padding: 0 18px;
}

.quick-card {
  padding: 22px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.14);
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  align-content: center;
}

.quick-card button {
  border-radius: 16px;
  padding: 16px;
  color: white;
  font-weight: 800;
  background: rgba(255, 255, 255, 0.18);
}

.home-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  gap: 28px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-head h2,
.category-card h2,
.stats-card h2 {
  margin: 0;
  font-size: 22px;
}

.section-head a {
  color: var(--primary);
  font-weight: 800;
}

.hot-card,
.category-card,
.stats-card {
  padding: 26px;
}

.hot-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 0;
  border-top: 1px solid var(--line);
  cursor: pointer;
}

.rank {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: #fff7ed;
  color: var(--warning);
  font-weight: 900;
}

.hot-row strong {
  font-weight: 900;
}

.hot-row p {
  margin: 6px 0 0;
  color: var(--muted);
}

.right-stack {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.category-list {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-list button {
  padding: 11px 18px;
  border-radius: 999px;
  background: white;
  border: 1px solid var(--line);
  color: #475569;
  font-weight: 700;
}

.category-list button.active {
  color: white;
  background: var(--primary);
}

.stats-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.stats-grid div {
  padding: 20px 14px;
  border-radius: 16px;
  background: #f8f9fd;
  text-align: center;
}

.stats-grid strong {
  display: block;
  color: var(--primary);
  font-size: 28px;
  font-weight: 900;
}

.stats-grid span {
  color: var(--muted);
}

@media (max-width: 1050px) {
  .hero,
  .home-grid {
    grid-template-columns: 1fr;
  }
}
</style>
