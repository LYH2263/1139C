import { http, HttpResponse } from 'msw'
import { faker } from '@faker-js/faker/locale/en'
import { apiResponse, apiError, delay, generateUser } from '../faker'

export const handlers = [
  http.post('/api/auth/register', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const body = await request.json() as { username: string; password: string; email?: string }

    const user = generateUser({
      username: body.username,
      email: body.email,
      role: 'USER',
    })

    return HttpResponse.json(
      apiResponse({
        userId: user.id,
        username: user.username,
        email: user.email,
        role: user.role,
      })
    )
  }),

  http.post('/api/auth/login', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const body = await request.json() as { username: string; password: string }

    const mockToken = `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${btoa(
      JSON.stringify({
        userId: 1,
        username: body.username,
        exp: Date.now() + 86400000,
      })
    )}.mock-signature`

    const user = generateUser({
      username: body.username,
      role: faker.helpers.arrayElement(['USER', 'ADMIN']),
    })

    return HttpResponse.json(
      apiResponse({
        token: mockToken,
        user,
      })
    )
  }),

  http.get('/api/auth/me', async ({ request }) => {
    await delay(faker.number.int({ min: 300, max: 800 }))

    const authHeader = request.headers.get('Authorization')
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return HttpResponse.json(apiError(401, '未授权访问'), { status: 401 })
    }

    const user = generateUser({
      role: faker.helpers.arrayElement(['USER', 'ADMIN']),
    })

    return HttpResponse.json(apiResponse(user))
  }),
]
