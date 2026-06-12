<template>
  <main class="center-page login-page">
    <section class="login-card card panel">
     <div class="login-box">
        <div v-if ="isLogin">
      <RouterLink class="login-brand" to="/">CampusGenie</RouterLink>
          <h1>用户登录</h1>
        <form class="login-form">
            <input class="input" v-model="loginForm.username" placeholder="用户账号" />
            <input class="input" type="password" v-model="loginForm.password" placeholder="密码" />
            <p v-if="loginError" class="error-tip">{{ loginError }}</p>
            <button @click="Login" type="button" class="primary-btn">登录</button>
            <p class="switch-tip">
              没有账号？
              <button type="button" class="switch-link" @click="isLogin = false">去注册</button>
            </p>
          </form>
        </div>
     
        <div v-else>
          <RouterLink class="login-brand" to="/">CampusGenie</RouterLink>
          <h1>用户注册</h1>
          <form class="login-form">
            <input class="input" v-model="registerForm.username" placeholder="用户账号">  
            <input class ="input" type="password" v-model="registerForm.password" placeholder="用户密码">
            <input class =  "input" type="password" v-model="confirpwd" placeholder="再次输入密码">  
            <input class =  "input" v-model="registerForm.email" placeholder="邮箱">
            <input class =  "input" v-model="registerForm.phone" placeholder="电话">
            <p v-if="registerError" class="error-tip">{{ registerError }}</p>
            <button @click ="Register" type ="button" class="primary-btn">注册</button>
            <p class="switch-tip">
              已有账号？
              <button type="button" class="switch-link" @click="isLogin = true">去登录</button>
            </p>
<!--@click表示监听点击事件，监听到就会执行Register函数或者isLogin=true赋值语句-->
          </form>
     </div>
      </div>
      <RouterLink class="forgot" to="/">返回首页</RouterLink>
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
const loginError = ref('')
const registerError = ref('')
const loginForm = ref({
  username:'',
  password:''
})
const registerForm = ref({
  username:'',
  password:'',
  email:'',
  phone:''
})
function getErrorMessage(error, fallback = '操作失败，请稍后重试') {
  return error?.response?.data?.msg || error?.message || fallback
}

//定义点击了登录按钮之后的函数
async function Login(){
  loginError.value = ''
  try {
    const login_res = await login(loginForm.value)
    if(login_res.code ==SUCCESS){
      localStorage.setItem(TOKEN_KEY,login_res.data.token)
      localStorage.setItem(USERNAME_KEY,login_res.data.username)
      localStorage.setItem(ROLE_KEY,login_res.data.role)
      router.push('/user/home')//跳转到提问页
      return
    }
    loginError.value = login_res.msg || '登录失败，请检查账号和密码'
  } catch (error) {
    loginError.value = getErrorMessage(error, '登录失败，请检查账号和密码')
  }
}

async function Register(){
  console.log("用户进行注册")
  registerError.value = ''
  if (registerForm.value.password!==confirpwd.value){
    registerError.value = '两次密码不一致'
    return
  }
  try {
    //调用定义在api的注册接口
    const register_res=await register(registerForm.value)
    if(register_res.code==SUCCESS){
      alert("注册成功！")//alert是浏览器自带弹窗
      router.push('/user/home')//跳转到提问页
      return
    }
    registerError.value = register_res.msg || '注册失败，请检查填写信息'
  } catch (error) {
    registerError.value = getErrorMessage(error, '注册失败，请检查填写信息')
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

.switch-tip {
  margin: 2px 0 0;
  color: var(--muted);
  font-size: 14px;
}

.switch-link {
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--green);
  font-weight: 700;
  cursor: pointer;
  transition: color 0.18s ease;
}

.switch-link:hover {
  color: var(--green-dark);
  text-decoration: underline;
}

.switch-link:focus-visible {
  outline: 2px solid rgba(35, 157, 83, 0.35);
  outline-offset: 3px;
  border-radius: 4px;
}

.forgot {
  display: inline-block;
  margin-top: 22px;
  color: var(--muted);
  font-size: 13px;
}
</style>
