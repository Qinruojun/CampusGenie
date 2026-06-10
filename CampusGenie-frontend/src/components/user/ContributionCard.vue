<template>
<!--  <article class="knowledge-card">-->
    <article class="usercontribution-card">
      <div class="main-info">
        <h3>{{ item.question }}</h3>

        <p class="answer">
          答案：{{ item.answer }}
        </p>

      <div class="meta">
          <span>◇ 分类：{{ item.categoryName }}</span>
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
      </div>

      <div class="actions">
        <button
          class="btn edit"
          @click="$emit('delete', item)"
        >
          删除
        </button>

        <button
          class="detail"
          @click="$emit('view', item)"
        >
          查看驳回原因
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

defineEmits(['view', 'delete'])


const statusClass = computed(() => {
  if(props.item.statusDesc ===REVIEW_PASS_MSG){
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
<style scoped src="@/styles/card-base.css">
.usercontribution-card{
/*TODO*/
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
</style>