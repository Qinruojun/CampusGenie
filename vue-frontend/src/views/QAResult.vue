<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { knowledgeItems } from '../data/mockData'

const route = useRoute()
const helpful = ref(null)
const query = computed(() => route.query.q || '图书馆周末开放吗？')

const matched = computed(() => {
  const q = String(query.value)
  return knowledgeItems.filter(item => q.includes(item.category) || q.includes(item.question.slice(0, 3))).slice(0, 3)
})

const answer = computed(() => {
  if (matched.value.length > 0) return matched.value[0].answer
  return '我已根据校园知识库为你检索，暂未找到完全匹配的条目。建议换一种说法，或提交用户贡献等待管理员审核入库。'
})
</script>

<template>
  <main class="page-shell result-page">
    <div class="result-header">
      <div>
        <h1 class="page-title">问答结果</h1>
        <p class="page-subtitle">系统会先进行关键词检索，再返回最匹配的知识库答案。</p>
      </div>
      <RouterLink to="/" class="btn btn-ghost">返回首页</RouterLink>
    </div>

    <section class="result-grid">
      <div class="card answer-card">
        <div class="question-box">
          <span>你的问题</span>
          <h2>{{ query }}</h2>
        </div>

        <div class="assistant-answer">
          <div class="avatar">🤖</div>
          <div>
            <span class="badge">智能回答</span>
            <p>{{ answer }}</p>
          </div>
        </div>

        <div class="feedback">
          <span>这个回答对你有帮助吗？</span>
          <button class="btn btn-soft" :class="{ selected: helpful === true }" @click="helpful = true">👍 有帮助</button>
          <button class="btn btn-ghost" :class="{ selected: helpful === false }" @click="helpful = false">👎 不准确</button>
        </div>
      </div>

      <aside class="card source-card">
        <h2>📌 匹配来源</h2>
        <div v-for="item in matched" :key="item.id" class="source-item">
          <strong>{{ item.question }}</strong>
          <p>{{ item.source }} · {{ item.updatedAt }}</p>
        </div>
        <RouterLink to="/contribute" class="btn btn-primary full-btn">补充新答案</RouterLink>
      </aside>
    </section>

    <section class="card related-card">
      <h2>你可能还想问</h2>
      <div class="related-list">
        <RouterLink
          v-for="item in knowledgeItems.slice(0, 4)"
          :key="item.id"
          :to="{ path: '/qa-result', query: { q: item.question } }"
        >
          {{ item.question }}
        </RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
.result-page {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.result-header,
.feedback {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.result-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 26px;
}

.answer-card,
.source-card,
.related-card {
  padding: 28px;
}

.question-box {
  padding: 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, var(--primary), var(--purple));
  color: white;
}

.question-box span {
  opacity: 0.8;
  font-weight: 700;
}

.question-box h2 {
  margin: 10px 0 0;
  font-size: 26px;
  font-weight: 900;
}

.assistant-answer {
  display: flex;
  gap: 18px;
  margin: 28px 0;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: var(--primary-soft);
  font-size: 24px;
}

.assistant-answer p {
  margin: 14px 0 0;
  line-height: 1.9;
  font-size: 17px;
}

.feedback {
  padding-top: 22px;
  border-top: 1px solid var(--line);
}

.feedback span {
  color: var(--muted);
  margin-right: auto;
}

.selected {
  outline: 3px solid rgba(88, 101, 242, 0.15);
}

.source-card h2,
.related-card h2 {
  margin: 0 0 18px;
}

.source-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--line);
}

.source-item strong {
  font-weight: 900;
}

.source-item p {
  margin: 8px 0 0;
  color: var(--muted);
}

.full-btn {
  width: 100%;
  margin-top: 20px;
}

.related-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.related-list a {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 16px;
  background: #fafbff;
  font-weight: 800;
}

@media (max-width: 1050px) {
  .result-grid,
  .related-list {
    grid-template-columns: 1fr;
  }
}
</style>
