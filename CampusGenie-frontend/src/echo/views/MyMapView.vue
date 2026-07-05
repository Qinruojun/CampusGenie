<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { islands as mockIslands, type Island } from "../data/mock";
import { customIslands } from "../stores/customIslands";

type MapLine = readonly [string, string];
type MapIslandNode = Island & {
  customId?: string;
};

const props = defineProps<{
  islands?: Island[];
  mapLines?: readonly MapLine[];
}>();

const emit = defineEmits<{
  openIsland: [slug: string];
}>();

const route = useRoute();
const router = useRouter();
const defaultIslandNames = ["保研岛", "树洞岛", "爵士岛", "跑步岛"];

const requestedIslands = computed(() => {
  const raw = route.query.islands;
  const values = Array.isArray(raw) ? raw : typeof raw === "string" ? raw.split(",") : defaultIslandNames;
  const names = values.filter((item): item is string => typeof item === "string" && item.trim().length > 0);
  return names.length > 0 ? names.map((item) => item.trim()) : defaultIslandNames;
});

const customMapNodes = computed<MapIslandNode[]>(() =>
  customIslands.value.map((island, index) => ({
    id: Number.MAX_SAFE_INTEGER - index,
    slug: `custom-${island.id}`,
    customId: island.id,
    name: island.name,
    subtitle: island.motto || "find your own way",
    description: "这是你创建的社区，已经加入社区推荐和精神空间首页。",
    slogan: island.motto || "find your own way",
    themeColor: island.themeColor,
    accentColor: "#eef5e8",
    season: island.isChildIsland ? "二级社区" : "自建社区",
    seasonSummary: "保存在本地的自建社区。",
    mood: "等待新的故事",
    population: 1,
    resources: 0,
    position: {
      x: 18 + (index % 4) * 21,
      y: 78 + Math.floor(index / 4) * 10
    }
  }))
);

const visibleIslands = computed<MapIslandNode[]>(() => {
  const source = props.islands?.length ? props.islands : mockIslands;
  const requested = new Set(requestedIslands.value);
  const selected = source.filter((island) => requested.has(island.name) || requested.has(island.slug));
  const baseNodes = selected.length > 0 ? selected : mockIslands.filter((island) => defaultIslandNames.includes(island.name));
  return [...baseNodes, ...customMapNodes.value];
});

const visibleMapLines = computed<readonly MapLine[]>(() => {
  const validSlugs = new Set(visibleIslands.value.map((island) => island.slug));
  const incomingLines = props.mapLines?.filter((line) => validSlugs.has(line[0]) && validSlugs.has(line[1]));

  if (incomingLines?.length) {
    return incomingLines;
  }

  const slugs = visibleIslands.value.map((island) => island.slug);
  return slugs.slice(1).map((slug, index) => [slugs[index], slug] as const);
});

function openMapNode(island: MapIslandNode) {
  if (island.customId) {
    void router.push({ name: "custom-island", params: { id: island.customId } });
    return;
  }

  emit("openIsland", island.slug);
}
</script>

<template>
  <section class="map-view">
    <div class="section-heading">
      <p class="eyebrow">我的地图</p>
      <h1>点击一座岛，进入它的社区生活。</h1>
    </div>

    <div class="map-stage">
      <svg viewBox="0 0 100 100" aria-hidden="true">
        <line
          v-for="line in visibleMapLines"
          :key="line.join('-')"
          :x1="visibleIslands.find((item) => item.slug === line[0])?.position.x"
          :y1="visibleIslands.find((item) => item.slug === line[0])?.position.y"
          :x2="visibleIslands.find((item) => item.slug === line[1])?.position.x"
          :y2="visibleIslands.find((item) => item.slug === line[1])?.position.y"
        />
      </svg>
      <button
        v-for="island in visibleIslands"
        :key="island.slug"
        type="button"
        class="map-node"
        :style="{ left: `${island.position.x}%`, top: `${island.position.y}%`, '--node-color': island.themeColor }"
        @click="openMapNode(island)"
      >
        <span></span>
        <strong>{{ island.name }}</strong>
        <small>{{ island.season }}</small>
      </button>
    </div>
  </section>
</template>
