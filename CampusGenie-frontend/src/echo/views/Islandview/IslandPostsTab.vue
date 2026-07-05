<script setup lang="ts">
import { Heart, MessageCircle, Plus } from "lucide-vue-next";
import { reactionLabel, statusCardLabel } from "../../utils/labels.ts";
import type { PostItem, ViewKey } from "../../data/mock";

defineProps<{
  islandPosts: PostItem[];
}>();

const emit = defineEmits<{
  navigate: [view: ViewKey];
  reactToPost: [post: PostItem, reaction: string];
}>();
</script>

<template>
  <div class="content-list">
    <div class="section-actions">
      <div>
        <p class="eyebrow">回声广场</p>
        <h2>线上社区生活</h2>
      </div>
      <button type="button" @click="emit('navigate', 'create-post')">
        <Plus :size="18" />
        发布回声
      </button>
    </div>

    <article v-for="post in islandPosts" :key="post.id" class="post-card">
      <div class="post-meta">
        <span>{{ post.author }}</span>
        <span>{{ post.createdAt }}</span>
        <span>{{ statusCardLabel(post.statusCard) }}</span>
      </div>
      <h3>{{ post.title }}</h3>
      <p>{{ post.content }}</p>
      <footer>
        <button
          v-for="(count, reaction) in post.reactions"
          :key="reaction"
          type="button"
          @click="emit('reactToPost', post, String(reaction))"
        >
          <Heart :size="15" />
          {{ reactionLabel(String(reaction)) }} {{ count }}
        </button>
        <button type="button">
          <MessageCircle :size="15" />
          {{ post.comments }} 回声
        </button>
      </footer>
    </article>
  </div>
</template>
