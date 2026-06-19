<template>
  <section>
    <h2 class="page-title">检测结果与历史记录</h2>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">查看自己提交的检测任务、风险结果和主管审核意见</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="rows" border>
        <el-table-column prop="taskId" label="任务ID" width="90" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="contentType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag>{{ row.contentType === 'FILE' ? '文件' : '文本' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险" width="110" />
        <el-table-column prop="riskScore" label="评分" width="80" />
        <el-table-column prop="taskStatus" label="任务状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.taskStatus)">{{ statusText(row.taskStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.auditStatus)">{{ statusText(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditOpinion" label="审核意见" min-width="220" />
        <el-table-column label="详情" width="100">
          <template #default="{ row }">
            <el-button size="small" @click="open(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="检测详情" width="760px">
      <el-descriptions v-if="current" :column="1" border>
        <el-descriptions-item label="命中摘要">{{ current.hitSummary }}</el-descriptions-item>
        <el-descriptions-item label="AI建议">{{ current.aiSuggestion }}</el-descriptions-item>
        <el-descriptions-item label="审核意见">{{ current.auditOpinion || '待审核' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { detectApi } from '../api/modules'

const rows = ref([])
const current = ref(null)
const dialogVisible = ref(false)

async function load() {
  rows.value = await detectApi.history()
}

function open(row) {
  current.value = row
  dialogVisible.value = true
}

function statusText(status) {
  return {
    WAIT_AUDIT: '待审核',
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回'
  }[status] || status || '待审核'
}

function statusType(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

onMounted(load)
</script>
