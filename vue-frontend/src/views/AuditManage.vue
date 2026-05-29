<script setup>
import { auditItems } from '../data/mockData'
</script>

<template>
  <main class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="brand" to="/">CampusGenie</RouterLink>
      <nav class="admin-menu">
        <RouterLink to="/admin/knowledge">知识库管理</RouterLink>
        <RouterLink to="/admin/audit">审核管理</RouterLink>
        <RouterLink to="/">返回首页</RouterLink>
      </nav>
    </aside>

    <section class="admin-main audit-main">
      <div class="admin-titlebar">
        <div>
          <p class="eyebrow">Admin</p>
          <h1>待审核内容</h1>
        </div>
        <span class="muted">{{ auditItems.length }} 条</span>
      </div>

      <div class="audit-list">
        <article v-for="item in auditItems" :key="item.id" class="card panel audit-card">
          <div>
            <h2>{{ item.title }}</h2>
            <p class="muted">提交者：{{ item.user }} · {{ item.date }}</p>
            <p>{{ item.question }}</p>
          </div>
          <div class="audit-actions">
            <button class="primary-btn">通过</button>
            <button class="ghost-btn">驳回</button>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.audit-main {
  max-width: 920px;
}

.audit-list {
  display: grid;
  gap: 14px;
}

.audit-card {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 22px;
  align-items: center;
}

.audit-card h2 {
  margin: 0 0 4px;
  font-size: 19px;
}

.audit-card p {
  margin: 6px 0 0;
}

.audit-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 760px) {
  .audit-card {
    grid-template-columns: 1fr;
  }
}
</style>
