<template>
  <div class="default-layout">
    <!-- Header -->
    <header class="site-header">
      <div class="header-inner container">
        <!-- Logo -->
        <router-link to="/" class="logo">
          <span class="logo-icon">
            <el-icon :size="26"><Compass /></el-icon>
          </span>
          <span class="logo-text">TravelVista</span>
        </router-link>

        <!-- Nav -->
        <nav class="main-nav">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
            首页
          </router-link>
          <router-link to="/scenic" class="nav-link" :class="{ active: $route.path.startsWith('/scenic') }">
            景点
          </router-link>
        </nav>

        <!-- Search -->
        <div class="header-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索景点..."
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            size="default"
          />
        </div>

        <!-- User Area -->
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click" @command="handleUserCommand">
              <div class="user-trigger">
                <el-avatar
                  :size="36"
                  :src="userStore.avatar ? userStore.avatar : ''"
                  class="user-avatar"
                >
                  <el-icon :size="18"><User /></el-icon>
                </el-avatar>
                <span class="user-name">{{ userStore.nickname }}</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Tickets /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="reviews">
                    <el-icon><ChatDotRound /></el-icon>我的评论
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </template>
        </div>

        <!-- Mobile Menu -->
        <button class="mobile-menu-btn" @click="showMobileMenu = !showMobileMenu">
          <el-icon :size="24"><Menu /></el-icon>
        </button>
      </div>

      <!-- Mobile Menu Drawer -->
      <transition name="slide-down">
        <div v-if="showMobileMenu" class="mobile-menu">
          <router-link to="/" class="mobile-nav-link" @click="showMobileMenu = false">首页</router-link>
          <router-link to="/scenic" class="mobile-nav-link" @click="showMobileMenu = false">景点</router-link>
          <div class="mobile-search">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索景点..."
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
            />
          </div>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/profile" class="mobile-nav-link" @click="showMobileMenu = false">个人中心</router-link>
            <router-link to="/favorites" class="mobile-nav-link" @click="showMobileMenu = false">我的收藏</router-link>
            <router-link to="/orders" class="mobile-nav-link" @click="showMobileMenu = false">我的订单</router-link>
            <router-link to="/reviews" class="mobile-nav-link" @click="showMobileMenu = false">我的评论</router-link>
            <a class="mobile-nav-link logout" @click="handleLogout">退出登录</a>
          </template>
          <template v-else>
            <router-link to="/login" class="mobile-nav-link" @click="showMobileMenu = false">登录</router-link>
            <router-link to="/register" class="mobile-nav-link" @click="showMobileMenu = false">注册</router-link>
          </template>
        </div>
      </transition>
    </header>

    <!-- Main Content -->
    <main class="site-main">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <div class="page-transition-wrap" :key="$route.fullPath">
            <component :is="Component" />
          </div>
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="site-footer">
      <div class="footer-inner container">
        <div class="footer-top">
          <div class="footer-brand">
            <div class="footer-logo">
              <el-icon :size="22"><Compass /></el-icon>
              <span>TravelVista</span>
            </div>
            <p class="footer-desc">发现世界之美，开启您的旅行探索之旅。</p>
          </div>
          <div class="footer-links">
            <div class="footer-col">
              <h4>探索</h4>
              <router-link to="/">首页</router-link>
              <router-link to="/scenic">景点列表</router-link>
            </div>
            <div class="footer-col">
              <h4>我的</h4>
              <router-link to="/profile">个人中心</router-link>
              <router-link to="/orders">我的订单</router-link>
              <router-link to="/favorites">我的收藏</router-link>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; {{ new Date().getFullYear() }} TravelVista. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search, User, Compass, ArrowDown, Star, Tickets, ChatDotRound, SwitchButton, Menu } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const searchKeyword = ref('')
const showMobileMenu = ref(false)

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value.trim() } })
    showMobileMenu.value = false
  }
}

function handleUserCommand(command) {
  if (command === 'logout') {
    handleLogout()
  } else {
    router.push({ name: command.charAt(0).toUpperCase() + command.slice(1) })
  }
}

function handleLogout() {
  userStore.logout()
  showMobileMenu.value = false
  ElMessage.success('已退出登录')
}
</script>

<style lang="scss" scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* ===== Header ===== */
.site-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--header-height);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--neutral-200);
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: var(--shadow-sm);
  }
}

.header-inner {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-primary);
  font-weight: 700;
  font-size: 20px;
  font-family: var(--font-serif);
  white-space: nowrap;
  text-decoration: none;

  .logo-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: var(--color-primary);
    color: #fff;
    border-radius: var(--radius-md);
  }
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;

  .nav-link {
    padding: 8px 16px;
    color: var(--neutral-600);
    font-size: 15px;
    font-weight: 500;
    border-radius: var(--radius-sm);
    text-decoration: none;
    transition: all 0.2s ease;

    &:hover,
    &.active {
      color: var(--color-primary);
      background: var(--color-primary-bg);
    }
  }
}

.header-search {
  flex: 1;
  max-width: 320px;

  :deep(.el-input__wrapper) {
    background: var(--neutral-100);
    border: 1px solid transparent;
    border-radius: 20px !important;
    padding: 2px 16px;

    &:hover,
    &.is-focus {
      background: #fff;
      border-color: var(--color-primary);
    }
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius-md);
  transition: background 0.2s;

  &:hover {
    background: var(--neutral-100);
  }

  .user-name {
    font-size: 14px;
    font-weight: 500;
    color: var(--neutral-700);
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dropdown-arrow {
    color: var(--neutral-400);
    font-size: 12px;
  }
}

.user-avatar {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.btn-login {
  padding: 8px 20px;
  color: var(--color-primary);
  font-weight: 500;
  font-size: 14px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  transition: all 0.2s ease;

  &:hover {
    background: var(--color-primary-bg);
  }
}

.btn-register {
  padding: 8px 20px;
  background: var(--color-primary);
  color: #fff;
  font-weight: 500;
  font-size: 14px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  transition: all 0.2s ease;

  &:hover {
    background: var(--color-primary-light);
  }
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  color: var(--neutral-700);
  margin-left: auto;
}

/* Mobile menu */
.mobile-menu {
  position: absolute;
  top: var(--header-height);
  left: 0;
  right: 0;
  background: #fff;
  border-bottom: 1px solid var(--neutral-200);
  box-shadow: var(--shadow-md);
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mobile-nav-link {
  padding: 12px 16px;
  color: var(--neutral-700);
  font-size: 15px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  cursor: pointer;

  &:hover {
    background: var(--neutral-100);
    color: var(--color-primary);
  }

  &.logout {
    color: var(--color-error);
  }
}

.mobile-search {
  padding: 8px 0;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.25s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Main ===== */
.site-main {
  flex: 1;
  margin-top: var(--header-height);
}

/* ===== Footer ===== */
.site-footer {
  background: var(--neutral-800);
  color: var(--neutral-300);
  margin-top: auto;
}

.footer-inner {
  padding-top: 48px;
  padding-bottom: 24px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  gap: 48px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-brand {
  max-width: 320px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-family: var(--font-serif);
  font-weight: 700;
  margin-bottom: 12px;
}

.footer-desc {
  font-size: 14px;
  color: var(--neutral-400);
  line-height: 1.6;
}

.footer-links {
  display: flex;
  gap: 64px;
}

.footer-col {
  display: flex;
  flex-direction: column;
  gap: 8px;

  h4 {
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 8px;
    font-family: var(--font-sans);
  }

  a {
    color: var(--neutral-400);
    font-size: 14px;
    text-decoration: none;
    transition: color 0.2s;

    &:hover {
      color: #fff;
    }
  }
}

.footer-bottom {
  padding-top: 24px;
  text-align: center;
  font-size: 13px;
  color: var(--neutral-500);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .main-nav,
  .header-search,
  .user-area {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .header-inner {
    gap: 12px;
  }

  .footer-top {
    flex-direction: column;
    gap: 32px;
  }

  .footer-links {
    gap: 32px;
  }
}
</style>
