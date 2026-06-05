import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, delay } from '../faker'

export const handlers = [
  http.get('/api/health', async () => {
    await delay(faker.number.int({ min: 100, max: 300 }))

    return HttpResponse.json(
      apiResponse({
        status: 'ok',
        timestamp: new Date().toISOString(),
        service: 'vocabulary-api',
      })
    )
  }),
]
