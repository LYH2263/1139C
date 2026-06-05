import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateQuizQuestions, generateWordList } from '../faker'

const quizStartTimeMap = new Map<string, number>()

export const handlers = [
  http.post('/api/quiz/start', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const url = new URL(request.url)
    const count = parseInt(url.searchParams.get('count') || '10', 10)

    const quizId = faker.string.hexadecimal({ length: 16, prefix: '' })
    const questions = generateQuizQuestions(Math.min(count, 20))

    quizStartTimeMap.set(quizId, Date.now())

    return HttpResponse.json(
      apiResponse({
        quizId,
        questions,
      })
    )
  }),

  http.post('/api/quiz/submit', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as {
      quizId: string
      answers: Array<{ wordId: number; answer: string }>
    }

    const startTime = quizStartTimeMap.get(body.quizId) || Date.now() - 60000
    const duration = Date.now() - startTime

    const totalCount = body.answers.length
    const correctCount = faker.number.int({ min: Math.floor(totalCount * 0.4), max: totalCount })
    const score = Math.round((correctCount / totalCount) * 100)

    const wrongWordIds = body.answers
      .slice(0, totalCount - correctCount)
      .map(a => a.wordId)

    const wrongWords = generateWordList(wrongWordIds.length)

    quizStartTimeMap.delete(body.quizId)

    return HttpResponse.json(
      apiResponse({
        score,
        correctCount,
        totalCount,
        duration,
        wrongWords,
      })
    )
  }),
]
