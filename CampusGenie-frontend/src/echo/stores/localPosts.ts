import { ref } from "vue";
import type { PostItem } from "../data/mock";

const STORAGE_KEY = "echo-local-posts";

function readStoredPosts(): PostItem[] {
  if (typeof window === "undefined") {
    return [];
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) as PostItem[] : [];
  } catch {
    return [];
  }
}

function persistPosts() {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(localPosts.value));
}

export const localPosts = ref<PostItem[]>(readStoredPosts());

export function addLocalPost(input: Omit<PostItem, "id" | "createdAt" | "reactions" | "comments">) {
  const post: PostItem = {
    ...input,
    id: Date.now(),
    createdAt: "刚刚",
    reactions: { HUG: 0, UNDERSTOOD: 0, ME_TOO: 0 },
    comments: 0
  };

  localPosts.value = [post, ...localPosts.value];
  persistPosts();
  return post;
}
