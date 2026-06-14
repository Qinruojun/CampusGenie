<template>
  <div class="page">
    <aside class="sidebar">
      <div class="brand">CampusGenie</div>

      <nav class="menu">
        <div class="menu-item">⌂ 首页概览</div>
        <div class="menu-item">▣ 问答管理</div>
        <div class="menu-item active">▤ 知识库管理</div>
        <div class="menu-item">◇ 知识分类</div>
        <div class="menu-item">▥ 数据统计</div>
        <div class="menu-item">♙ 用户管理</div>
        <div class="menu-item">♢ 通知公告</div>
        <div class="menu-item">⚙ 系统设置</div>
      </nav>

      <div class="tip">
        <div class="tip-title">💡 小贴士</div>
        <p>优质的知识库能显著提升 AI 回答的准确性和用户体验。</p>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <button class="icon-btn">☰</button>

        <div class="user-area">
          <div class="bell">
            🔔
            <span>3</span>
          </div>
          <div class="avatar">👨🏻‍💼</div>
          <div>
            <div class="user-name">管理员</div>
            <div class="role">校园管理员</div>
          </div>
          <span class="arrow">⌄</span>
        </div>
      </header>

      <section class="content">
        <div class="page-head">
          <div>
            <h1>知识库管理</h1>
            <p>管理和维护校园知识库，支持知识条目的增删改查与发布状态管理。</p>
          </div>

          <div class="head-actions">
            <button class="btn ghost">⇧ 批量导入</button>
            <button class="btn primary">＋ 新增知识</button>
          </div>
        </div>

        <div class="filter-panel">
          <div class="search">
            <span>⌕</span>
            <input v-model="keyword" placeholder="请输入问题关键词 / 知识条目ID" />
          </div>

          <select v-model="category">
            <option value="">全部分类</option>
            <option value="图书馆">图书馆</option>
            <option value="教务相关">教务相关</option>
            <option value="校园卡">校园卡</option>
            <option value="宿舍生活">宿舍生活</option>
          </select>

          <select v-model="status">
            <option value="">全部状态</option>
            <option value="published">已发布</option>
            <option value="pending">待审核</option>
            <option value="disabled">已停用</option>
          </select>

          <button class="btn primary small">搜索</button>
          <button class="btn ghost small" @click="resetFilter">重置</button>
        </div>

        <div class="stats">
          <div class="stat-card">
            <div class="stat-icon green">▧</div>
            <div>
              <p>知识总数</p>
              <strong>1,256</strong>
              <span>条</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon green">➤</div>
            <div>
              <p>已发布</p>
              <strong class="green-text">1,102</strong>
              <span>条</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon orange">◷</div>
            <div>
              <p>待审核/已停用</p>
              <strong class="orange-text">154</strong>
              <span>条</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon green">↗</div>
            <div>
              <p>本周更新</p>
              <strong>32</strong>
              <span>条</span>
              <em>较上周 ↑18%</em>
            </div>
          </div>
        </div>

        <div class="card-list">
          <KnowledgeCard
              v-for="item in knowledgeList"
              :key="item.id"
              :item="item"
              @view="handleView"
              @edit="handleEdit"
              @toggle="handleToggle"
              @delete="handleDelete"
          />
        </div>

        <div class="pagination">
          <button>上一页</button>
          <button class="current">1</button>
          <button>2</button>
          <button>3</button>
          <button>下一页</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import KnowledgeCard from '@/components/admin/Card/KnowledgeCard.vue'

const keyword = ref('')
const category = ref('')
const status = ref('')

const knowledgeList = ref([
  {
    id: 'KB1001',
    title: '图书馆闭馆时间是什么？',
    answer: '图书馆平时开放时间为8:00-22:00，考试周延长至23:00，寒暑假时间另行通知。',
    category: '图书馆',
    source: '图书馆官网',
    status: 'published',
    updatedAt: '2024-05-20 11:30',
    updatedBy: '张老师'
  },
  {
    id: 'KB1002',
    title: '如何查看课表？',
    answer: '登录教务系统（jw.campus.edu.cn），进入“学生服务”-“课表查询”即可查看个人课表。',
    category: '教务相关',
    source: '教务处',
    status: 'published',
    updatedAt: '2024-05-19 09:15',
    updatedBy: '李老师'
  },
  {
    id: 'KB1003',
    title: '校园卡如何挂失？',
    answer: '可通过“校园卡服务大厅”小程序或到学生服务中心现场办理挂失，挂失后原卡立即停用。',
    category: '校园卡',
    source: '学生服务中心',
    status: 'pending',
    updatedAt: '2024-05-18 16:40',
    updatedBy: '王老师'
  },
  {
    id: 'KB1004',
    title: '宿舍报修流程是什么？',
    answer: '登录“后勤服务平台”或“企业微信-报修平台”提交报修单，维修人员会尽快联系处理。',
    category: '宿舍生活',
    source: '后勤维修中心',
    status: 'disabled',
    updatedAt: '2024-05-17 14:25',
    updatedBy: '赵老师'
  },
  {
    id: 'KB1005',
    title: '食堂几点关门？',
    answer: '第一食堂营业时间为7:00-20:30，第二食堂营业时间为7:00-20:00，夜宵窗口营业至22:00。',
    category: '餐饮服务',
    source: '后勤饮食中心',
    status: 'published',
    updatedAt: '2024-05-16 10:05',
    updatedBy: '陈老师'
  }
])

const resetFilter = () => {
  keyword.value = ''
  category.value = ''
  status.value = ''
}

const handleView = item => {
  console.log('查看详情', item)
}

const handleEdit = item => {
  console.log('编辑', item)
}

const handleToggle = item => {
  console.log('切换状态', item)
}

const handleDelete = item => {
  console.log('删除', item)
}
</script>

<style scoped src="@/styles/card-list.css">

</style>