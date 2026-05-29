# CampusGenie Minimal Website

课题二：校园生活百事通（智能问答知识库）前端原型。

这版是正常网站结构，不包含“总览页面”。首页 `/` 就是用户首页。

## 运行

```bash
npm install
npm run dev
```

默认访问：

```text
http://localhost:5173/
```

## 页面路由

```text
/#/                  用户首页
/#/qa-result         问答结果页
/#/contribute        用户贡献页
/#/admin/login       管理员登录页
/#/admin/knowledge   知识库管理页
/#/admin/audit       审核管理页
/#/hot               热点问题页
```

## 说明

当前数据在 `src/data/mockData.js` 中，后续可替换为后端接口。
