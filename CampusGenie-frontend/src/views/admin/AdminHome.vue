<template>
  <div class="page">
    <main class="main">
      <section class="content">
        <div class="page-head">
          <div>
            <h1>管理控制台</h1>
            <p>欢迎回来，管理员。查看系统概览和快速导航。</p>
          </div>
        </div>

        <div class="stats">
          <StatCard
            title="知识条目"
            :value="knowledgeCount"
            unit="条"
            icon="▧"
            tone="green"
          />
          <StatCard
            title="待审核贡献"
            :value="pendingCount"
            unit="条"
            icon="⏳"
            tone="orange"
          />
          <StatCard
            title="热点问题"
            :value="hotCount"
            unit="个"
            icon="🔥"
            tone="red"
          />
          <StatCard
            title="知识草稿"
            :value="draftCount"
            unit="条"
            icon="📝"
            tone="blue"
          />
        </div>

        <div class="quick-nav">
          <h2>快速导航</h2>
          <div class="nav-grid">
            <button class="nav-item" @click="goTo('/admin/knowledge')">
              <span class="nav-icon">📚</span>
              <span class="nav-text">知识库管理</span>
            </button>
            <button class="nav-item" @click="goTo('/admin/knowledgeDraft')">
              <span class="nav-icon">📝</span>
              <span class="nav-text">知识草稿</span>
            </button>
            <button class="nav-item" @click="goTo('/admin/audit')">
              <span class="nav-icon">✓</span>
              <span class="nav-text">审核管理</span>
            </button>
            <button class="nav-item" @click="goTo('/admin/hotQuestion')">
              <span class="nav-icon">🔥</span>
              <span class="nav-text">热点问题</span>
            </button>
          </div>
        </div>

        <div class="recent-activity">
          <h2>最近动态</h2>
          <div class="activity-list">
            <div class="activity-item" v-for="item in recentActivity" :key="item.id">
              <span class="activity-icon">{{ item.icon }}</span>
              <div class="activity-content">
                <p>{{ item.content }}</p>
                <span class="activity-time">{{ item.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import StatCard from '@/components/StatCard.vue'
import { getStatistics as getKnowledgeStatistics } from '@/api/admin/knowledge.js'
import { getStatistics as getContributionStatistics } from '@/api/admin/contirbute.js'
import { getStatistics as getDraftStatistics } from '@/api/admin/knowledgeDraft.js'
import { adminGetHotlist } from '@/api/admin/hotQuestion.js'
import { SUCCESS } from '@/constants/code.js'

const router = useRouter()

const knowledgeCount = ref(0)
const pendingCount = ref(0)
const hotCount = ref(0)
const draftCount = ref(0)

const recentActivity = ref([
  { id: 1, icon: '✓', content: '审核通过了一条知识贡献', time: '10分钟前' },
  { id: 2, icon: '📝', content: '创建了知识草稿：如何申请助学金', time: '30分钟前' },
  { id: 3, icon: '🗑️', content: '删除了已停用的知识条目', time: '1小时前' },
  { id: 4, icon: '⬆️', content: '批量导入了15条知识条目', time: '2小时前' },
  { id: 5, icon: '🔥', content: '更新了热点问题列表', time: '3小时前' }
])

const goTo = (path) => {
  router.push(path)
}

const loadStatistics = async () => {
  try {
    const [knowledgeRes, contributionRes, draftRes, hotRes] = await Promise.all([
      getKnowledgeStatistics(),
      getContributionStatistics(),
      getDraftStatistics(),
      adminGetHotlist()
    ])

    if (knowledgeRes.code === SUCCESS && knowledgeRes.data) {
      knowledgeCount.value = (knowledgeRes.data.publishedCount || 0) + (knowledgeRes.data.stoppedCount || 0)
    }

    if (contributionRes.code === SUCCESS && contributionRes.data) {
      pendingCount.value = contributionRes.data.pendingCount || 0
    }

    if (draftRes.code === SUCCESS && draftRes.data) {
      draftCount.value = (draftRes.data.pendingCount || 0) + (draftRes.data.approvedCount || 0)
    }

    if (hotRes.code === SUCCESS && hotRes.data) {
      hotCount.value = Array.isArray(hotRes.data) ? hotRes.data.length : 0
    }
  } catch (error) {
    console.error('加载首页统计数据失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.page-head {
  margin-bottom: 24px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.quick-nav {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
  margin-bottom: 24px;
}

.quick-nav h2 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #111827;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #eff6ff;
  border-color: #3b82f6;
  transform: translateY(-2px);
}

.nav-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.nav-text {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
}

.recent-activity {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.recent-activity h2 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #111827;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  border-radius: 50%;
  font-size: 14px;
  margin-right: 12px;
}

.activity-content {
  flex: 1;
}

.activity-content p {
  margin: 0 0 4px;
  font-size: 14px;
  color: #374151;
}

.activity-time {
  font-size: 12px;
  color: #9ca3af;
}

@media (max-width: 900px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .nav-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>