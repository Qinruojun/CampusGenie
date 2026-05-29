import { createRouter, createWebHashHistory } from 'vue-router'

import UserHome from '../views/UserHome.vue'
import QAResult from '../views/QAResult.vue'
import UserContribution from '../views/UserContribution.vue'
import AdminLogin from '../views/AdminLogin.vue'
import KnowledgeManage from '../views/KnowledgeManage.vue'
import AuditManage from '../views/AuditManage.vue'
import HotQuestions from '../views/HotQuestions.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', name: 'home', component: UserHome },
    { path: '/qa-result', name: 'qa-result', component: QAResult },
    { path: '/contribute', name: 'contribute', component: UserContribution },
    { path: '/hot', name: 'hot', component: HotQuestions },
    { path: '/admin/login', name: 'admin-login', component: AdminLogin, meta: { hideNav: true } },
    { path: '/admin/knowledge', name: 'admin-knowledge', component: KnowledgeManage, meta: { hideNav: true } },
    { path: '/admin/audit', name: 'admin-audit', component: AuditManage, meta: { hideNav: true } }
  ]
})

export default router
