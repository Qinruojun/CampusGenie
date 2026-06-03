<!--管理员新增知识条目-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { add } from '@/api/admin/knowledge'
import CategorySelect from '@/components/CategorySelect.vue'
import {SUCCESS} from "@/constants/code.js";

const router = useRouter()

const form = ref({
  question: '',
  answer: '',
  categoryId: '',
  source: '',
  status: 1
})

const categoryList = ref([])
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


</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>新增知识条目</h2>
      <button class="back-btn" @click="handleCancel">返回</button>
    </div>

    <div class="form-card">
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
            placeholder="请选择问题分类"
            @change="handleCategoryChange"
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
        <button class="cancel-btn" @click="handleCancel">取消</button>
        <button class="submit-btn" :disabled="loading" @click="handleSubmit">
          {{ loading ? '提交中...' : '确认新增' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
}

.back-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
}

.form-card {
  max-width: 800px;
  padding: 24px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
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
}

.form-item textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
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
}

.submit-btn {
  background: #1677ff;
  color: white;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>