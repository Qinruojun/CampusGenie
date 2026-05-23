<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink to="/" class="admin-brand">
        <span>🎓</span>
        <strong>CampusGenie</strong>
      </RouterLink>

      <nav>
        <RouterLink to="/admin/knowledge">📚 知识库管理</RouterLink>
        <RouterLink to="/admin/audit">✅ 审核管理</RouterLink>
        <RouterLink to="/hot">🔥 热点问题</RouterLink>
        <RouterLink to="/">🏠 返回用户端</RouterLink>
      </nav>
    </aside>

    <section class="admin-main">
      <header class="admin-topbar">
        <div>
          <p>管理员后台</p>
          <strong>校园生活知识库运营中心</strong>
        </div>
        <RouterLink to="/admin/login" class="btn btn-ghost">退出登录</RouterLink>
      </header>
      <slot />
    </section>
  </div>
</template>

<style scoped>
.admin-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
  background: #f5f7fc;
}

.admin-sidebar {
  background: #111827;
  color: white;
  padding: 24px;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 34px;
  font-size: 20px;
}

.admin-brand span {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary), var(--purple));
}

.admin-sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.admin-sidebar nav a {
  padding: 13px 14px;
  border-radius: 12px;
  color: #d1d5db;
  font-weight: 700;
}

.admin-sidebar nav a.router-link-active {
  color: white;
  background: rgba(88, 101, 242, 0.8);
}

.admin-main {
  min-width: 0;
}

.admin-topbar {
  height: 76px;
  padding: 0 32px;
  background: white;
  border-bottom: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-topbar p {
  margin: 0 0 4px;
  color: var(--muted);
  font-size: 13px;
}

.admin-topbar strong {
  font-size: 18px;
}

@media (max-width: 900px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }
  .admin-sidebar {
    display: none;
  }
}
</style>
