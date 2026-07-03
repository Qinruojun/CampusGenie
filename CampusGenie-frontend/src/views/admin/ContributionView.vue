<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useContributionViewStore } from '@/stores/contributionViewStore'
import { WAIT_FOR_REVIEW_MSG, REVIEW_PASS_MSG, REVIEW_REJECT_MSG } from '@/constants/status.js'

const router = useRouter()
const contributionStore = useContributionViewStore()

const item = ref(null)
const pageLoading = ref(true)

onMounted(() => {
  const data = contributionStore.getContribution()
  if (!data) {
    alert('未找到贡献详情')
    router.back()
    return
  }
  item.value = data
  pageLoading.value = false
})

function statusClass(statusDesc) {
  if (statusDesc === REVIEW_PASS_MSG) return 'approved'
  if (statusDesc === WAIT_FOR_REVIEW_MSG) return 'pending'
  return 'rejected'
}

const handleBack = () => router.back()
</script>

<template>
  <div class="page">
    <div v-if="pageLoading" class="loading">正在加载贡献详情...</div>

    <div v-else-if="item" class="form-card">
      <h2 class="card-title">查看贡献详情</h2>
      <div class="form-item">
        <label>贡献ID</label>
        <div class="view-field">{{ item.id }}</div>
      </div>

      <div class="form-item" v-if="item.username">
        <label>提交用户</label>
        <div class="view-field">{{ item.username }}</div>
      </div>

      <div class="form-item">
        <label>问题</label>
        <div class="view-field">{{ item.question }}</div>
      </div>

      <div class="form-item">
        <label>答案</label>
        <div class="view-field answer-field">{{ item.answer }}</div>
      </div>

      <div class="form-item">
        <label>所属分类</label>
        <div class="view-field">{{ item.categoryName || '未分类' }}</div>
      </div>

      <div class="form-item" v-if="item.supplement">
        <label>补充说明</label>
        <div class="view-field">{{ item.supplement }}</div>
      </div>

      <div class="form-item" v-if="item.contact">
        <label>联系方式</label>
        <div class="view-field">{{ item.contact }}</div>
      </div>

      <div class="form-item">
        <label>审核状态</label>
        <div class="status-container">
          <span class="status-tag" :class="statusClass(item.statusDesc)">
            {{ item.statusDesc }}
          </span>
        </div>
      </div>

      <div class="form-item">
        <label>提交时间</label>
        <div class="view-field">{{ item.createdTime }}</div>
      </div>

      <div class="form-item" v-if="item.reviewedTime">
        <label>审核时间</label>
        <div class="view-field">{{ item.reviewedTime }}</div>
      </div>

      <div class="form-item" v-if="item.rejectReason" :class="{ 'reject-item': true }">
        <label>驳回理由</label>
        <div class="view-field reject-field">{{ item.rejectReason }}</div>
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
.status-tag.approved { color: #16a34a; background: #dcfce7; }
.status-tag.pending { color: #f59e0b; background: #fff7ed; }
.status-tag.rejected { color: #6b7280; background: #f3f4f6; }
.reject-item { margin-top: 8px; }
.reject-field {
  background: #fff8f7;
  border-color: #fecaca;
  color: #b91c1c;
}
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
