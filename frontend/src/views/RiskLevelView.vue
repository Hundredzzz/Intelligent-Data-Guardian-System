<template>
  <section>
    <h2 class="page-title">风险等级配置</h2>
    <div class="panel">
      <el-table :data="levels" border>
        <el-table-column prop="levelCode" label="等级编码" width="130" />
        <el-table-column label="等级名称" width="140">
          <template #default="{ row }"><el-input v-model="row.levelName" /></template>
        </el-table-column>
        <el-table-column label="最低分" width="110">
          <template #default="{ row }"><el-input-number v-model="row.minScore" :min="0" :max="100" /></template>
        </el-table-column>
        <el-table-column label="最高分" width="110">
          <template #default="{ row }"><el-input-number v-model="row.maxScore" :min="0" :max="100" /></template>
        </el-table-column>
        <el-table-column label="处理规则">
          <template #default="{ row }"><el-input v-model="row.handleRule" /></template>
        </el-table-column>
        <el-table-column label="启用" width="90">
          <template #default="{ row }"><el-switch v-model="row.enabled" :active-value="1" :inactive-value="0" /></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button size="small" type="primary" @click="save(row)">保存</el-button></template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../api/modules'

const levels = ref([])

async function load() {
  levels.value = await adminApi.riskLevels()
}

async function save(row) {
  await adminApi.saveRiskLevel(row)
  ElMessage.success('保存成功')
  load()
}

onMounted(load)
</script>
