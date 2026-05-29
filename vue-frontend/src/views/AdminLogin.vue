<template>
  <main class="center-page login-page">
    <section class="login-card card panel">
      <RouterLink class="login-brand" to="/">CampusGenie</RouterLink>
      <h1>管理员登录</h1>

      <form class="login-form">
        <input class="input" placeholder="管理员账号" />
        <input class="input" type="password" placeholder="密码" />
        <RouterLink class="primary-btn login-link" to="/admin/knowledge">登录</RouterLink>
      </form>

      <RouterLink class="forgot" to="/">返回用户首页</RouterLink>
    </section>
  </main>
</template>

<style scoped>
.login-card {
  width: min(380px, 100%);
  text-align: center;
}

.login-brand {
  display: inline-block;
  margin-bottom: 8px;
  color: var(--green);
  font-family: Georgia, "Times New Roman", serif;
  font-size: 22px;
  font-weight: 700;
}

h1 {
  margin: 0 0 26px;
  font-size: 20px;
}

.login-form {
  display: grid;
  gap: 12px;
}

.login-link {
  display: grid;
  place-items: center;
}

.forgot {
  display: inline-block;
  margin-top: 22px;
  color: var(--muted);
  font-size: 13px;
}
</style>
