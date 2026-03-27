<template>
  <div class="profile-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">个人中心</h1>
        <p class="page-desc animate-in animate-delay-1">管理您的个人信息</p>
      </div>
    </div>

    <div class="container page-body">
      <div class="profile-grid">
        <!-- Sidebar -->
        <aside class="profile-sidebar animate-in">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <el-avatar :size="100" :src="userStore.avatar || ''">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
              <div class="avatar-overlay">
                <el-icon><Camera /></el-icon>
              </div>
            </el-upload>
            <h3 class="user-nickname">{{ userStore.nickname }}</h3>
            <p class="user-username">@{{ userStore.userInfo?.username }}</p>
          </div>

          <nav class="profile-nav">
            <router-link to="/profile" class="nav-item active">
              <el-icon><User /></el-icon>个人信息
            </router-link>
            <router-link to="/favorites" class="nav-item">
              <el-icon><Star /></el-icon>我的收藏
            </router-link>
            <router-link to="/orders" class="nav-item">
              <el-icon><Tickets /></el-icon>我的订单
            </router-link>
            <router-link to="/reviews" class="nav-item">
              <el-icon><ChatDotRound /></el-icon>我的评论
            </router-link>
          </nav>
        </aside>

        <!-- Main Content -->
        <div class="profile-main">
          <!-- Edit Info -->
          <div class="card-block animate-in animate-delay-1">
            <h2 class="card-title">基本信息</h2>
            <el-form
              ref="infoFormRef"
              :model="infoForm"
              :rules="infoRules"
              label-width="80px"
              label-position="left"
              @submit.prevent="handleUpdateInfo"
            >
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="infoForm.gender">
                  <el-radio :value="0">保密</el-radio>
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo" :loading="infoLoading">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- Change Password -->
          <div class="card-block animate-in animate-delay-2">
            <h2 class="card-title">修改密码</h2>
            <el-form
              ref="pwdFormRef"
              :model="pwdForm"
              :rules="pwdRules"
              label-width="80px"
              label-position="left"
              @submit.prevent="handleUpdatePassword"
            >
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword" :loading="pwdLoading">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, updatePassword, uploadAvatar, getUserInfo } from '@/api/auth'
import { User, Star, Tickets, ChatDotRound, Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const infoFormRef = ref(null)
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)

const infoForm = reactive({
  nickname: '',
  email: '',
  phone: '',
  gender: 0
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function initForm() {
  const info = userStore.userInfo
  if (info) {
    infoForm.nickname = info.nickname || ''
    infoForm.email = info.email || ''
    infoForm.phone = info.phone || ''
    infoForm.gender = info.gender || 0
  }
}

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleAvatarUpload({ file }) {
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      ElMessage.success('头像上传成功')
      await userStore.fetchUserInfo()
    }
  } catch (e) {
    console.error(e)
  }
}

async function handleUpdateInfo() {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return

  infoLoading.value = true
  try {
    const res = await updateUserInfo(infoForm)
    if (res.code === 200) {
      ElMessage.success('信息更新成功')
      await userStore.fetchUserInfo()
    }
  } catch (e) {
    console.error(e)
  } finally {
    infoLoading.value = false
  }
}

async function handleUpdatePassword() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return

  pwdLoading.value = true
  try {
    const res = await updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
    }
  } catch (e) {
    console.error(e)
  } finally {
    pwdLoading.value = false
  }
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #1a6b4f 0%, #2d7d9a 100%);
  padding: 48px 0 40px;
  text-align: center;

  .page-title {
    font-family: var(--font-serif);
    font-size: 32px;
    color: #fff;
    margin-bottom: 8px;
  }

  .page-desc {
    font-size: 15px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.page-body {
  padding: 32px 24px 64px;
}

.profile-grid {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 32px;
  align-items: start;
}

/* Sidebar */
.profile-sidebar {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.avatar-section {
  padding: 32px 24px;
  text-align: center;
  border-bottom: 1px solid var(--neutral-200);
}

.avatar-uploader {
  position: relative;
  display: inline-block;
  cursor: pointer;
  margin-bottom: 16px;

  .el-avatar {
    background: var(--color-primary-bg);
    color: var(--color-primary);
    border: 3px solid var(--neutral-200);
  }

  .avatar-overlay {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 28px;
    height: 28px;
    background: var(--color-primary);
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    border: 2px solid #fff;
  }

  :deep(.el-upload) {
    display: block;
  }
}

.user-nickname {
  font-size: 18px;
  font-family: var(--font-serif);
  color: var(--neutral-800);
  margin-bottom: 4px;
}

.user-username {
  font-size: 13px;
  color: var(--neutral-400);
}

.profile-nav {
  padding: 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  font-size: 14px;
  color: var(--neutral-600);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;

  &:hover {
    background: var(--neutral-100);
    color: var(--color-primary);
  }

  &.active,
  &.router-link-exact-active {
    background: var(--color-primary-bg);
    color: var(--color-primary);
    font-weight: 600;
  }
}

/* Main */
.profile-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-block {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  padding: 28px;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 20px;
  color: var(--neutral-800);
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--neutral-200);
}

:deep(.el-form) {
  max-width: 480px;
}

@media (max-width: 768px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    order: 1;
  }

  .profile-main {
    order: 2;
  }

  .page-banner .page-title {
    font-size: 24px;
  }
}
</style>
