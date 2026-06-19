<template>
  <section>
    <h2 class="page-title">审核申请</h2>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">部门主管可查看员工提交的文本或文件检测内容，并依据风险结果审核</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="tasks" border>
        <el-table-column prop="auditTaskId" label="审核ID" width="90" />
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column prop="contentType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag>{{ row.contentType === 'FILE' ? '文件' : '文本' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险" width="120" />
        <el-table-column prop="riskScore" label="评分" width="90" />
        <el-table-column prop="auditStatus" label="审核状态" width="120" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDetail(row)">查看审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="审核详情" width="860px">
      <el-descriptions v-if="current" :column="2" border>
        <el-descriptions-item label="任务名称">{{ current.taskName }}</el-descriptions-item>
        <el-descriptions-item label="提交类型">{{ current.contentType === 'FILE' ? '文件检测' : '文本检测' }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">{{ current.riskLevel }}</el-descriptions-item>
        <el-descriptions-item label="风险评分">{{ current.riskScore }}</el-descriptions-item>
        <el-descriptions-item label="命中摘要" :span="2">{{ current.hitSummary }}</el-descriptions-item>
        <el-descriptions-item label="AI建议" :span="2">{{ current.aiSuggestion }}</el-descriptions-item>
      </el-descriptions>

      <el-tabs v-if="current" model-value="source" class="detail-tabs">
        <el-tab-pane label="原始内容" name="source">
          <el-input :model-value="current.sourceContent" type="textarea" :rows="8" readonly />
        </el-tab-pane>
        <el-tab-pane label="脱敏内容" name="masked">
          <el-input :model-value="current.desensitizedContent" type="textarea" :rows="8" readonly />
        </el-tab-pane>
      </el-tabs>

      <el-input v-model="opinion" type="textarea" :rows="3" placeholder="请输入审核意见" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submit('REJECTED')">驳回</el-button>
        <el-button type="success" @click="submit('APPROVED')">通过</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { auditApi } from '../api/modules'

const tasks = ref([])
const current = ref(null)
const opinion = ref('')
const dialogVisible = ref(false)

async function load() {
  tasks.value = await auditApi.taskDetails()
}

function openDetail(row) {
  current.value = row
  opinion.value = row.riskScore >= 75 ? '存在较高风险，请修改敏感内容后重新提交。' : '内容风险可控，同意通过。'
  dialogVisible.value = true
}

async function submit(status) {
  await auditApi.handle({ auditTaskId: current.value.auditTaskId, status, opinion: opinion.value })
  ElMessage.success('审核完成')
  dialogVisible.value = false
  load()
}

onMounted(load)
</script>

<style scoped>
.detail-tabs {
  margin: 14px 0;
}
</style>
