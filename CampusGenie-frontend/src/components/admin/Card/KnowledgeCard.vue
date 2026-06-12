<template>
  <article class="knowledge-card">
    <div class="main-info">
      <h3>{{ item.question }}</h3>

      <p class="answer">
        答案预览：{{ item.answer }}
      </p>

      <div class="meta">
        <span>◇ 分类：{{ item.category }}</span>
        <span>▣ 来源：{{ item.source }}</span>
        <span>▤ ID：{{ item.id }}</span>
      </div>
    </div>

    <div class="status-info">
      <span class="tag" :class="statusClass">
        {{ statusText }}
      </span>

      <p>更新时间：{{ item.updatedAt }}</p>
      <p>更新人：{{ item.updatedBy }}</p>
    </div>

    <div class="actions">
      <button
        class="btn toggle"
        :class="toggleButtonClass"
        @click="$emit('toggle', item)"
      >
        {{ toggleButtonText }}
      </button>

      <button class="btn edit" @click="$emit('edit', item)">
        编辑
      </button>

      <button class="btn delete" @click="$emit('delete', item)">
        删除
      </button>

      <button class="detail" @click="$emit('view', item)">
        查看详情 ›
      </button>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { ENABLE, DISABLE } from '@/constants/status.js'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'edit', 'toggle', 'delete'])

const statusMap = {
  [ENABLE]: '已发布',
  [DISABLE]: '已停用'
}

const statusText = computed(() => {
  const status = Number(props.item.status)
  return statusMap[status] || '未知'
})

const statusClass = computed(() => {
  const status = Number(props.item.status)
  return status === ENABLE ? 'enabled' : 'disabled'
})

const toggleButtonText = computed(() => {
  const status = Number(props.item.status)
  return status === ENABLE ? '停用' : '启用'
})

const toggleButtonClass = computed(() => {
  const status = Number(props.item.status)
  return status === ENABLE ? 'btn-stop' : 'btn-start'
})

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}
</script>
<style scoped>
.knowledge-card {
  display: grid;
  grid-template-columns: minmax(360px, 1fr) 260px 280px;
  align-items: center;
  gap: 28px;
  min-height: 104px;
  padding: 18px 28px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.main-info {
  min-width: 0;
}

.main-info h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #111827;
}

.answer {
  max-width: 680px;
  margin: 0 0 10px;
  color: #4b5563;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 22px;
  color: #6b7280;
  font-size: 14px;
}

.status-info {
  min-height: 76px;
  padding-left: 28px;
  border-left: 1px solid #e5e7eb;
}

.status-info p {
  margin: 7px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.tag {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 14px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 700;
}

.tag.enabled {
  color: #16a34a;
  background: #dcfce7;
}

.tag.disabled {
  color: #6b7280;
  background: #f3f4f6;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, 86px);
  justify-content: end;
  gap: 14px;
}

.btn {
  height: 40px;
  border-radius: 8px;
  background: #fff;
  font-weight: 700;
  cursor: pointer;
  border: 1px solid;
}

.btn.toggle.btn-start {
  color: #16a34a;
  border-color: #16a34a;
}

.btn.toggle.btn-start:hover {
  color: #fff;
  border-color: #16a34a;
  background: #16a34a;
}

.btn.toggle.btn-stop {
  color: #ef4444;
  border-color: #ef4444;
}

.btn.toggle.btn-stop:hover {
  color: #fff;
  border-color: #ef4444;
  background: #ef4444;
}

.btn.edit {
  color: #111827;
  border-color: #9ca3af;
}

.btn.delete {
  color: #ef4444;
  border-color: #fca5a5;
}

.detail {
  grid-column: 3;
  height: 28px;
  border: 0;
  background: transparent;
  color: #16a34a;
  font-weight: 700;
  cursor: pointer;
  text-align: right;
}

@media (max-width: 1200px) {
  .knowledge-card {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .status-info {
    padding-left: 0;
    border-left: 0;
  }

  .actions {
    justify-content: start;
  }
}
</style>