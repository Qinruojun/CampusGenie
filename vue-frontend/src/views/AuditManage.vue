<script setup>
import { ref } from 'vue'
import { auditItems } from '../data/mockData'

const items = ref(auditItems.map(item => ({ ...item })))

function approve(id) {
  const item = items.value.find(item => item.id === id)
  if (item) item.status = '已通过'
}

function reject(id) {
  const item = items.value.find(item => item.id === id)
  if (item) item.status = '已驳回'
}
</script>

<template>
  <main class="page-shell audit-page">
    <div>
      <h1 class="page-title">审核管理页</h1>
      <p class="page-subtitle">审核用户提交的新问答，保证知识库内容准确、可追溯。</p>
    </div>

    <section class="audit-summary">
      <div class="card summary-card"><strong>{{ items.filter(i => i.status === '待审核').length }}</strong><span>待审核</span></div>
      <div class="card summary-card"><strong>{{ items.filter(i => i.status === '已通过').length }}</strong><span>已通过</span></div>
      <div class="card summary-card"><strong>{{ items.filter(i => i.status === '已驳回').length }}</strong><span>已驳回</span></div>
    </section>

    <section class="audit-list">
      <article v-for="item in items" :key="item.id" class="card audit-card">
        <div class="audit-title">
          <div>
            <span class="badge">{{ item.category }}</span>
            <h2>{{ item.question }}</h2>
          </div>
          <span class="status" :class="item.status">{{ item.status }}</span>
        </div>

        <p class="answer">{{ item.answer }}</p>

        <div class="meta">
          <span>贡献者：{{ item.contributor }}</span>
          <span>提交时间：{{ item.createdAt }}</span>
        </div>

        <div class="actions" v-if="item.status === '待审核'">
          <button class="btn btn-primary" @click="approve(item.id)">通过并入库</button>
          <button class="btn btn-ghost" @click="reject(item.id)">驳回</button>
        </div>
      </article>
    </section>
  </main>
</template>

<style scoped>
.audit-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.audit-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.summary-card {
  padding: 24px;
}

.summary-card strong {
  display: block;
  color: var(--primary);
  font-size: 32px;
  font-weight: 900;
}

.summary-card span {
  color: var(--muted);
}

.audit-list {
  display: grid;
  gap: 18px;
}

.audit-card {
  padding: 26px;
}

.audit-title,
.meta,
.actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.audit-title h2 {
  margin: 12px 0 0;
  font-size: 22px;
  font-weight: 900;
}

.status {
  padding: 8px 12px;
  border-radius: 999px;
  font-weight: 900;
  background: #fff7ed;
  color: var(--warning);
}

.status.已通过 {
  background: #dcfce7;
  color: var(--success);
}

.status.已驳回 {
  background: #fee2e2;
  color: var(--danger);
}

.answer {
  margin: 18px 0;
  padding: 18px;
  border-radius: 16px;
  background: #fafbff;
  line-height: 1.8;
}

.meta {
  justify-content: flex-start;
  color: var(--muted);
}

.actions {
  justify-content: flex-end;
  margin-top: 18px;
}

@media (max-width: 900px) {
  .audit-summary {
    grid-template-columns: 1fr;
  }
  .audit-title,
  .meta,
  .actions {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
