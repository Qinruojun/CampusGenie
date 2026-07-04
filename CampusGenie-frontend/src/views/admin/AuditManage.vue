<template>
  <div class="page">
    <main class="main">
      <section class="content">
        <div class="page-head">
          <div>
            <h1>用户贡献审核</h1>
            <p>查看学生提交的知识贡献，支持按关键词、用户、分类、状态和时间筛选。</p>
          </div>
        </div>

        <div class="filter-panel contribution-filter">
          <div class="search">
            <span>⌕</span>
            <input
              v-model="queryForm.keyword"
              placeholder="请输入问题关键词"
              @keyup.enter="handleSearch"
            >
          </div>

          <div class="search">
            <span>👤</span>
            <input
              v-model="queryForm.username"
              placeholder="请输入用户名"
              @keyup.enter="handleSearch"
            >
          </div>

          <CategorySelect
            v-model="queryForm.categoryId"
            :options="categoryOptions"
            :loading="categoryLoading"
            :error-message="categoryError"
            placeholder="全部分类"
          />

          <select
            v-model="queryForm.status"
            class="filter-select"
          >
            <option value="">
              全部状态
            </option>
            <option :value="WAIT_FOR_REVIEW">
              待审核
            </option>
            <option :value="REVIEW_PASS">
              已通过
            </option>
            <option :value="REVIEW_REJECT">
              已驳回
            </option>
          </select>

          <input
            v-model="queryForm.startTime"
            class="filter-input"
            placeholder="开始时间 yyyy-MM-dd HH:mm:ss"
          >

          <input
            v-model="queryForm.endTime"
            class="filter-input"
            placeholder="结束时间 yyyy-MM-dd HH:mm:ss"
          >

          <button
            class="sort-btn"
            :class="{ 'sort-desc': queryForm.sortOrder === SORT_ORDER_DESC, 'sort-asc': queryForm.sortOrder === SORT_ORDER_ASC }"
            @click="handleToggleSortOrder"
          >
            更新时间
            <span class="sort-arrow">
              {{ queryForm.sortOrder === SORT_ORDER_DESC ? '⇩' : '⇧' }}
            </span>
          </button>

          <button
            class="btn primary small"
            @click="handleSearch"
          >
            搜索
          </button>
          <button
            class="btn ghost small"
            @click="handleReset"
          >
            重置
          </button>
        </div>
        <div class="stats">
          <StatCard
            v-for="item in statList"
            :key="item.title"
            :title="item.title"
            :value="item.value"
            :unit="item.unit"
            :icon="item.icon"
            :tone="item.tone"
            :extra="item.extra"
          />
        </div>


        <div class="batch-toggle-bar">
          <button
            class="batch-toggle-btn"
            :class="{ active: batchMode }"
            @click="toggleBatchMode"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <path d="m9 12 2 2 4-4" />
            </svg>
            {{ batchMode ? '退出批量' : '批量审核' }}
          </button>
          <span v-if="batchMode" class="batch-hint">勾选需要操作的贡献，然后点击批量通过或驳回</span>
        </div>

        <div
          v-if="list.length === 0 && !loading"
          class="empty-box"
        >
          暂无用户贡献数据
        </div>

        <div
          v-show="list.length > 0"
          class="card-list-wrapper"
        >
          <Transition name="list">
            <div class="card-list" :key="list.length">
              <div class="batch-bar" v-if="batchMode && selectedIds.length > 0">
                <span>已选 <strong>{{ selectedIds.length }}</strong> 条待审核贡献</span>
                <button class="btn batch-approve" @click="handleBatchApprove">批量通过</button>
                <button class="btn batch-reject" @click="handleBatchReject">批量驳回</button>
                <button class="btn ghost small" @click="selectedIds.value = []">取消选择</button>
              </div>

              <ContributionCard
                v-for="item in list"
                :key="item.id"
                :item="item"
                :checked="selectedIds.includes(item.id)"
                :show-checkbox="batchMode"
                @view="handleView"
                @approve="handleApprove"
                @reject="()=>openRejectDialog(item)"
                @toggle-check="handleToggleCheck"
              />
            </div>
          </Transition>
          
          <div v-if="loading" class="loading-overlay">
            <div class="loading-spinner"></div>
            <span>正在加载...</span>
          </div>
        </div>
        <ReasonDialog
          v-model:visible="rejectDialogVisible"
          title="驳回用户贡献"
          tip="请填写驳回原因，方便用户了解内容未通过的原因。"
          placeholder="例如：问题描述不清晰、答案内容不完整、与知识库已有内容重复等"
          confirm-text="确认驳回"
          :loading="rejectLoading"
          @confirm="submitReject"
        />
        <ReasonDialog
          v-model:visible="batchRejectDialogVisible"
          title="批量驳回用户贡献"
          tip="请填写驳回原因，选中的贡献将统一驳回。"
          placeholder="例如：问题描述不清晰、答案内容不完整、与知识库已有内容重复等"
          confirm-text="确认批量驳回"
          :loading="batchRejectLoading"
          @confirm="submitBatchReject"
        />

        <div class="pagination">
          <button @click="handlePrevPage">
            上一页
          </button>
          <button class="current">
            {{ page }}
          </button>
          <button @click="handleNextPage">
            下一页
          </button>
          <span class="page-info">
            共 {{ total }} 条，每页 {{ pageSize }} 条
          </span>
          <button @click="loadContributionList">
            刷新
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import {onMounted, computed, ref, watch} from 'vue'
import {REVIEW_PASS,REVIEW_REJECT,WAIT_FOR_REVIEW} from "@/constants/status.js";
import { SUCCESS } from "@/constants/code.js";
import CategorySelect from '@/components/CategorySelect.vue'
import ContributionCard from '@/components/admin/Card/ReviewContributionCard.vue'
import ReasonDialog from '@/components/admin/dialog/RejectReasonDialog.vue'
import { useCategoryOptions } from '@/composables/useCategoryOptions.js'
import { useContributionList } from '@/composables/admin/useContributionList.js'
import { SORT_ORDER_ASC, SORT_ORDER_DESC } from '@/constants/status.js'
import StatCard from "@/components/StatCard.vue";
import { useRouter, useRoute } from 'vue-router'
import { useContributionViewStore } from '@/stores/contributionViewStore'
import { batchReview } from '@/api/admin/contirbute.js'

const {
  categoryOptions,
  categoryLoading,
  categoryError,
  loadCategoryList
} = useCategoryOptions()

const {
  queryForm,
  page,
  pageSize,
  total,
  list,
  loading,
  loadContributionList,
  handleSearch,
  handleReset,
  handlePrevPage,
  handleNextPage,
  handleToggleSortOrder,
  handleApprove,
  handleReject,
  loadStatistics
} = useContributionList()

const router = useRouter()
const route = useRoute()

const statisticsData = ref({
  pendingCount: 0,
  approvedCount: 0,
  rejectedCount: 0
})

const statList = computed(() => [
  {
    title: '贡献总数',
    value: total.value,
    unit: '条',
    icon: '▧',
    tone: 'green'
  },

  {
    title: '待审核',
    value: statisticsData.value.pendingCount,
    unit: '条',
    icon: '⏳',
    tone: 'orange'
  },
  {
    title: '已通过',
    value: statisticsData.value.approvedCount,
    unit: '条',
    icon: '✓',
    tone: 'green',

  },
  {
    title:'已驳回',
    value: statisticsData.value.rejectedCount,
    unit: '条',
    icon: '×',
    tone: 'orange',

  }
])
// const handleToggleSortOrder = () => {
//   queryForm.value.sortOrder =
//       queryForm.value.sortOrder === SORT_ORDER_DESC
//           ? SORT_ORDER_ASC
//           : SORT_ORDER_DESC
//
//   handleSearch()
// }

const handleView = (item) => {
  contributionViewStore.setContribution(item)
  router.push(`/admin/contribution/${item.id}`)
}
const rejectDialogVisible = ref(false)
const rejectTarget = ref(null)
const rejectLoading = ref(false)
const contributionViewStore = useContributionViewStore()

// 批量审核模式
const batchMode = ref(false)

function toggleBatchMode() {
  batchMode.value = !batchMode.value
  if (batchMode.value) {
    queryForm.value.status = WAIT_FOR_REVIEW
    handleSearch()
  } else {
    selectedIds.value = []
    queryForm.value.status = ''
    handleSearch()
  }
}

// 批量选择
const selectedIds = ref([])

function handleToggleCheck(id) {
  const idx = selectedIds.value.indexOf(id)
  if (idx >= 0) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
}

// 批量通过
async function handleBatchApprove() {
  const ids = selectedIds.value.slice()
  if (ids.length === 0) return

  try {
    const res = await batchReview({ contributionIds: ids, action: 1 })
    if (res.code === SUCCESS) {
      const data = res.data || {}
      let msg = `批量审核完成，成功 ${data.successCount ?? ids.length} 条`
      if (data.failCount > 0) msg += `，${data.failCount} 条失败`
      alert(msg)
      selectedIds.value = []
    } else {
      alert(res.msg || '批量通过失败')
      return
    }
  } catch (error) {
    console.error('批量通过请求异常:', error)
    alert('批量通过异常: ' + (error.response?.data?.msg || error.message || '未知错误'))
    return
  }

  try {
    await loadContributionList()
    await loadStatisticsData()
  } catch (e) {
    console.error('刷新列表异常:', e)
  }
}

// 批量驳回
const batchRejectDialogVisible = ref(false)
const batchRejectLoading = ref(false)

function handleBatchReject() {
  if (selectedIds.value.length === 0) return
  batchRejectDialogVisible.value = true
}

async function submitBatchReject(reason) {
  const ids = selectedIds.value.slice()
  if (ids.length === 0) return

  batchRejectLoading.value = true
  try {
    const res = await batchReview({ contributionIds: ids, action: 2, rejectReason: reason })

    if (res.code === SUCCESS) {
      const data = res.data || {}
      let msg = `批量审核完成，成功 ${data.successCount ?? ids.length} 条`
      if (data.failCount > 0) msg += `，${data.failCount} 条失败`
      alert(msg)
      selectedIds.value = []
      batchRejectDialogVisible.value = false
    } else {
      alert(res.msg || '批量驳回失败')
      return
    }
  } catch (error) {
    console.error('批量驳回请求异常:', error)
    alert('批量驳回异常: ' + (error.response?.data?.msg || error.message || '未知错误'))
    return
  } finally {
    batchRejectLoading.value = false
  }

  try {
    await loadContributionList()
    await loadStatisticsData()
  } catch (e) {
    console.error('刷新列表异常:', e)
  }
}

function openRejectDialog(item) {
  rejectTarget.value = item
  rejectDialogVisible.value = true
}

async function submitReject(reason) {
  if (!rejectTarget.value) {
    alert('未选择要驳回的贡献')
    return
  }

  rejectLoading.value = true

  try {
    await handleReject(rejectTarget.value, reason)

    rejectDialogVisible.value = false
    rejectTarget.value = null

    await loadStatisticsData()
  } finally {
    rejectLoading.value = false
  }
}

async function loadStatisticsData() {
  const data = await loadStatistics()
  if (data) {
    statisticsData.value = data
  }
}

onMounted(() => {
  loadCategoryList()
  
  const statusParam = route.query.status
  if (statusParam !== undefined) {
    queryForm.value.status = statusParam
  }
  
  loadContributionList()
  loadStatisticsData()
})
</script>

<style scoped src="@/styles/card-list.css"></style>

<style scoped>
.contribution-filter {
  grid-template-columns:
    minmax(220px, 1.4fr)
    minmax(180px, 1fr)
    180px
    120px
    200px
    200px
    110px
    90px
    90px
    90px
    90px;
  align-items: center;
}

.batch-toggle-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.batch-toggle-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
}
.batch-toggle-btn svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.batch-toggle-btn.active {
  color: #fff;
  background: #16a34a;
  border-color: #16a34a;
}
.batch-hint {
  color: #6b7280;
  font-size: 13px;
}

.filter-select,
.filter-input {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #dfe3e8;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.filter-select:focus,
.filter-input:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
}

.sort-btn {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  border: 1px solid #dfe3e8;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  box-sizing: border-box;
}

.sort-btn.sort-desc {
  color: #f97316;
  border-color: #f97316;
}

.sort-btn.sort-desc:hover {
  color: #ea580c;
  border-color: #ea580c;
}

.sort-btn.sort-asc {
  color: #16a34a;
  border-color: #16a34a;
}

.sort-btn.sort-asc:hover {
  color: #15803d;
  border-color: #15803d;
}

.sort-arrow {
  margin-left: 4px;
  font-size: 14px;
  font-weight: 700;
}

.batch-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 20px;
  margin-bottom: 16px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
}
.batch-bar strong {
  color: #16a34a;
  font-size: 18px;
}
.batch-approve {
  color: #16a34a !important;
  border-color: #16a34a !important;
}
.batch-approve:hover {
  color: #fff !important;
  background: #16a34a !important;
}
.batch-reject {
  color: #ef4444 !important;
  border-color: #fca5a5 !important;
}
.batch-reject:hover {
  color: #fff !important;
  background: #ef4444 !important;
}

.loading-box,
.empty-box {
  padding: 40px;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  color: #6b7280;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.page-info {
  display: flex;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
}

@media (max-width: 1400px) {
  .contribution-filter {
    grid-template-columns: repeat(3, 1fr);
  }
}

.card-list-wrapper {
  position: relative;
  min-height: 200px;
}

.card-list {
  opacity: 1;
  transition: opacity 0.4s ease;
}

.list-enter-active,
.list-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.list-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  z-index: 10;
  gap: 12px;
  border-radius: 8px;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #e5e7eb;
  border-top-color: #16a34a;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-box {
  opacity: 0;
  animation: fadeIn 0.4s ease forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 900px) {
  .contribution-filter {
    grid-template-columns: 1fr;
  }
}
</style>