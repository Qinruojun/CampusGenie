import { ref } from "vue";

const STORAGE_KEY = "echo-custom-islands";

export type CustomIsland = {
  id: string;
  name: string;
  motto: string;
  coverUrl: string;
  themeColor: string;
  parentIsland: string;
  isChildIsland: boolean;
  visibility: "PUBLIC" | "PRIVATE";
  createdAt: string;
};

function readStoredIslands(): CustomIsland[] {
  if (typeof window === "undefined") {
    return [];
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) as CustomIsland[] : [];
  } catch {
    return [];
  }
}

function persistIslands() {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(customIslands.value));
}

export const customIslands = ref<CustomIsland[]>(readStoredIslands());

export function addCustomIsland(input: Omit<CustomIsland, "id" | "createdAt">) {
  const island: CustomIsland = {
    ...input,
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    createdAt: new Date().toISOString()
  };

  customIslands.value = [island, ...customIslands.value];
  persistIslands();
  return island;
}

export function removeCustomIsland(id: string) {
  const before = customIslands.value.length;
  customIslands.value = customIslands.value.filter((island) => island.id !== id);
  persistIslands();
  return customIslands.value.length < before;
}

export function findCustomIsland(id: string) {
  return customIslands.value.find((island) => island.id === id);
}
