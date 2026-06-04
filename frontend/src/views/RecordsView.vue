<template>
  <div class="records-page page-container">
    <PageHeader 
      title="学习记录" 
      subtitle="查看您的学习计划与历史进度。" 
    />
    
    <div class="records-content">
      <SectionCard class="records-card" :body-style="{ padding: '0' }">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="学习计划" name="plans">
            <div class="table-container">
              <el-table 
                :data="studyPlans" 
                v-loading="loading" 
                stripe
                style="width: 100%"
              >
                <el-table-column prop="word" label="单词" width="180">
                  <template #default="{ row }">
                    <span class="word-text">{{ row.word }}</span>
                  </template>
                </el-table-column>
                
                <el-table-column prop="meaning" label="释义" show-overflow-tooltip />
                
                <el-table-column prop="planType" label="计划类型" width="140">
                  <template #default="{ row }">
                    <el-tag 
                      :type="row.planType === 'TODAY' ? 'primary' : 'success'"
                      effect="light"
                      round
                    >
                      {{ row.planType === 'TODAY' ? '今日计划' : '本周计划' }}
                    </el-tag>
                  </template>
                </el-table-column>
                
                <el-table-column prop="createdAt" label="添加时间" width="200">
                  <template #default="{ row }">
                    <span class="time-text">{{ row.createdAt }}</span>
                  </template>
                </el-table-column>
                
                <template #empty>
                  <EmptyState 
                    title="暂无学习计划" 
                    description="去单词列表添加一些想要学习的单词吧！"
                    icon="Timer"
                  >
                    <template #action>
                      <el-button type="primary" @click="$router.push('/words')">去添加</el-button>
                    </template>
                  </EmptyState>
                </template>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </SectionCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { StudyPlan } from '@/types'
import { statsApi } from '@/api/study'
import PageHeader from '@/components/ui/PageHeader.vue'
import SectionCard from '@/components/ui/SectionCard.vue'
import EmptyState from '@/components/ui/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('plans')
const studyPlans = ref<StudyPlan[]>([])

const fetchStudyPlans = async () => {
  loading.value = true
  try {
    const res = await statsApi.getStudyPlans()
    studyPlans.value = res
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStudyPlans()
})
</script>

<style scoped>
.records-content {
  max-width: 1000px;
}

.custom-tabs :deep(.el-tabs__nav-wrap) {
  padding: 0 var(--space-lg);
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.custom-tabs :deep(.el-tabs__content) {
  padding: 0;
}

.word-text {
  font-weight: 600;
  color: var(--c-text-primary);
}

.time-text {
  color: var(--c-text-tertiary);
  font-size: var(--font-size-sm);
}
</style>
