<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AdminUserCard from '@/components/admin/Card/AdminUserCard.vue'
import { Import} from '@/api/admin/knowledge'

const router = useRouter()

const sidebarCollapsed = ref(false)

const fileInputRef = ref(null)

const templates = [
  {
    type: 'excel',
    title: 'Excel 导入模板',
    intro: '适合批量人工整理，表格字段为：',
    fields: ['问题', '回答', '分类', '来源'],
    button: '下载 Excel 模板',
    href: '/templates/KnowledgeBaseImportTemplate.xlsx'
  },
  {
    type: 'json',
    title: 'JSON 导入模板',
    intro: '适合程序生成数据，字段为：',
    fields: ['question', 'answer', 'category', 'source'],
    button: '下载 JSON 模板',
    href: '/templates/KnowledgeBaseImportTemplate.json'
  }
]

const importForm = reactive({
  strategy: 'ALL',
  autoCategory: false
})

const selectedFile = ref(null)
const dragActive = ref(false)
const submitting = ref(false)
const importStarted = ref(false)
const importResult = ref(null)
const responseMessage = ref('')

const uploadFileName = computed(() => {
  return selectedFile.value?.name || '暂未选择文件'
})

const uploadMeta = computed(() => {
  if (!selectedFile.value) {
    return '支持 .xlsx / .xls / .json，等待选择文件'
  }

  const size = selectedFile.value.size / 1024 / 1024
  const ext = selectedFile.value.name.split('.').pop()?.toUpperCase() || 'FILE'

  return `${ext} / ${size.toFixed(2)} MB / 待导入`
})

const fileStatusText = computed(() => {
  if (submitting.value) {
    return '导入中'
  }

  if (importResult.value) {
    return '已完成'
  }

  if (selectedFile.value) {
    return '待导入'
  }

  return '未选择'
})

const resultDescription = computed(() => {
  if (!importStarted.value) {
    return '等待导入任务开始'
  }

  if (submitting.value) {
    return '正在导入，请稍候'
  }

  if (!importResult.value) {
    return responseMessage.value || '暂无导入结果'
  }

  return importResult.value.summary || responseMessage.value || '导入已完成'
})

const importStats = computed(() => {
  const data = importResult.value || {}

  return [
    {
      label: '导入总数',
      value: data.totalCount ?? 0,
      tone: 'neutral'
    },
    {
      label: '成功数量',
      value: data.successCount ?? 0,
      tone: 'success'
    },
    {
      label: '失败数量',
      value: data.failCount ?? 0,
      tone: 'danger'
    },
    {
      label: '批次 ID',
      value: data.batchId ?? '-',
      tone: 'info'
    }
  ]
})

const failureRows = computed(() => {
  return (importResult.value?.errors || []).map((item) => {
    return {
      row: item.rowNo,
      question: item.question || '-',
      reason: item.reason || '-'
    }
  })
})

const progressPercent = computed(() => {
  const data = importResult.value

  if (!data || !data.totalCount) {
    return 0
  }

  return Math.round((data.successCount / data.totalCount) * 1000) / 10
})

const currentImportRecord = computed(() => {
  if (!importResult.value) {
    return []
  }

  return [
    {
      id: importResult.value.batchId || '-',
      fileName: selectedFile.value?.name || '-',
      type: getFileType(selectedFile.value?.name),
      total: importResult.value.totalCount || 0,
      success: importResult.value.successCount || 0,
      failed: importResult.value.failCount || 0,
      operator: '管理员',
      status: (importResult.value.failCount || 0) > 0 ? '部分失败' : '已完成',
      time: formatImportTime(importResult.value.importTime)
    }
  ]
})

function isSuccessCode(code) {
  return code === 200 || code === 1
}

function getFileType(fileName = '') {
  const lowerName = fileName.toLowerCase()

  if (lowerName.endsWith('.xlsx') || lowerName.endsWith('.xls')) {
    return 'Excel'
  }

  if (lowerName.endsWith('.json')) {
    return 'JSON'
  }

  return '-'
}

function validateFile(file) {
  const fileName = file.name.toLowerCase()

  const isValidType =
      fileName.endsWith('.xlsx') ||
      fileName.endsWith('.xls') ||
      fileName.endsWith('.json')

  if (!isValidType) {
    alert('文件格式不支持，请上传 .xlsx、.xls 或 .json 文件')
    return false
  }

  const maxSize = 20 * 1024 * 1024

  if (file.size > maxSize) {
    alert('单个文件不能超过 20 MB')
    return false
  }

  return true
}

function setSelectedFile(file) {
  if (!file) {
    selectedFile.value = null
    return
  }

  if (!validateFile(file)) {
    selectedFile.value = null

    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }

    return
  }

  selectedFile.value = file
  importStarted.value = false
  importResult.value = null
  responseMessage.value = ''
}

function handleFileChange(event) {
  const file = event.target.files?.[0] || null
  setSelectedFile(file)
}

function handleDrop(event) {
  dragActive.value = false
  const file = event.dataTransfer.files?.[0] || null
  setSelectedFile(file)
}

async function handleImport() {
  if (!selectedFile.value) {
    alert('请先选择要导入的文件')
    return
  }

  submitting.value = true
  importStarted.value = true
  importResult.value = null
  responseMessage.value = ''

  try {
    const formData = new FormData()

    formData.append('file', selectedFile.value)
    formData.append('strategy', importForm.strategy)
    formData.append('autoCategory', String(importForm.autoCategory))

    const res = await Import(formData)

    responseMessage.value = res.message || res.msg || ''

    if (isSuccessCode(res.code)) {
      importResult.value = res.data || {}
      return
    }

    if (res.data) {
      importResult.value = res.data
    }

    alert(res.message || res.msg || '导入失败')
  } catch (error) {
    console.error(error)

    const res = error?.response?.data

    if (res?.data) {
      responseMessage.value = res.message || res.msg || '导入失败'
      importResult.value = res.data
      return
    }

    alert(res?.message || res?.msg || '服务器异常，导入失败')
  } finally {
    submitting.value = false
  }
}

function handleReset() {
  selectedFile.value = null
  dragActive.value = false
  submitting.value = false
  importStarted.value = false
  importResult.value = null
  responseMessage.value = ''

  importForm.strategy = 'ALL'
  importForm.autoCategory = false

  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

function handleBack() {
  router.push('/admin/knowledge')
}

function getNotifications() {
  router.push('/admin/notifications')
}

function downloadFailureRecords() {
  if (!failureRows.value.length) {
    alert('暂无失败记录可导出')
    return
  }

  const header = ['行号', '问题', '失败原因']
  const rows = failureRows.value.map((item) => {
    return [item.row, item.question, item.reason]
  })

  const csvContent = [header, ...rows]
      .map((row) => row.map(formatCsvCell).join(','))
      .join('\n')

  const blob = new Blob([`\uFEFF${csvContent}`], {
    type: 'text/csv;charset=utf-8;'
  })

  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')

  link.href = url
  link.download = `导入失败记录_${Date.now()}.csv`
  link.click()

  URL.revokeObjectURL(url)
}

function formatCsvCell(value) {
  const text = String(value ?? '')
  return `"${text.replace(/"/g, '""')}"`
}

function formatImportTime(value) {
  if (!value) {
    return '-'
  }

  if (typeof value === 'string') {
    return value
  }

  if (Array.isArray(value)) {
    const [year, month, day, hour, minute, second] = value
    const pad = (num) => String(num).padStart(2, '0')

    return `${year}-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(second)}`
  }

  return '-'
}
</script>

<template>
  <div class="app-shell">
    <AdminSidebar v-model:collapsed="sidebarCollapsed" />

    <div class="workspace" :class="{ collapsed: sidebarCollapsed }">
      <header class="topbar">
        <div class="breadcrumb">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M15 18 9 12l6-6" />
          </svg>
          <span>知识库管理</span>
          <strong>/</strong>
          <span class="current">批量导入</span>
        </div>

        <div class="top-actions">
          <button
              class="icon-button"
              aria-label="通知"
              @click="getNotifications"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
              <path d="M10 21h4" />
            </svg>
            <span class="badge">12</span>
          </button>

          <AdminUserCard
              username="管理员"
              role-name="超级管理员"
              to="/admin/profile"
          />
        </div>
      </header>

      <main class="page">
        <div class="page-heading">
          <div>
            <h1>批量导入知识库</h1>
            <p>支持 Excel / JSON 两种模板导入，系统会返回导入总数、成功数量、失败数量和失败明细。</p>
          </div>

          <button class="back-button" @click="handleBack">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="m15 18-6-6 6-6" />
            </svg>
            返回列表
          </button>
        </div>

        <section class="panel template-panel">
          <div class="section-title">
            <h2>下载导入模板</h2>
            <p>请先下载模板，按固定字段填写后再上传。</p>
          </div>

          <div class="template-grid">
            <article
                v-for="template in templates"
                :key="template.type"
                class="template-card"
            >
              <div class="file-icon" :class="template.type" aria-hidden="true">
                <span v-if="template.type === 'excel'">X</span>
                <span v-else>{ }</span>
              </div>

              <div class="template-content">
                <h3>{{ template.title }}</h3>
                <p>{{ template.intro }}</p>

                <div class="field-list">
                  <span v-for="field in template.fields" :key="field">
                    {{ field }}
                  </span>
                </div>

                <a
                    class="download-button"
                    :href="template.href"
                    download
                >
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M12 3v12" />
                    <path d="m7 10 5 5 5-5" />
                    <path d="M5 21h14" />
                  </svg>
                  {{ template.button }}
                </a>
              </div>
            </article>
          </div>
        </section>

        <section class="import-layout">
          <div class="panel upload-panel">
            <div class="section-title compact">
              <h2>上传文件</h2>
              <p>支持 .xlsx、.xls、.json，单个文件不超过 20 MB。</p>
            </div>

            <label
                class="upload-dropzone"
                :class="{ active: dragActive }"
                @dragenter.prevent="dragActive = true"
                @dragover.prevent="dragActive = true"
                @dragleave.prevent="dragActive = false"
                @drop.prevent="handleDrop"
            >
              <input
                  ref="fileInputRef"
                  type="file"
                  accept=".xlsx,.xls,.json"
                  @change="handleFileChange"
              />

              <span class="upload-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <path d="M12 15V3" />
                  <path d="m7 8 5-5 5 5" />
                  <path d="M4 15v3a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-3" />
                </svg>
              </span>

              <strong>拖拽文件到此处，或点击选择文件</strong>
              <span>选择文件后点击开始导入，系统会返回导入结果</span>
            </label>

            <div class="selected-file">
              <div class="mini-file-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <path d="M6 2h8l6 6v14H6Z" />
                  <path d="M14 2v6h6" />
                </svg>
              </div>

              <div>
                <strong>{{ uploadFileName }}</strong>
                <span>{{ uploadMeta }}</span>
              </div>

              <span
                  class="file-status"
                  :class="{
                  running: submitting,
                  done: importResult
                }"
              >
                {{ fileStatusText }}
              </span>
            </div>

            <div class="form-row">
              <label>
                导入策略
                <select v-model="importForm.strategy">
                  <option value="ALL">ALL - 全部或全不</option>
                  <option value="ROW">ROW - 逐行事务</option>
                </select>
              </label>

              <label>
                未知分类处理
                <select v-model="importForm.autoCategory">
                  <option :value="false">false - 未知分类则失败</option>
                  <option :value="true">true - 自动归入其他</option>
                </select>
              </label>
            </div>

            <div class="button-row">
              <button class="secondary-button" @click="handleReset">
                重置
              </button>

              <button
                  class="primary-button"
                  :disabled="submitting || !selectedFile"
                  @click="handleImport"
              >
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M5 12h14" />
                  <path d="m13 6 6 6-6 6" />
                </svg>
                {{ submitting ? '导入中...' : '开始导入' }}
              </button>
            </div>
          </div>

          <div class="panel result-panel">
            <div class="section-title compact">
              <h2>导入结果</h2>
              <p>{{ resultDescription }}</p>
            </div>

            <div class="stat-grid">
              <div
                  v-for="stat in importStats"
                  :key="stat.label"
                  class="stat-card"
                  :class="stat.tone"
              >
                <span>{{ stat.label }}</span>
                <strong>{{ stat.value }}</strong>
              </div>
            </div>

            <div class="progress-block">
              <div class="progress-label">
                <span>成功率</span>
                <strong>{{ progressPercent }}%</strong>
              </div>

              <div class="progress-track">
                <span :style="{ width: progressPercent + '%' }"></span>
              </div>
            </div>

            <div class="failure-box">
              <div class="failure-head">
                <h3>失败明细</h3>
                <button @click="downloadFailureRecords">
                  导出失败记录
                </button>
              </div>

              <ul v-if="failureRows.length">
                <li
                    v-for="row in failureRows"
                    :key="`${row.row}-${row.reason}`"
                >
                  <span>第 {{ row.row }} 行</span>
                  <strong>{{ row.question }}</strong>
                  <em>{{ row.reason }}</em>
                </li>
              </ul>

              <div v-else class="empty-failure">
                暂无失败数据
              </div>
            </div>
          </div>
        </section>

        <section class="panel history-panel">
          <div class="section-title compact">
            <h2>本次导入记录</h2>
            <p>根据本次导入接口返回结果生成。</p>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
              <tr>
                <th>任务编号</th>
                <th>文件名称</th>
                <th>类型</th>
                <th>总数</th>
                <th>成功</th>
                <th>失败</th>
                <th>状态</th>
                <th>操作人</th>
                <th>导入时间</th>
              </tr>
              </thead>

              <tbody>
              <tr v-if="currentImportRecord.length === 0">
                <td colspan="9">暂无导入记录</td>
              </tr>

              <tr
                  v-for="item in currentImportRecord"
                  :key="item.id"
              >
                <td>{{ item.id }}</td>
                <td class="file-name">{{ item.fileName }}</td>
                <td>{{ item.type }}</td>
                <td>{{ item.total }}</td>
                <td class="success-text">{{ item.success }}</td>
                <td class="danger-text">{{ item.failed }}</td>
                <td>
                    <span
                        class="status-pill"
                        :class="{ warning: item.failed }"
                    >
                      {{ item.status }}
                    </span>
                </td>
                <td>{{ item.operator }}</td>
                <td>{{ item.time }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<style scoped>
:global(:root) {
  font-family:
      Inter,
      "PingFang SC",
      "Microsoft YaHei",
      system-ui,
      -apple-system,
      BlinkMacSystemFont,
      "Segoe UI",
      sans-serif;
  color: #152033;
  background: #f5f7fb;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
}

:global(*) {
  box-sizing: border-box;
}

:global(body) {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
}

button,
input,
select {
  font: inherit;
}

button {
  cursor: pointer;
}

a {
  color: inherit;
  text-decoration: none;
}

svg {
  display: block;
  width: 1em;
  height: 1em;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.app-shell {
  display: flex;
  min-height: 100vh;
  background: #f5f7fb;
}

.workspace {
  flex: 1;
  min-width: 0;
  margin-left: 204px;
  background: #f5f7fb;
  transition: margin-left 0.2s ease;
}

.workspace.collapsed {
  margin-left: 72px;
}


.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 22px;
  border-bottom: 1px solid #e5e9f2;
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(10px);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7688;
  font-size: 14px;
  font-weight: 600;
}

.breadcrumb svg {
  width: 16px;
  height: 16px;
}

.breadcrumb strong {
  color: #98a2b3;
}

.breadcrumb .current {
  color: #1d293d;
  font-weight: 800;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.icon-button {
  position: relative;
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border: 0;
  background: transparent;
  color: #111827;
}

.icon-button svg {
  width: 21px;
  height: 21px;
}

.badge {
  position: absolute;
  top: 2px;
  right: 0;
  display: grid;
  min-width: 18px;
  height: 18px;
  place-items: center;
  border-radius: 999px;
  border: 2px solid #fff;
  background: #e53935;
  color: #fff;
  font-size: 10px;
  font-weight: 800;
  line-height: 1;
}

.page {
  width: 100%;
  max-width: none;
  margin: 0;
  display: grid;
  gap: 18px;
  padding: 26px 24px 36px;
}

.page-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.page-heading h1 {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 850;
  line-height: 1.25;
  letter-spacing: 0;
}

.page-heading p,
.section-title p {
  margin: 0;
  color: #536174;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.7;
}

.back-button,
.download-button,
.primary-button,
.secondary-button,
.failure-head button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  min-height: 36px;
  border-radius: 8px;
  font-weight: 700;
  white-space: nowrap;
}

.back-button {
  padding: 0 14px;
  border: 1px solid #d8dee9;
  background: #fff;
  color: #1d293d;
  box-shadow: 0 3px 10px rgba(15, 23, 42, 0.04);
}

.back-button svg,
.download-button svg,
.primary-button svg {
  width: 16px;
  height: 16px;
}

.panel {
  border: 1px solid #e4e9f1;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 12px 35px rgba(24, 39, 75, 0.06);
}

.template-panel,
.upload-panel,
.result-panel,
.history-panel {
  padding: 22px;
}

.section-title {
  margin-bottom: 18px;
}

.section-title.compact {
  margin-bottom: 16px;
}

.section-title h2 {
  margin: 0 0 8px;
  color: #111827;
  font-size: 19px;
  font-weight: 850;
  line-height: 1.25;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.template-card {
  display: flex;
  gap: 24px;
  min-height: 160px;
  padding: 26px 28px;
  border: 1px solid #e4e9f1;
  border-radius: 8px;
  background: #fff;
}

.file-icon {
  position: relative;
  display: grid;
  flex: 0 0 auto;
  width: 56px;
  height: 68px;
  place-items: center;
  align-self: flex-start;
  border-radius: 8px;
  color: #fff;
  font-size: 28px;
  font-weight: 850;
  line-height: 1;
}

.file-icon::after {
  position: absolute;
  top: 0;
  right: 0;
  width: 20px;
  height: 20px;
  border-radius: 0 8px 0 8px;
  background: rgba(255, 255, 255, 0.24);
  content: "";
}

.file-icon.excel {
  background: linear-gradient(145deg, #18a35d, #08773f);
}

.file-icon.json {
  background: linear-gradient(145deg, #2d6ff1, #1d4ed8);
  font-size: 21px;
}

.template-content {
  min-width: 0;
}

.template-content h3 {
  margin: 0 0 10px;
  color: #0f172a;
  font-size: 18px;
  font-weight: 850;
  line-height: 1.3;
}

.template-content p {
  margin: 0 0 10px;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.6;
}

.field-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.field-list span {
  display: inline-flex;
  min-height: 26px;
  align-items: center;
  border-radius: 999px;
  padding: 0 10px;
  background: #f1f5f9;
  color: #334155;
  font-size: 12px;
  font-weight: 700;
}

.download-button {
  min-width: 158px;
  padding: 0 14px;
  border: 1px solid #c8e6d6;
  background: #ecf8f1;
  color: #137547;
}

.import-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(360px, 0.95fr);
  gap: 18px;
}

.upload-dropzone {
  display: grid;
  min-height: 170px;
  place-items: center;
  border: 1.5px dashed #bed3c9;
  border-radius: 8px;
  background: #fbfefd;
  color: #4b5c6f;
  text-align: center;
  transition:
      border-color 0.2s ease,
      background-color 0.2s ease,
      color 0.2s ease;
}

.upload-dropzone.active {
  border-color: #16834a;
  background: #effaf4;
  color: #16834a;
}

.upload-dropzone input {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
}

.upload-dropzone strong {
  margin-top: 10px;
  color: #152033;
  font-size: 15px;
}

.upload-dropzone span:last-child {
  font-size: 13px;
  font-weight: 600;
}

.upload-icon {
  display: grid;
  width: 48px;
  height: 48px;
  place-items: center;
  border-radius: 50%;
  background: #e7f6ee;
  color: #16834a;
}

.upload-icon svg {
  width: 24px;
  height: 24px;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 16px 0;
  padding: 12px;
  border-radius: 8px;
  background: #f7fafc;
}

.mini-file-icon {
  display: grid;
  flex: 0 0 auto;
  width: 36px;
  height: 36px;
  place-items: center;
  border-radius: 8px;
  background: #edf5ff;
  color: #2264d1;
}

.mini-file-icon svg {
  width: 19px;
  height: 19px;
}

.selected-file div:nth-child(2) {
  display: grid;
  min-width: 0;
  gap: 3px;
}

.selected-file strong {
  overflow: hidden;
  color: #152033;
  font-size: 14px;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.selected-file span {
  color: #667085;
  font-size: 12px;
  font-weight: 600;
}

.file-status {
  margin-left: auto;
  border-radius: 999px;
  padding: 5px 10px;
  background: #fff7ed;
  color: #c2410c;
  font-size: 12px;
  font-weight: 800;
  white-space: nowrap;
}

.file-status.running {
  background: #eff6ff;
  color: #2563eb;
}

.file-status.done {
  background: #ecfdf3;
  color: #16834a;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.form-row label {
  display: grid;
  gap: 8px;
  color: #475467;
  font-size: 13px;
  font-weight: 700;
}

.form-row select {
  width: 100%;
  min-height: 38px;
  border: 1px solid #d6dce6;
  border-radius: 8px;
  padding: 0 10px;
  background: #fff;
  color: #152033;
  outline: none;
}

.button-row {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 10px;
}

.secondary-button {
  border: 1px solid #d8dee9;
  background: #fff;
  color: #1d293d;
}

.primary-button {
  width: 100%;
  min-height: 42px;
  border: 0;
  background: #16834a;
  color: #fff;
  box-shadow: 0 8px 18px rgba(22, 131, 74, 0.2);
}

.primary-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.stat-card {
  display: grid;
  min-height: 86px;
  align-content: center;
  gap: 8px;
  border-radius: 8px;
  padding: 14px;
  background: #f6f8fb;
}

.stat-card span {
  color: #667085;
  font-size: 12px;
  font-weight: 700;
}

.stat-card strong {
  color: #111827;
  font-size: 24px;
  font-weight: 850;
}

.stat-card.success strong {
  color: #16834a;
}

.stat-card.danger strong {
  color: #dc2626;
}

.stat-card.info strong {
  color: #2563eb;
}

.progress-block {
  margin: 18px 0;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #475467;
  font-size: 13px;
  font-weight: 700;
}

.progress-label strong {
  color: #16834a;
}

.progress-track {
  height: 9px;
  overflow: hidden;
  border-radius: 999px;
  background: #edf2f7;
}

.progress-track span {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #16a34a, #2563eb);
}

.failure-box {
  border: 1px solid #eef1f5;
  border-radius: 8px;
  overflow: hidden;
}

.failure-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  background: #fafbfc;
}

.failure-head h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 850;
}

.failure-head button {
  min-height: 30px;
  border: 1px solid #fecaca;
  background: #fff5f5;
  color: #dc2626;
  font-size: 12px;
}

.failure-box ul {
  display: grid;
  gap: 0;
  margin: 0;
  padding: 0;
  list-style: none;
}

.failure-box li {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
  padding: 12px 14px;
  border-top: 1px solid #eef1f5;
  font-size: 13px;
}

.failure-box li span {
  color: #dc2626;
  font-weight: 800;
}

.failure-box li strong {
  overflow: hidden;
  color: #1f2937;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.failure-box li em {
  color: #64748b;
  font-style: normal;
  font-weight: 600;
  white-space: nowrap;
}

.empty-failure {
  padding: 18px 14px;
  border-top: 1px solid #eef1f5;
  color: #667085;
  font-size: 13px;
  font-weight: 600;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 920px;
  border-collapse: collapse;
}

th,
td {
  height: 46px;
  border-bottom: 1px solid #edf1f6;
  padding: 0 12px;
  text-align: left;
  white-space: nowrap;
}

th {
  background: #f8fafc;
  color: #667085;
  font-size: 12px;
  font-weight: 800;
}

td {
  color: #344054;
  font-size: 13px;
  font-weight: 600;
}

.file-name {
  color: #152033;
  font-weight: 800;
}

.success-text {
  color: #16834a;
}

.danger-text {
  color: #dc2626;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  border-radius: 999px;
  padding: 0 9px;
  background: #ecfdf3;
  color: #16834a;
  font-size: 12px;
  font-weight: 800;
}

.status-pill.warning {
  background: #fff7ed;
  color: #c2410c;
}

@media (max-width: 1100px) {
  .template-grid,
  .import-layout {
    grid-template-columns: 1fr;
  }

  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .app-shell,
  .workspace {
    display: block;
  }

  .workspace {
    padding-left: 0;
  }

  .workspace.collapsed {
    padding-left: 0;
  }

  .topbar {
    position: static;
    height: auto;
    min-height: 56px;
    padding: 12px 16px;
  }

  .breadcrumb {
    display: none;
  }

  .page {
    padding: 20px 14px 28px;
  }

  .page-heading {
    display: grid;
  }

  .page-heading h1 {
    font-size: 23px;
  }

  .template-panel,
  .upload-panel,
  .result-panel,
  .history-panel {
    padding: 16px;
  }

  .template-card {
    gap: 16px;
    padding: 18px;
  }

  .form-row,
  .stat-grid,
  .button-row {
    grid-template-columns: 1fr;
  }

  .failure-box li {
    grid-template-columns: 1fr;
  }

  .failure-box li strong,
  .failure-box li em {
    white-space: normal;
  }
}
</style>