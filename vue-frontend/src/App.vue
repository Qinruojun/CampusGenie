<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const hideNav = computed(() => route.meta.hideNav)
</script>

<template>
  <div class="app-shell">
    <header v-if="!hideNav" class="site-header">
      <RouterLink class="brand" to="/">CampusGenie</RouterLink>

      <nav class="nav-links">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/hot">热点问题</RouterLink>
        <RouterLink to="/contribute">用户贡献</RouterLink>
        <RouterLink to="/admin/login">管理员</RouterLink>
      </nav>
    </header>

    <RouterView />
  </div>
</template>
