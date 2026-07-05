<script setup lang="ts">
import { ref } from "vue";
import { Check } from "lucide-vue-next";
import type { RulesEditPayload } from "./types";

defineProps<{
  rulesMessage: string;
}>();

const emit = defineEmits<{
  submitRulesEdit: [payload: RulesEditPayload];
}>();

const rulesEditor = ref<HTMLElement | null>(null);

function submitRulesEdit() {
  emit("submitRulesEdit", {
    proposedContentHtml: rulesEditor.value?.innerHTML.trim() ?? "",
    proposedText: rulesEditor.value?.innerText.trim() ?? ""
  });
}
</script>

<template>
  <div class="rules-panel">
    <p class="eyebrow">温柔守则</p>
    <h2>编辑后提交，守岛人审核通过后发布。</h2>
    <div ref="rulesEditor" class="rules-editor" contenteditable="true">
      1. 回应之前，先确认对方想被怎样回应。<br />
      2. 不用效率、强大和正确压过一个人的当下感受。<br />
      3. 可以分享经验，但不要替别人下结论。<br />
      4. 发现风险内容时，请使用举报和守岛人审核流程。
    </div>
    <button type="button" @click="submitRulesEdit">
      <Check :size="18" />
      提交修改申请
    </button>
    <p v-if="rulesMessage" class="inline-message">{{ rulesMessage }}</p>
  </div>
</template>
