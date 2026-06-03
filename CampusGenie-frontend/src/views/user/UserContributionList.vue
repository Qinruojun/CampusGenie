<!--用户贡献页，查看自己的贡献-->
<template>
  <main class="contribution-page">
    <section class="page-head">
      <p class="eyebrow">CampusGenie</p>
      <h1>我的贡献</h1>
      <p class="desc">
        查看你提交过的校园问答内容，按提交时间从近到远排列。
      </p>
    </section>

    <section class="toolbar">
      <div>
        <strong>{{ total }}</strong>
        <span> 条贡献记录</span>
      </div>

      <button class="refresh-btn" @click="fetchContributions">
        刷新
      </button>
    </section>

    <section class="content-card">
      <div v-if="loading" class="state">
        正在加载你的贡献记录...
      </div>

      <div v-else-if="errorMessage" class="state error">
        {{ errorMessage }}
      </div>

      <div v-else-if="contributions.length === 0" class="state">
        暂时还没有提交过贡献。
      </div>

      <div v-else class="contribution-list">
        <article
            v-for="item in contributions"
            :key="item.id"
            class="contribution-item"
        >
          <div class="item-top">
            <div>
              <h3>{{ item.question }}</h3>
              <p class="time">提交时间：{{ item.createdTime }}</p>
            </div>

            <span :class="['status', getStatusClass(item.statusDesc)]">
              {{ item.statusDesc }}
            </span>
          </div>

          <p class="answer">
            {{ item.answer }}
          </p>

          <div class="meta">
            <span>所属类别：{{ item.categoryName || '未分类' }}</span>

            <span v-if="item.reviewedTime">
              审核时间：{{ item.reviewedTime }}
            </span>
          </div>

          <p v-if="item.rejectReason" class="reject-reason">
            驳回原因：{{ item.rejectReason }}
          </p>
        </article>
      </div>
    </section>

    <section class="pagination" v-if="totalPages > 1">
      <button :disabled="page <= 1" @click="changePage(page - 1)">
        上一页
      </button>

      <span>
        第 {{ page }} / {{ totalPages }} 页
      </span>

      <button :disabled="page >= totalPages" @click="changePage(page + 1)">
        下一页
      </button>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'

const contributions = ref([])
const total = ref(0)

const page = ref(1)
const pageSize = ref(6)

const loading = ref(false)
const errorMessage = ref('')

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(total.value / pageSize.value))
})

function getToken() {
  return localStorage.getItem('token') || localStorage.getItem('userToken') || ''
}

function parseTime(timeText) {
  if (!timeText) return 0

  // 兼容 "2026-06-02 10:30:00" 这种格式
  return new Date(String(timeText).replace(' ', 'T')).getTime()
}

function sortByNewest(records) {
  return [...records].sort((a, b) => {
    return parseTime(b.createdTime) - parseTime(a.createdTime)
  })
}

function getStatusClass(statusDesc) {
  if (statusDesc === '已通过') return 'approved'
  if (statusDesc === '已驳回') return 'rejected'
  return 'pending'
}

async function fetchContributions() {
  loading.value = true
  errorMessage.value = ''

  try {
    const token = getToken()

    const params = new URLSearchParams({
      page: String(page.value),
      pageSize: String(pageSize.value),
      sortOrder: 'desc'
    })

    const response = await fetch(`/user/contributions/page?${params}`, {
      method: 'GET',
      headers: token
          ? {
            token
          }
          : {}
    })

    if (!response.ok) {
      throw new Error(`请求失败：${response.status}`)
    }

    const result = await response.json()

    if (result.code !== 1) {
      throw new Error(result.msg || '查询失败')
    }

    const records = result.data?.records || []

    // 后端如果已经按时间倒序返回，这里不会改变结果；
    // 如果后端没有排序，这里前端再兜底排序一次。
    contributions.value = sortByNewest(records)
    total.value = Number(result.data?.total || 0)
  } catch (error) {
    console.error(error)
    errorMessage.value = '贡献记录加载失败，请稍后重试。'
  } finally {
    loading.value = false
  }
}

function changePage(targetPage) {
  if (targetPage < 1 || targetPage > totalPages.value) return

  page.value = targetPage
  fetchContributions()
}

onMounted(() => {
  fetchContributions()
})
</script>

<style scoped>
.contribution-page {
  width: 100%;
  min-height: calc(100vh - 72px);
  padding: 72px 10vw 96px;
  background: #f8f7f2;
  color: #1c241c;
}

.page-head {
  text-align: center;
  margin-bottom: 48px;
}

.eyebrow {
  margin: 0 0 12px;
  color: #219c55;
  font-size: 18px;
  font-weight: 700;
}

.page-head h1 {
  margin: 0;
  font-size: 56px;
  line-height: 1.1;
  font-weight: 600;
  color: #1d281d;
}

.desc {
  max-width: 560px;
  margin: 18px auto 0;
  color: #7c7a72;
  font-size: 16px;
  line-height: 1.8;
}

.toolbar {
  max-width: 960px;
  margin: 0 auto 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #68665f;
}

.toolbar strong {
  color: #219c55;
  font-size: 22px;
}

.refresh-btn {
  border: 1px solid #d9d7cf;
  background: #ffffff;
  color: #1d281d;
  border-radius: 999px;
  padding: 10px 22px;
  cursor: pointer;
}

.refresh-btn:hover {
  border-color: #219c55;
  color: #219c55;
}

.content-card {
  max-width: 960px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #e3e1da;
  border-radius: 22px;
  padding: 12px;
  box-shadow: 0 22px 70px rgba(31, 40, 31, 0.06);
}

.state {
  padding: 64px 20px;
  text-align: center;
  color: #88867e;
}

.state.error {
  color: #b94848;
}

.contribution-list {
  display: flex;
  flex-direction: column;
}

.contribution-item {
  padding: 26px 28px;
  border-radius: 18px;
}

.contribution-item + .contribution-item {
  border-top: 1px solid #eeeeea;
}

.contribution-item:hover {
  background: #fbfaf7;
}

.item-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.item-top h3 {
  margin: 0;
  color: #1d281d;
  font-size: 20px;
  font-weight: 700;
}

.time {
  margin: 8px 0 0;
  color: #96938b;
  font-size: 14px;
}

.status {
  flex: 0 0 auto;
  border-radius: 999px;
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 700;
}

.status.pending {
  background: #f5f2e8;
  color: #9a7a2f;
}

.status.approved {
  background: #eaf6ef;
  color: #219c55;
}

.status.rejected {
  background: #f8ecea;
  color: #b94848;
}

.answer {
  margin: 18px 0 0;
  color: #3a4038;
  line-height: 1.8;
}

.meta {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #8b887f;
  font-size: 14px;
}

.reject-reason {
  margin: 18px 0 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #fff6f4;
  color: #b94848;
  line-height: 1.7;
}

.pagination {
  max-width: 960px;
  margin: 26px auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
}

.pagination button {
  border: 1px solid #d9d7cf;
  background: #ffffff;
  color: #1d281d;
  border-radius: 999px;
  padding: 10px 22px;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  border-color: #219c55;
  color: #219c55;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination span {
  color: #77746d;
}

@media (max-width: 768px) {
  .contribution-page {
    padding: 48px 20px 72px;
  }

  .page-head h1 {
    font-size: 40px;
  }

  .item-top {
    flex-direction: column;
  }

  .toolbar {
    flex-direction: column;
    gap: 16px;
  }
}
</style>