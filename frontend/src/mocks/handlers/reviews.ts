import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateReviewRecord, generateReviewList } from '../faker'

export const handlers = [
  http.get('/api/reviews/today', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 }
    }

    const total = faker.number.int({ min: 5, max: 30 })
    const list = generateReviewList(Math.min(total, 10))

    return HttpResponse.json(
      apiResponse({
        list,
        total,
      })
    )
  }),

  http.post('/api/reviews/submit', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as {
      wordId: number
      result: string
      proficiency: number
    }

    const record = generateReviewRecord({
      wordId: body.wordId,
      result: body.result,
      proficiency: body.proficiency,
    })

    return HttpResponse.json(apiResponse(record))
  }),
]
