<template>
  <el-container class="layout">
    <el-aside width="232px" class="aside">
      <div class="logo">数据守护者</div>
      <el-menu router :default-active="$route.path" background-color="#172033" text-color="#cbd5e1" active-text-color="#ffffff">
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>智能数据守护者系统</span>
        <div class="userbar">
          <span>{{ user.realName || user.username }}</span>
          <el-tag size="small">{{ roleName }}</el-tag>
          <el-button :icon="SwitchButton" circle @click="logout" />
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Bell,
  Checked,
  Collection,
  DataAnalysis,
  Document,
  Files,
  Key,
  Search,
  Setting,
  SwitchButton,
  UserFilled,
  Warning
} from '@element-plus/icons-vue'

const router = useRouter()
const user = computed(() => JSON.parse(localStorage.getItem('guardian_user') || '{}'))

const roleLabels = {
  EMPLOYEE: '普通员工',
  MANAGER: '部门主管',
  SECURITY_OFFICER: '数据安全员',
  ADMIN: '系统管理员'
}

const menuMap = {
  EMPLOYEE: [
    { path: '/detect', label: '文本/文件检测', icon: Search },
    { path: '/history', label: '检测结果与历史', icon: Files },
    { path: '/profile', label: '个人中心', icon: Setting }
  ],
  MANAGER: [
    { path: '/audit', label: '审核申请', icon: Checked },
    { path: '/manager-stats', label: '部门统计', icon: DataAnalysis },
    { path: '/risk-handle', label: '风险处理', icon: Warning },
    { path: '/audit-records', label: '审核记录', icon: Document }
  ],
  SECURITY_OFFICER: [
    { path: '/ai-review', label: 'AI审核', icon: Bell },
    { path: '/ai-review-records', label: 'AI审核记录', icon: Document },
    { path: '/sensitive-words', label: '敏感词库管理', icon: Collection },
    { path: '/warnings', label: '风险预警管理', icon: Warning },
    { path: '/risk-levels', label: '风险等级配置', icon: DataAnalysis }
  ],
  ADMIN: [
    { path: '/users', label: '用户管理', icon: UserFilled },
    { path: '/roles', label: '角色管理', icon: Setting },
    { path: '/permissions', label: '权限管理', icon: Key },
    { path: '/logs', label: '日志管理', icon: Document },
    { path: '/system-config', label: '系统配置', icon: Setting }
  ]
}

const roleName = computed(() => roleLabels[user.value.roleCode] || user.value.roleCode)
const menus = computed(() => menuMap[user.value.roleCode] || [])

function logout() {
  localStorage.removeItem('guardian_token')
  localStorage.removeItem('guardian_user')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  background: #172033;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 22px;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  font-weight: 700;
}

.userbar {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 400;
}

.main {
  background: #f3f6fb;
  padding: 22px;
}

.el-menu {
  border-right: 0;
}
</style>
