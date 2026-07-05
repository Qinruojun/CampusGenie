<script setup lang="ts">
import { Bookmark, Send } from "lucide-vue-next";
import { typeLabel } from "../../utils/labels.ts";
import type { ResourceItem } from "../../data/mock";
import type { ResourceDraft } from "./types";

defineProps<{
  islandResources: ResourceItem[];
  showResourceForm: boolean;
  resourceDraft: ResourceDraft;
  resourceMessage: string;
  actionMessage: string;
  isSavedResource: (resourceId: number) => boolean;
}>();

const emit = defineEmits<{
  toggleResourceForm: [];
  submitResourceDraft: [];
  saveResource: [resourceId: number];
}>();

const resourceTypes = [
  ["EXPERIENCE", "经验"],
  ["ARTICLE", "文章"],
  ["BOOK", "书籍"],
  ["MUSIC", "音乐"],
  ["LINK", "链接"],
  ["TEMPLATE", "模板"],
  ["CHECKLIST", "清单"],
  ["TOOL", "工具"],
  ["WARNING", "避坑提醒"]
] as const;
</script>

<template>
  <div class="content-list">
    <div class="section-actions">
      <div>
        <p class="eyebrow">岛屿资源库</p>
        <h2>卡片化资源与投稿流程</h2>
      </div>
      <button type="button" @click="emit('toggleResourceForm')">
        <Send :size="18" />
        投稿到资源库
      </button>
    </div>

    <form v-if="showResourceForm" class="resource-form" @submit.prevent="emit('submitResourceDraft')">
      <label>
        类型
        <select v-model="resourceDraft.resourceType">
          <option v-for="[value, label] in resourceTypes" :key="value" :value="value">{{ label }}</option>
        </select>
      </label>
      <label>
        标题
        <input v-model="resourceDraft.title" type="text" placeholder="例如：夏令营材料清单" />
      </label>
      <label>
        简介
        <textarea v-model="resourceDraft.description" rows="3" placeholder="用几句话说明它适合谁。"></textarea>
      </label>
      <label>
        链接
        <input v-model="resourceDraft.url" type="url" placeholder="https://..." />
      </label>
      <label>
        标签
        <input v-model="resourceDraft.tags" type="text" placeholder="用逗号分隔，例如：保研, 模板" />
      </label>
      <label>
        推荐理由
        <textarea v-model="resourceDraft.reason" rows="3" placeholder="为什么值得进入这座岛的资源库？"></textarea>
      </label>
      <button type="submit">
        <Send :size="18" />
        提交审核
      </button>
      <p v-if="resourceMessage" class="inline-message">{{ resourceMessage }}</p>
    </form>

    <article v-for="resource in islandResources" :key="resource.id" class="resource-card">
      <div class="resource-type">{{ typeLabel(resource.type) }}</div>
      <div>
        <h3>{{ resource.title }}</h3>
        <p>{{ resource.description }}</p>
        <div class="tag-row">
          <span v-for="tag in resource.tags" :key="tag">{{ tag }}</span>
        </div>
      </div>
      <footer>
        <span>{{ resource.saves }} 人收藏</span>
        <span>{{ resource.comments }} 补充</span>
        <button type="button" :disabled="isSavedResource(resource.id)" @click="emit('saveResource', resource.id)">
          <Bookmark :size="16" />
          {{ isSavedResource(resource.id) ? "已在路标" : "收进路标" }}
        </button>
      </footer>
    </article>
    <p v-if="actionMessage" class="inline-message">{{ actionMessage }}</p>
  </div>
</template>
