<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const account = ref('admin')
const password = ref('123456')

function login() {
  router.push('/admin/knowledge')
}
</script>

<template>
  <main class="login-page">
    <section class="login-left">
      <RouterLink to="/" class="brand">🎓 CampusGenie</RouterLink>
      <h1>校园生活知识库后台</h1>
      <p>用于知识条目维护、用户贡献审核和热点问题运营。</p>
    </section>

    <form class="card login-card" @submit.prevent="login">
      <h2>管理员登录</h2>
      <p>演示账号已预填，可直接点击登录。</p>

      <label>
        账号
        <input v-model="account" class="form-control" />
      </label>

      <label>
        密码
        <input v-model="password" class="form-control" type="password" />
      </label>

      <button class="btn btn-primary" type="submit">登录后台</button>
      <RouterLink to="/" class="back-link">返回用户首页</RouterLink>
    </form>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 60px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  align-items: center;
  gap: 50px;
  background:
    radial-gradient(circle at 10% 20%, rgba(88, 101, 242, 0.26), transparent 30%),
    linear-gradient(135deg, #111827, #312e81);
  color: white;
}

.brand {
  display: inline-flex;
  margin-bottom: 50px;
  font-size: 26px;
  font-weight: 900;
}

.login-left h1 {
  max-width: 680px;
  margin: 0 0 18px;
  font-size: 54px;
  font-weight: 900;
}

.login-left p {
  max-width: 520px;
  margin: 0;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.74);
}

.login-card {
  padding: 34px;
  color: var(--text);
  display: grid;
  gap: 18px;
}

.login-card h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 900;
}

.login-card p {
  margin: 0;
  color: var(--muted);
}

.login-card label {
  display: grid;
  gap: 8px;
  font-weight: 800;
}

.back-link {
  text-align: center;
  color: var(--primary);
  font-weight: 800;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
    padding: 28px;
  }
  .login-left h1 {
    font-size: 38px;
  }
}
</style>
