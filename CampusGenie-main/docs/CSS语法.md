# CSS基本语法
可以定义全局的CSS变量，方便整个项目复用，统一主题色,本项目的全局CSS定义是`base.css`文件(导入了main.js里面)
但是页面组件样式(scoped)优先级高于全局样式，所以可以实现页面定制化
```
选择器 {
    属性名:属性值;
    属性名:属性值;
}
\*如*\
.card{
    background: white;
    padding: 24px;
    border-radius: 20px;
}
```

选择器：vue里面`<template>`的类名要加`.`,如`.card`如果是标签名则不用加`.`,如`botton`

## 常见样式属性

### 文字相关

```
{
    color: 
    font-size: 
    font-weight:
    text-align: /*文字对齐*/
    line-height: /*行高*/
}
```