<script setup>
import {ref, onMounted, reactive} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useKnowledgeEditStore } from '@/stores/knowledgeEditStore'
import CategorySelect from '@/components/CategorySelect.vue'
import {ENABLE } from '@/constants/status.js'
import { SUCCESS } from '@/constants/code.js'

import {edit, getKnowledgeById } from "@/api/admin/knowledge.js"
import {useCategoryOptions} from "@/composables/useCategoryOptions.js";

const knowledgeEditStore = useKnowledgeEditStore()

const {
  categoryOptions,
  categoryLoading,
  categoryError,
  loadCategoryList
} = useCategoryOptions()
const router = useRouter()
const route = useRoute()
const form = reactive({
  id: '',
  question: '',
  answer: '',
  categoryId: '',
  source: '',
  status: ENABLE,//默认修改后的知识条目应该是启用的
})
function fillForm(data){
  form.id = data.id || ''
  form.question = data.question || ''
  form.answer = data.answer || ''
  form.categoryId = data.categoryId || ''
  form.source = data.source || ''
  form.status = data.status ?? ENABLE
}






const loading = ref(false)
const pageLoading = ref(false)


const validateForm = () => {
  if (!form.question.trim()) {
    alert('请输入问题')
    return false
  }

  if (form.question.length > 200) {
    alert('问题长度不能超过 200 个字符')
    return false
  }

  if (!form.answer.trim()) {
    alert('请输入答案')
    return false
  }

  if (form.answer.length > 5000) {
    alert('答案长度不能超过 5000 个字符')
    return false
  }

  if (!form.categoryId) {
    alert('请选择分类')
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    const data = {
      id: Number(form.id),
      question: form.question,
      answer: form.answer,
      categoryId: Number(form.categoryId),
      source: form.source,
      status: Number(form.status)
    }

    const res = await edit(data)

    if ( res.code === SUCCESS) {
      alert(res.msg || '修改成功')
      router.push('/admin/knowledge')
    } else {
      alert(res.msg || '修改失败')
    }
  } catch (error) {
    console.error(error)
    alert('服务器异常，修改失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(async () => {
  pageLoading.value = true
  await loadCategoryList()
  const id = route.params.id

  try {
    const cachedKnowledge = knowledgeEditStore.getKnowledge(id)
    if (cachedKnowledge) {
      fillForm(cachedKnowledge)
    }

    const res = await getKnowledgeById(id)
    if (res.code === SUCCESS && res.data) {
      fillForm(res.data)
      knowledgeEditStore.setKnowledge(res.data)
    } else {
      alert(res.msg || '知识条目不存在')
      router.push('/admin/knowledge')
    }
  } catch (error) {
    console.error(error)
    alert('服务器异常，知识条目加载失败')
    router.push('/admin/knowledge')
  } finally {
    pageLoading.value = false
  }
})

</script>

<template>
  <div class="page">
    <div v-if="pageLoading" class="loading">
      正在加载知识条目...
    </div>

    <div v-else class="form-card">
      <h2 class="card-title">编辑知识条目</h2>
      <div class="form-item">
        <label>问题标题</label>
        <input
          v-model="form.question"
          type="text"
          placeholder="请输入问题"
        />
      </div>

      <div class="form-item">
        <label>答案内容</label>
        <textarea
          v-model="form.answer"
          rows="8"
          placeholder="请输入答案内容"
        ></textarea>
      </div>

      <div class="form-item">
        <label>所属分类</label>
        <CategorySelect
            v-model="form.categoryId"
            :options="categoryOptions"
            :loading="categoryLoading"
            :error-message="categoryError"
            placeholder="请选择分类"
        />
      </div>

      <div class="form-item">
        <label>来源</label>
        <input
          v-model="form.source"
          type="text"
          placeholder="例如：管理员录入、官网、学生手册"
        />
      </div>

      <div class="form-item">
        <label>状态</label>
        <select v-model="form.status">
          <option :value="1">发布</option>
          <option :value="0">停用</option>
        </select>
      </div>

      <div class="form-actions">
        <button class="cancel-btn" @click="handleCancel">取消修改</button>
        <button class="submit-btn" :disabled="loading" @click="handleSubmit">
          {{ loading ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
}

.loading {
  padding: 24px;
  color: #666;
}

.form-card {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
}

.card-title {
  text-align: center;
  margin: 0 0 20px 0;
  font-size: 24px;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  text-align: center;
}

.form-item input,
.form-item textarea,
.form-item select {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-item input:hover,
.form-item textarea:hover,
.form-item select:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-item textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  background: #eee;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.submit-btn {
  background: #3b82f6;
  color: white;
  transition: background 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #2563eb;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>