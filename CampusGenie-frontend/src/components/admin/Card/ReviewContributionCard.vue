<template>
<!--  <article class="knowledge-card">-->
    <article class="reviewcontribution-card">
    <div class="main-info">
      <h3>{{ item.question }}</h3>

      <p class="answer">
        答案：{{ item.answer }}
      </p>

      <div class="meta">
        <span>◇ 分类：{{ item.categoryName }}</span>
        <span>▣ 来源：{{ item.supplement }}</span>
        <span>▤ ID：{{ item.id }}</span>
      </div>
      </div>

    <div class="status-info">
      <span
        class="tag"
          :class="statusClass"
      >
          {{ item.statusDesc }}
        </span>

        <p>提交时间：{{ item.createdTime }}</p>
      <p>提交人：{{ item.username }}</p>
      </div>

      <div class="actions">
        <button
          v-if="item.statusDesc !== REVIEW_PASS_MSG && item.statusDesc !== REVIEW_REJECT_MSG"
          class="btn edit approve-btn"
          @click="$emit('approve', item)"
        >
          审核通过
        </button>

        <button
          v-if="item.statusDesc !== REVIEW_PASS_MSG && item.statusDesc !== REVIEW_REJECT_MSG"
          class="btn delete"
          @click="$emit('reject', item)"
        >
          驳回
        </button>

        <button
          class="detail"
          :class="{ 'detail-centered': item.statusDesc === REVIEW_PASS_MSG || item.statusDesc === REVIEW_REJECT_MSG }"
          @click="$emit('view', item)"
        >
          查看详情 ›
        </button>
      </div>
    </article>
<!--  </article>-->
</template>

<script setup>
import { computed } from 'vue'
import {WAIT_FOR_REVIEW_MSG, REVIEW_REJECT_MSG, REVIEW_PASS_MSG} from "@/constants/status.js";

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'approve', 'reject'])


const statusClass = computed(() => {
  if(props.item.statusDesc === REVIEW_PASS_MSG){
    return 'approved'
  }
  else if(props.item.statusDesc === WAIT_FOR_REVIEW_MSG){
    return 'pending'
  }
  else{
    return 'rejected'
  }
})
</script>
<style scoped src="@/styles/card-base.css"></style>
<style>
.reviewcontribution-card{
  /*TODO*/
  width:100%
}
.tag.approved {
  color: #16a34a;
  background: #dcfce7;
}

.tag.pending {
  color: #f59e0b;
  background: #fff7ed;
}

.tag.rejected {
  color: #6b7280;
  background: #f3f4f6;
}

.detail-centered {
  grid-column: 1 / -1;
  justify-self: center;
  margin-left: auto;
  margin-right: auto;
}

.btn.approve-btn {
  color: #16a34a !important;
  border-color: #16a34a !important;
}

.btn.approve-btn:hover {
  color: #fff !important;
  border-color: #16a34a !important;
  background: #16a34a !important;
}
</style>