<template>
  <section>
    <h2 class="page-title">敏感数据检测</h2>
    <el-row :gutter="14">
      <el-col :xs="24" :lg="12">
        <div class="panel">
          <el-form label-position="top">
            <el-form-item label="任务名称">
              <el-input v-model="form.taskName" placeholder="例如：外部公告发布审核" />
            </el-form-item>
            <el-form-item label="文件检测">
              <el-upload
                drag
                :auto-upload="false"
                :limit="1"
                :on-change="selectFile"
                :on-remove="removeFile"
                accept=".txt,.pdf,.doc,.docx"
              >
                <el-icon><UploadFilled /></el-icon>
                <div>拖拽或点击上传 txt、pdf、doc、docx</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="检测内容">
              <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入内部通信或外部发布内容" />
            </el-form-item>
            <el-button type="primary" :icon="Search" :loading="loading" @click="submitText">文本检测</el-button>
            <el-button :icon="UploadFilled" :loading="loading" @click="submitFile">文件检测</el-button>
          </el-form>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="panel result-panel">
          <template v-if="result">
            <div class="toolbar">
              <h3>检测结果</h3>
              <el-tag :type="tagType(result.riskLevel)">{{ result.riskLevel }} / {{ result.riskScore }}</el-tag>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="命中摘要">{{ result.hitSummary }}</el-descriptions-item>
              <el-descriptions-item label="AI建议">{{ result.aiSuggestion }}</el-descriptions-item>
              <el-descriptions-item label="脱敏内容">{{ result.desensitizedContent }}</el-descriptions-item>
            </el-descriptions>
            <el-table :data="result.hits" size="small" style="margin-top: 14px">
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="value" label="命中值" />
              <el-table-column prop="riskLevel" label="风险" width="110" />
            </el-table>
          </template>
          <el-empty v-else description="等待检测内容" />
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, UploadFilled } from '@element-plus/icons-vue'
import { detectApi } from '../api/modules'

const loading = ref(false)
const result = ref(null)
const selectedFile = ref(null)
const form = reactive({
  taskName: '外部信息发布审核',
  content: '客户张三手机号13812345678，邮箱test@example.com，合同金额属于财务数据。'
})

function tagType(level) {
  if (level?.includes('严重') || level?.includes('高')) return 'danger'
  if (level?.includes('中')) return 'warning'
  return 'success'
}

function selectFile(uploadFile) {
  selectedFile.value = uploadFile.raw
}

function removeFile() {
  selectedFile.value = null
}

async function submitText() {
  if (!form.taskName || !form.content) {
    ElMessage.warning('请输入任务名称和检测内容')
    return
  }
  loading.value = true
  try {
    result.value = await detectApi.detectText(form)
  } finally {
    loading.value = false
  }
}

async function submitFile() {
  if (!form.taskName || !selectedFile.value) {
    ElMessage.warning('请输入任务名称并选择文件')
    return
  }
  const data = new FormData()
  data.append('taskName', form.taskName)
  data.append('file', selectedFile.value)
  loading.value = true
  try {
    result.value = await detectApi.detectFile(data)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.result-panel {
  min-height: 420px;
}

h3 {
  margin: 0;
  font-size: 17px;
}
</style>
