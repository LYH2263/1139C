<template>
  <div class="quiz-page page-container">
    <PageHeader title="单词测验" subtitle="通过测试检验学习成果。" />

    <div class="quiz-content" v-loading="loading">
      <!-- 1. Start Screen -->
      <transition name="el-fade-in">
        <div v-if="!quizStarted" class="start-screen">
          <div class="hero-start">
            <div class="hero-icon">
              <el-icon><EditPen /></el-icon>
            </div>
            <h2 class="hero-title">准备好挑战了吗？</h2>
            <p class="hero-desc">本轮测验包含 10 道题目，每题 10 分。</p>
            <el-button type="primary" size="large" class="start-btn" @click="startQuiz">
              开始测验
            </el-button>
          </div>
          
          <SectionCard title="测验说明" class="info-card">
            <div class="info-grid">
              <div class="info-item">
                <el-icon class="info-icon"><Timer /></el-icon>
                <span>不限时间</span>
              </div>
              <div class="info-item">
                <el-icon class="info-icon"><CircleCheck /></el-icon>
                <span>即时反馈</span>
              </div>
              <div class="info-item">
                <el-icon class="info-icon"><TrendCharts /></el-icon>
                <span>记录成绩</span>
              </div>
            </div>
          </SectionCard>
        </div>
      </transition>

      <!-- 2. Quiz In Progress -->
      <transition name="el-fade-in">
        <div v-if="quizStarted && !quizFinished" class="quiz-session">
          <el-card class="question-card">
            <div class="quiz-header">
              <span class="q-counter">Question {{ currentIndex + 1 }} / {{ questions.length }}</span>
              <el-progress 
                :percentage="Math.round(((currentIndex + 1) / questions.length) * 100)" 
                :show-text="false"
                class="q-progress"
              />
            </div>

            <div class="question-body">
              <h1 class="target-word">{{ currentQuestion?.question }}</h1>
              <p class="instruction">请选择正确的中文释义：</p>
              
              <div class="options-list">
                <div 
                  v-for="option in currentQuestion?.options" 
                  :key="option"
                  class="option-item"
                  :class="{ selected: currentAnswer === option }"
                  @click="currentAnswer = option"
                >
                  <div class="radio-circle"></div>
                  <span class="option-text">{{ option }}</span>
                </div>
              </div>
            </div>

            <div class="quiz-footer">
              <el-button 
                type="primary" 
                size="large" 
                class="next-btn"
                :disabled="!currentAnswer"
                @click="nextQuestion"
              >
                {{ isLastQuestion ? '提交试卷' : '下一题' }}
              </el-button>
            </div>
          </el-card>
        </div>
      </transition>

      <!-- 3. Result Screen -->
      <transition name="el-fade-in">
        <div v-if="quizFinished" class="result-screen">
          <el-card class="result-card">
            <div class="score-display" :class="scoreLevelClass">
              <div class="score-circle">
                <span class="score-value">{{ quizResult?.score }}</span>
                <span class="score-label">分</span>
              </div>
              <h2 class="result-title">{{ resultTitle }}</h2>
              <p class="result-subtitle">{{ resultSubtitle }}</p>
            </div>

            <div class="stats-row">
               <div class="stat-box">
                 <div class="val">{{ quizResult?.correctCount }}</div>
                 <div class="lbl">答对</div>
               </div>
               <div class="stat-box">
                 <div class="val">{{ quizResult?.totalCount }}</div>
                 <div class="lbl">总题数</div>
               </div>
               <div class="stat-box">
                 <div class="val">{{ quizResult?.duration }}s</div>
                 <div class="lbl">用时</div>
               </div>
            </div>

            <el-divider v-if="quizResult?.wrongWords?.length">错题回顾</el-divider>
            
            <div v-if="quizResult?.wrongWords?.length" class="wrong-list">
              <div v-for="w in quizResult.wrongWords" :key="w.id" class="wrong-item">
                <span class="w-word">{{ w.word }}</span>
                <span class="w-meaning">{{ w.meaning }}</span>
              </div>
            </div>

            <div class="result-actions">
              <el-button @click="$router.push('/dashboard')">返回仪表盘</el-button>
              <el-button type="primary" @click="restartQuiz">再测一次</el-button>
            </div>
          </el-card>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, Timer, CircleCheck, TrendCharts } from '@element-plus/icons-vue'
import type { QuizQuestion, QuizSubmitResponse } from '@/types'
import { quizApi } from '@/api/study'
import PageHeader from '@/components/ui/PageHeader.vue'
import SectionCard from '@/components/ui/SectionCard.vue'

const loading = ref(false)
const quizStarted = ref(false)
const quizFinished = ref(false)
const quizId = ref('')
const questions = ref<QuizQuestion[]>([])
const currentIndex = ref(0)
const currentAnswer = ref('')
const answers = ref<{ wordId: number; answer: string }[]>([])
const quizResult = ref<QuizSubmitResponse | null>(null)

const currentQuestion = computed(() => questions.value[currentIndex.value] || null)
const isLastQuestion = computed(() => currentIndex.value === questions.value.length - 1)

// Result helpers
const scoreLevelClass = computed(() => {
  const s = quizResult.value?.score || 0
  if (s >= 80) return 'level-high'
  if (s >= 60) return 'level-mid'
  return 'level-low'
})

const resultTitle = computed(() => {
  const s = quizResult.value?.score || 0
  if (s >= 90) return '太棒了！'
  if (s >= 80) return '成绩优秀'
  if (s >= 60) return '合格'
  return '继续努力'
})

const resultSubtitle = computed(() => {
  return quizResult.value?.score === 100 
    ? '完美通关，你的词汇量令人印象深刻！' 
    : '多加练习，你一定可以做得更好。'
})

const startQuiz = async () => {
  loading.value = true
  try {
    const res = await quizApi.startQuiz(10)
    quizId.value = res.quizId
    questions.value = res.questions
    quizStarted.value = true
    currentIndex.value = 0
    currentAnswer.value = ''
    answers.value = []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const nextQuestion = () => {
  if (!currentQuestion.value || !currentAnswer.value) return
  
  answers.value.push({
    wordId: currentQuestion.value.wordId,
    answer: currentAnswer.value
  })
  
  if (isLastQuestion.value) {
    submitQuiz()
  } else {
    currentIndex.value++
    currentAnswer.value = ''
  }
}

const submitQuiz = async () => {
  loading.value = true
  try {
    const res = await quizApi.submitQuiz(quizId.value, answers.value)
    quizResult.value = res
    quizFinished.value = true
    ElMessage.success('测验完成！')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const restartQuiz = () => {
  quizStarted.value = false
  quizFinished.value = false
  quizId.value = ''
  questions.value = []
  currentIndex.value = 0
  currentAnswer.value = ''
  answers.value = []
  quizResult.value = null
}
</script>

<style scoped>
.quiz-content {
  max-width: 700px;
  margin: 0 auto;
}

/* Start Screen */
.start-screen {
  text-align: center;
  padding-top: var(--space-xl);
}

.hero-start {
  margin-bottom: var(--space-xl);
}

.hero-icon {
  font-size: 80px;
  color: var(--c-primary);
  margin-bottom: var(--space-md);
}

.hero-title {
  font-size: 32px;
  color: var(--c-text-primary);
  margin-bottom: var(--space-sm);
}

.hero-desc {
  color: var(--c-text-secondary);
  margin-bottom: var(--space-lg);
}

.start-btn {
  width: 200px;
  font-weight: 600;
}

.info-grid {
  display: flex;
  justify-content: space-around;
  padding: var(--space-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--c-text-secondary);
  font-size: var(--font-size-sm);
}

.info-icon {
  font-size: 24px;
  color: var(--c-primary-light);
}

/* Quiz Session */
.question-card {
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.quiz-header {
  margin-bottom: var(--space-lg);
}

.q-counter {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--c-text-tertiary);
  margin-bottom: 8px;
  text-align: right;
}

.question-body {
  flex: 1;
}

.target-word {
  font-size: 36px;
  color: var(--c-text-primary);
  text-align: center;
  margin-bottom: var(--space-md);
}

.instruction {
  text-align: center;
  color: var(--c-text-secondary);
  margin-bottom: var(--space-lg);
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.option-item {
  display: flex;
  align-items: center;
  padding: var(--space-md) var(--space-lg);
  border: 2px solid var(--c-border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
}

.option-item:hover {
  border-color: var(--c-primary-light);
  background: var(--c-bg-body);
}

.option-item.selected {
  border-color: var(--c-primary);
  background: var(--c-primary-bg);
}

.radio-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--c-border);
  margin-right: var(--space-md);
  flex-shrink: 0;
}

.selected .radio-circle {
  border-color: var(--c-primary);
  background: var(--c-primary);
  box-shadow: inset 0 0 0 4px white;
}

.option-text {
  font-size: var(--font-size-base);
  color: var(--c-text-primary);
}

.quiz-footer {
  margin-top: var(--space-xl);
  text-align: center;
}

.next-btn {
  width: 100%;
}

/* Result Screen */
.result-screen {
  text-align: center;
}

.score-display {
  padding: var(--space-lg);
  background: var(--c-bg-body);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-lg);
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 8px solid currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-md);
}

.score-value {
  font-size: 48px;
  font-weight: 800;
  line-height: 1;
}

.score-label {
  font-size: 16px;
  margin-top: 12px;
  margin-left: 2px;
}

.score-display.level-high { color: var(--c-success); }
.score-display.level-mid { color: var(--c-warning); }
.score-display.level-low { color: var(--c-danger); }

.result-title {
  margin: 0 0 8px;
}

.result-subtitle {
  color: var(--c-text-secondary);
  margin: 0;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-bottom: var(--space-lg);
}

.stat-box .val {
  font-size: 24px;
  font-weight: 700;
  color: var(--c-text-primary);
}

.stat-box .lbl {
  font-size: 12px;
  color: var(--c-text-tertiary);
}

.wrong-list {
  text-align: left;
  background: #fef2f2;
  padding: var(--space-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-lg);
}

.wrong-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.w-word { font-weight: 600; color: var(--c-danger); }
.w-meaning { color: var(--c-text-secondary); }

.result-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-md);
}

/* Responsive */
@media (max-width: 640px) {
  .hero-title { font-size: 24px; }
  .target-word { font-size: 28px; }
}
</style>
