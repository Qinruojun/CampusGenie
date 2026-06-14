<script setup>
import {onMounted, ref} from 'vue'
import {contribute } from '@/api/user/contribution.js'
import { useCategoryOptions } from '@/composables/useCategoryOptions'
import CategorySelect from '@/components/CategorySelect.vue'
import {SUCCESS } from '@/constants/code.js'
const {
  categoryOptions,
  categoryLoading,
  categoryError,
  loadCategoryList
} = useCategoryOptions()
const result =ref(false)
const submitted = ref(false)
const submitting = ref(false)

function createEmptyContributionForm() {
  return {
    question: '',
    answer: '',
    categoryId: '',
    supplement: '',
    contact: ''
  }
}

const contributionForm = ref(createEmptyContributionForm())
const  submit=async()=>{
  if (submitting.value) return

  submitting.value = true
  try {
    const res = await contribute(contributionForm.value)
    if(res.code === SUCCESS){
      result.value = true
      submitted.value = true
      contributionForm.value = createEmptyContributionForm()
    }
    else{
     submitted.value = false
     result.value = false
     alert(res.msg || '服务器异常，提交失败！')
    }
  }catch(error){
    console.error(error)
    submitted.value = false
    result.value = false
    alert('服务器异常，提交失败！')
  } finally {
    submitting.value = false
  }


}
onMounted(()=>{
  loadCategoryList()

})
</script>

<template>
  <main class="page contribute-page">
    <RouterLink class="back-link" to="/user/home">← 返回首页</RouterLink>

    <p class="eyebrow">用户贡献</p>
    <h1 class="page-title">补充一条校园知识</h1>
    <p class="page-desc">你提交的内容会进入审核队列，通过后加入知识库。</p>

    <div class="content-grid">
      <form class="card panel form" @submit.prevent="submit">
        <label>
          问题标题
          <input class="input" v-model =" contributionForm.question" placeholder="例如：体育馆周末开放吗？" required />
        </label>
        <label>
          答案
          <textarea v-model="contributionForm.answer" class="textarea" placeholder="请输入答案" ></textarea>
          <label>
            补充
            <textarea
              v-model="contributionForm.supplement"
              class="textarea"
              placeholder="请输入补充内容"
            ></textarea>
          </label>
        </label>
        <label>
          分类
          <CategorySelect
              v-model="contributionForm.categoryId"
          :options="categoryOptions"
          :loading="categoryLoading"
          :error-message="categoryError"
          placeholder="全部分类"
          />
        </label>
        <button
          class="primary-btn"
          type="submit"
          :disabled="submitting"
        >
          {{ submitting ? '提交中...' : '提交问题' }}
        </button>
      </form>

      <aside class="card panel status">
        <div class="status-mark">✓</div>
        <h2>{{ submitted ? '已提交' : '等待提交' }}</h2>
        <p class="muted">{{  result? '感谢你的贡献，我们会尽快审核。' : '填写左侧表单后，这里会显示提交状态。' }}</p>
      </aside>
    </div>
  </main>
</template>

<style scoped>
.contribute-page {
  max-width: 900px;
}

.back-link {
  display: inline-block;
  margin-bottom: 28px;
  color: var(--muted);
  font-weight: 700;
}

.back-link:hover {
  color: var(--green);
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
.page {
 width: min(980px, calc(100% - 48px));
 margin: 0 auto;
 padding: 70px 0 96px;
}
@media (max-width: 720px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
