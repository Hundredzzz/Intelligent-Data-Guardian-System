<template>
  <section>
    <h2 class="page-title">统计分析</h2>
    <el-row :gutter="14" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in cards" :key="item.label">
        <div class="stat-card">
          <el-icon><component :is="item.icon" /></el-icon>
          <div>
            <p>{{ item.label }}</p>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </el-col>
    </el-row>
    <div class="panel chart-panel">
      <div ref="chartRef" class="chart"></div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Bell, Files, TrendCharts, User } from '@element-plus/icons-vue'
import { dashboardApi } from '../api/modules'

const stats = ref({})
const chartRef = ref()

const cards = computed(() => [
  { label: '用户数量', value: stats.value.userCount ?? 0, icon: User },
  { label: '检测次数', value: stats.value.detectCount ?? 0, icon: Files },
  { label: '风险事件', value: stats.value.riskEventCount ?? 0, icon: Bell },
  { label: '审核通过率', value: `${Number(stats.value.auditPassRate || 0).toFixed(1)}%`, icon: TrendCharts }
])

onMounted(async () => {
  stats.value = await dashboardApi.stats()
  const rows = await dashboardApi.riskDistribution()
  const source = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((level) => ({
    name: level,
    value: rows.filter((row) => row.riskLevel === level).length
  }))
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['42%', '68%'],
      data: source,
      label: { formatter: '{b}: {c}' }
    }]
  })
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 14px;
}

.stat-card {
  height: 104px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.stat-card .el-icon {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  color: #0f766e;
  background: #dff5ef;
}

.stat-card p {
  margin: 0 0 8px;
  color: #64748b;
}

.stat-card strong {
  font-size: 24px;
}

.chart-panel {
  height: 360px;
}

.chart {
  width: 100%;
  height: 320px;
}
</style>
