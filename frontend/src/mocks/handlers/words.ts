import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateWord, generateWordList } from '../faker'

export const handlers = [
  http.get('/api/words', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const url = new URL(request.url)
    const keyword = url.searchParams.get('keyword') || ''
    const pos = url.searchParams.get('pos') || ''
    const page = parseInt(url.searchParams.get('page') || '1', 10)
    const size = parseInt(url.searchParams.get('size') || '20', 10)

    const total = faker.number.int({ min: 50, max: 200 })
    const list = generateWordList(size)

    if (keyword) {
      list.forEach((item, index) => {
        if (index < 3) {
          item.word = keyword + faker.string.alpha({ length: { min: 2, max: 5 } })
          item.meaning = `与${keyword}相关的释义`
        }
      })
    }

    if (pos) {
      list.forEach(item => {
        item.pos = pos
      })
    }

    return HttpResponse.json(
      apiResponse({
        list,
        total,
        page,
        size,
      })
    )
  }),

  http.get(/\/api\/words\/(\d+)/, async ({ request, params }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const id = parseInt(params[0] as string, 10)
    const word = generateWord({ id })

    return HttpResponse.json(apiResponse(word))
  }),
]
