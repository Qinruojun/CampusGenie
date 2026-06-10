<template>
  <div class="batch-import-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">批量导入知识库</h2>
      <el-button type="text">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>
    </div>
    <p class="page-desc">支持 Excel / JSON 两种模板导入，系统会返回导入总数、成功数量、失败数量和失败明细。</p>

    <!-- 下载导入模板区域 -->
    <div class="section">
      <h3 class="section-title">下载导入模板</h3>
      <p class="section-desc">请先下载模板，按固定字段填写后再上传。</p>

      <div class="template-cards">
        <!-- Excel模板卡片 -->
        <el-card class="template-card">
          <div class="template-content">
            <div class="template-icon excel-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="template-info">
              <h4>Excel 导入模板</h4>
              <p>适合批量人工整理，表格字段为：<br>问题、回答、分类、来源。</p>
            </div>
          </div>
          <el-button type="success" class="download-btn">
            <el-icon><Download /></el-icon>
            下载 Excel 模板
          </el-button>
        </el-card>

        <!-- JSON模板卡片 -->
        <el-card class="template-card">
          <div class="template-content">
            <div class="template-icon json-icon">
              <el-icon><Code /></el-icon>
            </div>
            <div class="template-info">
              <h4>JSON 导入模板</h4>
              <p>适合程序生成数据，字段为：<br>question、answer、category、source。</p>
            </div>
          </div>
          <el-button type="primary" class="download-btn">
            <el-icon><Download /></el-icon>
            下载 JSON 模板
          </el-button>
        </el-card>
      </div>
    </div>

    <!-- 上传导入文件区域 -->
    <div class="section">
      <h3 class="section-title">上传导入文件</h3>
      <p class="section-desc">请选择填写完成的 .xlsx 或 .json 文件。</p>

      <div class="upload-area">
        <div class="upload-left">
          <el-upload
              drag
              :show-file-list="true"
              :limit="1"
              accept=".xlsx,.json"
              :auto-upload="false"
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="el-upload__text">点击选择文件或将文件拖拽到此处</div>
            <div class="el-upload__tip">支持 .xlsx / .json，建议使用上方模板</div>

            <!-- 静态显示已上传的文件 -->
            <template #file-list>
              <el-upload-list-item
                  name="KnowledgeBaseImportTemplate.xlsx"
                  status="success"
                  size="12.5 KB"
              />
            </template>
          </el-upload>
        </div>

        <div class="upload-right">
          <div class="form-item">
            <label class="form-label">导入策略</label>
            <el-select model-value="ALL" class="form-select" placeholder="请选择导入策略">
              <el-option label="ALL - 全部或全不" value="ALL" />
              <el-option label="SKIP_ERROR - 跳过错误" value="SKIP_ERROR" />
            </el-select>
            <el-tooltip content="只要存在错误数据，本次导入全部回滚" placement="top">
              <el-icon class="info-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>

          <div class="form-item">
            <label class="form-label">未知分类处理</label>
            <el-select model-value="false" class="form-select" placeholder="请选择处理方式">
              <el-option label="false - 未知分类则失败" value="false" />
              <el-option label="true - 自动归入其他" value="true" />
            </el-select>
            <el-tooltip placement="top">
              <template #content>
                分类不存在时，可选择失败或自动归入"其他"
              </template>
              <el-icon class="info-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <el-button>重置</el-button>
        <el-button type="success">开始导入</el-button>
      </div>
    </div>

    <!-- 导入结果区域（静态显示） -->
    <div class="section">
      <h3 class="section-title">导入结果</h3>

      <div class="result-status success">
        <el-icon><CircleCheck /></el-icon>
        <span>导入已完成</span>
        <span class="import-time">导入时间：2026-06-01 20:40:10</span>
        <el-button type="text" class="reimport-btn">
          <el-icon><Refresh /></el-icon>
          重新导入
        </el-button>
      </div>

      <div class="result-stats">
        <el-card class="stat-card">
          <div class="stat-label">批次 ID</div>
          <div class="stat-value">10087</div>
          <el-icon class="stat-icon"><CopyDocument /></el-icon>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-label">总条数</div>
          <div class="stat-value">10</div>
          <el-icon class="stat-icon"><Document /></el-icon>
        </el-card>
        <el-card class="stat-card success">
          <div class="stat-label">成功数量</div>
          <div class="stat-value">7</div>
          <el-icon class="stat-icon"><CircleCheck /></el-icon>
        </el-card>
        <el-card class="stat-card error">
          <div class="stat-label">失败数量</div>
          <div class="stat-value">3</div>
          <el-icon class="stat-icon"><CircleClose /></el-icon>
        </el-card>
      </div>

      <!-- 失败明细表格（静态数据） -->
      <div class="fail-detail">
        <h4>失败明细</h4>
        <el-table :data="[
          { lineNumber: 2, question: '—', failReason: '问题不能为空' },
          { lineNumber: 5, question: '食堂几点开门？这是一个非常非常非常长的...', failReason: '问题长度超过200字符' },
          { lineNumber: 8, question: '如何办理校园卡？', failReason: '问题已存在' }
        ]" border stripe>
          <el-table-column prop="lineNumber" label="行号" width="80" align="center" />
          <el-table-column prop="question" label="问题" min-width="300" show-overflow-tooltip />
          <el-table-column prop="failReason" label="失败原因" min-width="200" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 以下样式完全不变，和之前一致 */
.batch-import-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.page-desc {
  color: #606266;
  margin-bottom: 24px;
  font-size: 14px;
}

.section {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #303133;
}

.section-desc {
  color: #606266;
  margin-bottom: 16px;
  font-size: 14px;
}

/* 模板卡片样式 */
.template-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.template-card {
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.template-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.template-content {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.template-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-right: 16px;
  flex-shrink: 0;
}

.excel-icon {
  background-color: #21b573;
}

.json-icon {
  background-color: #409eff;
}

.template-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.template-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.download-btn {
  width: 100%;
}

/* 上传区域样式 */
.upload-area {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  margin-bottom: 24px;
}

.upload-left {
  width: 100%;
}

.upload-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.form-label {
  width: 100px;
  font-size: 14px;
  color: #303133;
  margin-right: 8px;
}

.form-select {
  flex: 1;
}

.info-icon {
  color: #909399;
  margin-left: 8px;
  cursor: pointer;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 导入结果样式 */
.result-status {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 24px;
  font-size: 14px;
}

.result-status.success {
  background-color: #f0f9eb;
  color: #67c23a;
}

.result-status .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.import-time {
  margin-left: auto;
  color: #606266;
}

.reimport-btn {
  margin-left: 16px;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  padding: 16px;
  position: relative;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 20px;
  color: #c0c4cc;
}

.stat-card.success .stat-value {
  color: #67c23a;
}

.stat-card.error .stat-value {
  color: #f56c6c;
}

.fail-detail h4 {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #303133;
}
</style>