<script setup>
import { useHotQuestions } from '@/composables/useHotQuestions.js'

const props = defineProps({
  title: {
    type: String,
    default: '大家都在问什么？'
  },
  desc: {
    type: String,
    default: '根据近期查询频次整理，帮助你快速找到常见答案。'
  },
  homeLinkTo: {
    type: String,
    default: ''
  },
  homeLinkText: {
    type: String,
    default: '← 返回首页'
  },
  resultFrom: {
    type: String,
    default: 'hot'
  },
  eyebrow: {
    type: String,
    default: '热点问题'
  },

  // 是否显示手动刷新按钮
  showRefresh: {
    type: Boolean,
    default: false
  },

  // 是否显示统计卡片
  showStats: {
    type: Boolean,
    default: false
  },

  // 是否显示趋势文字：上升 / 下降 / 持平
  showTrendText: {
    type: Boolean,
    default: true
  },

  // 是否显示标题前面的火焰
  showTitleIcon: {
    type: Boolean,
    default: true
  },

  // 是否显示前 3 名火焰标识
  showHotMark: {
    type: Boolean,
    default: true
  },

  // 是否开启轮询
  polling: {
    type: Boolean,
    default: true
  },

  // 轮询间隔，默认 5 分钟
  pollingInterval: {
    type: Number,
    default: 10 * 60 * 1000
  }
})

const {
  list,
  loading,
  errorMessage,
  hasData,
  totalQueryCount,
  topQuestion,
  refreshHotQuestions
} = useHotQuestions({
  autoLoad: true,
  polling: props.polling,
  pollingInterval: props.pollingInterval
})

</script>

<template>
  <div class="page-bg">
    <main class="page hot-page">
      <RouterLink v-if="homeLinkTo" class="back-link" :to="homeLinkTo">
        {{ homeLinkText }}
      </RouterLink>

      <p class="eyebrow">{{ eyebrow }}</p>
      <h1 class="page-title">
        <span v-if="showTitleIcon" class="title-icon">🔥</span>
        {{ title }}
      </h1>
      <p class="page-desc">{{ desc }}</p>

      <section v-if="showStats" class="stats-grid" aria-label="热点问题统计">
        <article class="stat-card">
          <span>热点问题数</span>
          <strong>{{ list.length }}</strong>
        </article>
        <article class="stat-card">
          <span>总浏览次数</span>
          <strong>{{ totalQueryCount }}</strong>
        </article>
        <article class="stat-card">
          <span>最高频问题</span>
          <strong>{{ topQuestion?.question || '-' }}</strong>
        </article>
      </section>

      <button
          v-if="showRefresh"
          class="primary-btn refresh-btn"
          :disabled="loading"
          @click="refreshHotQuestions"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M21 12a9 9 0 1 1-3-6.7" />
          <path d="M21 3v6h-6" />
        </svg>
        {{ loading ? '刷新中...' : '刷新榜单' }}
      </button>
      <section class="card panel hot-card">
        <p v-if="loading" class="state-text">热点问题加载中...</p>
        <p v-else-if="errorMessage" class="state-text error-text">{{ errorMessage }}</p>
        <p v-else-if="!hasData" class="state-text">暂无热点问题数据</p>
        <ol v-else class="hot-list">
          <li v-for="item in list" :key="`${item.rank}-${item.question}`">
            <RouterLink :to="{name: 'qa-result',
                              query:{
                                q:item.question,
                                answer:item.answer,
                                from: resultFrom,
                              }
            }"
            >
              <span class="rank">{{ item.rank }}</span>
              <span class="hot-content">
                <strong>
                  <span v-if="showHotMark && item.rank <= 3" class="hot-mark">🔥</span>
                  {{ item.question }}
                </strong>
                <span class="hot-answer">{{ item.answer || '暂无答案' }}</span>
              </span>
              <small>{{ item.queryCount }} 次浏览</small>
            </RouterLink>
          </li>
        </ol>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page-bg {
  margin: -28px -36px;
  padding: 28px 36px;
  min-height: calc(100vh - 72px);
  background: radial-gradient(circle at top, #ffffff 0%, #fbfaf7 56%, #f7f5ef 100%);
}

.hot-page {
  display: block;
  width: min(980px, calc(100% - 48px));
  margin: 0 auto;
  padding: 70px 0 96px;
  max-width: 900px;
  text-align: center;
}

.back-link {
  display: inline-block;
  margin-bottom: 28px;
  color: var(--muted);
  font-weight: 700;
}

.back-link:hover {
  color: var(--green);
}

.hot-card {
  margin-top: 42px;
  text-align: left;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 28px;
}

.stat-card {
  padding: 18px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 90px;
}

.stat-card span {
  display: block;
  color: var(--muted);
  font-size: 13px;
  margin-bottom: 8px;
}

.stat-card strong {
  display: block;
  color: var(--text);
  font-size: 20px;
  line-height: 1.4;
  word-break: break-word;
}

.state-text {
  margin: 0;
  padding: 24px 12px;
  color: var(--muted);
  text-align: center;
}

.error-text {
  color: #c2410c;
}

.hot-list {
  display: grid;
  gap: 4px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.hot-list a {
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 16px 12px;
  border-radius: 12px;
}

.hot-list a:hover {
  background: var(--soft-gray);
}

.rank {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(35, 157, 83, 0.1);
  color: var(--green);
  font-weight: 800;
}

.hot-content {
  min-width: 0;
  display: grid;
  gap: 6px;
}

.hot-content strong,
.hot-answer {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-answer {
  color: var(--muted);
  font-size: 13px;
}

small {
  color: var(--muted);
}

.hot-mark {
  margin-right: 6px;
}

@media (max-width: 560px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .hot-list a {
    grid-template-columns: 34px 1fr;
  }

  small {
    grid-column: 2;
  }
}

.refresh-btn.primary-btn {
  margin-top: 24px;
}

.refresh-btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.refresh-btn svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}
</style>
