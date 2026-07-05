<script setup lang="ts">
import type { Component, PropType } from "vue";
import { RouterLink, type RouteLocationRaw } from "vue-router";
import { ArrowLeft, Compass, PenLine } from "lucide-vue-next";

defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  icon: {
    type: Object as PropType<Component>,
    required: true
  },
  tone: {
    type: String,
    required: true
  },
  accent: {
    type: String,
    required: true
  },
  primaryTo: {
    type: Object as PropType<RouteLocationRaw>,
    default: () => ({ name: "create-post" })
  },
  primaryLabel: {
    type: String,
    default: "发布一条回声"
  }
});
</script>

<template>
  <section class="space-route" :style="{ '--tone': tone, '--accent': accent }">
    <RouterLink class="back-link" :to="{ name: 'home' }">
      <ArrowLeft :size="17" />
      <span>返回精神空间</span>
    </RouterLink>

    <div class="space-hero">
      <div class="space-copy">
        <span class="route-kicker">
          <Compass :size="18" />
          精神空间入口
        </span>
        <h1>{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
        <p>{{ description }}</p>

        <div class="route-actions">
          <RouterLink class="primary-action" :to="primaryTo">
            <PenLine :size="18" />
            <span>{{ primaryLabel }}</span>
          </RouterLink>
          <RouterLink class="ghost-action" :to="{ name: 'map' }">看看全部岛屿</RouterLink>
        </div>
      </div>

      <div class="route-orb" aria-hidden="true">
        <component :is="icon" :size="86" />
      </div>
    </div>
  </section>
</template>

<style scoped>
.space-route {
  display: grid;
  gap: 24px;
  min-height: calc(100vh - 160px);
}

.back-link,
.primary-action,
.ghost-action {
  display: inline-flex;
  align-items: center;
  justify-self: start;
  gap: 8px;
  color: #26362f;
  text-decoration: none;
  font-weight: 900;
}

.space-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(240px, 0.55fr);
  gap: 32px;
  align-items: center;
  overflow: hidden;
  min-height: 560px;
  padding: clamp(28px, 6vw, 72px);
  border: 1px solid rgba(38, 49, 45, 0.12);
  border-radius: 8px;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--accent) 82%, #fff), rgba(255, 252, 246, 0.9)),
    #fffaf2;
  box-shadow: 0 24px 64px rgba(53, 61, 55, 0.12);
}

.route-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--tone);
  font-size: 14px;
  font-weight: 900;
}

h1 {
  max-width: 720px;
  margin-top: 22px;
  color: #1f342b;
  font-size: clamp(42px, 7vw, 78px);
}

.subtitle {
  margin-top: 18px;
  color: var(--tone);
  font-size: 22px;
  font-weight: 900;
}

.space-copy > p:not(.subtitle) {
  max-width: 680px;
  color: #53615a;
  font-size: 17px;
  line-height: 1.8;
}

.route-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 32px;
}

.primary-action,
.ghost-action {
  min-height: 44px;
  padding: 10px 16px;
  border-radius: 8px;
}

.primary-action {
  color: #fff;
  background: var(--tone);
}

.ghost-action {
  color: var(--tone);
  border: 1px solid color-mix(in srgb, var(--tone) 28%, transparent);
  background: rgba(255, 255, 255, 0.66);
}

.route-orb {
  display: grid;
  width: min(300px, 100%);
  aspect-ratio: 1;
  place-items: center;
  justify-self: center;
  color: var(--tone);
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(255, 255, 255, 0.28)),
    var(--accent);
  box-shadow:
    0 28px 80px rgba(53, 61, 55, 0.14),
    inset 0 0 34px rgba(255, 255, 255, 0.58);
}

@media (max-width: 760px) {
  .space-hero {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .route-orb {
    width: min(220px, 100%);
    order: -1;
  }
}
</style>
