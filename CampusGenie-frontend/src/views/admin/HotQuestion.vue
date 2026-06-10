<script setup>
import HotQuestionPanel from '@/components/panel/HotQuestionPanel.vue'
</script>
<template>
  <HotQuestionPanel
      title="热点问题统计"
      desc="后台定时统计用户近期高频查询问题，管理员可根据趋势优化知识库内容。"
      eyebrow="Admin Analytics"
      :show-refresh="true"
      :show-stats="true"
      :show-trend-text="true"
      :show-hot-mark="false"
      :polling="true"
  />
</template>