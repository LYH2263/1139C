import type { components, paths } from './api'

export type ApiPaths = paths

export type Schemas = components['schemas']

export type ApiResponse<T = unknown> = {
  code: number
  message: string
  data: T
  traceId?: string
}

export type UserInfo = Schemas['UserInfo']
export type RegisterRequest = Schemas['RegisterRequest']
export type RegisterResponse = Schemas['RegisterResponse']
export type LoginRequest = Schemas['LoginRequest']
export type LoginResponse = Schemas['LoginResponse']
export type Word = Schemas['WordResponse']
export type WordCreateRequest = Schemas['CreateWordRequest']
export type WordUpdateRequest = Schemas['UpdateWordRequest']
export type WordListResponse = Schemas['WordListResponse']
export type MindMapNode = Schemas['WordNode']
export type MindMapEdge = Schemas['RelationEdge']
export type MindMapResponse = Schemas['MindMapResponse']
export type RelationRequest = Schemas['RelationRequest']
export type SubmitReviewRequest = Schemas['SubmitReviewRequest']
export type ReviewRecord = Schemas['ReviewResponse']
export type TodayReviewResponse = Schemas['TodayResponse']
export type QuizQuestion = Schemas['Question']
export type QuizAnswer = Schemas['Answer']
export type QuizStartResponse = Schemas['StartResponse']
export type QuizSubmitRequest = Schemas['SubmitQuizRequest']
export type QuizSubmitResponse = Schemas['SubmitResponse']
export type StatsResponse = Schemas['StatsResponse']
export type CreateStudyPlanRequest = Schemas['CreateStudyPlanRequest']
export type StudyPlan = Schemas['StudyPlanResponse']
export type ImportResult = Schemas['ImportResult']

export * from './api'
