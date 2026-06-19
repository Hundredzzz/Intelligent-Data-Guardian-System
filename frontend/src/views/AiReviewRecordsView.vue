<template>
  <section>
    <h2 class="page-title">AI审核记录</h2>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">查看数据安全员提交过的文本和文件AI审核结论</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="records" border>
        <el-table-column prop="id" label="记录ID" width="90" />
        <el-table-column prop="reviewScene" label="审核场景" width="170" />
        <el-table-column prop="dataType" label="资料类型" width="120" />
        <el-table-column prop="publishScope" label="发布范围" width="120" />
        <el-table-column prop="contentType" label="内容类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.contentType === 'FILE' ? 'warning' : 'success'">
              {{ row.contentType === 'FILE' ? '文件' : '文本' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" width="160" show-overflow-tooltip />
        <el-table-column prop="reviewResult" label="审核结论" show-overflow-tooltip />
        <el-table-column prop="createTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="detailVisible" title="AI审核记录详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审核场景">{{ current.reviewScene }}</el-descriptions-item>
        <el-descriptions-item label="资料类型">{{ current.dataType }}</el-descriptions-item>
        <el-descriptions-item label="发布范围">{{ current.publishScope }}</el-descriptions-item>
        <el-descriptions-item label="内容类型">{{ current.contentType === 'FILE' ? '文件' : '文本' }}</el-descriptions-item>
        <el-descriptions-item label="文件名" :span="2">{{ current.fileName || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h3 class="detail-title">审核内容</h3>
      <el-input :model-value="current.sourceContent" type="textarea" :rows="7" readonly />
      <h3 class="detail-title">审核结论</h3>
      <el-input :model-value="current.reviewResult" type="textarea" :rows="9" readonly />
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { aiReviewApi } from '../api/modules'

const records = ref([])
const detailVisible = ref(false)
const current = ref({})

async function load() {
  records.value = await aiReviewApi.records()
}

function openDetail(row) {
  current.value = row
  detailVisible.value = true
}

onMounted(load)
</script>

<style scoped>
.detail-title {
  margin: 16px 0 8px;
  font-size: 15px;
}
</style>
