import { createRouter, createWebHashHistory } from 'vue-router'
const UserContributionList = () => import('../views/user/UserContributionList.vue')
const UserHome = () => import('../views/user/UserHome.vue')
const QAResult = () => import('../views/user/QAResult.vue')
const UserContribution = () => import('../views/user/UserContribution.vue')
const AdminLogin = () => import('../views/admin/AdminLogin.vue')
const KnowledgeManage = () => import('../views/admin/KnowledgeManage.vue')
const KnowledgeDraftManage = () => import('../views/admin/KnowledgeDraftManage.vue')
const AuditManage = () => import('../views/admin/AuditManage.vue')
const HotQuestions = () => import('../views/user/HotQuestions.vue')
const UserLogin = () => import('../views/user/UserLogin.vue')
const Welcome = () => import('../views/Welcome.vue')
const AddKnowledge = () => import('../views/admin/KnowledgeAdd.vue')
const EditKnowledge = () => import('../views/admin/KnowledgeEdit.vue')
const ImportKnowledge = () => import('../views/admin/KnowledgeImport.vue')
const ViewKnowledge = () => import('../views/admin/KnowledgeView.vue')
const ViewKnowledgeDraft = () => import('../views/admin/ViewKnowledgeDraft.vue')
const EditKnowledgeDraft = () => import('../views/admin/EditKnowledgeDraft.vue')
const ContributionView = () => import('../views/admin/ContributionView.vue')
const AdminHotQuestion = () => import('@/views/admin/HotQuestion.vue')
const AdminHome = () => import('@/views/admin/AdminHome.vue')
const AdminProfile = () => import('@/views/admin/AdminProfile.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')
const UserQAChat = () => import('@/views/user/QAChat.vue')
import { USER_ROLE, USERNAME_KEY } from '../constants/storage'
import { TOKEN_KEY, ADMIN_ROLE, ROLE_KEY } from '../constants/storage'
//把页面改成懒加载
const router = createRouter({
  history: createWebHashHistory(),

  //TODO:当前为了debug把一些界面需要认证的改为requireAuth:false了，后面记得改回来
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: Welcome,
      meta: {
        hideNav: true
      }
    },

    {
      path: '/user/home',
      name: 'user-home',
      component: UserHome,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE
      }
    },
    {
      path: '/user/login',
      name: 'user-login',
      component: UserLogin,
      meta: {
        hideNav: true
      }
    },
    {
      path: '/user/qa',
      name: 'user-qa',
      component: UserQAChat,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE,
      }
    },
    {
      path: '/qa-result',
      name: 'qa-result',
      component: QAResult,
      meta: {
        hideNav: true,
        requiresAuth: false//TODO:因为管理员目前也要用到这个页面，所以先这样设置，后面再改
      }
    },
    {
      path: '/user/hot',
      name: 'hot',
      component: HotQuestions,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE,
      }
    },
    {
      path: '/user/contribute',
      name: 'contribute',
      component: UserContribution,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE
      }
    },
    {
      path: '/user/contributionlist',
      name: 'my-contributions',
      component: UserContributionList,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE
      }
    },
    {
      path: '/user/contribution/:id',
      name: 'user-contribution-view',
      component: ContributionView,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: USER_ROLE
      }
    },

    {
      path: '/admin/login',
      name: 'admin-login',
      component: AdminLogin,
      meta: {
        hideNav: true
      }
    },

    {
      path: '/admin',
      component: AdminLayout,
      meta: {
        hideNav: true,
        requiresAuth: true,
        role: ADMIN_ROLE
      },
      children: [
        {
          path: '',
          redirect: '/admin/home'
        },
        {
          path: 'home',
          name: 'admin-home',
          component: AdminHome,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'knowledge',
          name: 'admin-knowledge',
          component: KnowledgeManage,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'knowledgeDraft',
          name: 'admin-knowledge-draft',
          component: KnowledgeDraftManage,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'viewKnowledgeDraft/:id',
          name: 'view-knowledge-draft',
          component: ViewKnowledgeDraft,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'editKnowledgeDraft/:id',
          name: 'edit-knowledge-draft',
          component: EditKnowledgeDraft,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'addKnowledge',
          name: 'add-knowledge',
          component: AddKnowledge,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }

        },
        {
          path: 'editKnowledge/:id',
          name: 'edit-knowledge',
          component: EditKnowledge,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        }
        ,
        {
          path: 'viewKnowledge/:id',
          name: 'view-knowledge',
          component: ViewKnowledge,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE
          }
        },
        {
          path: 'importKnowledge',
          name: 'import-knowledge',
          component: ImportKnowledge,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE,
          }

        },
        {
          path: 'audit',
          name: 'admin-audit',
          component: AuditManage,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE,
          }
        },
        {
          path: 'contribution/:id',
          name: 'contribution-view',
          component: ContributionView,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE,
          }
        },
        {
          path: 'hotQuestion',
          name: 'admin-hot-question',
          component: AdminHotQuestion,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE,
          }
        },
        {
          path: 'profile',
          name: 'admin-profile',
          component: AdminProfile,
          meta: {
            requiresAuth: true,
            role: ADMIN_ROLE,
          }

        }
      ]
    }
  ]
}

  //还要进行路由守卫，拦截未登陆的访问



)
router.beforeEach((to) => {
  console.log('--- 路由正在跳转到 ---', to.path)

  const token = localStorage.getItem(TOKEN_KEY)
  const role = localStorage.getItem(ROLE_KEY)

  // 1. 【核心修复】白名单直接放行：如果是去登录页或欢迎页，直接允许通行，不审查权限
  if (to.path === '/user/login' || to.path === '/admin/login' || to.path === '/') {
    return true;
  }

  // 2. 安全读取 meta 属性
  const requiresAuth = to.meta?.requiresAuth;
  const targetRole = to.meta?.role;

  // 3. 拦截未登录用户
  if (requiresAuth && !token) {
    // 没登录时：如果是管理员页面就去后台登录，否则去用户登录
    return targetRole === ADMIN_ROLE ? '/admin/login' : '/user/login';
  }

  // 4. 登录后的角色权限校验
  if (requiresAuth) {
    if (targetRole === ADMIN_ROLE && role !== ADMIN_ROLE) {
      alert('无权限访问管理员页面');
      return '/user/home';
    }

    if (targetRole === USER_ROLE && role !== USER_ROLE) {
      alert('无权限访问用户页面');
      return '/user/login';
    }
  }

  // 5. 放行其他所有正常路由
  return true;
});

//   if (to.meta.requiresAuth && !token) {
//     if (to.meta.role === USER_ROLE) {
//       return '/user/login'
//     }

//     return '/admin/login'
//   }

//   if (to.meta.requiresAuth && to.meta.role === ADMIN_ROLE && role !== ADMIN_ROLE) {
//     alert('无权限访问管理员页面')
//     return '/user/home'
//   }

//   if (to.meta.requiresAuth && to.meta.role === USER_ROLE && role !== USER_ROLE) {
//     alert('无权限访问用户页面')
//     return '/admin/login'
//   }

//   return true
// })

export default router


