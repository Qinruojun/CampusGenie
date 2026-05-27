<script setup>
import { ref } from 'vue'
import { categories } from '../data/mockData'

const form = ref({ category: '图书馆', question: '', answer: '', source: '' })
const submissions = ref([
  { id: 1, question: '社团招新一般什么时候开始？', category: '社团', status: '待审核' },
  { id: 2, question: '操场晚上开放到几点？', category: '活动', status: '审核中' }
])

function submitContribution() {
  if (!form.value.question || !form.value.answer) return
  submissions.value.unshift({
    id: Date.now(),
    question: form.value.question,
    category: form.value.category,
    status: '待审核'
  })
  form.value = { category: '图书馆', question: '', answer: '', source: '' }
}
</script>

<template>
  <main class="page-shell contribute-page">
    <header class="contribute-header card">
      <span class="badge">众包更新</span>
      <h1>用户贡献页</h1>
      <p>发现知识库没有收录的问题？你可以提交新问答，经管理员审核后进入校园知识库。</p>
    </header>

    <section class="contribute-grid">
      <form class="card contribution-form" @submit.prevent="submitContribution">
        <h2>提交新问答</h2>

        <label>
          问题分类
          <select v-model="form.category" class="form-control">
            <option v-for="category in categories.filter(c => c !== '全部')" :key="category">{{ category }}</option>
          </select>
        </label>

        <label>
          问题标题
          <input v-model="form.question" class="form-control" placeholder="例如：校医院周末上班吗？" />
        </label>

        <label>
          建议答案
          <textarea v-model="form.answer" class="form-control" placeholder="请尽量写清楚办理地点、时间、流程或注意事项"></textarea>
        </label>

        <label>
          信息来源
          <input v-model="form.source" class="form-control" placeholder="例如：学生手册 / 官网通知 / 公众号" />
        </label>

        <button class="btn btn-primary" type="submit">提交审核</button>
      </form>

      <aside class="card contribution-list">
        <h2>我的提交</h2>
        <div v-for="item in submissions" :key="item.id" class="submission-item">
          <div>
            <strong>{{ item.question }}</strong>
            <p>{{ item.category }}</p>
          </div>
          <span class="badge">{{ item.status }}</span>
        </div>
      </aside>
    </section>
  </main>
</template>

<style scoped>
.contribute-page {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.contribute-header {
  padding: 34px;
  background: linear-gradient(135deg, #ffffff, #eef2ff);
}

.contribute-header h1 {
  margin: 14px 0 8px;
  font-size: 34px;
  font-weight: 900;
}

.contribute-header p {
  margin: 0;
  color: var(--muted);
  line-height: 1.8;
}

.contribute-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 26px;
}

.contribution-form,
.contribution-list {
  padding: 28px;
}

.contribution-form h2,
.contribution-list h2 {
  margin: 0 0 22px;
}

.contribution-form {
  display: grid;
  gap: 18px;
}

.contribution-form label {
  display: grid;
  gap: 8px;
  color: #475569;
  font-weight: 800;
}

.submission-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 18px 0;
  border-bottom: 1px solid var(--line);
}

.submission-item strong {
  font-weight: 900;
}

.submission-item p {
  margin: 6px 0 0;
  color: var(--muted);
}

@media (max-width: 980px) {
  .contribute-grid {
    grid-template-columns: 1fr;
  }
}
</style>
