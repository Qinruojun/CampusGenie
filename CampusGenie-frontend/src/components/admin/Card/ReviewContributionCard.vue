<template>
    <article class="reviewcontribution-card" :class="{ 'batch-mode': showCheckbox }">
    <label class="checkbox-col" v-if="showCheckbox && item.statusDesc === WAIT_FOR_REVIEW_MSG">
      <input
        type="checkbox"
        :checked="checked"
        @change="$emit('toggle-check', item.id)"
      />
    </label>

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
        <template v-if="!showCheckbox">
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
        </template>

        <button
          class="detail"
          @click="$emit('view', item)"
        >
          查看详情 ›
        </button>
      </div>
    </article>
</template>

<script setup>
import { computed } from 'vue'
import {WAIT_FOR_REVIEW_MSG, REVIEW_REJECT_MSG, REVIEW_PASS_MSG} from "@/constants/status.js";

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  checked: {
    type: Boolean,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    default: false
  }
})

defineEmits(['view', 'approve', 'reject', 'toggle-check'])


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
  position: relative;
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

.reviewcontribution-card .actions {
  min-height: 76px;
  display: grid;
  grid-template-columns: repeat(3, 86px);
  justify-content: end;
  align-content: center;
  gap: 14px;
}

.reviewcontribution-card .detail {
  margin: 0;
  height: auto;
  text-align: right;
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

.btn.delete:hover {
  color: #fff !important;
  border-color: #ef4444 !important;
  background: #ef4444 !important;
}

.checkbox-col {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  min-height: 104px;
}
.checkbox-col input {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #16a34a;
}

.batch-mode .main-info {
  padding-left: 28px;
}
</style>