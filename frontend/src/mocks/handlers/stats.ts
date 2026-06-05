import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateStats, generateStudyPlan, generateStudyPlanList } from '../faker'

export const handlers = [
  http.get('/api/stats/me', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const stats = generateStats()

    return HttpResponse.json(apiResponse(stats))
  }),

  http.post('/api/study-plans', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as {
      wordId?: number
      planType?: string
    }

    const plan = generateStudyPlan({
      wordId: body.wordId,
      planType: body.planType,
    })

    return HttpResponse.json(apiResponse(plan))
  }),

  http.get('/api/study-records', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const count = faker.number.int({ min: 5, max: 20 })
    const plans = generateStudyPlanList(count)

    return HttpResponse.json(apiResponse(plans))
  }),
]
