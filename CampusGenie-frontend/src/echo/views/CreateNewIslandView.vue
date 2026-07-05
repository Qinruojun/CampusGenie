<script setup lang="ts">
import { computed, ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { ArrowLeft, ChevronDown, ImagePlus, Leaf, Plus, Sprout, UploadCloud } from "lucide-vue-next";
import { addCustomIsland } from "../stores/customIslands";

const router = useRouter();
const defaultCover = "/assets/create-island/lighthouse.jpg";

const fileInput = ref<HTMLInputElement | null>(null);
const islandName = ref("");
const islandMotto = ref("");
const coverUrl = ref(defaultCover);
const isChildIsland = ref(false);
const parentIsland = ref("");
const errorMessage = ref("");

const parentOptions = [
  { value: "", label: "请选择一个父岛屿" },
  { value: "quiet-forest", label: "冥想社区" },
  { value: "lighthouse-hill", label: "灯塔丘" },
  { value: "heard-bay", label: "被听见湾" },
  { value: "dream-island", label: "想象岛" }
];

const previewName = computed(() => islandName.value.trim() || "未命名岛屿");
const previewMotto = computed(() => islandMotto.value.trim() || "这里什么都还没有写");
const nameCount = computed(() => islandName.value.length);
const mottoCount = computed(() => islandMotto.value.length);

function openFilePicker() {
  fileInput.value?.click();
}

function handleCoverUpload(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) {
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    if (typeof reader.result === "string") {
      coverUrl.value = reader.result;
    }
  };
  reader.readAsDataURL(file);
}

function createIsland() {
  const name = islandName.value.trim();
  const motto = islandMotto.value.trim();

  if (!name) {
    errorMessage.value = "请先给岛屿起一个名字。";
    return;
  }

  const island = addCustomIsland({
    name,
    motto: motto || "慢慢建造属于这里的故事",
    coverUrl: coverUrl.value,
    themeColor: "#2f5f4f",
    parentIsland: isChildIsland.value ? parentIsland.value : "",
    isChildIsland: isChildIsland.value,
    visibility: "PUBLIC"
  });

  void router.push({ name: "map", query: { created: island.id } });
}
</script>

<template>
  <section class="cg-create-page">
    <RouterLink class="cg-back-link" :to="{ name: 'home' }">
      <ArrowLeft :size="19" />
      <span>返回精神空间</span>
    </RouterLink>

    <div class="cg-create-card">
      <form class="cg-create-form" @submit.prevent="createIsland">
        <header class="cg-create-header">
          <div class="cg-header-row">
            <span class="cg-plus-badge">
              <Plus :size="25" />
            </span>
            <span class="cg-kicker">Create island</span>
          </div>
          <h1>创建岛屿</h1>
          <p>这里可以接入新的社区创建表单：岛屿名、简介、主题色、可见性和加入方式。</p>
        </header>

        <div class="cg-field">
          <label>岛屿封面</label>
          <input
            ref="fileInput"
            class="cg-file-input"
            type="file"
            accept="image/png,image/jpeg,image/jpg"
            @change="handleCoverUpload"
          />
          <button class="cg-upload-control" type="button" @click="openFilePicker">
            <img :src="coverUrl" alt="岛屿封面预览" />
            <span class="cg-upload-copy">
              <span class="cg-upload-title">
                <UploadCloud :size="22" />
                点击上传或拖拽图片
              </span>
              <span>支持 JPG / PNG，建议 16:9</span>
            </span>
          </button>
        </div>

        <div class="cg-field">
          <label for="island-name">岛屿名称</label>
          <div class="cg-input-wrap">
            <input
              id="island-name"
              v-model="islandName"
              maxlength="20"
              type="text"
              placeholder="给你的岛屿起个名字吧~"
            />
            <span>{{ nameCount }} / 20</span>
          </div>
        </div>

        <div class="cg-field">
          <label for="island-motto">岛屿格言</label>
          <div class="cg-input-wrap cg-textarea-wrap">
            <textarea
              id="island-motto"
              v-model="islandMotto"
              maxlength="60"
              rows="3"
              placeholder="写一句属于这个岛的格言..."
            />
            <span>{{ mottoCount }} / 60</span>
          </div>
        </div>

        <label class="cg-check-row">
          <input v-model="isChildIsland" type="checkbox" />
          <span>创建为二级岛屿（归属于某个岛）</span>
        </label>

        <div class="cg-field">
          <label for="parent-island">选择父岛屿</label>
          <div class="cg-select-wrap" :class="{ 'is-disabled': !isChildIsland }">
            <Sprout :size="18" />
            <select id="parent-island" v-model="parentIsland" :disabled="!isChildIsland">
              <option v-for="option in parentOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
            <ChevronDown :size="18" />
          </div>
        </div>

        <p v-if="errorMessage" class="cg-error">{{ errorMessage }}</p>

        <button class="cg-submit" type="submit">
          <Leaf :size="18" />
          创建岛屿
        </button>
      </form>

      <aside class="cg-preview-card" aria-label="岛屿预览">
        <img :src="coverUrl" alt="岛屿预览图" />
        <span class="cg-preview-pill">预览</span>
        <div class="cg-preview-shade"></div>
        <div class="cg-preview-content">
          <h2>{{ previewName }}</h2>
          <p>“{{ previewMotto }}”</p>
          <div class="cg-preview-meta">
            <span>
              <ImagePlus :size="18" />
            </span>
            <strong>{{ isChildIsland ? "二级岛屿" : "一级岛屿" }}</strong>
            <i></i>
            <strong>公开可见</strong>
          </div>
        </div>
      </aside>
    </div>

    <div class="cg-bg-forest cg-bg-left"></div>
    <div class="cg-bg-forest cg-bg-right"></div>
  </section>
</template>

<style scoped>
.cg-create-page {
  position: relative;
  width: 100vw;
  min-height: 100vh;
  margin-left: calc(50% - 50vw);
  margin-top: -34px;
  overflow: hidden;
  padding: 38px clamp(28px, 5vw, 78px) 70px;
  color: #214436;
  background:
    radial-gradient(circle at 76% 27%, rgba(255, 255, 255, 0.96) 0 2px, transparent 3px),
    radial-gradient(circle at 89% 41%, rgba(255, 255, 255, 0.9) 0 2px, transparent 3px),
    linear-gradient(120deg, rgba(244, 239, 220, 0.66), rgba(248, 250, 237, 0.8) 46%, rgba(228, 235, 221, 0.78)),
    #f5f2e8;
  box-sizing: border-box;
}

.cg-create-page::before,
.cg-create-page::after {
  position: absolute;
  inset: auto auto -120px -80px;
  width: 520px;
  height: 280px;
  content: "";
  pointer-events: none;
  background:
    linear-gradient(140deg, transparent 0 35%, rgba(75, 112, 83, 0.22) 36% 40%, transparent 41%),
    linear-gradient(155deg, transparent 0 45%, rgba(56, 96, 71, 0.18) 46% 50%, transparent 51%),
    radial-gradient(ellipse at 50% 100%, rgba(87, 128, 92, 0.3), transparent 68%);
  filter: blur(0.2px);
  opacity: 0.55;
}

.cg-create-page::after {
  right: -120px;
  left: auto;
  transform: scaleX(-1);
}

.cg-back-link {
  position: relative;
  z-index: 2;
  display: inline-flex;
  gap: 9px;
  align-items: center;
  color: #395849;
  font-size: 16px;
  font-weight: 800;
  text-decoration: none;
}

.cg-create-card {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(420px, 526px) minmax(440px, 542px);
  gap: clamp(42px, 5vw, 64px);
  align-items: center;
  width: min(1216px, calc(100vw - 120px));
  min-height: 748px;
  margin: 34px auto 0;
  padding: 54px 66px;
  background: rgba(255, 253, 247, 0.88);
  border: 1px solid rgba(45, 83, 65, 0.1);
  border-radius: 30px;
  box-shadow: 0 28px 80px rgba(51, 66, 53, 0.16);
  box-sizing: border-box;
  backdrop-filter: blur(14px);
}

.cg-create-form {
  display: flex;
  flex-direction: column;
  gap: 17px;
  width: 100%;
}

.cg-create-header {
  margin-bottom: 5px;
}

.cg-header-row {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 8px;
}

.cg-plus-badge {
  display: grid;
  width: 58px;
  height: 58px;
  color: #2d6d55;
  place-items: center;
  background: #edf8ed;
  border: 1px solid #cce4cf;
  border-radius: 999px;
}

.cg-kicker {
  color: #6f8076;
  font-size: 15px;
  font-weight: 800;
}

.cg-create-header h1 {
  margin: 0;
  color: #1f4638;
  font-family: Georgia, "Times New Roman", "Microsoft YaHei", serif;
  font-size: clamp(46px, 4.3vw, 62px);
  font-weight: 900;
  line-height: 1.06;
  letter-spacing: 0;
}

.cg-create-header p {
  max-width: 560px;
  margin: 22px 0 0;
  color: #7c887f;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.7;
}

.cg-field {
  display: grid;
  gap: 9px;
}

.cg-field label {
  color: #415949;
  font-size: 15px;
  font-weight: 900;
}

.cg-file-input {
  position: fixed;
  width: 1px;
  height: 1px;
  opacity: 0;
  pointer-events: none;
}

.cg-upload-control {
  all: unset;
  display: grid;
  grid-template-columns: 172px minmax(0, 1fr);
  gap: 26px;
  align-items: center;
  min-height: 126px;
  padding: 12px 28px 12px 12px;
  background: rgba(255, 255, 255, 0.58);
  border: 1px solid rgba(59, 79, 65, 0.17);
  border-radius: 8px;
  box-sizing: border-box;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.cg-upload-control:hover {
  border-color: rgba(47, 95, 79, 0.34);
  box-shadow: 0 14px 28px rgba(43, 66, 53, 0.08);
  transform: translateY(-1px);
}

.cg-upload-control img {
  width: 172px;
  height: 98px;
  object-fit: cover;
  border-radius: 8px;
}

.cg-upload-copy {
  display: grid;
  gap: 10px;
  min-width: 0;
  color: #9aa29c;
  font-size: 15px;
  font-weight: 700;
}

.cg-upload-title {
  display: inline-flex;
  gap: 12px;
  align-items: center;
  color: #6f7e73;
  font-size: 17px;
  font-weight: 900;
  white-space: normal;
}

.cg-input-wrap,
.cg-select-wrap {
  display: flex;
  align-items: center;
  min-height: 50px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.54);
  border: 1px solid rgba(59, 79, 65, 0.18);
  border-radius: 8px;
  box-shadow: inset 0 1px 2px rgba(59, 79, 65, 0.04);
  box-sizing: border-box;
}

.cg-input-wrap:focus-within,
.cg-select-wrap:focus-within {
  border-color: rgba(47, 95, 79, 0.42);
  box-shadow: 0 0 0 3px rgba(78, 125, 90, 0.1);
}

.cg-input-wrap input,
.cg-input-wrap textarea,
.cg-select-wrap select {
  width: 100%;
  min-width: 0;
  color: #263f35;
  font: inherit;
  font-size: 15px;
  font-weight: 650;
  line-height: 1.5;
  background: transparent;
  border: 0;
  outline: 0;
}

.cg-input-wrap input::placeholder,
.cg-input-wrap textarea::placeholder {
  color: #aab2ac;
}

.cg-input-wrap > span {
  flex: 0 0 auto;
  margin-left: 10px;
  color: #9ba49e;
  font-size: 14px;
  font-weight: 800;
}

.cg-textarea-wrap {
  align-items: flex-end;
  min-height: 104px;
  padding-top: 13px;
  padding-bottom: 13px;
}

.cg-textarea-wrap textarea {
  height: 76px;
  resize: none;
}

.cg-check-row {
  display: inline-flex;
  gap: 10px;
  align-items: center;
  color: #526a5a;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.cg-check-row input {
  width: 18px;
  height: 18px;
  margin: 0;
  accent-color: #2f5f4f;
}

.cg-select-wrap {
  gap: 10px;
  color: #a2aca5;
}

.cg-select-wrap select {
  color: #6f7c72;
  appearance: none;
  cursor: pointer;
}

.cg-select-wrap.is-disabled {
  opacity: 0.64;
}

.cg-select-wrap.is-disabled select {
  cursor: not-allowed;
}

.cg-error {
  margin: -3px 0 0;
  color: #b55a4b;
  font-size: 14px;
  font-weight: 800;
}

.cg-submit {
  display: inline-flex;
  gap: 10px;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 52px;
  margin-top: 4px;
  color: #fffdf7;
  font-size: 16px;
  font-weight: 900;
  background: linear-gradient(180deg, #356957, #244b3e);
  border: 0;
  border-radius: 7px;
  box-shadow: 0 14px 30px rgba(35, 75, 61, 0.24);
  cursor: pointer;
}

.cg-submit:hover {
  transform: translateY(-1px);
}

.cg-preview-card {
  position: relative;
  height: 646px;
  overflow: hidden;
  background: #dce9ef;
  border-radius: 13px;
  box-shadow: 0 20px 46px rgba(41, 67, 57, 0.18);
}

.cg-preview-card > img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cg-preview-pill {
  position: absolute;
  top: 24px;
  right: 22px;
  z-index: 2;
  min-width: 78px;
  padding: 11px 18px;
  color: #6c7d72;
  font-size: 16px;
  font-weight: 900;
  text-align: center;
  background: rgba(255, 255, 255, 0.86);
  border-radius: 16px;
  box-shadow: 0 8px 22px rgba(41, 67, 57, 0.11);
  box-sizing: border-box;
}

.cg-preview-shade {
  position: absolute;
  inset: 46% 0 0;
  background: linear-gradient(180deg, transparent, rgba(25, 64, 51, 0.82));
}

.cg-preview-content {
  position: absolute;
  right: 30px;
  bottom: 32px;
  left: 30px;
  color: #ffffff;
}

.cg-preview-content h2 {
  margin: 0;
  color: #fff;
  font-size: clamp(34px, 3vw, 44px);
  font-weight: 950;
  line-height: 1.05;
  letter-spacing: 0;
  text-shadow: 0 3px 16px rgba(0, 0, 0, 0.2);
}

.cg-preview-content p {
  margin: 18px 0 28px;
  color: rgba(255, 255, 255, 0.92);
  font-size: 21px;
  font-weight: 750;
  line-height: 1.5;
}

.cg-preview-meta {
  display: inline-flex;
  gap: 14px;
  align-items: center;
  max-width: 100%;
  padding: 9px 18px 9px 11px;
  background: rgba(34, 82, 64, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 999px;
  backdrop-filter: blur(10px);
}

.cg-preview-meta span {
  display: grid;
  width: 34px;
  height: 34px;
  color: #f5fff2;
  place-items: center;
  background: rgba(183, 218, 111, 0.68);
  border-radius: 999px;
}

.cg-preview-meta strong {
  color: rgba(255, 255, 255, 0.94);
  font-size: 16px;
  font-style: normal;
  font-weight: 900;
  white-space: nowrap;
}

.cg-preview-meta i {
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.72);
  border-radius: 999px;
}

.cg-bg-forest {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  height: 190px;
  pointer-events: none;
  background:
    linear-gradient(145deg, transparent 0 48%, rgba(68, 104, 75, 0.2) 49% 53%, transparent 54%),
    linear-gradient(156deg, transparent 0 42%, rgba(77, 118, 84, 0.18) 43% 48%, transparent 49%),
    linear-gradient(180deg, transparent, rgba(133, 158, 128, 0.24));
  opacity: 0.55;
}

@media (max-width: 1440px) and (min-width: 1121px) {
  .cg-create-page {
    padding: 26px 44px 48px;
  }

  .cg-back-link {
    font-size: 14px;
  }

  .cg-create-card {
    grid-template-columns: minmax(360px, 470px) minmax(360px, 470px);
    gap: 38px;
    width: min(1080px, calc(100vw - 80px));
    min-height: 640px;
    margin-top: 24px;
    padding: 38px 44px;
    border-radius: 24px;
  }

  .cg-create-form {
    gap: 12px;
  }

  .cg-header-row {
    gap: 12px;
    margin-bottom: 6px;
  }

  .cg-plus-badge {
    width: 48px;
    height: 48px;
  }

  .cg-create-header h1 {
    font-size: 48px;
  }

  .cg-create-header p {
    margin-top: 12px;
    font-size: 14px;
    line-height: 1.5;
  }

  .cg-upload-control {
    grid-template-columns: 142px minmax(0, 1fr);
    gap: 16px;
    min-height: 98px;
    padding: 10px 18px 10px 10px;
  }

  .cg-upload-control img {
    width: 142px;
    height: 80px;
  }

  .cg-upload-copy {
    gap: 6px;
    font-size: 13px;
  }

  .cg-upload-title {
    gap: 8px;
    font-size: 15px;
  }

  .cg-input-wrap,
  .cg-select-wrap {
    min-height: 44px;
    padding: 0 14px;
  }

  .cg-textarea-wrap {
    min-height: 84px;
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .cg-textarea-wrap textarea {
    height: 58px;
  }

  .cg-submit {
    min-height: 46px;
  }

  .cg-preview-card {
    height: 520px;
  }

  .cg-preview-pill {
    top: 18px;
    right: 18px;
    min-width: 68px;
    padding: 9px 14px;
    font-size: 14px;
  }

  .cg-preview-content {
    right: 24px;
    bottom: 26px;
    left: 24px;
  }

  .cg-preview-content h2 {
    font-size: 36px;
  }

  .cg-preview-content p {
    margin: 14px 0 22px;
    font-size: 18px;
  }

  .cg-preview-meta {
    gap: 10px;
    padding: 8px 14px 8px 9px;
  }

  .cg-preview-meta strong {
    font-size: 14px;
  }
}

@media (max-height: 760px) and (min-width: 1121px) {
  .cg-create-page {
    padding-top: 22px;
    padding-bottom: 36px;
  }

  .cg-create-card {
    min-height: 580px;
    margin-top: 18px;
    padding: 30px 40px;
  }

  .cg-preview-card {
    height: 470px;
  }
}

@media (max-width: 1120px) {
  .cg-create-card {
    grid-template-columns: 1fr;
    width: min(760px, calc(100vw - 48px));
    padding: 42px;
  }

  .cg-preview-card {
    height: 520px;
  }
}

@media (max-width: 680px) {
  .cg-create-page {
    margin-top: -20px;
    padding: 24px 16px 42px;
  }

  .cg-create-card {
    width: 100%;
    min-height: 0;
    margin-top: 24px;
    padding: 26px 18px;
    border-radius: 22px;
  }

  .cg-create-header h1 {
    font-size: 42px;
  }

  .cg-upload-control {
    grid-template-columns: 1fr;
    padding: 12px;
  }

  .cg-upload-control img {
    width: 100%;
    height: 156px;
  }

  .cg-preview-card {
    height: 440px;
  }

  .cg-preview-content {
    right: 22px;
    left: 22px;
  }

  .cg-preview-meta {
    gap: 10px;
  }

  .cg-preview-meta strong {
    font-size: 14px;
  }
}
</style>
