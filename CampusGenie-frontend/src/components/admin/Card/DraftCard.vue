<template>
  <article class="draft-card">
    <div class="main-info">
      <div class="main-info-content">
        <h3>{{ item.question }}</h3>

        <p class="answer">
          答案预览：{{ item.answer }}
        </p>

        <div class="meta">
          <span>◇ 分类：{{ item.categoryName }}</span>
          <span>▣ 来源：{{ item.source }}</span>
          <span>▤ ID：{{ item.id }}</span>
        </div>
      </div>
    </div>

    <div class="status-info">
      <span class="tag" :class="statusClass">
        {{ item.statusDesc }}
      </span>

      <p>创建时间：{{ formatTime(item.createdTime) }}</p>
      <p v-if="item.reviewedTime">审核时间：{{ formatTime(item.reviewedTime) }}</p>
    </div>

    <div class="actions">
      <button v-if="item.statusDesc === '待审核'" class="btn approve" @click="$emit('approve', item.id)">
        审核通过
      </button>

      <button v-if="item.statusDesc === '待审核'" class="btn reject" @click="$emit('reject', item.id)">
        驳回
      </button>

      <button class="btn edit" @click="$emit('edit', item)">
        编辑
      </button>

      <button class="detail" @click="$emit('view', item)">
        查看详情 ›
      </button>
    </div>
  </article>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'edit', 'approve', 'reject'])

const statusClass = {
  computed() {
    const statusDesc = this.item.statusDesc
    if (statusDesc === '待审核') return 'pending'
    if (statusDesc === '已通过') return 'approved'
    if (statusDesc === '已驳回') return 'rejected'
    return ''
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.draft-card {
  display: flex;
  align-items: center;
  gap: 28px;
  min-height: 104px;
  padding: 18px 28px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

.main-info {
  flex: 1;
  min-width: 0;
  overflow-x: auto;
}

.main-info::-webkit-scrollbar {
  display: none;
}

.main-info-content {
  display: inline-block;
  white-space: nowrap;
}

.main-info h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #111827;
  white-space: nowrap;
}

.answer {
  margin: 0 0 10px;
  color: #4b5563;
  line-height: 1.5;
  white-space: nowrap;
}

.meta {
  display: inline-flex;
  gap: 22px;
  color: #6b7280;
  font-size: 14px;
  white-space: nowrap;
}

.status-info {
  flex-shrink: 0;
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

.tag.rejected {
  color: #ef4444;
  background: #fee2e2;
}

.draft-card .actions {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: repeat(3, 86px);
  justify-content: end;
  gap: 14px;
}

.draft-card .btn {
  height: 40px;
  padding: 0;
  border-radius: 8px;
  background: #fff;
  font-weight: 700;
  cursor: pointer;
  border: 1px solid;
}

.draft-card .btn.approve {
  color: #16a34a;
  border-color: #16a34a;
}

.draft-card .btn.approve:hover {
  color: #fff;
  border-color: #16a34a;
  background: #16a34a;
}

.draft-card .btn.reject {
  color: #ef4444;
  border-color: #ef4444;
}

.draft-card .btn.reject:hover {
  color: #fff;
  border-color: #ef4444;
  background: #ef4444;
}

.draft-card .btn.edit {
  color: #111827;
  border-color: #9ca3af;
}

.draft-card .btn.edit:hover {
  color: #fff;
  border-color: #3b82f6;
  background: #3b82f6;
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