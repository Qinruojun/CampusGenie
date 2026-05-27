<template>
  <div class="page">
    <header class="topbar">
      <div class="brand">
        <div class="logo">🎓</div>
        <span>CampusGenie</span>
      </div>

      <nav class="nav">
        <a class="active" href="#">智能问答</a>
        <a href="#">知识库</a>
        <a href="#">校园资讯</a>
        <a href="#">关于我们</a>
      </nav>

      <div class="actions">
        <button class="btn ghost">登录</button>
        <button class="btn primary">注册</button>
      </div>
    </header>

    <main class="layout">
      <section class="main-panel">
        <section class="hero-card">
          <h1>校园生活百宝箱</h1>
          <p>你的智能校园助手 —— 查校历、看课表、找食堂、问政策，随时随地，有问必答！</p>

          <div class="quick-list">
            <button v-for="item in quickActions" :key="item.label" class="quick-btn">
              <span>{{ item.icon }}</span>
              {{ item.label }}
            </button>
          </div>
        </section>

        <section class="chat-card">
          

          <div class="chat-body">
            <div v-for="message in messages" :key="message.id" :class="['message-row', message.role]">
              <div v-if="message.role === 'assistant'" class="avatar bot">
                <svg t="1779428064128" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="13756" width="200" height="200"><path d="M512 781.672m-178.592 0a178.592 178.592 0 1 0 357.184 0 178.592 178.592 0 1 0-357.184 0Z" fill="#FFFFFF" p-id="13757"></path><path d="M512 623.08c42.362 0 82.188 16.496 112.142 46.451 29.954 29.954 46.45 69.78 46.45 112.142s-16.496 82.188-46.45 112.142c-29.954 29.954-69.78 46.451-112.142 46.451s-82.187-16.497-112.142-46.451c-29.954-29.954-46.45-69.78-46.45-112.142s16.496-82.188 46.45-112.142c29.955-29.955 69.78-46.451 112.142-46.451m0-20c-98.634 0-178.592 79.958-178.592 178.592S413.366 960.264 512 960.264s178.592-79.958 178.592-178.592S610.634 603.08 512 603.08z" fill="" p-id="13758"></path><path d="M868.292 420.028c0-196.774-159.517-356.292-356.292-356.292S155.708 223.253 155.708 420.028c0 131.794 71.561 246.87 177.944 308.503v54.359h356.88v-54.467c106.281-61.659 177.76-176.678 177.76-308.395z" fill="#FFFFFF" p-id="13759"></path><path d="M512 83.736c45.408 0 89.447 8.889 130.894 26.419 40.043 16.937 76.009 41.187 106.9 72.078 30.891 30.891 55.142 66.857 72.078 106.9 17.531 41.447 26.419 85.486 26.419 130.894 0 60.014-15.997 118.911-46.26 170.324-29.394 49.935-71.42 91.697-121.536 120.772l-9.964 5.78v45.986h-316.88v-45.886l-9.974-5.778c-50.163-29.062-92.232-70.83-121.659-120.787-30.297-51.435-46.311-110.362-46.311-170.411 0-45.408 8.889-89.447 26.419-130.894 16.937-40.043 41.187-76.009 72.078-106.9 30.891-30.891 66.857-55.142 106.9-72.078C422.553 92.625 466.592 83.736 512 83.736m0-20c-196.774 0-356.291 159.517-356.291 356.292 0 131.794 71.561 246.87 177.944 308.504v54.359h356.88v-54.467c106.281-61.66 177.759-176.678 177.759-308.396 0-196.775-159.518-356.292-356.292-356.292z" fill="" p-id="13760"></path><path d="M512 421.193m-301.542 0a301.542 301.542 0 1 0 603.084 0 301.542 301.542 0 1 0-603.084 0Z" fill="#F96765" p-id="13761"></path><path d="M454.971 389.248a43.334 21.667 0 1 0 86.668 0 43.334 21.667 0 1 0-86.668 0Z" fill="#FFFFFF" p-id="13762"></path><path d="M498.306 417.915c-28.695 0-50.335-12.324-50.335-28.667 0-16.343 21.64-28.667 50.335-28.667s50.334 12.324 50.334 28.667c0 16.343-21.639 28.667-50.334 28.667z m0-43.335c-22.181 0-36.335 8.687-36.335 14.667s14.154 14.667 36.335 14.667c22.18 0 36.334-8.687 36.334-14.667s-14.155-14.667-36.334-14.667z" fill="" p-id="13763"></path><path d="M500.889 383.831m-8 0a8 8 0 1 0 16 0 8 8 0 1 0-16 0Z" fill="" p-id="13764"></path><path d="M659.368 389.248a43.334 21.667 0 1 0 86.668 0 43.334 21.667 0 1 0-86.668 0Z" fill="#FFFFFF" p-id="13765"></path><path d="M702.702 417.915c-28.695 0-50.335-12.324-50.335-28.667 0-16.343 21.64-28.667 50.335-28.667s50.334 12.324 50.334 28.667c0 16.343-21.639 28.667-50.334 28.667z m0-43.335c-22.181 0-36.335 8.687-36.335 14.667s14.154 14.667 36.335 14.667c22.18 0 36.334-8.687 36.334-14.667s-14.154-14.667-36.334-14.667z" fill="" p-id="13766"></path><path d="M702.285 383.831m-8 0a8 8 0 1 0 16 0 8 8 0 1 0-16 0Z" fill="" p-id="13767"></path><path d="M319.27 650.009a9.964 9.964 0 0 1-6.596-2.487c-65.345-57.403-102.821-140.169-102.821-227.078 0-80.707 31.429-156.583 88.497-213.65 57.068-57.068 132.944-88.497 213.65-88.497 5.522 0 10 4.477 10 10s-4.478 10-10 10c-75.364 0-146.218 29.349-199.509 82.639-53.29 53.291-82.639 124.144-82.639 199.508 0 81.156 34.998 158.446 96.021 212.052 4.149 3.645 4.559 9.963 0.913 14.112a9.973 9.973 0 0 1-7.516 3.401z" fill="" p-id="13768"></path><path d="M576.112 135.353a289.923 289.923 0 0 1 32.657 9.351" fill="#FCEE21" p-id="13769"></path><path d="M608.77 154.707a10 10 0 0 1-3.312-0.567 281.039 281.039 0 0 0-31.53-9.029c-5.39-1.207-8.78-6.554-7.574-11.943 1.207-5.389 6.551-8.783 11.944-7.573a301.274 301.274 0 0 1 33.783 9.673c5.211 1.829 7.953 7.536 6.124 12.748-1.444 4.115-5.308 6.691-9.435 6.691z" fill="" p-id="13770"></path><path d="M380.376 804.178c10.7 63.024 65.553 111.016 131.624 111.016s120.925-47.992 131.624-111.016H380.376z" fill="#00DFD4" p-id="13771"></path><path d="M512 915.682c-64.722 0-120.155-46.136-131.81-109.701-0.996-5.432 2.601-10.644 8.032-11.639 5.43-0.998 10.644 2.6 11.64 8.033 9.913 54.066 57.073 93.308 112.138 93.308 5.522 0 10 4.477 10 10s-4.478 9.999-10 9.999z" fill="" p-id="13772"></path><path d="M386.225 680.374h251.55v58h-251.55z" fill="#F96765" p-id="13773"></path></svg>
              </div>

              <div class="bubble">
                <template v-if="message.role === 'assistant'">
                  <p>今天小灯可以帮助你什么？</p>
                </template>

                <template v-else>
                  {{ message.content }}
                </template>
              </div>

              <div v-if="message.role === 'user'" class="avatar user">👤</div>
            </div>
          </div>

          <form class="chat-input" @submit.prevent="sendMessage">
            <input v-model.trim="inputText" placeholder="输入你的问题，按回车发送..." />
            <button type="submit">➤</button>
          </form>
        </section>
      </section>

      <aside class="sidebar">
        <section class="side-card">
          <h3>🔥 热门问题</h3>
          <div v-for="(item, index) in hotQuestions" :key="item.title" class="hot-item" @click="askHot(item.title)">
            <span class="rank">{{ index + 1 }}</span>
            <div>
              <p>{{ item.title }}</p>
              <small>{{ item.count }} 次提问</small>
            </div>
          </div>
        </section>

        <section class="side-card">
          <h3>📁 分类浏览</h3>
          <div class="category-grid">
            <button v-for="category in categories" :key="category" :class="['category', { selected: selectedCategory === category }]" @click="selectedCategory = category">
              {{ category }}
            </button>
          </div>
        </section>

        <section class="side-card data-card">
          <h3>📊 平台数据</h3>
          <div class="stats-grid">
            <div>
              <strong>12,680</strong>
              <span>知识条目</span>
            </div>
            <div>
              <strong>5,421</strong>
              <span>今日提问</span>
            </div>
          </div>
        </section>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'//导入，后面可以创建响应式变量

const quickActions = [
  { icon: '🗓️', label: '校历查询' },
  { icon: '🍚', label: '食堂菜单' },
  { icon: '📚', label: '图书馆' },
  { icon: '📢', label: '通知公告' },
  { icon: '🏃', label: '校园活动' },
  { icon: '💡', label: 'AI 问答' }
]

const hotQuestions = [
  { title: '期末考试成绩什么时候出', count: '2.3k' },
  { title: '暑假放假时间安排', count: '1.8k' },
  { title: '图书馆借书流程', count: '1.5k' },
  { title: '一食堂今日菜单', count: '1.2k' },
  { title: '校园卡挂失补办', count: '980' }
]

const categories = ['全部', '食堂', '图书馆', '教务', '宿舍', '活动', '社团', '就业']
const selectedCategory = ref('全部')

const inputText = ref('')
const messages = ref([
  { id: 1, role: 'user', content: '1' },
  { id: 2, role: 'assistant', content: '' }
])

function sendMessage() {
  if (!inputText.value) return

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: inputText.value
  })

  setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: ''
    })
  }, 400)

  inputText.value = ''
}

function askHot(question) {
  inputText.value = question
  sendMessage()
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.page {
  min-height: 100vh;
  background: #f4f6fb;
  color: #1f2937;
  font-family: Inter, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  height: 74px;
  display: flex;
  align-items: center;
  justify-content: space-between;/*这三行决定了垂直居中分散横向排列*/
  padding: 0 56px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid #e8ebf3;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 25px;
  font-weight: 800;
  color: #5865f2;
}

.logo {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: linear-gradient(135deg, #5865f2, #7c3aed);
  box-shadow: 0 12px 25px rgba(88, 101, 242, 0.25);
}

.nav {
  display: flex;
  gap: 28px;
}

.nav a {
  text-decoration: none;
  color: #4b5563;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 600;
}

.nav a.active,
.nav a:hover {
  color: #5865f2;
  background: #f0f2ff;
}

.actions {
  display: flex;
  gap: 14px;
}

.btn {
  min-width: 84px;
  height: 44px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  border: 1px solid #5865f2;
}

.btn.ghost {
  background: white;
  color: #5865f2;
}

.btn.primary {
  background: #5865f2;
  color: white;
  box-shadow: 0 12px 22px rgba(88, 101, 242, 0.28);
}

.layout {
  max-width: 1440px;
  margin: 0 auto;
  padding: 36px 38px 64px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: 28px;
}

.hero-card {
  border-radius: 20px;
  padding: 34px 46px;
  background: linear-gradient(135deg, #536dfe, #7c3aed);
  color: white;
  box-shadow: 0 18px 40px rgba(83, 109, 254, 0.28);
}

.hero-card h1 {
  margin: 0 0 12px;
  font-size: 34px;
}

.hero-card p {
  margin: 0 0 26px;
  font-size: 18px;
  opacity: 0.95;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-btn {
  border: 0;
  border-radius: 999px;
  padding: 12px 18px;
  color: white;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(12px);
  font-weight: 700;
  cursor: pointer;
}

.quick-btn:hover {
  background: rgba(255, 255, 255, 0.26);
  transform: translateY(-1px);
}

.chat-card,
.side-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 12px 32px rgba(31, 41, 55, 0.08);
  border: 1px solid #eef1f7;
}

.chat-card {
  margin-top: 28px;
  min-height: 620px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tips {
  width: 310px;
  margin: 0 0 0 86px;
  padding: 18px 22px;
  background: #f1f3f8;
  color: #374151;
  border-radius: 0 0 14px 14px;
  line-height: 1.75;
}

.tips p {
  margin: 0;
}

.chat-body {
  flex: 1;
  padding: 22px 32px 24px;
  overflow-y: auto;
}

.message-row {
  display: flex;
  gap: 14px;
  margin: 26px 0;
  align-items: flex-start;
}

.message-row.user {
  justify-content: flex-end;
}

.avatar {
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #eef2ff;
}

.bubble {
  max-width: 430px;
  padding: 18px 22px;
  border-radius: 14px;
  line-height: 1.8;
  background: #f0f2f7;
}

.bubble p {
  margin: 0;
}

.message-row.user .bubble {
  width: 54px;
  height: 54px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: #536dfe;
  color: white;
  font-size: 20px;
}

.chat-input {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 28px 26px;
  border-top: 1px solid #edf0f6;
}

.chat-input input {
  flex: 1;
  height: 56px;
  border: 1.5px solid #64748b;
  border-radius: 999px;
  padding: 0 24px;
  outline: none;
  font-size: 16px;
}

.chat-input input:focus {
  border-color: #536dfe;
  box-shadow: 0 0 0 4px rgba(83, 109, 254, 0.12);
}

.chat-input button {
  width: 60px;
  height: 60px;
  border: 0;
  border-radius: 50%;
  background: #536dfe;
  color: white;
  font-size: 22px;
  cursor: pointer;
  box-shadow: 0 14px 24px rgba(83, 109, 254, 0.28);
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.side-card {
  padding: 26px;
}

.side-card h3 {
  margin: 0 0 22px;
  font-size: 22px;
}

.hot-item {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 14px 0;
  cursor: pointer;
}

.hot-item + .hot-item {
  border-top: 1px solid #f0f2f6;
}

.rank {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: #fff7ed;
  color: #f59e0b;
  font-weight: 800;
}

.hot-item:nth-child(n + 5) .rank {
  background: #f3f4f6;
  color: #6b7280;
}

.hot-item p {
  margin: 0 0 4px;
  font-weight: 600;
}

.hot-item small {
  color: #8a93a3;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category {
  border: 1px solid #edf0f6;
  background: white;
  border-radius: 999px;
  padding: 10px 18px;
  cursor: pointer;
  color: #4b5563;
}

.category.selected {
  background: #536dfe;
  color: white;
  border-color: #536dfe;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.stats-grid div {
  background: #f8f9fc;
  border-radius: 16px;
  padding: 24px 18px;
  text-align: center;
}

.stats-grid strong {
  display: block;
  color: #536dfe;
  font-size: 30px;
  margin-bottom: 8px;
}

.stats-grid span {
  color: #7b8495;
}

@media (max-width: 1080px) {
  .topbar {
    padding: 0 22px;
  }

  .nav {
    display: none;
  }

  .layout {
    grid-template-columns: 1fr;
    padding: 24px 18px 48px;
  }

  .sidebar {
    order: -1;
  }
}

@media (max-width: 640px) {
  .brand span {
    font-size: 20px;
  }

  .actions {
    display: none;
  }

  .hero-card {
    padding: 26px 22px;
  }

  .tips {
    width: auto;
    margin: 0 18px;
    border-radius: 0 0 14px 14px;
  }

  .bubble {
    max-width: 82vw;
  }
}
</style>

