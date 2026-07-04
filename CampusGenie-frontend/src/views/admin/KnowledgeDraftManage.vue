<template>
  <div class="page">
    <main class="main">

      <section class="content">
        <div class="page-head">
          <div>
            <h1>知识草稿管理</h1>
            <p>管理和维护知识草稿，支持审核通过、驳回和编辑操作。</p>
          </div>

          <div class="head-actions">
          </div>
        </div>

        <div class="filter-panel">
          <div class="search">
            <span>⌕</span>
            <input
                v-model="queryForm.keyword"
                placeholder="请输入问题关键词"
                @keyup.enter="handleSearch"
            />
          </div>

          <CategorySelect
              v-model="queryForm.categoryId"
              :options="categoryOptions"
              :loading="categoryLoading"
              :error-message="categoryError"
              placeholder="全部分类"
          />

          <select class="filter-select" v-model="queryForm.status">
            <option value="">全部状态</option>
            <option :value="0">待审核</option>
            <option :value="1">已通过</option>
          </select>

          <button class="sort-btn" :class="{ 'sort-desc': queryForm.sortOrder === SORT_ORDER_DESC, 'sort-asc': queryForm.sortOrder === SORT_ORDER_ASC }" @click="handleToggleSortOrder">
            创建时间
            <span class="sort-arrow">
              {{ queryForm.sortOrder === SORT_ORDER_DESC ? '⇩' : '⇧' }}
            </span>
          </button>

          <button class="btn primary small" @click="handleSearch">搜索</button>
          <button class="btn ghost small" @click="handleReset">重置</button>
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
          />
        </div>

        <div class="card-list">
          <DraftCard
              v-for="item in list"
              :key="item.id"
              :item="item"
              @view="handleView"
              @edit="handleEdit"
              @approve="handleApprove"
              @delete="handleDelete"
          />
        </div>

        <div class="pagination">
          <button @click="handlePrevPage">上一页</button>
          <template v-for="p in Math.min(3, Math.ceil(total / pageSize))" :key="p">
            <button :class="{ current: page === p }" @click="page = p">{{ p }}</button>
          </template>
          <button @click="handleNextPage">下一页</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CategorySelect from '@/components/CategorySelect.vue'
import { useCategoryOptions } from '@/composables/useCategoryOptions'
import { useKnowledgeDraftList } from "@/composables/admin/useKnowledgeDraftList.js";
import { SORT_ORDER_ASC, SORT_ORDER_DESC } from "@/constants/status.js";
import DraftCard from '@/components/admin/Card/DraftCard.vue'
import StatCard from "@/components/StatCard.vue";
import { getStatistics } from '@/api/admin/knowledgeDraft.js'
import { SUCCESS } from '@/constants/code.js'

const router = useRouter()

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
  loadKnowledgeDraftList,
  handleSearch,
  handleReset,
  handlePrevPage,
  handleNextPage,
  handleToggleSortOrder,
  handleApprove: baseHandleApprove,
  handleDelete: baseHandleDelete,
  handleEdit: baseHandleEdit
} = useKnowledgeDraftList()

const handleApprove = async (id) => {
  await baseHandleApprove(id)
  await loadStatistics()
}

const handleDelete = async (id) => {
  await baseHandleDelete(id)
  await loadStatistics()
}

const handleEdit = (item) => {
  router.push(`/admin/editKnowledgeDraft/${item.id}`)
}

const handleView = (item) => {
  router.push(`/admin/viewKnowledgeDraft/${item.id}`)
}

const statisticsData = ref({
  pendingCount: 0,
  approvedCount: 0,
  weeklyUpdateCount: 0,
  lastWeekUpdateCount: 0
})

const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === SUCCESS) {
      statisticsData.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  loadCategoryList()
  loadKnowledgeDraftList()
  loadStatistics()
})

const statList = computed(() => {
  return [
    {
      title: '草稿总数',
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
      tone: 'green'
    },
    {
      title: '本周更新',
      value: statisticsData.value.weeklyUpdateCount,
      unit: '条',
      icon: '↗',
      tone: 'green'
    }
  ]
})
</script>

<style scoped src="@/styles/card-list.css"></style>
<style>
.filter-panel {
  display: grid !important;
  grid-template-columns: repeat(3, 1fr) !important;
  gap: 16px;
  align-items: center;
  padding: 20px;
  margin-bottom: 18px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.filter-select {
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

.filter-select:focus {
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
</style>