import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateMindMap } from '../faker'

export const handlers = [
  http.get(/\/api\/mindmap\/(\d+)/, async ({ request, params }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const wordId = parseInt(params[0] as string, 10)
    const url = new URL(request.url)
    const depth = parseInt(url.searchParams.get('depth') || '1', 10)

    const mindMap = generateMindMap(wordId, Math.min(depth, 3))

    return HttpResponse.json(apiResponse(mindMap))
  }),
]
