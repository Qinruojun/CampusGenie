<!-- 

<template>
  <main class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="brand" to="/">CampusGenie</RouterLink>
      <nav class="admin-menu">
        <RouterLink to="/admin/knowledge">知识库管理</RouterLink>
        <RouterLink to="/admin/audit">审核管理</RouterLink>
        <RouterLink to="/">返回首页</RouterLink>
      </nav>
    </aside>

    <section class="admin-main">
      <div class="admin-titlebar">
        <div>
          <p class="eyebrow">Admin</p>
          <h1>知识库列表</h1>
        </div>
        <button class="primary-btn">+ 新增知识</button>
      </div>

      <div class="card panel">
        <table class="table">
          <thead>
            <tr>
              <th>标题</th>
              <th>分类</th>
              <th>更新时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in knowledgeItems" :key="item.id">
              <td>{{ item.title }}</td>
              <td>{{ item.category }}</td>
              <td>{{ item.updatedAt }}</td>
              <td><a href="#">编辑</a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>
<script setup>
import { knowledgeItems } from '../../data/mockData'
</script> -->



<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pageKnowledge, deleteKnowledge } from '@/api/admin/knowledge'
import { getCategoryList } from '@/api/admin/category'

const router = useRouter()

const queryForm = ref({
  keyword: '',
  categoryId: '',
  status: ''
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref([])
const categoryList = ref([])
const loading = ref(false)

const loadCategoryList = async () => {
  const res = await getCategoryList()
  if (res.code === 1 || res.code === 200) {
    categoryList.value = res.data || []
  }
}

const loadKnowledgeList = async () => {
  loading.value = true

  try {
    const res = await pageKnowledge({
      page: page.value,
      pageSize: pageSize.value,
      keyword: queryForm.value.keyword,
      categoryId: queryForm.value.categoryId || null,
      status: queryForm.value.status === '' ? null : queryForm.value.status
    })

    if (res.code === 1 || res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      alert(res.msg || '查询失败')
    }
  } catch (error) {
    console.error(error)
    alert('服务器异常，查询失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadKnowledgeList()
}

const handleReset = () => {
  queryForm.value = {
    keyword: '',
    categoryId: '',
    status: ''
  }
  page.value = 1
  loadKnowledgeList()
}

const handleAdd = () => {
  router.push('/admin/knowledge/add')
}

const handleEdit = (id) => {
  router.push(`/admin/knowledge/edit/${id}`)
}

const handleDelete = async (id) => {
  const ok = confirm('确定要删除这条知识吗？')
  if (!ok) return

  const res = await deleteKnowledge(id)

  if (res.code === 1 || res.code === 200) {
    alert(res.msg || '删除成功')
    loadKnowledgeList()
  } else {
    alert(res.msg || '删除失败')
  }
}

const handlePrevPage = () => {
  if (page.value > 1) {
    page.value--
    loadKnowledgeList()
  }
}

const handleNextPage = () => {
  const maxPage = Math.ceil(total.value / pageSize.value)

  if (page.value < maxPage) {
    page.value++
    loadKnowledgeList()
  }
}

onMounted(() => {
  loadCategoryList()
  loadKnowledgeList()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>知识库管理</h2>
      <button class="primary-btn" @click="handleAdd">新增知识</button>
    </div>

    <div class="search-card">
      <input
        v-model="queryForm.keyword"
        placeholder="请输入问题关键词"
        @keyup.enter="handleSearch"
      />

      <select v-model="queryForm.categoryId">
        <option value="">全部分类</option>
        <option
          v-for="item in categoryList"
          :key="item.id"
          :value="item.id"
        >
          {{ item.name }}
        </option>
      </select>

      <select v-model="queryForm.status">
        <option value="">全部状态</option>
        <option :value="1">已发布</option>
        <option :value="0">已停用</option>
      </select>

      <button @click="handleSearch">搜索</button>
      <button @click="handleReset">重置</button>
    </div>

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>问题</th>
            <th>分类</th>
            <th>来源</th>
            <th>状态</th>
            <th>更新时间</th>
            <th width="180">操作</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="loading">
            <td colspan="7">加载中...</td>
          </tr>

          <tr v-else-if="list.length === 0">
            <td colspan="7">暂无数据</td>
          </tr>

          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id }}</td>
            <td class="question-cell">{{ item.question }}</td>
            <td>{{ item.categoryName }}</td>
            <td>{{ item.source }}</td>
            <td>
              <span v-if="item.status === 1">已发布</span>
              <span v-else>已停用</span>
            </td>
            <td>{{ item.updatedTime }}</td>
            <td>
              <button @click="handleEdit(item.id)">编辑</button>
              <button class="danger-btn" @click="handleDelete(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button :disabled="page <= 1" @click="handlePrevPage">上一页</button>

        <span>第 {{ page }} 页</span>
        <span>共 {{ total }} 条</span>

        <button :disabled="page >= Math.ceil(total / pageSize)" @click="handleNextPage">
          下一页
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
  margin-bottom: 18px;
}

.page-header h2 {
  margin: 0;
}

.search-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: 18px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
}

.search-card input,
.search-card select {
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.search-card input {
  width: 260px;
}

button {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.primary-btn {
  background: #1677ff;
  color: white;
  border-color: #1677ff;
}

.danger-btn {
  margin-left: 8px;
  color: #d93025;
}

.table-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 12px 10px;
  border-bottom: 1px solid #eee;
  text-align: left;
}

.question-cell {
  max-width: 360px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
}
</style>