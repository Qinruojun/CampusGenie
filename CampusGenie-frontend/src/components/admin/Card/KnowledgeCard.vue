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
      <button class="btn toggle" @click="$emit('toggle', item)">
        {{ item.status === 'published' ? '停用' : '启用' }}
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

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'edit', 'toggle', 'delete'])

const statusMap = {
  published: '已发布',
  pending: '待审核',
  disabled: '已停用'
}

const statusText = computed(() => statusMap[props.item.status] || '未知')
const statusClass = computed(() => props.item.status)//FIXME:要改的，知识库返回状态没这个
</script>
<style scoped src="@/styles/card-base.css">

.tag.published {
  color: #16a34a;
  background: #dcfce7;
}

.tag.pending {
  color: #f59e0b;
  background: #fff7ed;
}

.tag.disabled {
  color: #6b7280;
  background: #f3f4f6;
}

</style>