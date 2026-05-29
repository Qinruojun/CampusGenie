<script setup>
import { knowledgeItems } from '../data/mockData'
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

    <section class="admin-main">
      <div class="admin-titlebar">
        <div>
          <p class="eyebrow">Admin</p>
          <h1>知识库列表</h1>
        </div>
        <button class="primary-btn">+ 新增知识</button>
      </div>

      <div class="card panel">
        <table class="table">
          <thead>
            <tr>
              <th>标题</th>
              <th>分类</th>
              <th>更新时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in knowledgeItems" :key="item.id">
              <td>{{ item.title }}</td>
              <td>{{ item.category }}</td>
              <td>{{ item.updatedAt }}</td>
              <td><a href="#">编辑</a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>
