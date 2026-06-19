<template>
  <section>
    <h2 class="page-title">审核记录</h2>
    <div class="panel">
      <el-table :data="records" border>
        <el-table-column prop="id" label="记录ID" width="90" />
        <el-table-column prop="auditTaskId" label="审核任务" width="110" />
        <el-table-column prop="reviewerId" label="审核人" width="100" />
        <el-table-column prop="action" label="动作" width="120" />
        <el-table-column prop="opinion" label="意见" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { auditApi } from '../api/modules'

const records = ref([])

onMounted(async () => {
  records.value = await auditApi.records()
})
</script>
