import { createRouter, createWebHistory } from 'vue-router'

import UserHome from '../views/UserHome.vue'
import QAResult from '../views/QAResult.vue'
import UserContribution from '../views/UserContribution.vue'
import AdminLogin from '../views/AdminLogin.vue'
import KnowledgeManage from '../views/KnowledgeManage.vue'
import AuditManage from '../views/AuditManage.vue'
import HotQuestions from '../views/HotQuestions.vue'

const routes = [
  { path: '/', name: 'home', component: UserHome, meta: { title: '用户首页' } },
  { path: '/qa-result', name: 'qa-result', component: QAResult, meta: { title: '问答结果页' } },
  { path: '/contribute', name: 'contribute', component: UserContribution, meta: { title: '用户贡献页' } },
  { path: '/hot', name: 'hot', component: HotQuestions, meta: { title: '热点问题页' } },
  { path: '/admin/login', name: 'admin-login', component: AdminLogin, meta: { title: '管理员登录页', publicAdmin: true } },
  { path: '/admin/knowledge', name: 'knowledge-manage', component: KnowledgeManage, meta: { title: '知识库管理页', admin: true } },
  { path: '/admin/audit', name: 'audit-manage', component: AuditManage, meta: { title: '审核管理页', admin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
