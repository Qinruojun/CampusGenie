import { createRouter, createWebHashHistory } from 'vue-router'
const UserContributionList = () => import('../views/user/UserContributionList.vue')
const  UserHome =()=>import( '../views/user/UserHome.vue')
const QAResult =()=>import('../views/user/QAResult.vue')
const UserContribution =()=>import( '../views/user/UserContribution.vue')
const AdminLogin=()=>import('../views/admin/AdminLogin.vue')
const  KnowledgeManage=()=>import( '../views/admin/KnowledgeManage.vue')
const  AuditManage=() =>import( '../views/admin/AuditManage.vue')
const  HotQuestions =()=>import( '../views/user/HotQuestions.vue')
const  UserLogin =()=> import( '../views/user/UserLogin.vue')
const Welcome=() => import ( '../views/Welcome.vue')
const AddKnowledge =()=>import('../views/admin/KnowledgeAdd.vue')
const EditKnowledge=()=>import('../views/admin/KnowledgeEdit.vue')
import {USER_ROLE ,USERNAME_KEY} from '../constants/storage'
import {TOKEN_KEY,ADMIN_ROLE,ROLE_KEY} from '../constants/storage'
//把页面改成懒加载
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {path: '/', name: 'welcome', component:Welcome,meta:{ hideNav:true} },//hideNav决定导航栏会不会隐藏
    {path: '/admin/addKnowledge', name:'add-knowledge', component:AddKnowledge, meta:{hideNav:true  , requiresAuth: true,
        role: USER_ROLE}},
     {path: '/admin/editKnowledge', name:'edit-knowledge', component:EditKnowledge, meta:{hideNav:true,    requiresAuth: true,
         role: ADMIN_ROLE}},
    { path: '/user/login',  name: 'user-login',component: UserLogin,meta:{ hideNav:true} },//根地址/对应哪个组件，哪个组件就是首页
    { path: '/qa-result', name: 'qa-result', component: QAResult },
    { path: '/contribute', name: 'contribute', component: UserContribution, meta: { hideNav: true, requiresAuth:  true,role: USER_ROLE} },
    { path: '/hot', name: 'hot', component: HotQuestions,meta: { hideNav: true} },
    { path: '/admin/login', name: 'admin-login', component: AdminLogin, meta: { hideNav: true } },
    { path: '/admin/knowledge', name: 'admin-knowledge', component: KnowledgeManage, meta: { hideNav: true , requiresAuth: true,role : ADMIN_ROLE} },
    { path: '/admin/audit', name: 'admin-audit', component: AuditManage, meta: { hideNav: true , requiresAuth: true,role : ADMIN_ROLE} },
    {path: '/user/home',name: 'user-home',component: UserHome,meta:{ hideNav:true, requiresAuth: true,role: USER_ROLE}},
    {
      path: '/user/contributionlist',
      name: 'my-contributions',
      component: UserContributionList
    }
  ]
//还要进行路由守卫，拦截未登陆的访问



})
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem(TOKEN_KEY)
  const role = localStorage.getItem(ROLE_KEY)

  if (to.meta.requiresAuth && !token ) {
    if(to.meta.role === USER_ROLE)
    next('/user/login')//如果是用户专用页面则跳转到用户登录页面
    else
    next('/admin/login')//如果是管理员专用页面则跳转到管理员登录页面
  } else {
    if(to.meta.role === ADMIN_ROLE &&  role !== ADMIN_ROLE){
      alert('无权限访问管理员页面')
      next('/user/home')
    }
    else if(to.meta.role === USER_ROLE && role !== USER_ROLE){
      alert('无权限访问用户页面')
      next('/admin/knowledge')
    }
  }
  next()
})

export default router

