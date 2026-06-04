<template>
  <div class="review-page page-container">
    <PageHeader title="今日复习" subtitle="基于艾宾浩斯曲线智能安排复习计划。" />

    <div class="review-content" v-loading="loading">
      <!-- Empty State -->
      <div v-if="!currentWord && reviewList.length === 0 && !loading" class="empty-view">
        <EmptyState 
          title="无需复习" 
          description="您今天已经完成所有复习任务，去学习新单词或进行测验吧！"
          icon="Calendar"
        >
          <template #action>
            <div class="empty-actions">
              <el-button type="primary" @click="$router.push('/words')">添加单词</el-button>
              <el-button @click="$router.push('/quiz')">去测验</el-button>
            </div>
          </template>
        </EmptyState>
        
        <!-- Algorithm Explanation -->
        <SectionCard class="algo-explanation">
          <template #header>
            <span class="algo-title">🧠 记忆算法说明</span>
          </template>
          <div class="algo-steps">
            <div class="step-item">
              <div class="step-icon known"><el-icon><Check /></el-icon></div>
              <div class="step-text">
                <strong>认识 (熟练度 +1)</strong>
                <span>复习间隔延长</span>
              </div>
            </div>
            <div class="step-item">
              <div class="step-icon vague"><el-icon><Warning /></el-icon></div>
              <div class="step-text">
                <strong>模糊 (熟练度 -1)</strong>
                <span>复习间隔缩短</span>
              </div>
            </div>
            <div class="step-item">
              <div class="step-icon unknown"><el-icon><Close /></el-icon></div>
              <div class="step-text">
                <strong>不认识 (归零)</strong>
                <span>立即重新复习</span>
              </div>
            </div>
          </div>
        </SectionCard>
      </div>

      <!-- Review Card -->
      <div v-else-if="currentWord" class="review-session">
        <el-card class="review-card">
          <div class="session-progress">
            <span class="progress-text">进度 {{ currentIndex + 1 }} / {{ reviewList.length }}</span>
            <el-progress 
              :percentage="Math.round((currentIndex / reviewList.length) * 100)" 
              :show-text="false"
              stroke-width="6"
            />
          </div>

          <div class="flashcard">
            <h1 class="word-text">{{ currentWord.word }}</h1>
            <span class="phonetic" v-if="currentWord.phonetic">/{{ currentWord.phonetic }}/</span>
            
            <transition name="el-fade-in">
              <div v-if="showAnswer" class="answer-area">
                <el-divider />
                <p class="meaning">{{ currentWord.meaning }}</p>
                <p v-if="currentWord.example" class="example">{{ currentWord.example }}</p>
              </div>
            </transition>
          </div>

          <div class="action-area">
            <el-button 
              v-if="!showAnswer" 
              type="primary" 
              size="large" 
              class="reveal-btn"
              @click="showAnswer = true"
            >
              显示答案
            </el-button>

            <div v-else class="feedback-actions">
              <div class="action-col">
                <el-button type="danger" size="large" circle class="action-btn" @click="submitReview('UNKNOWN')">
                  <el-icon><Close /></el-icon>
                </el-button>
                <span class="action-label">不认识</span>
              </div>
              
              <div class="action-col">
                <el-button type="warning" size="large" circle class="action-btn" @click="submitReview('VAGUE')">
                  <el-icon><Warning /></el-icon>
                </el-button>
                <span class="action-label">模糊</span>
              </div>
              
              <div class="action-col">
                <el-button type="success" size="large" circle class="action-btn" @click="submitReview('KNOWN')">
                  <el-icon><Check /></el-icon>
                </el-button>
                <span class="action-label">认识</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- Completion State -->
      <div v-else class="completion-view">
        <EmptyState 
          title="复习完成！" 
          description="您已完成今日所有复习任务。"
          icon="CircleCheck"
        >
          <template #action>
            <el-button type="primary" size="large" @click="$router.push('/dashboard')">
              返回仪表盘
            </el-button>
          </template>
        </EmptyState>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Check, Close, Warning, CircleCheck, InfoFilled } from '@element-plus/icons-vue'
import type { ReviewRecord } from '@/types'
import { reviewApi } from '@/api/study'
import PageHeader from '@/components/ui/PageHeader.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import SectionCard from '@/components/ui/SectionCard.vue'

const loading = ref(false)
const reviewList = ref<ReviewRecord[]>([])
const currentIndex = ref(0)
const showAnswer = ref(false)

const currentWord = computed(() => {
  if (currentIndex.value < reviewList.value.length) {
    return reviewList.value[currentIndex.value]
  }
  return null
})

const fetchTodayReviews = async () => {
  loading.value = true
  try {
    const res = await reviewApi.getTodayReviews()
    reviewList.value = res.list
    currentIndex.value = 0
    showAnswer.value = false
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const submitReview = async (result: string) => {
  if (!currentWord.value) return
  
  try {
    await reviewApi.submitReview(currentWord.value.wordId, result)
    
    // Slight delay for feedback perception if needed, but keeping it snappy
    currentIndex.value++
    showAnswer.value = false
    
    if (currentIndex.value >= reviewList.value.length) {
      ElMessage.success('今日复习完成！')
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchTodayReviews()
})
</script>

<style scoped>
.review-content {
  max-width: 600px;
  margin: 0 auto;
}

/* Empty View */
.empty-view {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

.empty-actions {
  display: flex;
  gap: var(--space-md);
}

.algo-explanation {
  background: var(--c-bg-card);
}

.algo-title {
  font-weight: 600;
  font-size: var(--font-size-md);
}

.algo-steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
}

.step-item {
  text-align: center;
  padding: var(--space-sm);
}

.step-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-sm);
  color: white;
}

.step-icon.known { background-color: var(--c-success); }
.step-icon.vague { background-color: var(--c-warning); }
.step-icon.unknown { background-color: var(--c-danger); }

.step-text {
  display: flex;
  flex-direction: column;
  font-size: var(--font-size-sm);
}

/* Review Session */
.review-card {
  min-height: 500px;
  display: flex;
  flex-direction: column;
  position: relative;
  border-radius: var(--radius-lg);
}

.session-progress {
  margin-bottom: var(--space-xl);
}

.progress-text {
  display: block;
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--c-text-tertiary);
  margin-bottom: var(--space-xs);
}

.flashcard {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: start; /* Should start from top to avoid jump */
  padding-top: var(--space-xl);
}

.word-text {
  font-size: 48px;
  font-weight: 700;
  color: var(--c-text-primary);
  margin-bottom: var(--space-xs);
}

.phonetic {
  color: var(--c-text-secondary);
  font-family: serif;
  font-size: 20px;
  margin-bottom: var(--space-lg);
}

.answer-area {
  width: 100%;
  margin-top: var(--space-md);
}

.meaning {
  font-size: 24px;
  color: var(--c-primary);
  font-weight: 500;
  margin-bottom: var(--space-md);
}

.example {
  background: var(--c-bg-body);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  font-style: italic;
  color: var(--c-text-secondary);
}

/* Actions */
.action-area {
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  display: flex;
  justify-content: center;
  height: 80px; /* Fixed height to prevent layout jump */
}

.reveal-btn {
  width: 200px;
  font-weight: 600;
}

.feedback-actions {
  display: flex;
  gap: 40px;
}

.action-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 64px;
  height: 64px;
  font-size: 24px;
  transition: transform var(--transition-fast);
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-label {
  font-size: var(--font-size-xs);
  color: var(--c-text-secondary);
}

/* Completion */
.completion-view {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 640px) {
  .algo-steps {
    grid-template-columns: 1fr;
    text-align: left;
  }
  
  .step-item {
    display: flex;
    align-items: center;
    gap: var(--space-md);
    margin: 0;
  }
  
  .step-icon {
    margin: 0;
  }
  
  .word-text {
    font-size: 36px;
  }
  
  .feedback-actions {
    gap: 20px;
  }
}
</style>
