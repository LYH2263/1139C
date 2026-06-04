<template>
  <div class="login-wrapper">
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-logo">
          <el-icon :size="48"><Reading /></el-icon>
        </div>
        <h1 class="brand-title">WordMind</h1>
        <p class="brand-slogan">
          The clear path to vocabulary mastery.<br>
          英语单词思维导图记忆系统
        </p>
      </div>
      <div class="brand-bg-circle"></div>
      <div class="brand-bg-circle small"></div>
    </div>
    
    <div class="form-section">
      <div class="form-container">
        <h2 class="form-title">{{ activeTab === 'login' ? '欢迎回来' : '创建账号' }}</h2>
        <p class="form-subtitle">
          {{ activeTab === 'login' ? '登录以继续您的学习之旅' : '加入 WordMind 开始高效记忆' }}
        </p>

        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="auth-form" label-position="top">
              <el-form-item prop="username" label="用户名">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="请输入您的用户名"
                  :prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              
              <el-form-item prop="password" label="密码">
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="请输入您的密码"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                  @keyup.enter="handleLogin"
                />
              </el-form-item>
              
              <el-form-item class="submit-item">
                <el-button 
                  type="primary" 
                  size="large" 
                  :loading="loading"
                  @click="handleLogin"
                  class="submit-btn"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
            
            <!-- Test Accounts -->
            <div class="test-accounts">
              <div class="divider">
                <span>测试账号快速通道</span>
              </div>
              <div class="account-shortcuts">
                <div class="shortcut-card" @click="fillTestAccount('user', 'user123')">
                  <div class="shortcut-icon user">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="shortcut-info">
                    <span class="name">普通用户</span>
                    <span class="action">点击填充</span>
                  </div>
                </div>
                <div class="shortcut-card" @click="fillTestAccount('admin', 'admin123')">
                  <div class="shortcut-icon admin">
                    <el-icon><UserFilled /></el-icon>
                  </div>
                  <div class="shortcut-info">
                    <span class="name">管理员</span>
                    <span class="action">点击填充</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="注册" name="register">
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="auth-form" label-position="top">
              <el-form-item prop="username" label="用户名">
                <el-input 
                  v-model="registerForm.username" 
                  placeholder="3-20个字符"
                  :prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              
              <el-form-item prop="password" label="密码">
                <el-input 
                  v-model="registerForm.password" 
                  type="password" 
                  placeholder="6-32个字符"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              
              <el-form-item prop="email" label="邮箱 (可选)">
                <el-input 
                  v-model="registerForm.email" 
                  placeholder="用于找回密码"
                  :prefix-icon="Message"
                  size="large"
                />
              </el-form-item>
              
              <el-form-item class="submit-item">
                <el-button 
                  type="success" 
                  size="large" 
                  :loading="loading"
                  @click="handleRegister"
                  class="submit-btn"
                >
                  注册并登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, UserFilled, Reading } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeTab = ref('login')
const loginFormRef = ref()
const registerFormRef = ref()

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await authApi.login(loginForm)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await authApi.register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fillTestAccount = (username: string, password: string) => {
  loginForm.username = username
  loginForm.password = password
  ElMessage.success({
    message: `已填充 ${username === 'admin' ? '管理员' : '普通用户'} 账号`,
    type: 'success',
  })
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  background-color: var(--c-bg-body);
}

/* Brand Section */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, var(--c-primary-dark) 0%, var(--c-primary) 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
  padding: 40px;
  text-align: center;
}

.brand-content {
  position: relative;
  z-index: 2;
}

.brand-logo {
  font-size: 64px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.2);
  width: 100px;
  height: 100px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  backdrop-filter: blur(10px);
}

.brand-title {
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 16px;
  letter-spacing: -1px;
}

.brand-slogan {
  font-size: 18px;
  line-height: 1.6;
  opacity: 0.9;
  font-weight: 300;
}

.brand-bg-circle {
  position: absolute;
  top: -10%;
  right: -10%;
  width: 50%;
  padding-bottom: 50%;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.brand-bg-circle.small {
  top: auto;
  bottom: -5%;
  left: -5%;
  width: 30%;
  padding-bottom: 30%;
}

/* Form Section */
.form-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background-color: var(--c-bg-body);
}

.form-container {
  width: 100%;
  max-width: 420px;
}

.form-title {
  font-size: 32px;
  color: var(--c-text-primary);
  margin-bottom: 8px;
  font-weight: 700;
}

.form-subtitle {
  color: var(--c-text-secondary);
  font-size: 16px;
  margin-bottom: 32px;
}

/* Custom Tabs */
:deep(.custom-tabs .el-tabs__header) {
  margin-bottom: 32px;
}

:deep(.custom-tabs .el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: var(--c-border-light);
}

:deep(.custom-tabs .el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

.auth-form .el-form-item {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--c-border) inset;
  padding: 8px 15px;
  border-radius: var(--radius-md);
  transition: all 0.2s;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--c-primary-light) inset;
}

.submit-btn {
  width: 100%;
  font-weight: 600;
  letter-spacing: 0.5px;
  height: 48px;
}

/* Test Accounts */
.test-accounts {
  margin-top: 40px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  color: var(--c-text-tertiary);
  font-size: 13px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: var(--c-border);
}

.divider span {
  padding: 0 12px;
}

.account-shortcuts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.shortcut-card {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--c-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  background-color: var(--c-bg-card);
}

.shortcut-card:hover {
  border-color: var(--c-primary);
  background-color: var(--c-primary-bg);
}

.shortcut-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  background-color: var(--c-bg-body);
}

.shortcut-icon.user { color: var(--c-info); }
.shortcut-icon.admin { color: var(--c-warning); }

.shortcut-info {
  display: flex;
  flex-direction: column;
}

.shortcut-info .name {
  font-size: 14px;
  font-weight: 600;
  color: var(--c-text-primary);
}

.shortcut-info .action {
  font-size: 12px;
  color: var(--c-text-tertiary);
}

/* Responsive */
@media (max-width: 900px) {
  .brand-section {
    display: none;
  }
  
  .form-section {
    padding: 20px;
  }
}
</style>
