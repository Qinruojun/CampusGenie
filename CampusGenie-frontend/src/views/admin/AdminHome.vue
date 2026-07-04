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
              <span class="nav-icon"><ShieldIcon /></span>
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
import ShieldIcon from '@/components/icons/ShieldIcon.vue'
import { getStatistics as getKnowledgeStatistics } from '@/api/admin/knowledge.js'
import { getStatistics as getContributionStatistics } from '@/api/admin/contirbute.js'
import { getStatistics as getDraftStatistics } from '@/api/admin/knowledgeDraft.js'
import { adminGetHotlist } from '@/api/admin/hotQuestion.js'
import { getRecentLogs } from '@/api/admin/adminLog.js'
import { SUCCESS } from '@/constants/code.js'

const router = useRouter()

const knowledgeCount = ref(0)
const pendingCount = ref(0)
const hotCount = ref(0)
const draftCount = ref(0)

const recentActivity = ref([])

function formatActivity(log) {
  const iconMap = {
    INSERT: {
      knowledge_base: '📚',
      knowledge_draft: '📝',
      contribution: '✍️',
      category: '📁'
    },
    UPDATE: {
      knowledge_base: '✏️',
      knowledge_draft: '✏️',
      category: '✏️'
    },
    DELETE: {
      knowledge_base: '🗑️',
      knowledge_draft: '🗑️'
    },
    REVIEW_PASS: {
      contribution: '✓',
      knowledge_draft: '✓'
    },
    REVIEW_REJECT: {
      contribution: '✗',
      knowledge_draft: '✗'
    },
    DISABLE: {
      knowledge_base: '🔒'
    },
    ENABLE: {
      knowledge_base: '🔓'
    },
    BATCH_IMPORT: {
      knowledge_base: '⬆️'
    },
    BATCH_DELETE: {
      knowledge_base: '🗑️'
    }
  }

  const contentMap = {
    INSERT: {
      knowledge_base: '创建了知识条目',
      knowledge_draft: '创建了知识草稿',
      contribution: '提交了知识贡献',
      category: '创建了分类'
    },
    UPDATE: {
      knowledge_base: '更新了知识条目',
      knowledge_draft: '编辑了知识草稿',
      category: '更新了分类'
    },
    DELETE: {
      knowledge_base: '删除了知识条目',
      knowledge_draft: '删除了知识草稿'
    },
    REVIEW_PASS: {
      contribution: '审核通过了一条知识贡献',
      knowledge_draft: '审核通过了知识草稿'
    },
    REVIEW_REJECT: {
      contribution: '驳回了知识贡献',
      knowledge_draft: '驳回了知识草稿'
    },
    DISABLE: {
      knowledge_base: '停用了知识条目'
    },
    ENABLE: {
      knowledge_base: '启用了知识条目'
    },
    BATCH_IMPORT: {
      knowledge_base: '批量导入了知识条目'
    },
    BATCH_DELETE: {
      knowledge_base: '批量删除了知识条目'
    }
  }

  const actionType = String(log.actionType)
  const targetType = log.targetType || ''
  const detail = log.detail || ''
  const createdTime = log.createdTime || log.created_time

  const actionMapping = {
    '1': 'REVIEW_PASS',
    '2': 'REVIEW_REJECT'
  }
  const normalizedActionType = actionMapping[actionType] || actionType

  const icon = iconMap[normalizedActionType]?.[targetType] || '📋'
  const content = contentMap[normalizedActionType]?.[targetType] || `执行了操作: ${actionType}`

  return {
    id: log.id,
    icon,
    content,
    time: formatTime(createdTime)
  }
}

function formatTime(dateValue) {
  if (!dateValue) return ''
  let date
  if (Array.isArray(dateValue)) {
    date = new Date(dateValue[0], dateValue[1] - 1, dateValue[2], dateValue[3], dateValue[4], dateValue[5])
  } else {
    date = new Date(String(dateValue).replace('T', ' '))
  }
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const goTo = (path) => {
  router.push(path)
}

const loadStatistics = async () => {
  try {
    const [knowledgeRes, contributionRes, draftRes, hotRes, logsRes] = await Promise.all([
      getKnowledgeStatistics(),
      getContributionStatistics(),
      getDraftStatistics(),
      adminGetHotlist(),
      getRecentLogs({ limit: 10 })
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

    if (logsRes.code === SUCCESS && logsRes.data) {
      console.log('Admin logs:', logsRes.data)
      console.log('Log count:', logsRes.data.length)
      recentActivity.value = logsRes.data.map(formatActivity)
    } else {
      console.log('Logs response:', logsRes)
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon svg {
  width: 32px;
  height: 32px;
  color: #374151;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
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