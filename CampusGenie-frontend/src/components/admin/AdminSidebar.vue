<template>
  <aside class="sidebar" :class="{ collapsed }">
    <RouterLink class="brand" to="/admin/knowledge" aria-label="CampusGenie">
      <span class="brand-mark" aria-hidden="true">
        <svg viewBox="0 0 36 36">
          <path d="M18 3v5" />
          <path d="M13.5 4h9" />
          <rect x="6" y="11" width="24" height="18" rx="7" />
          <path d="M6 20H3.5M32.5 20H30" />
          <circle cx="14" cy="20" r="2.2" />
          <circle cx="22" cy="20" r="2.2" />
          <path d="M14 25h8" />
        </svg>
      </span>

      <span v-if="!collapsed" class="brand-text">CampusGenie</span>
    </RouterLink>

    <nav class="nav-list" aria-label="主导航">
      <section
          v-for="group in menuGroups"
          :key="group.label"
          class="nav-group"
      >
        <RouterLink
            v-if="!group.children?.length"
            class="nav-item"
            :to="group.to"
        >
          <span class="nav-icon" aria-hidden="true">
            <component :is="group.icon" />
          </span>

          <span v-if="!collapsed">{{ group.label }}</span>
        </RouterLink>

        <RouterLink
            v-else
            class="nav-item"
            :class="{ expanded: isGroupActive(group) }"
            :to="group.to"
        >
          <span class="nav-icon" aria-hidden="true">
            <component :is="group.icon" />
          </span>

          <span v-if="!collapsed">{{ group.label }}</span>

          <svg
              v-if="!collapsed"
              class="chevron"
              viewBox="0 0 24 24"
              aria-hidden="true"
          >
            <path d="m8 10 4 4 4-4" />
          </svg>
        </RouterLink>

        <div
            v-if="!collapsed && group.children?.length && isGroupActive(group)"
            class="sub-nav"
        >
          <RouterLink
              v-for="child in group.children"
              :key="child.label"
              :to="child.to"
              class="sub-nav-item"
          >
            {{ child.label }}
          </RouterLink>
        </div>
      </section>

    </nav>

    <div v-if="!collapsed" class="tip">
      <div class="tip-title">💡 小贴士</div>
      <p>优质的知识库能显著提升 AI 回答的准确性和用户体验。</p>
    </div>

    <button class="collapse-btn" @click="toggleCollapsed">
      <span class="collapse-icon">
        {{ collapsed ? '»' : '«' }}
      </span>

      <span v-if="!collapsed">收起菜单</span>
    </button>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import LibraryIcon from '@/components/icons/LibraryIcon.vue'
import FireIcon from '@/components/icons/FireIcon.vue'
import ShieldIcon from '@/components/icons/ShieldIcon.vue'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:collapsed'])

const route = useRoute()

const menuGroups = [
  {
    label: '首页',
    icon: HomeIcon,
    to: '/admin/home'
  },
  {
    label: '知识库管理',
    icon: LibraryIcon,
    to: '/admin/knowledge',
    children: [
      {
        label: '知识条目管理',
        to: '/admin/knowledge'
      },
      {
        label: '批量导入',
        to: '/admin/importKnowledge'
      },

    ]
  },
  {
    label: '热点统计',
    icon: FireIcon,
    to: '/admin/hotQuestion'
  },
  {
    label: '审核管理',
    icon: ShieldIcon,
    to: '/admin/audit'
  },

  // {
  //   label: '系统设置',
  //   icon: 'settings',
  //   to: '/admin/settings'
  // }
]

function toggleCollapsed() {
  emit('update:collapsed', !props.collapsed)
}

function isGroupActive(group) {
  if (route.path === group.to) {
    return true
  }

  return group.children?.some((child) => child.to === route.path)
}
// const MenuSvgIcon = {
//   props: {
//     name: {
//       type: String,
//       required: true
//     }
//   },

//   template: `
//     <svg v-if="name === 'home'" viewBox="0 0 24 24">
//       <path d="m3 11 9-7 9 7" />
//       <path d="M5 10v10h5v-6h4v6h5V10" />
//     </svg>

//     <svg v-else-if="name === 'library'" viewBox="0 0 24 24">
//       <rect x="4" y="5" width="16" height="16" rx="2" />
//       <path d="M8 9h8M8 13h8M8 17h5" />
//     </svg>

//     <svg v-else-if="name === 'shield'" viewBox="0 0 24 24">
//       <path d="M12 3 5 6v5c0 5 3 8 7 10 4-2 7-5 7-10V6l-7-3Z" />
//       <path d="m9.5 12 1.8 1.8 4-4" />
//     </svg>

//     <svg v-else-if="name === 'users'" viewBox="0 0 24 24">
//       <path d="M16 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2" />
//       <circle cx="9.5" cy="7" r="4" />
//       <path d="M22 21v-2a4 4 0 0 0-3-3.8" />
//       <path d="M16 3.2a4 4 0 0 1 0 7.6" />
//     </svg>

//     <svg v-else viewBox="0 0 24 24">
//       <path d="M12 8a4 4 0 1 1 0 8 4 4 0 0 1 0-8Z" />
//       <path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.6V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1-1.6 1.7 1.7 0 0 0-1.9.3l-.1.1A2 2 0 1 1 4.2 17l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.6-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.6-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1A2 2 0 1 1 7 4.2l.1.1a1.7 1.7 0 0 0 1.9.3 1.7 1.7 0 0 0 1-1.6V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.6 1.7 1.7 0 0 0 1.9-.3l.1-.1A2 2 0 1 1 19.8 7l-.1.1a1.7 1.7 0 0 0-.3 1.9c.2.6.8 1 1.6 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1Z" />
//     </svg>
//   `
// }



</script>

<style scoped>
.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 20;
  width: 204px;
  border-right: 1px solid #e5e9f2;
  background: #fff;
  box-shadow: 8px 0 28px rgba(18, 32, 54, 0.05);
  transition: width 0.2s ease;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 72px;
}

svg {
  display: block;
  width: 1em;
  height: 1em;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 56px;
  padding: 0 16px;
  color: #16834a;
  font-weight: 800;
  text-decoration: none;
  white-space: nowrap;
}

.sidebar.collapsed .brand {
  justify-content: center;
  padding: 0;
}

.brand-mark {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  color: #16834a;
  flex-shrink: 0;
}

.brand-mark svg {
  width: 33px;
  height: 33px;
  stroke-width: 3;
}

.brand-text {
  font-size: 18px;
}

.nav-list {
  display: grid;
  gap: 8px;
  padding: 28px 8px;
}

.nav-group {
  display: grid;
  gap: 6px;
}

.nav-item,
.sub-nav-item {
  display: flex;
  align-items: center;
  min-height: 44px;
  border-radius: 8px;
  color: #1d293d;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.nav-item {
  gap: 10px;
  padding: 0 14px;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 0;
}

.nav-item:hover,
.nav-item.router-link-active,
.sub-nav-item:hover,
.sub-nav-item.router-link-active {
  color: #16834a;
  background: linear-gradient(90deg, #e2f4ea, #eff8f3);
}

.nav-icon {
  display: grid;
  width: 18px;
  height: 18px;
  place-items: center;
  color: currentColor;
  flex-shrink: 0;
}

.nav-icon svg {
  width: 17px;
  height: 17px;
}

.chevron {
  width: 14px;
  height: 14px;
  margin-left: auto;
}

.sub-nav {
  display: grid;
  gap: 4px;
}

.sub-nav-item {
  padding: 0 16px 0 43px;
}

.collapse-btn {
  position: absolute;
  left: 8px;
  right: 8px;
  bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 38px;
  border: 0;
  border-radius: 8px;
  background: #f7fafc;
  color: #475467;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.collapse-btn:hover {
  color: #16834a;
  background: #effaf4;
}
.tip {
  margin: auto 20px 32px;
  padding: 22px;
  border-radius: 8px;
  background: linear-gradient(135deg, #eefaf2, #f7fbf7);
  color: #4b5563;
}

.tip-title {
  color: #16a34a;
  font-weight: 700;
  margin-bottom: 12px;
}

.tip p {
  margin: 0;
  line-height: 1.7;
  font-size: 14px;
}

.collapse-icon {
  font-size: 18px;
  line-height: 1;
}
</style>