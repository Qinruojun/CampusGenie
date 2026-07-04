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


        <div
          v-if="loading"
          class="loading-box"
        >
          正在加载贡献列表...
        </div>

        <div
          v-else-if="list.length === 0"
          class="empty-box"
        >
          暂无用户贡献数据
        </div>

        <div
          v-else
          class="card-list"
        >
          <ContributionCard
            v-for="item in list"
            :key="item.id"
            :item="item"
            @view="handleView"
            @approve="handleApprove"
            @reject="()=>openRejectDialog(item)"
          />
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
import CategorySelect from '@/components/CategorySelect.vue'
import ContributionCard from '@/components/admin/Card/ReviewContributionCard.vue'
import ReasonDialog from '@/components/admin/dialog/RejectReasonDialog.vue'
import { useCategoryOptions } from '@/composables/useCategoryOptions.js'
import { useContributionList } from '@/composables/admin/useContributionList.js'
import { SORT_ORDER_ASC, SORT_ORDER_DESC } from '@/constants/status.js'
import StatCard from "@/components/StatCard.vue";
import { useRouter, useRoute } from 'vue-router'
import { useContributionViewStore } from '@/stores/contributionViewStore'

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

@media (max-width: 900px) {
  .contribution-filter {
    grid-template-columns: 1fr;
  }
}
</style>