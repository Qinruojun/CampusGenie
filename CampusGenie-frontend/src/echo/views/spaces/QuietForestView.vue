<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { ArrowLeft, CirclePlay, TreePine } from "lucide-vue-next";

const isMeditating = ref(false);

function startMeditation() {
  isMeditating.value = true;
}
</script>

<template>
  <section class="meditation-view">
    <RouterLink class="meditation-back" :to="{ name: 'home' }">
      <ArrowLeft :size="17" />
      <span>返回精神空间</span>
    </RouterLink>

    <div class="meditation-hero">
      <div class="meditation-copy">
        <span class="meditation-kicker">
          <TreePine :size="18" />
          冥想社区
        </span>
        <h1>冥想社区</h1>
        <p class="subtitle">静下来，听听自己</p>
        <p>
          这里适合低能量、疲惫或者想短暂停靠的人。你可以跟着一段很短的呼吸练习，
          先把注意力放回身体，再慢慢整理今天的感受。
        </p>

        <button class="meditation-start" type="button" @click="startMeditation">
          <CirclePlay :size="19" />
          <span>{{ isMeditating ? "冥想进行中" : "开始冥想" }}</span>
        </button>
      </div>

      <div class="meditation-orb" :class="{ active: isMeditating }" aria-hidden="true">
        <TreePine :size="86" />
      </div>
    </div>

    <p v-if="isMeditating" class="breath-line">吸气 4 秒，停留 2 秒，呼气 6 秒。重复三轮就好。</p>
  </section>
</template>

<style scoped>
.meditation-view {
  display: grid;
  gap: 18px;
  min-height: calc(100vh - 160px);
}

.meditation-back {
  display: inline-flex;
  align-items: center;
  justify-self: start;
  gap: 8px;
  color: #26362f;
  text-decoration: none;
  font-weight: 900;
}

.meditation-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(240px, 0.55fr);
  gap: 32px;
  align-items: center;
  min-height: 560px;
  padding: clamp(28px, 6vw, 72px);
  border: 1px solid rgba(38, 49, 45, 0.12);
  border-radius: 8px;
  background:
    radial-gradient(circle at 74% 28%, rgba(255, 255, 255, 0.82), transparent 34%),
    linear-gradient(135deg, #eff6d7, rgba(255, 252, 246, 0.92)),
    #fffaf2;
  box-shadow: 0 24px 64px rgba(53, 61, 55, 0.12);
}

.meditation-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #729646;
  font-size: 14px;
  font-weight: 900;
}

.meditation-copy h1 {
  max-width: 720px;
  margin: 22px 0 0;
  color: #1f342b;
  font-size: clamp(42px, 7vw, 78px);
}

.subtitle {
  margin: 18px 0 0;
  color: #729646;
  font-size: 22px;
  font-weight: 900;
}

.meditation-copy > p:not(.subtitle) {
  max-width: 680px;
  color: #53615a;
  font-size: 17px;
  line-height: 1.8;
}

.meditation-start {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  min-height: 46px;
  margin-top: 26px;
  padding: 10px 18px;
  color: #fff;
  border: 0;
  border-radius: 8px;
  background: #729646;
  font-weight: 900;
}

.meditation-orb {
  display: grid;
  width: min(300px, 100%);
  aspect-ratio: 1;
  place-items: center;
  justify-self: center;
  color: #729646;
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(255, 255, 255, 0.28)),
    #eff6d7;
  box-shadow:
    0 28px 80px rgba(53, 61, 55, 0.14),
    inset 0 0 34px rgba(255, 255, 255, 0.58);
}

.meditation-orb.active {
  animation: breathe 5s ease-in-out infinite;
}

.breath-line {
  justify-self: start;
  margin: 0;
  padding: 12px 16px;
  color: #4d6840;
  border: 1px solid rgba(114, 150, 70, 0.2);
  border-radius: 8px;
  background: rgba(239, 246, 215, 0.72);
  font-weight: 800;
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(0.96);
  }

  50% {
    transform: scale(1.05);
  }
}

@media (max-width: 760px) {
  .meditation-hero {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .meditation-orb {
    width: min(220px, 100%);
    order: -1;
  }
}
</style>
