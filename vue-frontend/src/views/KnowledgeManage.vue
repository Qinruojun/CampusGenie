<script setup>
import { computed, ref } from 'vue'
import { categories, knowledgeItems } from '../data/mockData'

const keyword = ref('')
const selectedCategory = ref('全部')
const showForm = ref(false)
const items = ref([...knowledgeItems])
const draft = ref({ question: '', answer: '', category: '教务', source: '' })

const filteredItems = computed(() => {
  return items.value.filter(item => {
    const matchKeyword = !keyword.value || item.question.includes(keyword.value) || item.answer.includes(keyword.value)
    const matchCategory = selectedCategory.value === '全部' || item.category === selectedCategory.value
    return matchKeyword && matchCategory
  })
})

function addItem() {
  if (!draft.value.question || !draft.value.answer) return
  items.value.unshift({
    id: Date.now(),
    ...draft.value,
    status: '已发布',
    updatedAt: new Date().toISOString().slice(0, 10)
  })
  draft.value = { question: '', answer: '', category: '教务', source: '' }
  showForm.value = false
}

function removeItem(id) {
  items.value = items.value.filter(item => item.id !== id)
}
</script>

<template>
  <main class="page-shell manage-page">
    <div class="manage-header">
      <div>
        <h1 class="page-title">知识库管理页</h1>
        <p class="page-subtitle">管理员可以维护 FAQ 条目、分类、来源和发布状态。</p>
      </div>
      <button class="btn btn-primary" @click="showForm = !showForm">+ 新增知识</button>
    </div>

    <section class="stat-row">
      <div class="card stat-card"><strong>{{ items.length }}</strong><span>知识总数</span></div>
      <div class="card stat-card"><strong>8</strong><span>知识分类</span></div>
      <div class="card stat-card"><strong>92%</strong><span>命中率</span></div>
    </section>

    <form v-if="showForm" class="card add-form" @submit.prevent="addItem">
      <input v-model="draft.question" class="form-control" placeholder="问题标题" />
      <select v-model="draft.category" class="form-control">
        <option v-for="category in categories.filter(c => c !== '全部')" :key="category">{{ category }}</option>
      </select>
      <input v-model="draft.source" class="form-control" placeholder="来源" />
      <textarea v-model="draft.answer" class="form-control" placeholder="答案内容"></textarea>
      <button class="btn btn-primary">保存条目</button>
    </form>

    <section class="card table-card">
      <div class="tools">
        <input v-model="keyword" class="form-control" placeholder="搜索问题或答案" />
        <select v-model="selectedCategory" class="form-control">
          <option v-for="category in categories" :key="category">{{ category }}</option>
        </select>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th>问题</th>
            <th>分类</th>
            <th>来源</th>
            <th>状态</th>
            <th>更新时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.id">
            <td><strong>{{ item.question }}</strong><p>{{ item.answer }}</p></td>
            <td>{{ item.category }}</td>
            <td>{{ item.source }}</td>
            <td><span class="badge">{{ item.status }}</span></td>
            <td>{{ item.updatedAt }}</td>
            <td><button class="text-danger" @click="removeItem(item.id)">删除</button></td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<style scoped>
.manage-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.manage-header,
.tools,
.stat-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
}

.stat-card {
  padding: 24px;
}

.stat-card strong {
  display: block;
  color: var(--primary);
  font-size: 32px;
  font-weight: 900;
}

.stat-card span {
  color: var(--muted);
}

.add-form {
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr 180px 220px;
  gap: 14px;
}

.add-form textarea,
.add-form button {
  grid-column: 1 / -1;
}

.table-card {
  padding: 24px;
  overflow-x: auto;
}

.tools {
  margin-bottom: 18px;
}

.tools input {
  max-width: 420px;
}

.tools select {
  max-width: 180px;
}

td p {
  margin: 7px 0 0;
  color: var(--muted);
  line-height: 1.6;
}

td strong {
  font-weight: 900;
}

.text-danger {
  color: var(--danger);
  background: transparent;
  font-weight: 800;
}

@media (max-width: 900px) {
  .stat-row,
  .add-form {
    grid-template-columns: 1fr;
  }
}
</style>
