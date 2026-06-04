<template>
  <el-card class="stat-card" :body-style="{ padding: 'var(--space-lg)' }">
    <div class="stat-icon" :class="type">
      <el-icon><slot name="icon"></slot></el-icon>
    </div>
    <div class="stat-content">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
      <div v-if="trend" class="stat-trend" :class="trend > 0 ? 'up' : 'down'">
        {{ trend > 0 ? '+' : '' }}{{ trend }}%
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  type?: 'primary' | 'success' | 'warning' | 'danger'
  trend?: number
}>()
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  transition: transform var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: var(--space-md);
  background-color: var(--c-primary-bg);
  color: var(--c-primary);
}

.stat-icon.success { background-color: #ecfdf5; color: var(--c-success); }
.stat-icon.warning { background-color: #fffbeb; color: var(--c-warning); }
.stat-icon.danger { background-color: #fef2f2; color: var(--c-danger); }

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  color: var(--c-text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--c-text-secondary);
  margin-top: 4px;
}

.stat-trend {
  font-size: var(--font-size-xs);
  margin-top: 4px;
  font-weight: 600;
}

.stat-trend.up { color: var(--c-success); }
.stat-trend.down { color: var(--c-danger); }
</style>
