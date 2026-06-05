# Vue3项目结构

在Vue项目里，通常分为两类样式
- 全局样式，本项目中命名为`main.css`
- 当前页面，当前组件样式 如`HomePage.vue`

运行项目的方法：
在CampusGenie总文件夹里面运行:
```
npm run dev
```
## src 目录结构
### `src/components` :放置可复用组件
### src/router : 这是路由配置目录，控制页面跳转
### src/stores: 这是Pinia状态管理页面，保存多个页面都要用的数据
### src/views : 放页面型组件
### src/App.vue: 这是整个Vue项目的根组件，所有页面都会显示在这个App.vue里面
### src/main.js: 这是Vue项目的入口文件