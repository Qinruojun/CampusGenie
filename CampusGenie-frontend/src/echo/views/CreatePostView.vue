<script setup lang="ts">
import { computed, watch } from "vue";
import { Send } from "lucide-vue-next";
import { useRoute } from "vue-router";
import { replyPreferences, statusCards } from "../data/mock";
import type { Island } from "../data/mock";

interface DraftPost {
  islandSlug: string;
  title: string;
  content: string;
  statusCard: string;
  replyPreference: string;
  anonymous: boolean;
}

const props = defineProps<{
  islands: Island[];
  postableIslands?: Island[];
  draftPost: DraftPost;
  isSubmittingPost: boolean;
  actionMessage: string;
}>();

const emit = defineEmits<{
  publishPost: [];
}>();

const route = useRoute();
const selectableIslands = computed(() => props.postableIslands?.length ? props.postableIslands : props.islands);

watch(
  () => [route.query.island, selectableIslands.value.map((island) => island.slug).join("|")],
  ([island]) => {
    if (typeof island !== "string") {
      return;
    }

    if (selectableIslands.value.some((item) => item.slug === island)) {
      props.draftPost.islandSlug = island;
    }
  },
  { immediate: true }
);
</script>

<template>
  <section class="form-view">
    <div class="section-heading">
      <p class="eyebrow">分享感受</p>
      <h1>给这段状态一张温柔的卡片。</h1>
    </div>

    <form class="post-form" @submit.prevent="emit('publishPost')">
      <label>
        岛屿
        <select v-model="draftPost.islandSlug">
          <option v-for="island in selectableIslands" :key="island.slug" :value="island.slug">{{ island.name }}</option>
        </select>
      </label>
      <label>
        标题
        <input v-model="draftPost.title" type="text" placeholder="例如：我发现自己真的没办法像别人一样高效" />
      </label>
      <label>
        内容
        <textarea v-model="draftPost.content" rows="6" placeholder="把想留下来的话写在这里。"></textarea>
      </label>

      <div class="choice-field">
        <span>状态卡</span>
        <div>
          <button
            v-for="[value, label] in statusCards"
            :key="value"
            type="button"
            :class="{ active: draftPost.statusCard === value }"
            @click="draftPost.statusCard = value"
          >
            {{ label }}
          </button>
        </div>
      </div>

      <div class="choice-field">
        <span>希望别人怎么回应</span>
        <div>
          <button
            v-for="[value, label] in replyPreferences"
            :key="value"
            type="button"
            :class="{ active: draftPost.replyPreference === value }"
            @click="draftPost.replyPreference = value"
          >
            {{ label }}
          </button>
        </div>
      </div>

      <label class="check-row">
        <input v-model="draftPost.anonymous" type="checkbox" />
        匿名发布
      </label>

      <button type="submit" class="submit-button" :disabled="isSubmittingPost">
        <Send :size="18" />
        {{ isSubmittingPost ? "发布中" : "发布" }}
      </button>
      <p class="inline-message">发布后会进入所选社区的回声广场。</p>
      <p v-if="actionMessage" class="inline-message">{{ actionMessage }}</p>
    </form>
  </section>
</template>
