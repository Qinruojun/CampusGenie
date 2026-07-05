<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Leaf, Trash2 } from "lucide-vue-next";
import { findCustomIsland, removeCustomIsland } from "../../stores/customIslands";
import type { PostItem } from "../../data/mock";
import SpaceRouteView from "./SpaceRouteView.vue";

defineProps<{
  islandPosts?: PostItem[];
  actionMessage?: string;
}>();

const emit = defineEmits<{
  reactToPost: [post: PostItem, reaction: string];
}>();

const route = useRoute();
const router = useRouter();
const island = computed(() => findCustomIsland(String(route.params.id ?? "")));

function deleteIsland() {
  if (!island.value) {
    return;
  }

  const ok = window.confirm(`确定删除「${island.value.name}」吗？删除后首页节点也会消失。`);
  if (!ok) {
    return;
  }

  removeCustomIsland(island.value.id);
  void router.push({ name: "map" });
}
</script>

<template>
  <section v-if="island" class="custom-island-detail">
    <div class="custom-island-actions">
      <RouterLink class="back-to-explore" :to="{ name: 'map' }">返回社区探索</RouterLink>
      <button class="delete-island-button" type="button" @click="deleteIsland">
        <Trash2 :size="17" />
        <span>删除这个社区</span>
      </button>
    </div>

    <SpaceRouteView
      :title="island.name"
      :subtitle="island.motto || '这里什么都还没有写'"
      description="这是你创建的社区节点。它已经同步到精神空间首页和社区推荐页，并保存在本地。"
      :icon="Leaf"
      :tone="island.themeColor"
      accent="#eef5e8"
      :island-slug="`custom-${island.id}`"
      :island-posts="islandPosts ?? []"
      :action-message="actionMessage"
      @react-to-post="(post, reaction) => emit('reactToPost', post, reaction)"
    />
  </section>

  <SpaceRouteView
    v-else
    title="未找到岛屿"
    subtitle="这个节点可能已经被清理"
    description="返回精神空间后可以重新创建一个新的岛屿。"
    :icon="Leaf"
    tone="#6f7d68"
    accent="#eef2e9"
  />
</template>

<style scoped>
.custom-island-detail {
  display: grid;
  gap: 16px;
}

.custom-island-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.back-to-explore,
.delete-island-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 40px;
  padding: 9px 14px;
  border-radius: 8px;
  font-weight: 900;
  text-decoration: none;
}

.back-to-explore {
  color: #2f5f4f;
  border: 1px solid rgba(47, 95, 79, 0.22);
  background: rgba(238, 245, 232, 0.9);
}

.delete-island-button {
  color: #9b3d34;
  border: 1px solid rgba(155, 61, 52, 0.22);
  background: rgba(255, 246, 244, 0.9);
}

.back-to-explore:hover {
  background: rgba(47, 95, 79, 0.1);
}

.delete-island-button:hover {
  background: rgba(155, 61, 52, 0.1);
}
</style>
