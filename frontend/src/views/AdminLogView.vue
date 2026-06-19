<template>
  <section>
    <h2 class="page-title">日志管理</h2>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">查看登录、操作、审核、异常等系统审计日志</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="logs" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="logType" label="类型" width="120" />
        <el-table-column prop="operation" label="操作" width="160" />
        <el-table-column prop="detail" label="详情" />
        <el-table-column prop="ipAddress" label="IP" width="140" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { adminApi } from '../api/modules'

const logs = ref([])

async function load() {
  logs.value = await adminApi.logs()
}

onMounted(load)
</script>
