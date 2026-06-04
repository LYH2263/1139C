<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon><slot name="icon"><component :is="icon || 'Files'" /></slot></el-icon>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p class="empty-description" v-if="description">{{ description }}</p>
    <div class="empty-action" v-if="$slots.action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Files } from '@element-plus/icons-vue'

defineProps<{
  title?: string
  description?: string
  icon?: any
}>()
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-xl);
  text-align: center;
  color: var(--c-text-secondary);
}

.empty-icon {
  font-size: 48px;
  color: var(--c-text-tertiary);
  margin-bottom: var(--space-md);
  background: var(--c-bg-body);
  width: 96px;
  height: 96px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-title {
  font-size: var(--font-size-lg);
  color: var(--c-text-primary);
  margin-bottom: var(--space-xs);
}

.empty-description {
  max-width: 300px;
  margin-bottom: var(--space-lg);
  font-size: var(--font-size-sm);
}
</style>
