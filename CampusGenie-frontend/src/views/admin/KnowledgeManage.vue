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

          <button class="sort-btn" @click="handleToggleSortOrder">
            更新时间
            <span class="sort-arrow">
      {{ queryForm.sortOrder === SORT_ORDER_DESC ? '↓' : '↑' }}
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
              :extra="item.extra"
          />
        </div>


        <div class="card-list">
          <KnowledgeCard
              v-for="item in list"
              :key="item.id"
              :item="item"
              @view="handleView"
              @edit="handleEdit"
              @toggle="handleToggleStatus"
              @delete="handleDelete"
          />
        </div>

        <div class="pagination">
          <button @click="handlePrevPage">上一页</button>
          <button class="current" @click="page = 1" >1</button>
          <button @click="page = 2">2</button>
          <button @click="page = 3">3</button>
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
import { SORT_ORDER_ASC, SORT_ORDER_DESC } from "@/constants/status.js";
import KnowledgeCard from '@/components/admin/Card/KnowledgeCard.vue'
import StatCard from "@/components/StatCard.vue";
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
  handleDelete,
  handleToggleStatus,

} = useKnowledgeList()
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
onMounted(()=>{
  loadCategoryList()
  loadKnowledgeList()
})
const handleView = item => {
  console.log('查看详情', item)//TODO：显示知识卡片详情
}
//TODO: 还要后端返回统计信息
const statList = computed(() => [
  {
    title: '知识总数',
    value: total.value,
    unit: '条',
    icon: '▧',
    tone: 'green'
  },
  {
    title: '已发布',
    value: '1,102',//HACK
    unit: '条',
    icon: '➤',
    tone: 'green'
  },
  {
    title: '待审核/已停用',
    value: 154, //HACK
    unit: '条',
    icon: '◷',
    tone: 'orange'
  },
  {
    title: '本周更新',
    value: 32,//HACK
    unit: '条',
    icon: '↗',
    tone: 'green',
    extra: '较上周 ↑18%'
  }
])

</script>

<style scoped src="@/styles/card-list.css"></style>
<style>
.filter-panel {
  display: grid;
  grid-template-columns: minmax(280px, 1.7fr) 220px 160px 110px 90px 90px;
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

.sort-btn:hover {
  color: #16a34a;
  border-color: #16a34a;
}

.sort-arrow {
  margin-left: 4px;
}
</style>