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

