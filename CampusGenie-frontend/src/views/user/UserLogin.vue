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
         <button @click="Login" type="button" class="primary-btn">登录</button>
         <p @click="isLogin =false">没有账号?去注册</p>
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
            <button @click ="Register" type ="button" class="primary-btn">注册</button>
            <p @click="isLogin = true">已有账号?去登陆</p>
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

async function Register(){
  console.log("用户进行注册")
  if (registerForm.value.password!==confirpwd.value){
    alert('两次密码不一致')
    return
  }
  //调用定义在api的注册接口
  const register_res=await register(registerForm.value)
  print(register_res)
  if(register_res.code==SUCCESS){
  alert("注册成功！")//alert是浏览器自带弹窗
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