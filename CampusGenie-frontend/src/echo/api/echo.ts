import { apiRequest, setAuthToken } from "../components/client.ts";

export interface ApiUser {
  id: number;
  username: string;
  nickname: string;
  avatarUrl?: string;
  email?: string;
  bio?: string;
  status: string;
}

export interface ApiAuthResponse {
  token: string;
  user: ApiUser;
}

export interface ApiIsland {
  id: number;
  name: string;
  slug: string;
  description?: string;
  slogan?: string;
  coverUrl?: string;
  iconUrl?: string;
  themeColor?: string;
  createdAt?: string;
}

export interface ApiPost {
  id: number;
  islandId: number;
  authorId?: number;
  authorName?: string;
  title: string;
  content: string;
  postType?: string;
  statusCard?: string;
  replyPreference?: string;
  anonymous: boolean;
  createdAt?: string;
}

export interface ApiResource {
  id: number;
  islandId: number;
  title: string;
  description?: string;
  resourceType: string;
  url?: string;
  coverUrl?: string;
  content?: string;
  status?: string;
  createdAt?: string;
}

export interface ApiRoadmark {
  id: number;
  userId: number;
  targetType: "POST" | "RESOURCE";
  targetId: number;
  islandId?: number;
  note?: string;
  createdAt?: string;
}

export interface ApiReaction {
  id: number;
  targetType: string;
  targetId: number;
  userId: number;
  reactionType: string;
  createdAt?: string;
}

export interface ApiWikiPage {
  id: number;
  islandId: number;
  title: string;
  slug: string;
  contentJson?: Record<string, unknown>;
  contentHtml?: string;
  version: number;
}

export interface ApiEmotionWeather {
  date: string;
  total: number;
  items: Array<{
    emotion: string;
    label: string;
    percent: number;
  }>;
}

export function login(username: string, password: string) {
  return apiRequest<ApiAuthResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password })
  });
}

export async function loginAsDemo() {
  const response = await login("demo", "demo123");
  setAuthToken(response.token);
  return response.user;
}

export function me() {
  return apiRequest<ApiUser>("/api/auth/me");
}

export function listIslands() {
  return apiRequest<ApiIsland[]>("/api/islands");
}

export function listPosts(islandId: number) {
  return apiRequest<ApiPost[]>(`/api/islands/${islandId}/posts`);
}

export function createPost(
  islandId: number,
  payload: {
    title: string;
    content: string;
    postType?: string;
    statusCard: string;
    replyPreference: string;
    anonymous: boolean;
  }
) {
  return apiRequest<ApiPost>(`/api/islands/${islandId}/posts`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function listResources(islandId: number) {
  return apiRequest<ApiResource[]>(`/api/islands/${islandId}/resources`);
}

export function submitResource(payload: {
  islandId: number;
  title: string;
  description: string;
  resourceType: string;
  url?: string;
  reason?: string;
}) {
  return apiRequest<{ id: number; status: string }>("/api/resources/submissions", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function listRoadmarks() {
  return apiRequest<ApiRoadmark[]>("/api/me/roadmarks");
}

export function createRoadmark(payload: {
  targetType: "POST" | "RESOURCE";
  targetId: number;
  islandId?: number;
  note?: string;
}) {
  return apiRequest<ApiRoadmark>("/api/roadmarks", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function createReaction(payload: {
  targetType: "POST" | "COMMENT" | "RESOURCE";
  targetId: number;
  reactionType: string;
}) {
  return apiRequest<ApiReaction>("/api/reactions", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function listWikiPages(islandId: number) {
  return apiRequest<ApiWikiPage[]>(`/api/islands/${islandId}/wiki`);
}

export function createWikiEditRequest(
  pageId: number,
  payload: {
    proposedContentJson?: Record<string, unknown>;
    proposedContentHtml: string;
    changeSummary: string;
  }
) {
  return apiRequest<{ id: number; status: string }>(`/api/wiki/pages/${pageId}/edit-requests`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function emotionWeather(islandId: number) {
  return apiRequest<ApiEmotionWeather>(`/api/islands/${islandId}/emotion-weather`);
}
