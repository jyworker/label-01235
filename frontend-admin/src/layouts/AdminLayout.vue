<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo-area">
          <div class="logo-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <path d="M14 2L26 10V22L14 26L2 22V10L14 2Z" fill="#1A6B4F" opacity="0.9"/>
              <path d="M14 6L22 11V21L14 24L6 21V11L14 6Z" fill="#238C68"/>
              <path d="M14 10L18 12.5V17.5L14 20L10 17.5V12.5L14 10Z" fill="#FAFAF8"/>
            </svg>
          </div>
          <transition name="fade">
            <span v-if="!isCollapsed" class="logo-text">TravelVista</span>
          </transition>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: currentRoute === item.name }"
        >
          <el-icon :size="20">
            <component :is="item.icon" />
          </el-icon>
          <transition name="fade">
            <span v-if="!isCollapsed" class="nav-label">{{ item.title }}</span>
          </transition>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-container" :class="{ expanded: isCollapsed }">
      <!-- Top Bar -->
      <header class="topbar" :class="{ 'dropdown-open': showDropdown }">
        <div class="topbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentMeta.title">{{ currentMeta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <div class="admin-profile" @click="showDropdown = !showDropdown">
            <div class="admin-avatar">
              <el-icon :size="18"><UserFilled /></el-icon>
            </div>
            <span class="admin-name">{{ adminInfo?.nickname || adminInfo?.username || '管理员' }}</span>
            <el-icon :size="14" class="arrow-icon" :class="{ rotated: showDropdown }">
              <ArrowDown />
            </el-icon>

            <transition name="dropdown">
              <div v-if="showDropdown" class="dropdown-menu">
                <div class="dropdown-item" @click.stop="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>

    <!-- Click overlay to close dropdown -->
    <div v-if="showDropdown" class="overlay" @click="showDropdown = false"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataAnalysis, Place, Menu, User, ChatDotSquare, Tickets,
  Fold, Expand, UserFilled, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)
const showDropdown = ref(false)

const menuItems = [
  { path: '/dashboard', name: 'Dashboard', title: '数据看板', icon: 'DataAnalysis' },
  { path: '/scenic', name: 'Scenic', title: '景点管理', icon: 'Place' },
  { path: '/category', name: 'Category', title: '分类管理', icon: 'Menu' },
  { path: '/user', name: 'User', title: '用户管理', icon: 'User' },
  { path: '/review', name: 'Review', title: '评论管理', icon: 'ChatDotSquare' },
  { path: '/order', name: 'Order', title: '订单管理', icon: 'Tickets' }
]

const currentRoute = computed(() => route.name)
const currentMeta = computed(() => route.meta || {})
const adminInfo = computed(() => userStore.adminInfo)

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
}

function handleLogout() {
  showDropdown.value = false
  userStore.logout()
}

watch(() => route.path, () => {
  showDropdown.value = false
})

onMounted(() => {
  userStore.fetchAdminInfo()
})
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--neutral-50);
}

// ==================== Sidebar ====================
.sidebar {
  width: var(--sidebar-width);
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;

  &.collapsed {
    width: var(--sidebar-collapsed-width);
  }
}

.sidebar-header {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow: hidden;
  white-space: nowrap;
}

.logo-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.55);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
  position: relative;

  &:hover {
    color: rgba(255, 255, 255, 0.85);
    background: rgba(255, 255, 255, 0.06);
  }

  &.active {
    color: #fff;
    background: var(--color-primary);
    box-shadow: 0 2px 8px rgba(26, 107, 79, 0.4);

    &::before {
      content: '';
      position: absolute;
      left: -8px;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 20px;
      background: var(--color-accent);
      border-radius: 0 2px 2px 0;
    }
  }
}

.nav-label {
  display: inline-block;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.collapse-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  border: none;
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.5);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.8);
  }
}

// ==================== Main Container ====================
.main-container {
  flex: 1;
  margin-left: var(--sidebar-width);
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  &.expanded {
    margin-left: var(--sidebar-collapsed-width);
  }
}

// ==================== Top Bar ====================
.topbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);

  &.dropdown-open {
    z-index: 101;
  }
}

.topbar-left {
  display: flex;
  align-items: center;
}

.topbar-right {
  display: flex;
  align-items: center;
}

.admin-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;

  &:hover {
    background: var(--neutral-100);
  }
}

.admin-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-primary-lighter);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--neutral-700);
}

.arrow-icon {
  transition: transform 0.2s;

  &.rotated {
    transform: rotate(180deg);
  }
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: var(--shadow-lg);
  min-width: 140px;
  padding: 4px;
  z-index: 200;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--neutral-600);
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background: var(--neutral-100);
    color: var(--color-error);
  }
}

.overlay {
  position: fixed;
  inset: 0;
  z-index: 99;
}

// ==================== Content ====================
.content-area {
  flex: 1;
  padding: 0;
  overflow-y: auto;
}

// ==================== Transitions ====================
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.dropdown-enter-active {
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.dropdown-leave-active {
  transition: all 0.15s ease-in;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}
</style>
