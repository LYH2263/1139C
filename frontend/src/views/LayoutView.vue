<template>
  <div class="layout-wrapper">
    <!-- Desktop Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo-area">
        <div class="logo-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <span class="logo-text" v-if="!isCollapsed">WordMind</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="main-menu"
        :collapse="isCollapsed"
        background-color="transparent"
        text-color="var(--c-text-secondary)"
        active-text-color="var(--c-primary)"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-menu-item index="/words">
          <el-icon><Document /></el-icon>
          <template #title>单词列表</template>
        </el-menu-item>
        
        <el-menu-item index="/mindmap">
          <el-icon><Share /></el-icon>
          <template #title>思维导图</template>
        </el-menu-item>
        
        <el-menu-item index="/review">
          <el-icon><Calendar /></el-icon>
          <template #title>今日复习</template>
        </el-menu-item>
        
        <el-menu-item index="/quiz">
          <el-icon><Edit /></el-icon>
          <template #title>单词测验</template>
        </el-menu-item>
        
        <el-menu-item index="/records">
          <el-icon><Timer /></el-icon>
          <template #title>学习记录</template>
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/admin">
          <el-icon><Setting /></el-icon>
          <template #title>词库管理</template>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-toggle" @click="toggleSidebar">
        <el-icon v-if="isCollapsed"><Expand /></el-icon>
        <el-icon v-else><Fold /></el-icon>
      </div>
    </aside>
    
    <!-- Mobile Header -->
    <div class="mobile-header">
      <div class="mobile-menu-btn" @click="showMobileDrawer = true">
        <el-icon><Menu /></el-icon>
      </div>
      <span class="mobile-logo">WordMind</span>
      <div class="mobile-user">
         <el-avatar :size="32" class="user-avatar">{{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}</el-avatar>
      </div>
    </div>
    
    <!-- Mobile Drawer -->
    <el-drawer
      v-model="showMobileDrawer"
      direction="ltr"
      size="240px"
      :with-header="false"
      class="mobile-drawer"
    >
      <div class="drawer-logo">
        <el-icon><Reading /></el-icon>
        <span>WordMind</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        class="drawer-menu"
      >
        <el-menu-item index="/dashboard" @click="showMobileDrawer = false">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
         <el-menu-item index="/words" @click="showMobileDrawer = false">
          <el-icon><Document /></el-icon>
          <span>单词列表</span>
        </el-menu-item>
        <el-menu-item index="/mindmap" @click="showMobileDrawer = false">
          <el-icon><Share /></el-icon>
          <span>思维导图</span>
        </el-menu-item>
        <el-menu-item index="/review" @click="showMobileDrawer = false">
          <el-icon><Calendar /></el-icon>
          <span>今日复习</span>
        </el-menu-item>
        <el-menu-item index="/quiz" @click="showMobileDrawer = false">
          <el-icon><Edit /></el-icon>
          <span>单词测验</span>
        </el-menu-item>
        <el-menu-item index="/records" @click="showMobileDrawer = false">
          <el-icon><Timer /></el-icon>
          <span>学习记录</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/admin" @click="showMobileDrawer = false">
          <el-icon><Setting /></el-icon>
          <span>词库管理</span>
        </el-menu-item>
        <el-menu-item @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-drawer>

    <!-- Main Content -->
    <main class="main-container">
      <header class="main-header">
        <div class="header-breadcrumb">
          <!-- Future Breadcrumb Slot -->
        </div>
        <div class="header-actions">
           <el-dropdown @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="32" class="user-avatar">{{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}</el-avatar>
              <span class="username">{{ userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  DataLine, Document, Share, Calendar, Edit, Timer, Setting, 
  Reading, Expand, Fold, Menu, SwitchButton, ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)
const showMobileDrawer = ref(false)

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    handleLogout()
  }
}

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      showMobileDrawer.value = false
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
}
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  min-height: 100vh;
  background-color: var(--c-bg-body);
}

/* Sidebar */
.sidebar {
  width: var(--sidebar-width);
  background: var(--c-bg-card);
  border-right: 1px solid var(--c-border-light);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  z-index: 10;
}

.sidebar.collapsed {
  width: var(--sidebar-width-collapsed);
}

.logo-area {
  height: var(--header-height);
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid var(--c-border-light);
  overflow: hidden;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: var(--c-primary);
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.logo-text {
  font-weight: 700;
  font-size: 18px;
  color: var(--c-text-primary);
  white-space: nowrap;
}

.main-menu {
  flex: 1;
  border-right: none;
  padding-top: var(--space-md);
}

.main-menu :deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: var(--radius-md);
  height: 48px;
  line-height: 48px;
}

.main-menu :deep(.el-menu-item.is-active) {
  background-color: var(--c-primary-bg);
  font-weight: 600;
}

.sidebar-toggle {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--c-text-secondary);
  border-top: 1px solid var(--c-border-light);
}

.sidebar-toggle:hover {
  color: var(--c-primary);
  background-color: var(--c-bg-body);
}

/* Main Content */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.main-header {
  height: var(--header-height);
  background: var(--c-bg-card);
  border-bottom: 1px solid var(--c-border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-xl);
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius-full);
  transition: background-color var(--transition-fast);
}

.user-profile:hover {
  background-color: var(--c-bg-body);
}

.user-avatar {
  background-color: var(--c-primary-light);
  color: white;
  font-weight: 600;
  margin-right: 8px;
}

.username {
  font-size: var(--font-size-sm);
  color: var(--c-text-primary);
  font-weight: 500;
  margin-right: 4px;
}

.content-body {
  flex: 1;
  padding: var(--space-xl);
  overflow-y: auto;
}

/* Mobile Header & Drawer */
.mobile-header {
  display: none;
  height: var(--header-height);
  background: var(--c-bg-card);
  border-bottom: 1px solid var(--c-border-light);
  align-items: center;
  padding: 0 var(--space-md);
  position: sticky;
  top: 0;
  z-index: 20;
}

.mobile-menu-btn {
  font-size: 24px;
  color: var(--c-text-primary);
  cursor: pointer;
}

.mobile-logo {
  flex: 1;
  text-align: center;
  font-weight: 700;
  font-size: 18px;
}

.drawer-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-weight: 700;
  font-size: 18px;
  gap: 12px;
  color: var(--c-primary);
  border-bottom: 1px solid var(--c-border-light);
}

/* Transitions */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive */
@media (max-width: 768px) {
  .layout-wrapper {
    flex-direction: column;
  }
  
  .sidebar {
    display: none;
  }
  
  .main-header {
    display: none;
  }
  
  .mobile-header {
    display: flex;
  }
  
  .content-body {
    padding: var(--space-md);
  }
}
</style>
