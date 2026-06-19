<template>
  <section>
    <h2 class="page-title">敏感词库</h2>
    <div class="panel">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索敏感词" clearable style="max-width: 260px" @keyup.enter="load" />
        <el-button type="primary" :icon="Plus" @click="open()">新增敏感词</el-button>
      </div>
      <el-table :data="words" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="敏感词" />
        <el-table-column prop="categoryId" label="分类ID" width="100" />
        <el-table-column prop="riskLevel" label="风险等级" width="120" />
        <el-table-column prop="enabled" label="启用" width="90" />
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <el-button size="small" @click="open(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dialogVisible" title="敏感词信息" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="敏感词">
          <el-input v-model="form.word" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="form.riskLevel">
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
            <el-option label="严重风险" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { sensitiveApi } from '../api/modules'

const keyword = ref('')
const words = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, categoryId: null, word: '', riskLevel: 'MEDIUM', enabled: 1 })

async function load() {
  words.value = await sensitiveApi.list(keyword.value)
  categories.value = await sensitiveApi.categories()
}

function open(row) {
  Object.assign(form, row || { id: null, categoryId: categories.value[0]?.id, word: '', riskLevel: 'MEDIUM', enabled: 1 })
  dialogVisible.value = true
}

async function save() {
  await sensitiveApi.save(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(id) {
  await ElMessageBox.confirm('确认删除该敏感词？', '提示')
  await sensitiveApi.delete(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
