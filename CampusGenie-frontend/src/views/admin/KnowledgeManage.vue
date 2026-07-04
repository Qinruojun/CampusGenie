<template>
  <div class="page">
    <main class="main">

      <section class="content">
        <div class="page-head">
          <div>
            <h1>知识库管理</h1>
            <p>管理和维护校园知识库，支持知识条目的增删改查与发布状态管理。</p>
          </div>

          <div class="head-actions">
            <button class="btn ghost" @click="handleImport">⇧ 批量导入</button>
            <button class="btn primary" @click="handleAdd">＋ 新增知识</button>
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
            <option :value="1">已发布</option>
            <option :value="0">已停用</option>
          </select>

          <button class="sort-btn" :class="{ 'sort-desc': queryForm.sortOrder === SORT_ORDER_DESC, 'sort-asc': queryForm.sortOrder === SORT_ORDER_ASC }" @click="handleToggleSortOrder">
            更新时间
            <span class="sort-arrow">
              {{ queryForm.sortOrder === SORT_ORDER_DESC ? '⇩' : '⇧' }}
            </span>
          </button>

          <button class="btn primary small" @click="handleSearch">搜索</button>
          <button class="btn ghost small" @click="handleReset">重置</button>
          <button class="btn ghost small" @click="handleExport">▼ 导出</button>
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
              :extra-tone="item.extraTone"
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
            {{ batchMode ? '退出批量' : '批量删除' }}
          </button>
          <span v-if="batchMode" class="batch-hint">勾选需要删除的已停用知识，然后点击批量删除</span>
        </div>

        <div
          v-if="loading"
          class="loading-box"
        >
          正在加载知识列表...
        </div>

        <div
          v-else-if="list.length === 0"
          class="empty-box"
        >
          暂无知识数据
        </div>

        <div
          v-show="list.length > 0"
          class="card-list-wrapper"
        >
          <Transition name="list">
            <div class="card-list" :key="list.length">
              <div class="batch-bar" v-if="batchMode && selectedIds.length > 0">
                <span>已选 <strong>{{ selectedIds.length }}</strong> 条已停用知识</span>
                <button class="btn batch-delete" @click="handleBatchDelete">批量删除</button>
                <button class="btn ghost small" @click="handleCancelSelection">取消选择</button>
              </div>

              <KnowledgeCard
                  v-for="item in list"
                  :key="item.id"
                  :item="item"
                  :checked="selectedIds.includes(item.id)"
                  :show-checkbox="batchMode"
                  @view="handleView"
                  @edit="handleEdit"
                  @toggle="handleToggleStatus"
                  @delete="handleDelete"
                  @toggle-check="handleToggleCheck"
              />
            </div>
          </Transition>

          <div v-if="loading" class="loading-overlay">
            <div class="loading-spinner"></div>
            <span>正在加载...</span>
          </div>
        </div>

        <div class="pagination">
          <button @click="handlePrevPage">上一页</button>
          <button   :class="{ current: page === 1 }" @click="page = 1" >1</button>
          <button :class="{ current: page === 2 }" @click="page = 2">2</button>
          <button    :class="{ current: page === 3 }" @click="page = 3">3</button>
          <button @click="handleNextPage">下一页</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useKnowledgeEditStore } from '@/stores/knowledgeEditStore'
import CategorySelect from '@/components/CategorySelect.vue'
import { useCategoryOptions } from '@/composables/useCategoryOptions'
import {useKnowledgeList} from "@/composables/admin/useKnowledgeList.js";
import { SORT_ORDER_ASC, SORT_ORDER_DESC, DISABLE } from "@/constants/status.js";
import KnowledgeCard from '@/components/admin/Card/KnowledgeCard.vue'
import StatCard from "@/components/StatCard.vue";
import { exportKnowledge, batchDeleteKnowledge } from '@/api/admin/knowledge.js'
import { SUCCESS } from "@/constants/code.js"
const knowledgeEditStore = useKnowledgeEditStore()
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
  loadKnowledgeList,
  handleSearch,
  handleReset,
  handlePrevPage,
  handleNextPage,
  handleToggleSortOrder,
  handleDelete: baseHandleDelete,
  handleToggleStatus: baseHandleToggleStatus,
  loadStatistics
} = useKnowledgeList()

// 重写 handleDelete，确保更新统计数据
const handleDelete = async (id) => {
  await baseHandleDelete(id)
  // 删除成功后刷新统计数据
  await loadStatisticsData()
}

// 重写 handleToggleStatus，确保更新统计数据
const handleToggleStatus = async (item) => {
  await baseHandleToggleStatus(item)
  // 状态切换成功后刷新统计数据
  await loadStatisticsData()
}

const handleAdd = () => {
  router.push('/admin/addKnowledge')
}
const handleEdit = (item) => {
  knowledgeEditStore.setKnowledge(item)
  router.push(`/admin/editKnowledge/${item.id}`)
}
const handleImport = () => {
  router.push('/admin/importKnowledge')
}

const handleExport = () => {
  const f = queryForm.value
  exportKnowledge({
    keyword: f.keyword,
    categoryId: f.categoryId,
    status: f.status
  })
}

const statisticsData = ref({
  publishedCount: 0,
  stoppedCount: 0,
  weeklyUpdateCount: 0,
  lastWeekUpdateCount: 0
})

async function loadStatisticsData() {
  const data = await loadStatistics()
  console.log('加载统计数据:', data)
  if (data) {
    statisticsData.value = {
      publishedCount: data.publishedCount || 0,
      stoppedCount: data.stoppedCount || 0,
      weeklyUpdateCount: data.weeklyUpdateCount || 0,
      lastWeekUpdateCount: data.lastWeekUpdateCount || 0
    }
    console.log('更新后的统计数据:', statisticsData.value)
  }
}

const batchMode = ref(false)
const selectedIds = ref([])

function toggleBatchMode() {
  batchMode.value = !batchMode.value
  if (batchMode.value) {
    queryForm.value.status = DISABLE
    handleSearch()
  } else {
    selectedIds.value = []
    queryForm.value.status = ''
    clearBatchState()
    handleSearch()
  }
}

function handleToggleCheck(id) {
  const idx = selectedIds.value.indexOf(id)
  if (idx >= 0) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
}

function handleCancelSelection() {
  selectedIds.value = []
}

async function handleBatchDelete() {
  const ids = selectedIds.value.slice()
  if (ids.length === 0) {
    alert('请先勾选要删除的知识条目')
    return
  }

  const confirmMsg = '确定要删除选中的 ' + ids.length + ' 条已停用知识吗？'
  const ok = window.confirm(confirmMsg)
  if (!ok) return

  try {
    const res = await batchDeleteKnowledge({ ids })

    if (res.code === SUCCESS) {
      const data = res.data || {}
      let msg = '批量删除完成，成功 ' + (data.successCount ?? ids.length) + ' 条'
      if (data.failCount > 0) msg += '，' + data.failCount + ' 条失败'
      alert(msg)
      selectedIds.value = []
      await loadKnowledgeList()
      await loadStatisticsData()
    } else {
      alert(res.msg || '批量删除失败')
    }
  } catch (error) {
    console.error(error)
    alert('批量删除异常: ' + (error.response?.data?.msg || error.message || '未知错误'))
  }
}

const BATCH_STORAGE_KEY = 'campusgenie:knowledge-batch-cache'

function saveBatchState() {
  sessionStorage.setItem(BATCH_STORAGE_KEY, JSON.stringify({
    batchMode: batchMode.value,
    selectedIds: selectedIds.value,
    status: queryForm.value.status
  }))
}

function restoreBatchState() {
  try {
    const raw = sessionStorage.getItem(BATCH_STORAGE_KEY)
    if (raw) {
      const data = JSON.parse(raw)
      if (data.batchMode) {
        batchMode.value = true
        selectedIds.value = data.selectedIds || []
        queryForm.value.status = data.status || DISABLE
      }
    }
  } catch (e) {
    console.error('Failed to restore batch state:', e)
  }
}

function clearBatchState() {
  sessionStorage.removeItem(BATCH_STORAGE_KEY)
}

onMounted(()=>{
  loadCategoryList()
  restoreBatchState()
  loadKnowledgeList()
  loadStatisticsData()
})

const handleView = item => {
  saveBatchState()
  router.push(`/admin/viewKnowledge/${item.id}`)
}

const statList = computed(() => {
  const weeklyCount = statisticsData.value.weeklyUpdateCount
  const lastWeekCount = statisticsData.value.lastWeekUpdateCount

  let trendText = ''
  let trendTone = 'green' // green: 上升, red: 下降, orange: 持平

  if (lastWeekCount > 0) {
    const percent = Math.round(((weeklyCount - lastWeekCount) / lastWeekCount) * 100)
    if (percent > 0) {
      trendText = `较上周 ↑${percent}%`
      trendTone = 'green'
    } else if (percent < 0) {
      trendText = `较上周 ↓${Math.abs(percent)}%`
      trendTone = 'red'
    } else {
      trendText = '较上周持平'
      trendTone = 'orange'
    }
  } else if (weeklyCount > 0) {
    trendText = '上周无数据'
    trendTone = 'orange'
  }

  return [
    {
      title: '知识总数',
      value: total.value,
      unit: '条',
      icon: '▧',
      tone: 'green'
    },
    {
      title: '已发布',
      value: statisticsData.value.publishedCount,
      unit: '条',
      icon: '➤',
      tone: 'green'
    },
    {
      title: '已停用',
      value: statisticsData.value.stoppedCount,
      unit: '条',
      icon: '◷',
      tone: 'orange'
    },
    {
      title: '本周更新',
      value: weeklyCount,
      unit: '条',
      icon: '↗',
      tone: 'green',
      extra: trendText,
      extraTone: trendTone
    }
  ]
})

</script>

<style scoped src="@/styles/card-list.css"></style>
<style>
.filter-panel {
  display: grid;
  grid-template-columns: minmax(280px, 1.7fr) 220px 160px 110px 90px 90px 90px;
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

.batch-toggle-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.batch-toggle-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: 2px solid #dfe3e8;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.batch-toggle-btn svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
}

.batch-toggle-btn:hover {
  border-color: #ef4444;
  color: #ef4444;
}

.batch-toggle-btn.active {
  border-color: #ef4444;
  background: #fef2f2;
  color: #ef4444;
}

.batch-hint {
  color: #6b7280;
  font-size: 14px;
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
  border-top-color: #ef4444;
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

.batch-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  margin-bottom: 16px;
  background: #fef2f2;
  border-radius: 8px;
}

.batch-bar span {
  color: #374151;
  font-size: 14px;
}

.batch-bar span strong {
  color: #ef4444;
}

.batch-bar .btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid;
}

.batch-bar .btn.batch-delete {
  background: #ef4444;
  color: #fff;
  border-color: #ef4444;
}

.batch-bar .btn.batch-delete:hover {
  background: #dc2626;
  border-color: #dc2626;
}

.batch-bar .btn.ghost {
  background: #fff;
  color: #374151;
  border-color: #dfe3e8;
}

.batch-bar .btn.ghost:hover {
  border-color: #9ca3af;
}
</style>