import { createRouter, createWebHashHistory } from 'vue-router'

import UserHome from '../views/user/UserHome.vue'
import QAResult from '../views/user/QAResult.vue'
import UserContribution from '../views/user/UserContribution.vue'
import AdminLogin from '../views/admin/AdminLogin.vue'
import KnowledgeManage from '../views/admin/KnowledgeManage.vue'
import AuditManage from '../views/admin/AuditManage.vue'
import HotQuestions from '../views/user/HotQuestions.vue'
import UserLogin from '../views/user/UserLogin.vue'
import Welcome from '../views/Welcome.vue'
import AddKnowledge from'../views/admin/KnowledgeEdit.vue'
import EditKnowledge from '../views/admin/KnowledgeEdit.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
   // {path: '/', name: 'welcome', component:Welcome,meta:{ hideNav:true} },//hideNav决定导航栏会不会隐藏
    {path: '/', name:'add-knowledge', component:AddKonwledge, meta:{hideNav:true}},
     {path: '/admin/editKnowledge', name:'edit-knowledge', component:EditKonwledge, meta:{hideNav:true}},
    { path: '/user/login',  name: 'user-login',component: UserLogin,meta:{ hideNav:true} },//根地址/对应哪个组件，哪个组件就是首页
    { path: '/qa-result', name: 'qa-result', component: QAResult },
    { path: '/contribute', name: 'contribute', component: UserContribution },
    { path: '/hot', name: 'hot', component: HotQuestions },
    { path: '/admin/login', name: 'admin-login', component: AdminLogin, meta: { hideNav: true } },
    { path: '/admin/knowledge', name: 'admin-knowledge', component: KnowledgeManage, meta: { hideNav: true } },
    { path: '/admin/audit', name: 'admin-audit', component: AuditManage, meta: { hideNav: true } },
    {path: '/user/home',name: 'user-home',component: UserHome,meta:{ hideNav:true}},
    
  ]
})

export default router
