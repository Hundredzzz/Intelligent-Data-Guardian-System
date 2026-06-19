<template>
  <main class="login-page">
    <section class="login-shell">
      <div class="brand">
        <h1>智能数据守护者系统</h1>
        <p>敏感信息检测、AI 审核、风险预警与权限管理一体化平台</p>
      </div>
      <el-form :model="form" class="login-form" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.username" size="large" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <div class="captcha-row">
            <el-input v-model="form.captcha" size="large" placeholder="验证码" :prefix-icon="Key" />
            <button class="captcha-image" type="button" title="点击刷新验证码" @click="loadCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <span v-else>刷新</span>
            </button>
          </div>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="submit">登录</el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { authApi } from '../api/modules'

const router = useRouter()
const loading = ref(false)
const captchaImage = ref('')
const form = reactive({ username: 'admin', password: '123456', captchaKey: '', captcha: '' })

async function loadCaptcha() {
  const data = await authApi.captcha()
  form.captchaKey = data.captchaKey
  form.captcha = ''
  captchaImage.value = data.imageBase64
}

async function submit() {
  if (!form.captcha) {
    ElMessage.warning('请输入验证码')
    return
  }
  loading.value = true
  try {
    const data = await authApi.login(form)
    localStorage.setItem('guardian_token', data.token)
    localStorage.setItem('guardian_user', JSON.stringify(data))
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    await loadCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #eaf2ff 0%, #eef8f4 55%, #f7f7fb 100%);
}

.login-shell {
  width: min(920px, calc(100vw - 32px));
  min-height: 420px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 32px;
  align-items: center;
  padding: 42px;
  background: #fff;
  border: 1px solid #dbe4f0;
  border-radius: 8px;
  box-shadow: 0 24px 70px rgba(31, 41, 55, 0.12);
}

.brand h1 {
  margin: 0 0 14px;
  font-size: 34px;
  letter-spacing: 0;
}

.brand p {
  margin: 0;
  color: #52616f;
  line-height: 1.8;
}

.login-form {
  width: 100%;
}

.login-form .el-button {
  width: 100%;
}

.captcha-row {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 132px;
  gap: 10px;
}

.captcha-image {
  width: 132px;
  height: 40px;
  padding: 0;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f8fafc;
  cursor: pointer;
  overflow: hidden;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
}

@media (max-width: 720px) {
  .login-shell {
    grid-template-columns: 1fr;
    padding: 26px;
  }

  .brand h1 {
    font-size: 26px;
  }
}
</style>
