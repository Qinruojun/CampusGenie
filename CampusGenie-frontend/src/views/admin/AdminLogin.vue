<template>
  <main class="center-page login-page">
    <section class="login-card card panel">
      <RouterLink class="login-brand" to="/">CampusGenie</RouterLink>
      <h1>管理员登录</h1>

      <form class="login-form">
        <input class="input" v-model="loginForm.username" placeholder="管理员账号" />
        <input class="input" type="password" v-model="loginForm.password" placeholder="密码" />
        <button @click="Login" type="button" class="primary-btn">登录</button>

      </form>

      <RouterLink class="forgot" to="/">返回</RouterLink>
    </section>
  </main>
</template>
<script setup>
import { ref } from 'vue'
import { SUCCESS } from '@/constants/code'
import { login } from '@/api/user/user'
import { register } from '@/api/user/user'
import { useRouter } from 'vue-router'
import {ROLE_KEY, TOKEN_KEY,USERNAME_KEY} from '@/constants/storage'
const router = useRouter()
const isLogin =ref(true)//如果这个是true那么显示登陆界面
const confirpwd = ref(null)
const loginForm = ref({
  username:'',
  password:''
})
const registerForm = ref({
  username:'',
  password:'',
  email:''
})
//定义点击了登录按钮之后的函数
async function Login(){
  const login_res = await login(loginForm.value)
  if(login_res.code ==SUCCESS){
    localStorage.setItem(TOKEN_KEY,login_res.data.token)
    localStorage.setItem(USERNAME_KEY,login_res.data.username)
   localStorage.setItem(ROLE_KEY,login_res.data.role)
    router.push('/user/home')//跳转到提问页
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

.forgot {
  display: inline-block;
  margin-top: 22px;
  color: var(--muted);
  font-size: 13px;
}
</style>
