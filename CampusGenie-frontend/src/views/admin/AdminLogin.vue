<template>
  <main class="center-page login-page">
    <section class="login-card card panel">
      <RouterLink class="login-brand" to="/">CampusGenie</RouterLink>
      <h1>管理员登录</h1>

      <form class="login-form">
        <input class="input" v-model="loginForm.username" placeholder="管理员账号" />
        <input class="input" type="password" v-model="loginForm.password" placeholder="密码" />
        <p v-if="loginError" class="error-tip">{{ loginError }}</p>
        <button @click="Login" type="button" class="primary-btn">登录</button>

      </form>

      <RouterLink class="forgot" to ="/">返回</RouterLink>
    </section>
  </main>
</template>
<script setup>
import { ref } from 'vue'
import { SUCCESS } from '@/constants/code'
import { login } from '@/api/admin/admin.js'

import { useRouter } from 'vue-router'
import {ROLE_KEY, TOKEN_KEY,USERNAME_KEY, ADMIN_ROLE} from '@/constants/storage'
const router = useRouter()
const loginError = ref('')
const loginForm = ref({
  username:'',
  password:''
})

function getErrorMessage(error, fallback = '登录失败，请检查账号和密码') {
  return error?.response?.data?.msg || error?.message || fallback
}

//定义点击了登录按钮之后的函数
async function Login(){
  loginError.value = ''
  try {
    const login_res = await login(loginForm.value)
    if(login_res.code ===SUCCESS){
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USERNAME_KEY)
      localStorage.removeItem(ROLE_KEY)
      localStorage.setItem(TOKEN_KEY,login_res.data.token)
      localStorage.setItem(USERNAME_KEY,login_res.data.username)
      localStorage.setItem(ROLE_KEY,login_res.data.role)
      console.log('登录成功，角色:', login_res.data.role, 'ADMIN_ROLE:', ADMIN_ROLE)
      router.push('/admin/home')
      return
    }
    loginError.value = login_res.msg || '登录失败，请检查账号和密码'
  } catch (error) {
    loginError.value = getErrorMessage(error)
  }
}

</script>
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

.error-tip {
  margin: 0;
  padding: 9px 12px;
  border-radius: 8px;
  background: #fef2f2;
  color: #b91c1c;
  font-size: 13px;
  text-align: left;
}

.forgot {
  display: inline-block;
  margin-top: 22px;
  color: var(--muted);
  font-size: 13px;
}
</style>
