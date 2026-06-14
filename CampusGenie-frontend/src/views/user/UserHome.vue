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
    <button class="logout-btn" type="button" @click="logout">
      {{ username }} · 退出登录
    </button>

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
  min-height: calc(100vh - 238px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.logout-btn {
  position: fixed;
  top: 24px;
  right: 28px;
  z-index: 10;
  height: 40px;
  padding: 0 16px;
  border: 1px solid rgba(35, 157, 83, 0.24);
  border-radius: 999px;
  background: #fff;
  color: var(--green);
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
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
  width: min(980px, calc(100% - 48px));
  margin: 0 auto;
  padding: 70px 0 96px;
}

.shortcut small {
  color: var(--muted);
}

@media (max-width: 640px) {
  .search-box,
  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}
</style>
