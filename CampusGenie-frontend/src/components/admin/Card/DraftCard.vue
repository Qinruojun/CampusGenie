<template>
  <article class="draft-card">
    <div class="main-info">
      <h3>{{ item.question }}</h3>

      <p class="answer">
        答案预览：{{ item.answer }}
      </p>

      <div class="meta">
        <span>◇ 分类：{{ item.categoryName || '未分类' }}</span>
        <span>▣ 来源：{{ item.source }}</span>
        <span>▤ ID：{{ item.id }}</span>
      </div>
    </div>

    <div class="status-info">
      <span class="tag" :class="statusClass">
        {{ statusText }}
      </span>

      <p>创建时间：{{ formatTime(item.createdTime) }}</p>
      <p v-if="item.reviewedTime">审核时间：{{ formatTime(item.reviewedTime) }}</p>
    </div>

    <div class="actions">
      <button v-if="item.status === 0" class="btn approve" @click="$emit('approve', item.id)">
        审核通过
      </button>

      <button v-if="item.status === 0" class="btn edit" @click="$emit('edit', item)">
        编辑
      </button>

      <button v-if="item.status === 0" class="btn delete" @click="$emit('delete', item.id)">
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

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'edit', 'approve', 'delete'])

const statusText = computed(() => {
  const status = Number(props.item.status)
  return status === 0 ? '待审核' : '已通过'
})

const statusClass = computed(() => {
  const status = Number(props.item.status)
  return status === 0 ? 'pending' : 'approved'
})

const formatTime = (time) => {
  if (!time) return '-'
  if (Array.isArray(time)) {
    const [year, month, day, hour, minute, second] = time
    return `${year}/${String(month).padStart(2, '0')}/${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`
  }
  return time.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.draft-card {
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

.tag.pending {
  color: #f97316;
  background: #ffedd5;
}

.tag.approved {
  color: #16a34a;
  background: #dcfce7;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, 100px);
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
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn.approve {
  color: #16a34a;
  border-color: #16a34a;
}

.btn.approve:hover {
  color: #fff;
  border-color: #16a34a;
  background: #16a34a;
}

.btn.edit {
  color: #111827;
  border-color: #9ca3af;
}

.btn.edit:hover {
  color: #fff;
  border-color: #3b82f6;
  background: #3b82f6;
}

.btn.delete {
  color: #ef4444;
  border-color: #fca5a5;
}

.btn.delete:hover {
  color: #fff;
  border-color: #ef4444;
  background: #ef4444;
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
  .draft-card {
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
