<template>
  <div class="admin-layout">
    <AdminSidebar v-model:collapsed="sidebarCollapsed" />

    <div class="admin-body" :class="{ collapsed: sidebarCollapsed }">
      <AdminTopbar
          :title="pageTitle"
          :subtitle="pageSubtitle"
          @toggle-sidebar="toggleSidebar"
      />

      <main class="admin-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AdminTopbar from '@/components/admin/AdminTopbar.vue'

const route = useRoute()
const sidebarCollapsed = ref(false)

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const pageTitle = computed(() => {
  if (route.path.includes('/admin/knowledge') && !route.path.includes('/admin/knowledgeDraft')) return '知识库管理'
  if (route.path.includes('/admin/knowledgeDraft')) return '知识草稿管理'
  if (route.path.includes('/admin/importKnowledge')) return '批量导入'
  if (route.path.includes('/admin/hotQuestion')) return '热点统计'
  if (route.path.includes('/admin/audit')) return '审核管理'
  if (route.path.includes('/admin/home')) return '首页概览'

  return '后台管理'
})

const pageSubtitle = computed(() => {
  if (route.path.includes('/admin/knowledge') && !route.path.includes('/admin/knowledgeDraft')) return '管理和维护校园知识库内容'
  if (route.path.includes('/admin/knowledgeDraft')) return '管理和维护知识草稿，支持审核通过、驳回和编辑操作'
  if (route.path.includes('/admin/audit')) return '审核用户提交的知识贡献'
  if (route.path.includes('/admin/hotQuestion')) return '查看校园热点问题趋势'

  return 'CampusGenie 管理控制台'
})
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.admin-body {
  min-height: 100vh;
  margin-left: 204px;
  transition: margin-left 0.2s ease;
}

.admin-body.collapsed {
  margin-left: 72px;
}

.admin-main {
  min-height: calc(100vh - 72px);
  padding: 28px 36px;
  box-sizing: border-box;
}
</style>