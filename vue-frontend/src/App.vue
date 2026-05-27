<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopNav from './components/TopNav.vue'
import AdminShell from './components/AdminShell.vue'

const route = useRoute()
const isAdminPage = computed(() => route.meta.admin)
const isAdminLogin = computed(() => route.name === 'admin-login')
</script>

<template>
  <div class="app">
    <AdminShell v-if="isAdminPage">
      <RouterView />
    </AdminShell>

    <template v-else>
      <TopNav v-if="!isAdminLogin" />
      <RouterView />
    </template>
  </div>
</template>
