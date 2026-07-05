import { statusCards } from "../data/mock";
import type { ResourceType } from "../data/mock";

export function typeLabel(type: string) {
  const labels: Record<string, string> = {
    EXPERIENCE: "经验",
    ARTICLE: "文章",
    BOOK: "书籍",
    MUSIC: "音乐",
    MOVIE: "电影",
    LINK: "链接",
    TEMPLATE: "模板",
    FILE: "文件",
    CHECKLIST: "清单",
    TOOL: "工具",
    WARNING: "避坑",
    POST: "帖子"
  };
  return labels[type] ?? type;
}

export function optionLabel(options: readonly (readonly [string, string])[], value: string) {
  return options.find(([key]) => key === value)?.[1] ?? value;
}

export function statusCardLabel(value: string) {
  return optionLabel(statusCards, value);
}

export function reactionLabel(value: string) {
  const labels: Record<string, string> = {
    HUG: "抱抱",
    THANK_YOU: "谢谢你",
    UNDERSTOOD: "我懂了",
    HELPFUL: "帮到我了",
    ME_TOO: "我也经历过",
    SAVE_ROADMARK: "收进路标"
  };
  return labels[value] ?? value;
}

export function normalizeResourceType(type: string): ResourceType {
  const allowedTypes = new Set([
    "EXPERIENCE",
    "ARTICLE",
    "BOOK",
    "MUSIC",
    "LINK",
    "TEMPLATE",
    "CHECKLIST",
    "TOOL",
    "WARNING",
    "POST"
  ]);
  return (allowedTypes.has(type) ? type : "ARTICLE") as ResourceType;
}
