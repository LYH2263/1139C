import { handlers as authHandlers } from './auth'
import { handlers as wordsHandlers } from './words'
import { handlers as mindmapHandlers } from './mindmap'
import { handlers as reviewsHandlers } from './reviews'
import { handlers as quizHandlers } from './quiz'
import { handlers as statsHandlers } from './stats'
import { handlers as adminHandlers } from './admin'
import { handlers as healthHandlers } from './health'

export const handlers = [
  ...authHandlers,
  ...wordsHandlers,
  ...mindmapHandlers,
  ...reviewsHandlers,
  ...quizHandlers,
  ...statsHandlers,
  ...adminHandlers,
  ...healthHandlers,
]
