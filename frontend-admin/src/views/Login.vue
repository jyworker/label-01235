<template>
  <div class="login-page">
    <!-- Background decoration -->
    <div class="bg-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
      <div class="bg-pattern"></div>
    </div>

    <div class="login-container">
      <!-- Left branding panel -->
      <div class="branding-panel">
        <div class="branding-content">
          <div class="brand-logo">
            <svg width="48" height="48" viewBox="0 0 28 28" fill="none">
              <path d="M14 2L26 10V22L14 26L2 22V10L14 2Z" fill="#fff" opacity="0.2"/>
              <path d="M14 6L22 11V21L14 24L6 21V11L14 6Z" fill="#fff" opacity="0.4"/>
              <path d="M14 10L18 12.5V17.5L14 20L10 17.5V12.5L14 10Z" fill="#fff"/>
            </svg>
          </div>
          <h1 class="brand-title">TravelVista</h1>
          <p class="brand-subtitle">旅游景点管理系统</p>
          <div class="brand-features">
            <div class="feature-item">
              <div class="feature-dot"></div>
              <span>景点数据可视化管理</span>
            </div>
            <div class="feature-item">
              <div class="feature-dot"></div>
              <span>订单全流程追踪</span>
            </div>
            <div class="feature-item">
              <div class="feature-dot"></div>
              <span>智能数据统计看板</span>
            </div>
          </div>
        </div>
        <div class="branding-footer">
          <span>Powered by Vue 3 + Element Plus</span>
        </div>
      </div>

      <!-- Right login form -->
      <div class="form-panel">
        <div class="form-wrapper">
          <div class="form-header">
            <h2 class="form-title">管理员登录</h2>
            <p class="form-desc">请输入您的账号和密码</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="UserIcon"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                :prefix-icon="LockIcon"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登  录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef } from 'vue'
import { User as UserIcon, Lock as LockIcon } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能少于3个字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!loginFormRef.value) return
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm.value)
  } catch (e) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--neutral-50);
  position: relative;
  overflow: hidden;
}

// Background decoration
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;

  &.circle-1 {
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, rgba(26, 107, 79, 0.06) 0%, transparent 70%);
    top: -200px;
    right: -100px;
  }

  &.circle-2 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(45, 125, 154, 0.05) 0%, transparent 70%);
    bottom: -100px;
    left: -50px;
  }

  &.circle-3 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(232, 133, 61, 0.04) 0%, transparent 70%);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}

.bg-pattern {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(26, 107, 79, 0.02) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(45, 125, 154, 0.02) 0%, transparent 50%);
}

// Login Container
.login-container {
  display: flex;
  width: 880px;
  min-height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08), 0 1px 3px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  position: relative;
  z-index: 1;
  animation: scaleIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}

// Branding Panel
.branding-panel {
  width: 380px;
  background: linear-gradient(145deg, #1A6B4F 0%, #145A42 50%, #0E4432 100%);
  padding: 48px 36px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -60px;
    right: -60px;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -40px;
    left: -40px;
    width: 160px;
    height: 160px;
    background: rgba(255, 255, 255, 0.03);
    border-radius: 50%;
  }
}

.branding-content {
  position: relative;
  z-index: 1;
}

.brand-logo {
  margin-bottom: 24px;
  animation: fadeSlideIn 0.6s 0.2s both;
}

.brand-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  letter-spacing: 1px;
  animation: fadeSlideIn 0.6s 0.3s both;
}

.brand-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 40px;
  font-weight: 400;
  animation: fadeSlideIn 0.6s 0.4s both;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  animation: fadeSlideIn 0.6s both;

  &:nth-child(1) { animation-delay: 0.5s; }
  &:nth-child(2) { animation-delay: 0.6s; }
  &:nth-child(3) { animation-delay: 0.7s; }
}

.feature-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-accent);
  flex-shrink: 0;
}

.branding-footer {
  position: relative;
  z-index: 1;
  color: rgba(255, 255, 255, 0.35);
  font-size: 12px;
}

// Form Panel
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.form-wrapper {
  width: 100%;
  max-width: 340px;
  animation: fadeSlideIn 0.5s 0.3s both;
}

.form-header {
  margin-bottom: 36px;
}

.form-title {
  font-family: var(--font-heading);
  font-size: 24px;
  font-weight: 700;
  color: var(--neutral-900);
  margin-bottom: 8px;
}

.form-desc {
  font-size: 14px;
  color: var(--neutral-400);
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }

  .el-input {
    --el-input-height: 46px;
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  border-radius: 8px !important;
  background: var(--color-primary);
  border-color: var(--color-primary);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(26, 107, 79, 0.35);
  }

  &:active {
    transform: translateY(0);
  }
}

@keyframes fadeSlideIn {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.96);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
