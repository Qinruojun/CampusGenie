<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

import { ROLE_KEY, TOKEN_KEY, USERNAME_KEY } from "@/constants/storage.js";

const router = useRouter();
const question = ref("");
const username = localStorage.getItem(USERNAME_KEY) || "用户";

function search() {
  const query = question.value.trim();
  if (!query) {
    alert("请输入问题");
    return;
  }
  router.push({
    path: "/user/qa",
    query: {
      // 这里写你要传的参数
      question: query,
    },
  });
}

function logout() {
  const ok = confirm("确定要退出登录吗？");
  if (!ok) return;

  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(USERNAME_KEY);
  router.replace("/user/login");
}
</script>

<template>
  <main class="home page">
    <header class="home-topbar">
      <RouterLink class="home-brand" to="/user/home">CampusGenie</RouterLink>
      <nav class="home-nav" aria-label="用户导航">
        <RouterLink class="home-nav-link" to="/user/home">首页</RouterLink>
        <RouterLink to="/user/hot">热点问题</RouterLink>
        <RouterLink to="/user/qa">快速问答</RouterLink>
        <RouterLink to="/user/contribute">我要贡献</RouterLink>
        <RouterLink to="/user/contributionlist">我的贡献</RouterLink>
        <RouterLink to="/user/islands">岛屿</RouterLink>
      </nav>
      <button class="logout-btn" type="button" @click="logout">
        {{ username }} · 退出登录
      </button>
    </header>

    <RouterView />
    <section class="hero-center">
      <p class="eyebrow">CampusGenie</p>
      <h1 class="page-title">你好，有什么可以帮助你？</h1>
      <p class="page-desc">智能问答 · 校园知识 · 快速解决</p>

      <form class="search-box" @submit.prevent="search">
        <input
          v-model="question"
          class="input"
          placeholder="请输入你的问题，例如：图书馆几点关门？"
        />
        <button class="primary-btn" type="submit">搜索</button>
      </form>

      <div class="shortcut-grid">
        <RouterLink class="shortcut card" to="/user/hot">
          <span class="shortcut-icon">□</span>
          <strong>热点问题</strong>
          <small>查看热门的问题</small>
        </RouterLink>
        <RouterLink class="shortcut card" to="/user/qa">
          <span class="shortcut-icon">◇</span>
          <strong>快速问答</strong>
          <small>输入问题获得答案</small>
        </RouterLink>
        <RouterLink class="shortcut card" to="/user/contribute">
          <span class="shortcut-icon">△</span>
          <strong>我要贡献</strong>
          <small>补充新的校园知识</small>
        </RouterLink>
        <RouterLink class="shortcut card" to="/user/contributionlist">
          <span class="shortcut-icon">☆</span>
          <strong>我的贡献</strong>
          <small>查看已有贡献</small>
        </RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
.hero-center {
  width: 100%;
  min-height: calc(100vh - 238px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.home-topbar {
  position: fixed;
  top: 18px;
  left: 50%;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 18px;
  width: min(1080px, calc(100% - 48px));
  min-height: 56px;
  padding: 8px 10px 8px 18px;
  border: 1px solid rgba(35, 157, 83, 0.14);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 16px 42px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(14px);
  transform: translateX(-50%);
}

.home-brand {
  flex: 0 0 auto;
  color: var(--deep);
  font-size: 18px;
  font-weight: 900;
  text-decoration: none;
}

.home-nav {
  display: flex;
  flex: 1;
  justify-content: center;
  gap: 6px;
}

.home-nav a {
  display: inline-flex;
  align-items: center;
  min-height: 38px;
  padding: 8px 13px;
  color: var(--muted);
  border-radius: 999px;
  text-decoration: none;
  font-weight: 800;
}

.home-nav a:hover,
.home-nav a.router-link-active {
  color: var(--green);
  background: rgba(35, 157, 83, 0.08);
}

.home-nav .home-nav-link {
  color: #fff;
  background: var(--green);
}

.home-nav .home-nav-link:hover {
  color: #fff;
  background: #1b7f45;
}

.home-nav .island-nav-link {
  color: #fff;
  background: var(--green);
}

.home-nav .island-nav-link:hover {
  color: #fff;
  background: #1b7f45;
}

.logout-btn {
  flex: 0 0 auto;
  height: 40px;
  padding: 0 16px;
  border: 1px solid rgba(35, 157, 83, 0.24);
  border-radius: 999px;
  background: #fff;
  color: var(--green);
  font-weight: 800;
  cursor: pointer;
}

.logout-btn:hover {
  background: rgba(35, 157, 83, 0.08);
}

.search-box {
  width: min(640px, 100%);
  margin: 44px auto 28px;
  display: grid;
  grid-template-columns: 1fr 110px;
  gap: 12px;
}

.shortcut-grid {
  width: min(640px, 100%);
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.shortcut {
  padding: 24px 16px;
  display: grid;
  gap: 8px;
  place-items: center;
  color: var(--deep);
  transition: 0.18s ease;
}

.shortcut:hover {
  transform: translateY(-3px);
  border-color: rgba(35, 157, 83, 0.28);
}

.shortcut-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: var(--green);
  border: 1px solid rgba(35, 157, 83, 0.22);
  background: rgba(35, 157, 83, 0.06);
}
.page {
  width: 100%;
  min-height: 100vh;
  padding: 96px 0 96px;
  background: radial-gradient(circle at top, #ffffff 0%, #fbfaf7 56%, #f7f5ef 100%);
}

.shortcut small {
  color: var(--muted);
}

@media (max-width: 640px) {
  .home-topbar {
    position: static;
    flex-wrap: wrap;
    width: min(100% - 24px, 1080px);
    margin: 12px auto 0;
    border-radius: 18px;
    transform: none;
  }

  .home-nav {
    order: 3;
    justify-content: flex-start;
    overflow-x: auto;
    width: 100%;
  }

  .search-box,
  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}
</style>