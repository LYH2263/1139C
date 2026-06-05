import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateWord } from '../faker'

const relationTypes = ['synonym', 'antonym', 'hyponym', 'hypernym', 'derivative', 'collocation']
const relationLabels = ['同义词', '反义词', '下义词', '上义词', '派生词', '搭配']

export const handlers = [
  http.post('/api/admin/words', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as {
      word: string
      meaning: string
      pos?: string
      phonetic?: string
      example?: string
      memoryTip?: string
    }

    const word = generateWord({
      word: body.word,
      meaning: body.meaning,
      pos: body.pos,
      phonetic: body.phonetic,
      example: body.example,
      memoryTip: body.memoryTip,
    })

    return HttpResponse.json(apiResponse(word))
  }),

  http.put(/\/api\/admin\/words\/(\d+)/, async ({ request, params }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const id = parseInt(params[0] as string, 10)
    const body = await request.json() as {
      word?: string
      meaning?: string
      pos?: string
      phonetic?: string
      example?: string
      memoryTip?: string
    }

    const word = generateWord({
      id,
      ...body,
    })

    return HttpResponse.json(apiResponse(word))
  }),

  http.delete(/\/api\/admin\/words\/(\d+)/, async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    return HttpResponse.json(apiResponse(null))
  }),

  http.post('/api/admin/words/import', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as Array<{
      word: string
      meaning: string
      pos?: string
    }>

    const importedCount = faker.number.int({ min: Math.floor(body.length * 0.7), max: body.length })
    const failedRows = body.length - importedCount

    return HttpResponse.json(
      apiResponse({
        importedCount,
        failedRows,
      })
    )
  }),

  http.post('/api/admin/relations', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const body = await request.json() as {
      source: number
      target: number
      relationType?: string
      label?: string
    }

    const relIndex = faker.number.int({ min: 0, max: relationTypes.length - 1 })

    return HttpResponse.json(
      apiResponse({
        source: body.source,
        target: body.target,
        relationType: body.relationType || relationTypes[relIndex],
        label: body.label || relationLabels[relIndex],
      })
    )
  }),

  http.delete(/\/api\/admin\/relations\/(\d+)/, async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    return HttpResponse.json(apiResponse(null))
  }),
]
