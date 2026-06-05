<template>
  <main class="import-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>批量导入知识库</h1>
        <p class="page-desc">
          支持 Excel / JSON 两种模板导入，系统会返回导入总数、成功数量、失败数量和失败明细。
        </p>
      </div>
      <button class="secondary-btn" @click="handleBack">返回列表</button>
    </div>

    <!-- 下载模板 -->
    <section class="template-card">
      <div class="section-title">
        <h2>下载导入模板</h2>
        <p>请先下载模板，按固定字段填写后再上传。</p>
      </div>
      <div class="template-list">
        <a class="template-item" href="/templates/KnowledgeBaseImportTemplate.xlsx" download>
          <div class="template-icon excel">XLSX</div>
          <div>
            <h3>Excel 导入模板</h3>
            <p>适合批量人工整理，字段：问题、回答、分类、来源</p>
            <el-icon><Download /></el-icon>
          </div>
        </a>
        <a class="template-item" href="/templates/KnowledgeBaseImportTemplate.json" download
       >
          <div class="template-icon json">JSON</div>
          <div>
            <h3>JSON 导入模板</h3>
            <p>适合程序生成数据，字段：question、answer、category、source</p>
          </div>
        </a>
      </div>
    </section>

    <!-- 上传导入文件 -->
    <section class="import-card">
      <div class="section-title">
        <h2>上传导入文件</h2>
        <p>请选择填写完成的 .xlsx 或 .json 文件。</p>
      </div>

      <div class="form-grid">
        <div class="form-item full">
          <label>导入文件</label>
          <label class="upload-box" :class="{ active: selectedFile }" for="knowledge-file">
            <input
                id="knowledge-file"
                type="file"
                accept=".xlsx,.json,application/json,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                @change="handleFileChange"
            />
            <div v-if="!selectedFile" class="upload-empty">
              <strong>点击选择文件</strong>
              <span>支持 .xlsx / .json，建议使用页面上方模板</span>
            </div>
            <div v-else class="upload-file">
              <strong>{{ selectedFile.name }}</strong>
              <span>{{ formatFileSize(selectedFile.size) }}</span>
            </div>
          </label>
        </div>

        <div class="form-item">
          <label>导入策略</label>
          <select v-model="importForm.strategy">
            <option value="ALL">全部或全不</option>
            <option value="ROW">逐行事务</option>
          </select>
          <p class="field-tip">全部或全不：只要存在错误数据，本次导入全部回滚。</p>
        </div>

        <div class="form-item">
          <label>未知分类处理</label>
          <select v-model="importForm.autoCategory">
            <option :value="false">未知分类则失败</option>
            <option :value="true">自动归入其他</option>
          </select>
          <p class="field-tip">分类不存在时，可选择失败或自动归入“其他”。</p>
        </div>
      </div>

      <div class="actions">
        <button class="secondary-btn" @click="handleReset">重置</button>
        <button class="primary-btn" :disabled="submitting" @click="handleImport">
          {{ submitting ? '导入中...' : '开始导入' }}
        </button>
      </div>
    </section>

    <!-- 导入结果 -->
    <section v-if="importResult" class="result-card">
      <div class="result-header">
        <div>
          <p class="eyebrow">Result</p>
          <h2>{{ resultTitle }}</h2>
          <p class="summary-text">{{ importResult.summary || responseMessage }}</p>
        </div>
        <span
            class="result-badge"
            :class="importResult.failCount > 0 ? 'warning' : 'success'"
        >
          {{ importResult.failCount > 0 ? '存在失败数据' : '全部成功' }}
        </span>
      </div>

      <div class="stat-list">
        <div class="stat-item"><span>批次 ID</span><strong>{{ importResult.batchId || '-' }}</strong></div>
        <div class="stat-item"><span>总条数</span><strong>{{ importResult.totalCount || 0 }}</strong></div>
        <div class="stat-item success"><span>成功</span><strong>{{ importResult.successCount || 0 }}</strong></div>
        <div class="stat-item fail"><span>失败</span><strong>{{ importResult.failCount || 0 }}</strong></div>

      </div>

      <div class="result-meta">
        <span>导入时间：{{ formatImportTime(importResult.importTime) }}</span>
      </div>

      <div v-if="errorList.length > 0" class="error-table-wrap">
        <h3>失败明细</h3>
        <table class="error-table">
          <thead>
          <tr><th>行号</th><th>问题</th><th>失败原因</th></tr>
          </thead>
          <tbody>
          <tr v-for="error in errorList" :key="`${error.rowNo}-${error.reason}`">
            <td>{{ error.rowNo }}</td>
            <td class="question-cell">{{ error.question || '-' }}</td>
            <td class="reason-cell">{{ error.reason }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Import } from '@/api/admin/knowledge'

const router = useRouter()
const selectedFile = ref(null)
const submitting = ref(false)
const importResult = ref(null)
const responseMessage = ref('')

const importForm = reactive({
  strategy: 'ALL',
  autoCategory: false
})

const handleFileChange = (e) => {
  const file = e.target.files?.[0]
  if (!file) { selectedFile.value = null; return }
  selectedFile.value = file
  importResult.value = null
  responseMessage.value = ''
}

const handleReset = () => {
  selectedFile.value = null
  importForm.strategy = 'ALL'
  importForm.autoCategory = false
  importResult.value = null
  responseMessage.value = ''
  const input = document.getElementById('knowledge-file')
  if (input) input.value = ''
}

const handleImport = async () => {
  if (!selectedFile.value) return alert('请先选择文件')
  submitting.value = true
  importResult.value = true
  responseMessage.value = ''
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('strategy', importForm.strategy)
    formData.append('autoCategory', String(importForm.autoCategory))

    const res = await Import(formData)
    responseMessage.value = res.message || res.msg || ''
    if (res.code === 1 || res.code === 200) {
      importResult.value = res.data || {}
      return
    }
    if (res.data) importResult.value = res.data
  } catch (error) {
    console.error(error)
    alert('服务器异常，导入失败')
  } finally { submitting.value = false }
}

const handleBack = () => router.push('/admin/knowledge')

const errorList = computed(() => importResult.value?.errors || [])
const resultTitle = computed(() => {
  if (!importResult.value) return ''
  return (importResult.value.failCount || 0) > 0 ? '导入完成，请检查失败明细' : '导入成功'
})

const formatFileSize = (size) => size < 1024 ? `${(size/1024).toFixed(1)} KB` : `${(size/1024/1024).toFixed(2)} MB`
const formatImportTime = (val) => {
  if (!val) return '-'
  if (typeof val === 'string') return val
  const [y,m,d,h,mi,s] = val
  const pad = n=>String(n).padStart(2,'0')
  return `${y}-${pad(m)}-${pad(d)} ${pad(h)}:${pad(mi)}:${pad(s)}`
}
</script>

<style scoped>
/* 样式与截图一致 */
/*#fbfaf7*/
.import-page { min-height:100vh;padding:36px 56px;background:#ffffff;color:#1c241c }
.page-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:28px }
.eyebrow { margin:0 0 8px;color:#219c55;font-size:13px;font-weight:800;letter-spacing:.08em;text-transform:uppercase }
.page-header h1 { margin:0;font-size:38px;font-weight:850;color:#1c241c }
.page-desc { margin:12px 0 0;color:#687268;font-size:15px }
.template-card,.import-card,.result-card {
  padding:30px 34px;margin-bottom:24px;background:#fff;border:1px solid #ece7df;border-radius:10px;box-shadow:0 20px 50px rgba(28,36,28,.07) }
.section-title { margin-bottom:20px }
.section-title h2 { margin:0;font-size:22px;font-weight:800 }
.section-title p { margin:8px 0 0;color:#687268;font-size:14px }
.template-list { display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px }
.template-item { display:flex;gap:16px;align-items:center;padding:18px;border:1px solid #ece7df;border-radius:18px;background:#fffefb;text-decoration:none;transition:.18s ease }
.template-item:hover { border-color:rgba(33,156,85,.5);transform:translateY(-1px) }
.template-icon { display:flex;align-items:center;justify-content:center;width:58px;height:58px;border-radius:16px;color:#fff;font-weight:900;font-size:14px }
.template-icon.excel { background:#219c55 }
.template-icon.json { background:#1c7ed6 }
.template-item h3 { margin:0;font-size:17px }
.template-item p { margin:6px 0 0;color:#687268;font-size:14px;line-height:1.6 }
.form-grid { display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:20px }
.form-item.full { grid-column:1/-1 }
.form-item label { display:block;margin-bottom:8px;color:#566157;font-size:15px;font-weight:800 }
.form-item select { width:100%;height:44px;padding:0 14px;border:1px solid #d8d6cf;border-radius:10px;background:#fff;color:#1c241c;font-size:14px;outline:none }
.form-item select:focus { border-color:#219c55;box-shadow:0 0 0 3px rgba(33,156,85,.12) }
.field-tip { margin:8px 0 0;color:#7a827a;font-size:13px }
.upload-box { display:flex;align-items:center;justify-content:center;min-height:132px;border:2px dashed #d8d6cf;border-radius:18px;background:#fffefb;cursor:pointer;transition:.18s ease }
.upload-box:hover,.upload-box.active { border-color:#219c55;background:rgba(33,156,85,.04) }
.upload-box input { display:none }
.upload-empty,.upload-file { display:flex;flex-direction:column;align-items:center;gap:8px;color:#687268 }
.upload-empty strong,.upload-file strong { color:#1c241c;font-size:17px }
.actions { display:flex;justify-content:center;gap:12px;margin-top:26px }
.primary-btn,.secondary-btn { height:44px;padding:0 22px;border-radius:12px;font-size:15px;font-weight:800;cursor:pointer }
.primary-btn { background:#219c55;color:#fff;border:1px solid #219c55 }
.primary-btn:disabled { cursor:not-allowed;opacity:.65 }
.secondary-btn { background:#fff;color:#219c55;border:1px solid rgba(33,156,85,.35) }
.result-header { display:flex;justify-content:space-between;align-items:flex-start;gap:18px }
.result-header h2 { margin:0;font-size:24px;font-weight:850 }
.summary-text { margin:10px 0 0;color:#687268 }
.result-badge { padding:8px 12px;border-radius:999px;font-size:13px;font-weight:800 }
.result-badge.success { color:#167a3f;background:rgba(33,156,85,.12) }
.result-badge.warning { color:#9a5b00;background:rgba(255,169,64,.16) }
.stat-list { display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:14px;margin-top:24px }
.stat-item { padding:18px;border:1px solid #ece7df;border-radius:18px;background:#fffefb }
.stat-item span { display:block;color:#687268;font-size:13px;font-weight:700 }
.stat-item strong { display:block;margin-top:8px;color:#1c241c;font-size:26px }
.stat-item.success strong { color:#219c55 }
.stat-item.fail strong { color:#d93025 }
.result-meta { margin-top:16px;color:#687268;font-size:14px }
.error-table-wrap { margin-top:24px }
.error-table-wrap h3 { margin:0 0 12px;font-size:18px }
.error-table { width:100%;border-collapse:collapse }
.error-table th,.error-table td { padding:14px 12px;border-bottom:1px solid #ece7df;text-align:left;font-size:14px }
.error-table th { color:#687268;font-weight:800 }
.question-cell { max-width:420px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis }
.reason-cell { color:#d93025;font-weight:700 }
@media (max-width:900px){.import-page{padding:24px}.template-list,.form-grid,.stat-list{grid-template-columns:1fr}.page-header,.result-header{flex-direction:column}}
</style>