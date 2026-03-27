<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="bg-pattern"></div>
    </div>
    <div class="auth-container">
      <div class="auth-card animate-in">
        <!-- Logo -->
        <router-link to="/" class="auth-logo">
          <span class="logo-icon">
            <el-icon :size="24"><Compass /></el-icon>
          </span>
          <span class="logo-text">TravelVista</span>
        </router-link>

        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">登录您的账户，继续探索旅行的美好</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="submit-btn"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <span>还没有账户？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, Compass } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(form)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      // 显示后端返回的错误信息
      ElMessage.error(res.message || '用户名或密码错误')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #0d3b2e 0%, #1a5c45 35%, #2d7d9a 65%, #1a6b4f 100%);

  .bg-pattern {
    position: absolute;
    inset: 0;
    opacity: 0.04;
    background-image:
      radial-gradient(circle at 20% 30%, #fff 1.5px, transparent 1.5px),
      radial-gradient(circle at 80% 70%, #fff 1px, transparent 1px);
    background-size: 60px 60px, 40px 40px;
  }
}

.auth-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 24px;
}

.auth-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.auth-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-primary);
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 700;
  text-decoration: none;
  margin-bottom: 32px;

  .logo-icon {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-primary);
    color: #fff;
    border-radius: var(--radius-sm);
  }
}

.auth-title {
  font-family: var(--font-serif);
  font-size: 28px;
  color: var(--neutral-900);
  margin-bottom: 8px;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--neutral-500);
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-md);
  letter-spacing: 2px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--neutral-500);

  a {
    color: var(--color-primary);
    font-weight: 600;
    margin-left: 4px;
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--neutral-700);
}

@media (max-width: 480px) {
  .auth-card {
    padding: 32px 24px;
  }

  .auth-title {
    font-size: 24px;
  }
}
</style>
