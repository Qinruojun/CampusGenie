<template>
  <div class="stat-card">
    <div class="stat-icon" :class="tone">
      {{ icon }}
    </div>

    <div>
      <p>{{ title }}</p>

      <strong :class="textClass">
        {{ value }}
      </strong>

      <span>{{ unit }}</span>

      <em v-if="extra">{{ extra }}</em>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {//标题
    type: String,
    required: true
  },
  value: {//值
    type: [String, Number],
    default: 0
  },
  unit: {//单位
    type: String,
    default: '条'
  },
  icon: {
    type: String,
    default: '▧'
  },
  tone: {
    type: String,
    default: 'green'
  },
  extra: {
    type: String,
    default: ''
  }
})

const textClass = computed(() => {
  if (props.tone === 'green') return 'green-text'
  if (props.tone === 'orange') return 'orange-text'
  return ''
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 22px;
  min-height: 92px;
  padding: 0 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  font-size: 28px;
}

.stat-icon.green {
  color: #16a34a;
  background: #e9f9ef;
}

.stat-icon.orange {
  color: #f59e0b;
  background: #fff4e5;
}

.stat-card p {
  margin: 0 0 4px;
  color: #6b7280;
  font-weight: 600;
}

.stat-card strong {
  font-size: 26px;
  color: #111827;
}

.stat-card span {
  margin-left: 6px;
  color: #6b7280;
  font-size: 13px;
}

.stat-card em {
  margin-left: 18px;
  color: #16a34a;
  font-size: 13px;
  font-style: normal;
  font-weight: 700;
}

.green-text {
  color: #16a34a !important;
}

.orange-text {
  color: #f59e0b !important;
}
</style>