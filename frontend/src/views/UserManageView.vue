<template>
  <section>
    <h2 class="page-title">用户管理</h2>
    <div class="panel">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索用户名或姓名" clearable style="max-width: 260px" @keyup.enter="load" />
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="users" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="roleCode" label="角色" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(value) => toggle(row.id, value)"
            />
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
import { userApi } from '../api/modules'

const keyword = ref('')
const users = ref([])

async function load() {
  users.value = await userApi.list(keyword.value)
}

async function toggle(id, status) {
  await userApi.toggleStatus(id, status)
  ElMessage.success('用户状态已更新')
  load()
}

onMounted(load)
</script>
