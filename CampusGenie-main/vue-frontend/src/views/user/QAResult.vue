<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { relatedQuestions } from '../../data/mockData'
import { askQuestion } from '../../api/user/qa' // 引入接口

const route = useRoute()
const question = computed(() => route.query.q || '图书馆的开放时间是多少？')


// 定义响应式状态
const answerData = ref(null) // 存放后端返回的AnswerVO
const loading = ref(false)   // 加载状态
const errorMsg = ref('')     // 错误提示

// 发起后端请求获取答案
const fetchAnswer = async () => {
  if (!question.value) return
  
  loading.value = true
  errorMsg.value = ''
  
  try {
    const res = await askQuestion({ question: question.value })
    // 根据 Result.java 封装的数据结构，成功时 code 通常为 1 或 200
    if (res.code === 1) { 
      answerData.value = res.data // 取出 AnswerVO 数据
    } else {
      errorMsg.value = res.msg || '获取答案失败，请稍后重试'
    }
  } catch (error) {
    console.error('问答请求失败:', error)
    errorMsg.value = '网络请求异常，请检查后端服务是否启动'
  } finally {
    loading.value = false
  }
}

// 页面加载时自动发起提问
onMounted(() => {
  fetchAnswer()
})

</script>

<template>
  <main class="page result-page">
    <RouterLink class="back-link" to="/">← 返回首页</RouterLink>

    <p class="eyebrow">问答结果</p>
    <h1>{{ question }}</h1>

    <section class="answer card panel">
      <div class="check">✓</div>
      <div>
        <p class="muted">
          来源:{{ answerData.source || '未知' }} 
        </p>
        <h2>{{answerData.answer}}</h2>
      </div>
    </section>

    <section class="card panel related">
      <h3>相关问题</h3>
      <ul class="simple-list">
        <li v-for="item in relatedQuestions" :key="item" class="simple-row">
          <span>{{ item }}</span>
          <span>›</span>
        </li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.result-page {
  max-width: 760px;
}

/* 加载和错误提示 */
.back-link { 
  display: inline-block; margin-bottom: 40px; color: var(--muted); 
}

h1 { 
  margin: 0 0 26px; 
  font-size: clamp(28px, 5vw, 48px); 
  letter-spacing: -0.04em; 
}

.back-link {
  display: inline-block;
  margin-bottom: 40px;
  color: var(--muted);
}

h1 {
  margin: 0 0 26px;
  font-size: clamp(28px, 5vw, 48px);
  letter-spacing: -0.04em;
}

.answer {
  display: grid;
  grid-template-columns: 42px 1fr;
  gap: 18px;
  margin-bottom: 18px;
}

.check {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: var(--green);
  border: 1px solid rgba(35, 157, 83, 0.35);
}

.answer h2 {
  margin: 4px 0 8px;
  font-size: 22px;
}

.related h3 {
  margin: 0 0 16px;
}
</style>
