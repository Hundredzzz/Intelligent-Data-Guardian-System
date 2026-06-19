<template>
  <section>
    <h2 class="page-title">个人中心</h2>
    <el-row :gutter="14">
      <el-col :xs="24" :md="12">
        <div class="panel">
          <el-form :model="profile" label-width="90px">
            <el-form-item label="姓名"><el-input v-model="profile.realName" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="profile.phone" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="profile.email" /></el-form-item>
            <el-button type="primary" @click="saveProfile">保存资料</el-button>
          </el-form>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="panel">
          <el-form :model="passwordForm" label-width="90px">
            <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
            <el-button type="warning" @click="changePassword">修改密码</el-button>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '../api/modules'

const profile = reactive({ realName: '', phone: '', email: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '' })

async function load() {
  const data = await authApi.profile()
  Object.assign(profile, data)
}

async function saveProfile() {
  await authApi.updateProfile(profile)
  ElMessage.success('资料已保存')
}

async function changePassword() {
  await authApi.changePassword(passwordForm)
  ElMessage.success('密码已修改，请重新登录')
}

onMounted(load)
</script>
