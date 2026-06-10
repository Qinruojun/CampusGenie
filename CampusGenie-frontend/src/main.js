import { createApp } from 'vue'
import { createPinia } from 'pinia'
 import App from './App.vue'
import router from './router'
import './assets/main.css'
/**
 *
 * 测试代码
 */
//import Test from './views/user/HotQuestions.vue' //方便测试单个页面，只引入想测试的页面
//import Test from './components/panel/HotQuestionPanel.vue'
//import Test from './TestImport.vue'
//import Test from './views/admin/KnowledgeImport.vue'
//import Test from './views/admin/KnowledgeAdd.vue'
//import Test from './views/admin/KnowledgeManage.vue'
//import Test from './views/user/UserContributionList.vue'
//import Test from'./views/admin/AuditManage.vue'
//import Test from '@/components/admin/dialog/RejectReasonDialog.vue'
//import Test from './Test2.vue'
//import Test from './views/user/QAResult.vue'
//import Test from './views/admin/HotQuestion.vue'
//import Test from './views/user/HotQuestions.vue'
//import Test from '@/views/admin/AuditManage.vue'
//import Test from '@/components/admin/KnowledgeCard.vue'
//import Test from './components/panel/KnowledgeCard.vue'
//import Test from '@/views/user/QAChat.vue'
//import Test from '@/views/user/UserHome.vue'
// import Test from '@/views/Welcome.vue'
// const app = createApp(Test)
// app.config.errorHandler = (err, instance, info) => {
//     console.error('Vue error:', err)
//     console.error('Info:', info)
// }
//
// window.addEventListener('unhandledrejection', event => {
//     console.error('Promise error:', event.reason)
// })
// app.use(router)
// app.use(createPinia()) // ✅ 先注册插件
// app.mount('#app')      // ✅ 最后挂载

/**
 *
 * 项目运行代码
 */
const app = createApp(App)
createApp(App).use(router).mount('#app')
app.use(createPinia())
