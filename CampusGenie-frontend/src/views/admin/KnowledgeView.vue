<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useKnowledgeEditStore } from '@/stores/knowledgeEditStore'
import { ENABLE } from '@/constants/status.js'
import { SUCCESS } from '@/constants/code.js'
import { getKnowledgeById } from "@/api/admin/knowledge.js"
import { useCategoryOptions } from "@/composables/useCategoryOptions.js";

const knowledgeEditStore = useKnowledgeEditStore()
const { categoryOptions, loadCategoryList } = useCategoryOptions()
const router = useRouter()
const route = useRoute()

const form = reactive({
  id: '', question: '', answer: '', categoryId: '', categoryName: '',
  source: '', status: ENABLE, updatedAt: '', updatedBy: '',
})
const pageLoading = ref(false)

onMounted(async () => {
  pageLoading.value = true
  await loadCategoryList()
  const id = route.params.id
  try {
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

function fillForm(data) {
  form.id = data.id || ''
  form.question = data.question || ''
  form.answer = data.answer || ''
  form.categoryId = data.categoryId || ''
  form.categoryName = data.categoryName || data.category || ''
  form.source = data.source || ''
  form.status = data.status ?? ENABLE
  form.updatedAt = data.updatedAt || ''
  form.updatedBy = data.updatedBy || ''
}
const handleBack = () => router.back()
</script>

<template>
  <div class="page">
    <div v-if="pageLoading" class="loading">正在加载知识条目...</div>
    <div v-else class="form-card">
      <h2 class="card-title">查看知识条目</h2>
      <div class="form-item">
        <label>知识ID</label>
        <div class="view-field">{{ form.id }}</div>
      </div>
      <div class="form-item">
        <label>问题标题</label>
        <div class="view-field">{{ form.question }}</div>
      </div>
      <div class="form-item">
        <label>答案内容</label>
        <div class="view-field answer-field">{{ form.answer }}</div>
      </div>
      <div class="form-item">
        <label>所属分类</label>
        <div class="view-field">
          {{ categoryOptions.find(c => String(c.id) === String(form.categoryId))?.name || form.categoryName || '未分类' }}
        </div>
      </div>
      <div class="form-item">
        <label>来源</label>
        <div class="view-field">{{ form.source || '未填写' }}</div>
      </div>
      <div class="form-item">
        <label>状态</label>
        <div class="status-container">
          <span class="status-tag" :class="form.status === 1 ? 'enabled' : 'disabled'">
            {{ form.status === 1 ? '已发布' : '已停用' }}
          </span>
        </div>
      </div>
      <div class="form-item" v-if="form.updatedAt">
        <label>更新时间</label>
        <div class="view-field">{{ form.updatedAt }}</div>
      </div>
      <div class="form-item" v-if="form.updatedBy">
        <label>更新人</label>
        <div class="view-field">{{ form.updatedBy }}</div>
      </div>
      <div class="form-actions">
        <button class="back-btn primary" @click="handleBack">返回</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { padding: 24px; }
.loading { padding: 24px; color: #666; }
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
.view-field {
  width: 100%; box-sizing: border-box; padding: 10px 12px;
  border: 1px solid #e5e7eb; border-radius: 6px; font-size: 14px;
  background: #f9fafb; color: #374151; line-height: 1.6; min-height: 20px;
}
.answer-field { white-space: pre-wrap; min-height: 120px; }
.status-tag { display: inline-flex; align-items: center; height: 28px; padding: 0 14px; border-radius: 6px; font-size: 14px; font-weight: 700; }
.status-container {
  display: flex;
  justify-content: center;
}
.status-tag.enabled { color: #16a34a; background: #dcfce7; }
.status-tag.disabled { color: #6b7280; background: #f3f4f6; }
.form-actions { display: flex; justify-content: center; gap: 12px; margin-top: 28px; padding-top: 20px; border-top: 1px solid #eee; }
.back-btn {
  padding: 10px 24px;
  background: #fff;
  border: 1px solid #16a34a;
  color: #16a34a;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.back-btn:hover {
  background: #16a34a;
  color: #fff;
}
</style>
