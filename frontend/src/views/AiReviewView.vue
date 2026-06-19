<template>
  <section>
    <h2 class="page-title">AI审核</h2>
    <el-row :gutter="14">
      <el-col :xs="24" :lg="10">
        <div class="panel">
          <el-form label-position="top">
            <el-form-item label="审核场景">
              <el-select v-model="form.reviewScene">
                <el-option label="外部信息发布审核" value="外部信息发布审核" />
                <el-option label="内部通信内容检查" value="内部通信内容检查" />
                <el-option label="合同资料复核" value="合同资料复核" />
                <el-option label="技术文档脱敏检查" value="技术文档脱敏检查" />
              </el-select>
            </el-form-item>
            <el-form-item label="资料类型">
              <el-select v-model="form.dataType">
                <el-option label="客户资料" value="客户资料" />
                <el-option label="财务信息" value="财务信息" />
                <el-option label="合同内容" value="合同内容" />
                <el-option label="技术资料" value="技术资料" />
                <el-option label="综合文本" value="综合文本" />
              </el-select>
            </el-form-item>
            <el-form-item label="发布范围">
              <el-select v-model="form.publishScope">
                <el-option label="仅内部流转" value="仅内部流转" />
                <el-option label="部门间共享" value="部门间共享" />
                <el-option label="对外发布" value="对外发布" />
              </el-select>
            </el-form-item>
            <el-form-item label="文件审核">
              <el-upload drag :auto-upload="false" :limit="1" :on-change="selectFile" :on-remove="removeFile" accept=".txt,.pdf,.doc,.docx">
                <el-icon><UploadFilled /></el-icon>
                <div>上传 txt、pdf、doc、docx，直接交给 DeepSeek 审核</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="文本审核">
              <el-input v-model="form.content" type="textarea" :rows="8" placeholder="输入需要 AI 审核的文本内容" />
            </el-form-item>
            <el-button type="primary" :loading="loading" @click="reviewText">审核文本</el-button>
            <el-button :icon="UploadFilled" :loading="loading" @click="reviewFile">审核文件</el-button>
          </el-form>
        </div>
      </el-col>
      <el-col :xs="24" :lg="14">
        <div class="panel result-panel">
          <div class="toolbar">
            <h3>结构化审核结果</h3>
            <el-tag type="success">DeepSeek</el-tag>
          </div>
          <el-input v-model="suggestion" type="textarea" :rows="20" readonly placeholder="审核结果会显示在这里" />
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { aiReviewApi } from '../api/modules'

const loading = ref(false)
const suggestion = ref('')
const selectedFile = ref(null)
const form = reactive({
  reviewScene: '外部信息发布审核',
  dataType: '客户资料',
  publishScope: '对外发布',
  content: '客户名单包含 A 公司、B 公司，合同报价策略为年度折扣 8.5 折，联系人手机号 13812345678。请判断是否可以对外发布。'
})

function selectFile(uploadFile) {
  selectedFile.value = uploadFile.raw
}

function removeFile() {
  selectedFile.value = null
}

async function reviewText() {
  if (!form.content) {
    ElMessage.warning('请输入审核内容')
    return
  }
  loading.value = true
  try {
    suggestion.value = await aiReviewApi.review({ ...form })
  } finally {
    loading.value = false
  }
}

async function reviewFile() {
  if (!selectedFile.value) {
    ElMessage.warning('请选择需要审核的文件')
    return
  }
  const data = new FormData()
  data.append('reviewScene', form.reviewScene)
  data.append('dataType', form.dataType)
  data.append('publishScope', form.publishScope)
  data.append('file', selectedFile.value)
  loading.value = true
  try {
    suggestion.value = await aiReviewApi.reviewFile(data)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.result-panel h3 {
  margin: 0;
  font-size: 17px;
}
</style>
