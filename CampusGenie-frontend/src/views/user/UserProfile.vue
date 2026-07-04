<template>
  <div class="page">
    <main class="main">
      <section class="content">
        <div class="page-head">
          <div>
            <h1>个人设置</h1>
            <p>管理您的账户信息。</p>
          </div>
        </div>

        <div class="profile-card">
          <div class="profile-header">
            <div class="avatar">
              <span>👤</span>
            </div>
            <div class="profile-info">
              <h2>{{ userInfo.username || '' }}</h2>
              <p>用户</p>
            </div>
          </div>

          <div class="form-section">
            <h3>基本信息</h3>
            <div class="form-grid">
              <div class="form-item">
                <label>用户名</label>
                <input v-model="userInfo.username" placeholder="请输入用户名" />
              </div>
              <div class="form-item">
                <label>邮箱</label>
                <input v-model="userInfo.email" placeholder="请输入邮箱" />
              </div>
              <div class="form-item">
                <label>手机号</label>
                <input v-model="userInfo.phone" placeholder="请输入手机号" />
              </div>
              <div class="form-item">
                <label>创建时间</label>
                <input :value="formatCreatedTime(userInfo.createdTime)" readonly />
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3>修改密码</h3>
            <div class="form-grid">
              <div class="form-item">
                <label>旧密码</label>
                <input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码" />
              </div>
              <div class="form-item">
                <label>新密码</label>
                <input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
              </div>
              <div class="form-item">
                <label>确认新密码</label>
                <input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button class="btn primary" @click="handleSave">保存修改</button>
            <button class="btn ghost" @click="handleReset">重置</button>
          </div>
        </div>

        <RouterLink class="back-btn" to="/user/home">返回首页</RouterLink>
      </section>
    </main>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user/user.js'
import { SUCCESS } from '@/constants/code.js'

const userInfo = reactive({
  username: '',
  email: '',
  phone: '',
  createdTime: null
})

let originalUsername = ''

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function formatCreatedTime(dateValue) {
  if (!dateValue) return ''
  let date
  if (Array.isArray(dateValue)) {
    date = new Date(dateValue[0], dateValue[1] - 1, dateValue[2], dateValue[3], dateValue[4], dateValue[5])
  } else {
    date = new Date(String(dateValue).replace('T', ' '))
  }
  if (isNaN(date.getTime())) return ''
  return date.toLocaleString('zh-CN')
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === SUCCESS && res.data) {
      userInfo.username = res.data.username || ''
      userInfo.email = res.data.email || ''
      userInfo.phone = res.data.phone || ''
      userInfo.createdTime = res.data.createdTime || null
      originalUsername = userInfo.username
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const handleSave = async () => {
  const updateData = {}
  if (userInfo.username) updateData.username = userInfo.username
  if (userInfo.email) updateData.email = userInfo.email
  if (userInfo.phone) updateData.phone = userInfo.phone

  try {
    if (Object.keys(updateData).length > 0) {
      const res = await updateUserInfo(updateData)
      if (res.code === SUCCESS) {
        alert('基本信息更新成功')
        // 如果用户名变了，更新顶栏显示
        if (updateData.username) {
          localStorage.setItem('username', updateData.username)
        }
      } else if (res.msg) {
        alert(res.msg)
      }
    }

    if (passwordForm.newPassword) {
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        alert('两次输入的新密码不一致')
        return
      }
      if (!passwordForm.oldPassword) {
        alert('请输入旧密码')
        return
      }
      const res = await changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
        confirmPassword: passwordForm.confirmPassword
      })
      if (res.code === SUCCESS) {
        alert('密码修改成功')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      }
    }

    if (Object.keys(updateData).length === 0 && !passwordForm.newPassword) {
      alert('请输入要修改的内容')
    }
  } catch (error) {
    console.error('保存失败:', error)
    const msg = error.response?.data?.msg || '保存失败'
    alert(msg)
    userInfo.username = originalUsername
  }
}

const handleReset = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  color: #1f2937;
}

.main {
  flex: 1;
  min-width: 0;
}

.content {
  padding: 28px 36px;
}

.page-head {
  margin-bottom: 24px;
}

.page-head h1 {
  margin: 0 0 8px;
  font-size: 28px;
}

.page-head p {
  margin: 0;
  color: #6b7280;
}

.profile-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.profile-header {
  display: flex;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.avatar {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  border-radius: 50%;
  font-size: 40px;
  margin-right: 20px;
}

.profile-info h2 {
  margin: 0 0 4px;
  font-size: 20px;
  color: #111827;
}

.profile-info p {
  margin: 0;
  color: #6b7280;
}

.form-section {
  margin-bottom: 24px;
}

.form-section h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #374151;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
}

.form-item label {
  margin-bottom: 8px;
  font-size: 14px;
  color: #374151;
  font-weight: 700;
}

.form-item input {
  height: 44px;
  padding: 0 16px;
  border: 1px solid #dfe3e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.form-item input:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
}

.form-item input[readonly] {
  background: #f9fafb;
  color: #9ca3af;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.btn {
  height: 44px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  font-size: 14px;
}

.btn.primary {
  background: #16a34a;
  color: #fff;
  border: none;
}

.btn.primary:hover {
  background: #15803d;
}

.btn.ghost {
  background: #fff;
  color: #374151;
  border: 1px solid #dfe3e8;
}

.btn.ghost:hover {
  background: #f3f4f6;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 32px;
  margin-top: 24px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  border-color: #16a34a;
  color: #16a34a;
}

@media (max-width: 600px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    justify-content: center;
  }
}
</style>
