export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface UserInfo {
  id: number
  username: string
  email?: string
  role: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
}

export interface Word {
  id: number
  word: string
  phonetic?: string
  pos?: string
  meaning: string
  example?: string
  memoryTip?: string
  createdAt?: string
}

export interface WordListResponse {
  list: Word[]
  total: number
  page: number
  size: number
}

export interface MindMapNode {
  id: number
  word: string
  meaning: string
  category?: string
  depth: number
}

export interface MindMapEdge {
  source: number
  target: number
  relationType: string
  label: string
}

export interface MindMapResponse {
  centerWord: MindMapNode
  nodes: MindMapNode[]
  edges: MindMapEdge[]
}

export interface ReviewRecord {
  id: number
  wordId: number
  word: string
  meaning: string
  result: string
  proficiency: number
  nextReviewAt?: string
  createdAt?: string
}

export interface TodayReviewResponse {
  list: ReviewRecord[]
  total: number
}

export interface QuizQuestion {
  wordId: number
  word: string
  type: string
  question: string
  options: string[]
  correctAnswer: string
}

export interface QuizStartResponse {
  quizId: string
  questions: QuizQuestion[]
}

export interface QuizSubmitResponse {
  score: number
  correctCount: number
  totalCount: number
  duration: number
  wrongWords: Word[]
}

export interface StatsResponse {
  totalWords: number
  todayReviewCount: number
  accuracy: number
  streakDays: number
}

export interface StudyPlan {
  id: number
  wordId: number
  word: string
  meaning: string
  planType: string
  createdAt: string
}
