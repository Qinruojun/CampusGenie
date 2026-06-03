<script setup>
import { ref } from 'vue'

const submitted = ref(false)
const registerForm = ref({
  question:'',
  answer:'',
  categoryId:'',
  supplement:'',
  contact:''
})
function submit() {
  submitted.value = true
}
</script>

<template>
  <main class="page contribute-page">
    <p class="eyebrow">用户贡献</p>
    <h1 class="page-title">补充一条校园知识</h1>
    <p class="page-desc">你提交的内容会进入审核队列，通过后加入知识库。</p>

    <div class="content-grid">
      <form class="card panel form" @submit.prevent="submit">
        <label>
          问题标题
          <input class="input" v-model =" question" placeholder="例如：体育馆周末开放吗？" required />
        </label>
        <label>
          答案
          <textarea class="textarea" v-model="answer"  placeholder="请输入答案" ></textarea>
          <label>
            补充
            <textarea
              class="textarea"
              v-model="supplement"
              placeholder="请输入补充内容"
            ></textarea>
          </label>
        </label>
        <label>
          分类
          <select class="select">
            <option>生活服务</option>
            <option>学习资源</option>
            <option>住宿管理</option>
            <option>教务相关</option>
          </select>
        </label>
        <button class="primary-btn"  type="submit" >提交问题</button>
      </form>

      <aside class="card panel status">
        <div class="status-mark">✓</div>
        <h2>{{ submitted ? '已提交' : '等待提交' }}</h2>
        <p class="muted">{{ submitted ? '感谢你的贡献，我们会尽快审核。' : '填写左侧表单后，这里会显示提交状态。' }}</p>
      </aside>
    </div>
  </main>
</template>

<style scoped>
.contribute-page {
  max-width: 900px;
}

.contribute-page > .eyebrow,
.contribute-page > .page-title,
.contribute-page > .page-desc {
  text-align: center;
}

.content-grid {
  margin-top: 44px;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 20px;
}

.form {
  display: grid;
  gap: 18px;
}

label {
  display: grid;
  gap: 8px;
  color: #53564f;
  font-size: 14px;
  font-weight: 700;
}

.status {
  display: grid;
  place-items: center;
  text-align: center;
  align-content: center;
}

.status-mark {
  width: 72px;
  height: 72px;
  display: grid;
  place-items: center;
  margin-bottom: 18px;
  border-radius: 50%;
  color: var(--green);
  border: 1px solid rgba(35, 157, 83, 0.3);
  font-size: 30px;
}

.status h2 {
  margin: 0 0 8px;
}

@media (max-width: 720px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
