<template>
  <section>
    <h2 class="page-title">{{ title }}</h2>
    <div v-if="isPermissionPage" class="explain">
      <strong>权限管理用于维护“系统功能点字典”。</strong>
      <span>例如：文本检测、文件检测、审核处理、敏感词管理、用户管理。角色管理决定有哪些角色，权限管理决定有哪些可授权功能，后续可以通过角色-权限关系把功能分配给角色。</span>
    </div>
    <div class="panel">
      <div class="toolbar">
        <span class="muted">{{ description }}</span>
        <el-button type="primary" :icon="Plus" @click="add">新增</el-button>
      </div>
      <el-table :data="rows" border>
        <el-table-column v-for="column in columns" :key="column.prop" :label="column.label" :width="column.width">
          <template #default="{ row }">
            <el-input v-model="row[column.prop]" />
          </template>
        </el-table-column>
        <el-table-column label="用途说明" v-if="isPermissionPage">
          <template #default="{ row }">
            <el-tag>{{ permissionUsage(row.permissionCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="save(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { adminApi } from '../api/modules'

const route = useRoute()
const rows = ref([])
const isPermissionPage = computed(() => route.path === '/permissions')

const config = computed(() => {
  if (route.path === '/roles') {
    return {
      title: '角色管理',
      description: '维护系统角色，例如员工、部门主管、数据安全员、系统管理员。',
      columns: [
        { prop: 'roleCode', label: '角色编码', width: 170 },
        { prop: 'roleName', label: '角色名称', width: 160 },
        { prop: 'description', label: '说明' }
      ],
      load: adminApi.roles,
      save: adminApi.saveRole,
      empty: { roleCode: '', roleName: '', description: '' }
    }
  }
  if (route.path === '/permissions') {
    return {
      title: '权限管理',
      description: '维护后端接口和前端菜单对应的权限编码。',
      columns: [
        { prop: 'permissionCode', label: '权限编码', width: 180 },
        { prop: 'permissionName', label: '权限名称', width: 160 },
        { prop: 'menuPath', label: '菜单路径', width: 180 }
      ],
      load: adminApi.permissions,
      save: adminApi.savePermission,
      empty: { permissionCode: '', permissionName: '', menuPath: '' }
    }
  }
  return {
    title: '系统配置',
    description: '维护 JWT、模型、检测限制等系统参数。',
    columns: [
      { prop: 'configKey', label: '配置键', width: 220 },
      { prop: 'configValue', label: '配置值', width: 220 },
      { prop: 'description', label: '说明' }
    ],
    load: adminApi.configs,
    save: adminApi.saveConfig,
    empty: { configKey: '', configValue: '', description: '' }
  }
})

const title = computed(() => config.value.title)
const description = computed(() => config.value.description)
const columns = computed(() => config.value.columns)

async function load() {
  rows.value = await config.value.load()
}

function add() {
  rows.value.unshift({ ...config.value.empty })
}

async function save(row) {
  await config.value.save(row)
  ElMessage.success('保存成功')
  load()
}

function permissionUsage(code) {
  if (!code) return '待配置'
  if (code.startsWith('detect')) return '员工检测权限'
  if (code.startsWith('audit')) return '主管审核权限'
  if (code.startsWith('warning')) return '风险处理权限'
  if (code.startsWith('sensitive')) return '安全员管理权限'
  if (code.startsWith('admin')) return '管理员运维权限'
  return '扩展权限'
}

watch(() => route.path, load)
onMounted(load)
</script>

<style scoped>
.explain {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
  padding: 14px 16px;
  background: #eef7ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
  color: #1f2937;
  line-height: 1.6;
}
</style>
