<script setup lang="ts">
import { BookOpen, Music } from "lucide-vue-next";

defineProps<{
  isJazzIsland: boolean;
  isJazzPlaying: boolean;
}>();

const emit = defineEmits<{
  playJazz: [];
}>();

const jazzLibraryCards = [
  {
    icon: BookOpen,
    title: "爵士乐的历史",
    copy: "从新奥尔良、摇摆年代到硬波普，先听见爵士乐为什么会这样说话。",
    action: ""
  },
  {
    icon: Music,
    title: "Play Jazz",
    copy: "等你把音频资源放进来后，这里会直接播放岛内爵士乐。",
    action: "play"
  },
  {
    icon: BookOpen,
    title: "即兴的智慧",
    copy: "即兴不是没有规则，而是在规则里保留回应现场的自由。",
    action: ""
  }
] as const;
</script>

<template>
  <div class="library-view" :class="{ 'jazz-library-view': isJazzIsland }">
    <template v-if="isJazzIsland">
      <figure class="jazz-room-hero">
        <img src="/assets/jazz/jazz-room.jpg" alt="爵士唱片室里的钢琴、唱片架和木凳" />
        <figcaption>
          <p class="eyebrow">Vinyl Room</p>
          <h2>唱片、钢琴和留给即兴的安静角落</h2>
          <p>把书房从普通资料列表变成爵士岛的唱片室，资源仍然可以被收藏进路标。</p>
        </figcaption>
      </figure>

      <div class="book-list jazz-book-list">
        <article v-for="item in jazzLibraryCards" :key="item.title">
          <component :is="item.icon" :size="20" />
          <div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.copy }}</p>
          </div>
          <button v-if="item.action === 'play'" type="button" class="library-play-button" @click="emit('playJazz')">
            {{ isJazzPlaying ? "暂停" : "播放" }}
          </button>
        </article>
      </div>
    </template>

    <div v-else class="book-list">
      <article>
        <BookOpen :size="20" />
        <div>
          <h3>爵士乐的历史</h3>
          <p>了解过去，才能听懂斑驳的即兴。</p>
        </div>
      </article>
      <article>
        <Music :size="20" />
        <div>
          <h3>Play Jazz</h3>
          <p>打开文档指定的 Bilibili 爵士乐入口。</p>
        </div>
        <a href="https://www.bilibili.com/video/BV1k64y1B76M/" target="_blank" rel="noreferrer">播放</a>
      </article>
      <article>
        <BookOpen :size="20" />
        <div>
          <h3>即兴的智慧</h3>
          <p>即兴不是没有规则，而是拥有自己的规则。</p>
        </div>
      </article>
    </div>
  </div>
</template>
