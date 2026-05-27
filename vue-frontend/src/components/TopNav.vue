<template>
  <header class="top-nav">
    <RouterLink to="/" class="brand">
      <span class="logo-box">🎓</span>
      <span>CampusGenie</span>
    </RouterLink>

    <nav class="nav-links">
      <RouterLink to="/">智能问答</RouterLink>
      <RouterLink to="/hot">热点问题</RouterLink>
      <RouterLink to="/contribute">用户贡献</RouterLink>
      <RouterLink to="/admin/login">后台管理</RouterLink>
    </nav>

    <div class="nav-actions">
      <button class="btn btn-ghost">登录</button>
      <button class="btn btn-primary">注册</button>
    </div>
  </header>
</template>

<style scoped>
.top-nav {
  position: sticky;
  top: 0;
  z-index: 20;
  width: 100%;
  height: 74px;
  padding: 0 56px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--primary);
  font-size: 25px;
  font-weight: 900;
}

.logo-box {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--primary), var(--purple));
  box-shadow: 0 12px 25px rgba(88, 101, 242, 0.24);
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-links a {
  padding: 12px 16px;
  border-radius: 12px;
  color: #4b5563;
  font-weight: 700;
  white-space: nowrap;
}

.nav-links a.router-link-active {
  color: var(--primary);
  background: var(--primary-soft);
}

.nav-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 960px) {
  .top-nav {
    padding: 0 18px;
  }
  .nav-links {
    display: none;
  }
  .brand {
    font-size: 20px;
  }
}
</style>
