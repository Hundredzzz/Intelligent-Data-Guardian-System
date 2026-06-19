<template>
  <section>
    <h2 class="page-title">部门统计</h2>
    <el-row :gutter="14">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in cards" :key="item.label">
        <div class="stat-card">
          <strong>{{ item.value }}</strong>
          <span>{{ item.label }}</span>
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { dashboardApi } from '../api/modules'

const stats = ref({})
const cards = computed(() => [
  { label: '部门检测任务', value: stats.value.detectCount ?? 0 },
  { label: '风险事件', value: stats.value.riskEventCount ?? 0 },
  { label: '审核通过率', value: `${Number(stats.value.auditPassRate || 0).toFixed(1)}%` },
  { label: '敏感词命中项', value: stats.value.sensitiveWordCount ?? 0 }
])

onMounted(async () => {
  stats.value = await dashboardApi.stats()
})
</script>

<style scoped>
.stat-card {
  height: 112px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  padding: 18px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.stat-card strong {
  font-size: 28px;
}

.stat-card span {
  color: #64748b;
}
</style>
