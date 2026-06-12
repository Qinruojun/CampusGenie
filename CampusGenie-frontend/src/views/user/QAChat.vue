<template>
  <div class="chat-page">
    <aside class="chat-sidebar">
      <div class="eyebrow logo">CampusGenie</div>

      <RouterLink class="home-link" to="/user/home">
        ← 返回首页
      </RouterLink>

      <button
class="new-chat"
@click="startNewChat">
        ＋ 新对话
      </button>

      <div class="history-title">历史对话</div>
      <div
        v-if="conversations.length === 0"
        class="history-empty"
      >
        暂无历史对话
      </div>
      <button
        v-for="conversation in conversations"
        :key="conversation.id"
        class="history-item"
        :class="{ active: conversation.id === activeConversationId }"
        @click="selectConversation(conversation.id)"
      >
        {{ conversation.title }}
      </button>
    </aside>

    <main class="chat-main">
      <header class="chat-header">
        <div>
          <h2>AI 校园智能助手</h2>
          <p>基于校园知识库，为你快速解答问题</p>
        </div>
      </header>

      <section class="message-list">
        <div
            v-for="message in messages"
            :key="message.id"
            class="message"
            :class="message.role"
        >
          <div class="bubble">
            {{ message.content }}
          </div>
        </div>

        <div v-if="loading" class="message assistant">
          <div class="bubble">
            正在思考中...
          </div>
        </div>
      </section>

      <footer class="chat-input-bar">
        <input
            v-model="inputText"
            placeholder="继续追问，例如：周末也开放吗？"
            @keyup.enter="sendCurrentMessage"
        />

        <button @click="sendCurrentMessage">
          发送
        </button>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  createConversation,
  getConversationMessages,
  getConversations,
  sendConversationMessage
} from '@/api/user/qa.js'

const route = useRoute()

const inputText = ref('')//用户输入的问题
const loading = ref(false)
const conversations = ref([])
const activeConversationId = ref(null)

function createInitialMessage() {
  return {
    id: Date.now(),
    role: 'assistant',
    content: '你好，我是 CampusGenie 校园智能助手，有什么可以帮你？'
  }
}

const messages = ref([createInitialMessage()])

const loadConversations = async () => {
  try {
    const res = await getConversations()

    conversations.value = res.data || []
  } catch (error) {
    console.error(error)
    alert(error.response?.data?.msg || '历史对话加载失败')
  }
}

const startNewChat = async ()=>{
  messages.value = [createInitialMessage()]
  activeConversationId.value = null
}

const ensureConversation = async () => {
  if (activeConversationId.value) {
    return activeConversationId.value
  }

  const res = await createConversation()
  const conversation = res.data

  activeConversationId.value = conversation.id
  conversations.value = [
    conversation,
    ...conversations.value.filter(item => item.id !== conversation.id)
  ]

  return conversation.id
}

const selectConversation = async (id) => {
  if (loading.value) return

  try {
    activeConversationId.value = id
    const res = await getConversationMessages(id)
    const historyMessages = res.data || []

    messages.value = historyMessages.length > 0
      ? historyMessages.map(item => ({
          id: item.id,
          role: item.role,
          content: item.content
        }))
      : [createInitialMessage()]
  } catch (error) {
    console.error(error)
    alert(error.response?.data?.msg || '历史消息加载失败')
  }
}

const sendQuestion = async (questionText) => {
  const text = questionText.trim()

  if (!text || loading.value) return//如果test为空或者正在思考就返回

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: text
  })

  loading.value = true

  try {
    const conversationId = await ensureConversation()
    const res = await sendConversationMessage(conversationId, text)

    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: res.data.answer
    })
    await loadConversations()
  } catch (error) {
    console.error(error)

    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: '服务器异常，请稍后再试。'
    })
  } finally {
    loading.value = false
  }
}

const sendCurrentMessage = () => {
  const text = inputText.value.trim()

  if (!text) {
    alert('请输入问题')
    return
  }

  inputText.value = ''
  sendQuestion(text)
}

onMounted(() => {
  const questionFromHome = route.query.question

  loadConversations()

  if (questionFromHome) {
    sendQuestion(String(questionFromHome))
  }
})
</script>
<style scoped>
.chat-page {
  min-height: 100vh;
  display: flex;
  background: var(--background);
  /*color: #172418;*/
}

.chat-sidebar {
  width: 260px;
  padding: 24px;
  background: var(--background);
  border-right: 1px solid #e5e7eb;
  box-sizing: border-box;
}

.logo {
  color: #16a34a;
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 12px;
}

.home-link {
  display: inline-block;
  margin-bottom: 24px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 700;
}

.home-link:hover {
  color: #16a34a;
}

.new-chat {
  width: 100%;
  height: 44px;
  border: 0;
  border-radius: 12px;
  background: #16a34a;
  color: #fff;
  font-weight: 800;
  cursor: pointer;
}

.history-title {
  margin: 28px 0 12px;
  color: #6b7280;
  font-size: 13px;
  font-weight: 700;
}

.history-item {
  width: 100%;
  padding: 12px;
  margin-bottom: 8px;
  border: 0;
  border-radius: 10px;
  background: #f3f6f2;
  color: #374151;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
}

.history-item.active {
  color: #16a34a;
  background: #eaf8ef;
  font-weight: 800;
}

.history-empty {
  color: #9ca3af;
  font-size: 14px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  height: 76px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.chat-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
}

.chat-header p {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.message-list {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}

.message {
  display: flex;
  margin-bottom: 18px;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 680px;
  padding: 14px 18px;
  border-radius: 16px;
  line-height: 1.7;
  font-size: 15px;
}

.message.user .bubble {
  color: #fff;
  background: #16a34a;
  border-bottom-right-radius: 4px;
}

.message.assistant .bubble {
  color: #1f2937;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 4px;
}

.chat-input-bar {
  display: flex;
  gap: 12px;
  padding: 20px 32px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
}

.chat-input-bar input {
  flex: 1;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #dfe3e8;
  border-radius: 12px;
  outline: none;
  font-size: 15px;
}

.chat-input-bar input:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
}

.chat-input-bar button {
  width: 96px;
  height: 48px;
  border: 0;
  border-radius: 12px;
  background: #16a34a;
  color: #fff;
  font-weight: 800;
  cursor: pointer;
}
</style>