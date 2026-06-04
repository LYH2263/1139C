<template>
  <div class="dashboard-page page-container">
    <PageHeader 
      title="仪表盘" 
      subtitle="欢迎回来，坚持学习是掌握词汇的关键。"
    >
      <template #actions>
        <el-button type="primary" @click="fetchStats">
          <el-icon class="mr-1"><Refresh /></el-icon> 刷新数据
        </el-button>
      </template>
    </PageHeader>
    
    <div class="dashboard-content" v-loading="loading">
      <!-- Stats Grid -->
      <div class="stats-grid">
        <StatCard 
          label="已掌握单词" 
          :value="stats?.totalWords || 0" 
          type="primary"
        >
          <template #icon><Document /></template>
        </StatCard>
        
        <StatCard 
          label="今日复习" 
          :value="stats?.todayReviewCount || 0" 
          type="success"
        >
          <template #icon><Calendar /></template>
        </StatCard>
        
        <StatCard 
          label="测验正确率" 
          :value="(stats?.accuracy || 0).toFixed(0) + '%'" 
          type="warning"
        >
          <template #icon><TrendCharts /></template>
        </StatCard>
        
        <StatCard 
          label="连续学习天数" 
          :value="stats?.streakDays || 0" 
          type="danger"
        >
          <template #icon><Timer /></template>
        </StatCard>
      </div>
      
      <!-- Quick Actions -->
      <h3 class="section-title">快速开始</h3>
      <div class="actions-grid">
        <ActionCard 
          title="开始复习" 
          description="复习今日计划单词"
          @click="$router.push('/review')"
        >
          <template #icon><Calendar /></template>
        </ActionCard>
        
        <ActionCard 
          title="思维导图" 
          description="可视化记忆单词"
          @click="$router.push('/mindmap')"
        >
          <template #icon><Share /></template>
        </ActionCard>
        
        <ActionCard 
          title="单词测验" 
          description="检验学习成果"
          @click="$router.push('/quiz')"
        >
          <template #icon><Edit /></template>
        </ActionCard>
      </div>
      
      <!-- Learning Guide -->
      <SectionCard title="使用指南" class="guide-section">
        <template #header>
          <div class="guide-header-icon">
            <el-icon><InfoFilled /></el-icon>
            学习贴士
          </div>
        </template>
        
        <div class="guide-content">
          <el-collapse v-model="activeGuide" accordion>
            <el-collapse-item title="📚 单词测验如何进行？" name="1">
              <div class="guide-text">
                <p>测验是检验记忆效果的最佳方式：</p>
                <ol>
                  <li>点击"快速开始"中的<strong>单词测验</strong>。</li>
                  <li>系统随机抽取10道题，每题从4个中文释义中选出正确答案。</li>
                  <li>完成后查看得分，并可通过错题回顾加强记忆。</li>
                </ol>
              </div>
            </el-collapse-item>
            
            <el-collapse-item title="📅 今日复习如何使用？" name="2">
              <div class="guide-text">
                <p>基于<strong>艾宾浩斯遗忘曲线</strong>的科学复习法：</p>
                <ol>
                  <li>点击<strong>今日复习</strong>卡片。</li>
                  <li>查看单词 -> 回忆释义 -> 点击"显示答案"。</li>
                  <li>根据掌握程度评价：
                    <ul>
                      <li><strong class="text-success">认识</strong>：下次复习间隔延长。</li>
                      <li><strong class="text-warning">模糊</strong>：下次复习间隔缩短。</li>
                      <li><strong class="text-danger">不认识</strong>：立即重新加入复习队列。</li>
                    </ul>
                  </li>
                </ol>
              </div>
            </el-collapse-item>
            
            <el-collapse-item title="🧠 思维导图如何使用？" name="3">
              <div class="guide-text">
                <p>利用联想记忆法构建词汇网络：</p>
                <ol>
                  <li>进入<strong>思维导图</strong>页面。</li>
                  <li>输入中心词 ID 或使用搜索功能。</li>
                  <li>选择关联深度（1度或2度），点击加载。</li>
                  <li>观察单词间的关联，点击节点查看详细释义。</li>
                </ol>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </SectionCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  Document, Calendar, TrendCharts, Timer, 
  Share, Edit, Refresh, InfoFilled 
} from '@element-plus/icons-vue'
import type { StatsResponse } from '@/types'
import { statsApi } from '@/api/study'
import PageHeader from '@/components/ui/PageHeader.vue'
import StatCard from '@/components/ui/StatCard.vue'
import ActionCard from '@/components/ui/ActionCard.vue'
import SectionCard from '@/components/ui/SectionCard.vue'

const stats = ref<StatsResponse | null>(null)
const loading = ref(false)
const activeGuide = ref(['1'])

const fetchStats = async () => {
  loading.value = true
  try {
    stats.value = await statsApi.getStats()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-lg);
  margin-bottom: var(--space-xl);
}

.section-title {
  margin-bottom: var(--space-md);
  color: var(--c-text-primary);
  font-size: var(--font-size-lg);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-lg);
  margin-bottom: var(--space-xl);
}

.guide-header-icon {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  color: var(--c-text-secondary);
  font-size: var(--font-size-sm);
}

.guide-text {
  padding: var(--space-xs) var(--space-md);
  color: var(--c-text-secondary);
  line-height: 1.6;
}

.guide-text ol {
  padding-left: var(--space-lg);
}

.guide-text ul {
  padding-left: var(--space-lg);
  margin-top: var(--space-xs);
}

.text-success { color: var(--c-success); }
.text-warning { color: var(--c-warning); }
.text-danger { color: var(--c-danger); }

.mr-1 { margin-right: 4px; }

/* Responsive */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
