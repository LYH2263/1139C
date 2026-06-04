<template>
  <div class="section-card">
    <div class="card-header" v-if="title || $slots.header">
      <div class="header-left">
        <h3 v-if="title" class="section-title">{{ title }}</h3>
        <slot name="header"></slot>
      </div>
      <div class="header-right" v-if="$slots.actions">
        <slot name="actions"></slot>
      </div>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
}>()
</script>

<style scoped>
.section-card {
  background: var(--c-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--c-border-light);
  overflow: hidden;
  margin-bottom: var(--space-lg);
}

.card-header {
  padding: var(--space-md) var(--space-lg);
  border-bottom: 1px solid var(--c-border-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--c-text-primary);
  margin: 0;
}

.card-body {
  padding: var(--space-lg);
}

@media (max-width: 640px) {
  .card-body {
    padding: var(--space-md);
  }
}
</style>
