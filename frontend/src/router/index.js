import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import LayoutView from '../views/LayoutView.vue'
import DetectionView from '../views/DetectionView.vue'
import HistoryView from '../views/HistoryView.vue'
import AuditView from '../views/AuditView.vue'
import ManagerStatsView from '../views/ManagerStatsView.vue'
import AuditRecordsView from '../views/AuditRecordsView.vue'
import WarningView from '../views/WarningView.vue'
import SensitiveWordView from '../views/SensitiveWordView.vue'
import AiReviewView from '../views/AiReviewView.vue'
import AiReviewRecordsView from '../views/AiReviewRecordsView.vue'
import RiskLevelView from '../views/RiskLevelView.vue'
import UserManageView from '../views/UserManageView.vue'
import AdminConfigView from '../views/AdminConfigView.vue'
import AdminLogView from '../views/AdminLogView.vue'
import ProfileView from '../views/ProfileView.vue'

export const roleHome = {
  EMPLOYEE: '/detect',
  MANAGER: '/audit',
  SECURITY_OFFICER: '/ai-review',
  ADMIN: '/users'
}

const routes = [
  { path: '/login', component: LoginView },
  {
    path: '/',
    component: LayoutView,
    redirect: () => roleHome[getCurrentRole()] || '/login',
    children: [
      { path: 'detect', component: DetectionView, meta: { roles: ['EMPLOYEE'] } },
      { path: 'history', component: HistoryView, meta: { roles: ['EMPLOYEE'] } },
      { path: 'profile', component: ProfileView, meta: { roles: ['EMPLOYEE', 'MANAGER', 'SECURITY_OFFICER', 'ADMIN'] } },

      { path: 'audit', component: AuditView, meta: { roles: ['MANAGER'] } },
      { path: 'manager-stats', component: ManagerStatsView, meta: { roles: ['MANAGER'] } },
      { path: 'risk-handle', component: WarningView, meta: { roles: ['MANAGER'] } },
      { path: 'audit-records', component: AuditRecordsView, meta: { roles: ['MANAGER'] } },

      { path: 'ai-review', component: AiReviewView, meta: { roles: ['SECURITY_OFFICER'] } },
      { path: 'ai-review-records', component: AiReviewRecordsView, meta: { roles: ['SECURITY_OFFICER'] } },
      { path: 'sensitive-words', component: SensitiveWordView, meta: { roles: ['SECURITY_OFFICER'] } },
      { path: 'warnings', component: WarningView, meta: { roles: ['SECURITY_OFFICER'] } },
      { path: 'risk-levels', component: RiskLevelView, meta: { roles: ['SECURITY_OFFICER'] } },

      { path: 'users', component: UserManageView, meta: { roles: ['ADMIN'] } },
      { path: 'roles', component: AdminConfigView, meta: { roles: ['ADMIN'], title: '角色管理' } },
      { path: 'permissions', component: AdminConfigView, meta: { roles: ['ADMIN'], title: '权限管理' } },
      { path: 'logs', component: AdminLogView, meta: { roles: ['ADMIN'] } },
      { path: 'system-config', component: AdminConfigView, meta: { roles: ['ADMIN'], title: '系统配置' } },
      { path: 'dashboard', redirect: () => roleHome[getCurrentRole()] || '/profile' }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: () => roleHome[getCurrentRole()] || '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function getCurrentRole() {
  const user = JSON.parse(localStorage.getItem('guardian_user') || '{}')
  return user.roleCode
}

router.beforeEach((to) => {
  const token = localStorage.getItem('guardian_token')
  const role = getCurrentRole()

  if (to.path !== '/login' && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return roleHome[role] || '/profile'
  }
  const roles = to.meta?.roles
  if (roles?.length && !roles.includes(role)) {
    return roleHome[role] || '/profile'
  }
})

export default router
