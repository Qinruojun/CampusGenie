<!--管理员新增知识条目-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { add } from '@/api/admin/knowledge'
import CategorySelect from '@/components/CategorySelect.vue'
import {SUCCESS} from "@/constants/code.js";
import { useCategoryOptions } from '@/composables/useCategoryOptions'

const router = useRouter()
const {
  categoryOptions,
  categoryLoading,
  categoryError,
  loadCategoryList
} = useCategoryOptions()

const form = ref({
  question: '',
  answer: '',
  categoryId: '',
  source: '',
  status: 1
})

const loading = ref(false)


const validateForm = () => {
  if (!form.value.question.trim()) {
    alert('请输入问题')
    return false
  }

  if (form.value.question.length > 200) {
    alert('问题长度不能超过 200 个字符')
    return false
  }

  if (!form.value.answer.trim()) {
    alert('请输入答案')
    return false
  }

  if (form.value.answer.length > 5000) {
    alert('答案长度不能超过 5000 个字符')
    return false
  }

  if (!form.value.categoryId) {
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
      question: form.value.question,
      answer: form.value.answer,
      categoryId: Number(form.value.categoryId),
      source: form.value.source,
      status: Number(form.value.status)
    }

    const res = await add(data)

    if (res.code === SUCCESS) {
      alert(res.msg || '新增成功')
      router.push('/admin/knowledge')
    } else {
      alert(res.msg || '新增失败')
    }
  } catch (error) {
    console.error(error)
    alert('服务器异常，新增失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  loadCategoryList()
})

</script>

<template>
  <div class="page">
    <div class="form-card">
      <h2 class="card-title">新增知识条目</h2>

      <div class="form-item">
        <label>问题标题</label>
        <input
          v-model="form.question"
          type="text"
          placeholder="请输入问题，例如：如何修改密码？"
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
            placeholder="请选择问题分类"
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
        <div class="status-container">
          <select v-model="form.status" class="status-select">
            <option :value="1">发布</option>
            <option :value="0">停用</option>
          </select>
        </div>
      </div>

      <div class="form-actions">
        <button class="cancel-btn" @click="handleCancel">取消</button>
        <button class="submit-btn" :disabled="loading" @click="handleSubmit">
          {{ loading ? '提交中...' : '确认新增' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { padding: 24px; }
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
.form-item { margin-bottom: 20px; }
.form-item label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
  text-align: center;
}
.form-item input,
.form-item textarea,
.form-item select {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  color: #374151;
  line-height: 1.6;
  transition: all 0.2s;
}
.form-item textarea {
  resize: vertical;
  min-height: 120px;
}
.form-item input:focus,
.form-item textarea:focus,
.form-item select:focus {
  outline: none;
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.1);
}
.status-container {
  display: flex;
  justify-content: center;
}
.status-select {
  padding: 10px 16px;
  cursor: pointer;
}
.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.cancel-btn,
.submit-btn {
  padding: 10px 24px;
  border: 1px solid;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.cancel-btn {
  background: #fff;
  border-color: #9ca3af;
  color: #374151;
}
.cancel-btn:hover {
  background: #f3f4f6;
}
.submit-btn {
  background: #16a34a;
  border-color: #16a34a;
  color: #fff;
}
.submit-btn:hover:not(:disabled) {
  background: #15803d;
  border-color: #15803d;
}
.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>