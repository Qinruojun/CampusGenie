<script setup>
import { useHotQuestions } from '@/composables/useHotQuestions.js'

import {onMounted, ref} from "vue";
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '大家都在问什么？'
  },
  desc: {
    type: String,
    default: '根据近期查询频次整理，帮助你快速找到常见答案。'
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
    default: 5 * 60 * 1000
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


function getTrendText(trend) {
  if (trend === 'up') return '上升'
  if (trend === 'down') return '下降'
  if (trend === 'flat') return '持平'
  return '-'
}

function getTrendClass(trend) {
  if (trend === 'up') return 'trend-up'
  if (trend === 'down') return 'trend-down'
  if (trend === 'flat') return 'trend-flat'
  return ''
}



</script>

<template>
  <main class="page hot-page">
    <p class="eyebrow">{{ eyebrow }}</p>
    <h1 class="page-title">
      <span class="title-icon">🔥</span>
      {{ title }}</h1>
    <p class="page-desc">{{ desc }}</p>

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
      <ol class="hot-list">
        <li v-for="item in list" :key="item.id">
          <RouterLink :to="{name: 'qa-result',
                            query:{
                              q:item.question,
                              answer:item.answer,
                            }
          }"
          >
            <span class="rank">{{ item.rank }}</span>
            <strong>{{ item.question }}</strong>
            <small>{{ item.queryCount }} 次浏览</small>
            <span
class="trend-tag"
:class="getTrendClass(item.trend)">
              <svg
v-if="item.trend === 'up'"
viewBox="0 0 24 24"
aria-hidden="true">
                <path d="M7 17 17 7" />
                <path d="M9 7h8v8" />
              </svg>

  <svg v-else-if="item.trend === 'down'"
viewBox="0 0 24 24" aria-hidden="true">
    <path d="M7 7 17 17" />
    <path d="M9 17h8V9" />
  </svg>

  <svg v-else viewBox="0 0 24 24" aria-hidden="true">
    <path d="M5 12h14" />
  </svg>

  <span>{{ getTrendText(item.trend) }}</span>
</span>
          </RouterLink>
        </li>
      </ol>
    </section>
  </main>
</template>

<style scoped>
.page {
  width: min(980px, calc(100% - 48px));
  margin: 0 auto;
  padding: 70px 0 96px;
}

.hot-page {
  max-width: 900px;

  text-align: center;
}

.hot-card {
  margin-top: 42px;
  text-align: left;
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

small {
  color: var(--muted);
}

@media (max-width: 560px) {
  .hot-list a {
    grid-template-columns: 34px 1fr;
  }

  small {
    grid-column: 2;
  }
}

/* 按钮右上角定位 */
.refresh-btn.primary-btn {
  position: fixed;
  top: 20px;    /* 距离顶部距离 */
  right: 20px;  /* 距离右侧距离 */
  z-index: 999; /* 确保在最上层，不被挡住 */
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
