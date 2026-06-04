<template>
  <div class="action-card" @click="$emit('click')">
    <div class="icon-wrapper">
      <el-icon><slot name="icon"></slot></el-icon>
    </div>
    <div class="card-info">
      <h3 class="card-title">{{ title }}</h3>
      <p class="card-desc">{{ description }}</p>
    </div>
    <div class="card-arrow">
      <el-icon><ArrowRight /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue'

defineProps<{
  title: string
  description: string
}>()

defineEmits(['click'])
</script>

<style scoped>
.action-card {
  display: flex;
  align-items: center;
  padding: var(--space-lg);
  background: var(--c-bg-card);
  border: 1px solid var(--c-border-light);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  border-color: var(--c-primary-light);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  background: var(--c-primary-bg);
  color: var(--c-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: var(--space-md);
  flex-shrink: 0;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: var(--font-size-base);
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--c-text-primary);
}

.card-desc {
  font-size: var(--font-size-sm);
  color: var(--c-text-secondary);
  margin: 0;
}

.card-arrow {
  color: var(--c-text-tertiary);
  transition: transform var(--transition-fast);
}

.action-card:hover .card-arrow {
  color: var(--c-primary);
  transform: translateX(4px);
}
</style>
