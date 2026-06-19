<template>
  <section>
    <h2 class="page-title">风险预警</h2>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">高风险和严重风险内容会自动进入预警列表</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="warnings" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="detectTaskId" label="任务ID" width="100" />
        <el-table-column prop="warningLevel" label="等级" width="120">
          <template #default="{ row }">
            <el-tag :type="row.warningLevel === 'CRITICAL' ? 'danger' : 'warning'">{{ row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" :disabled="row.status === 'HANDLED'" @click="handle(row.id)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { warningApi } from '../api/modules'

const warnings = ref([])

async function load() {
  warnings.value = await warningApi.list()
}

async function handle(id) {
  await warningApi.handle(id)
  ElMessage.success('预警已处理')
  load()
}

onMounted(load)
</script>
