import { faker } from '@faker-js/faker/locale/en'

export interface MockWord {
  id: number
  word: string
  phonetic?: string
  pos?: string
  meaning: string
  example?: string
  memoryTip?: string
  createdAt?: string
}

export interface MockUser {
  id: number
  username: string
  email?: string
  role: string
}

export interface MockReviewRecord {
  id: number
  wordId: number
  word: string
  meaning: string
  result: string
  proficiency: number
  nextReviewAt?: string
  createdAt?: string
}

export interface MockQuizQuestion {
  wordId: number
  word: string
  type: string
  question: string
  options: string[]
  correctAnswer: string
}

export interface MockMindMapNode {
  id: number
  word: string
  meaning: string
  category?: string
  depth: number
}

export interface MockMindMapEdge {
  source: number
  target: number
  relationType: string
  label: string
}

const posList = ['n.', 'v.', 'adj.', 'adv.', 'prep.', 'conj.', 'int.']
const relationTypes = ['synonym', 'antonym', 'hyponym', 'hypernym', 'derivative', 'collocation']
const relationLabels = ['同义词', '反义词', '下义词', '上义词', '派生词', '搭配']

const advancedWords = [
  { word: 'ephemeral', meaning: '短暂的，瞬息的', pos: 'adj.' },
  { word: 'ubiquitous', meaning: '无处不在的，普遍存在的', pos: 'adj.' },
  { word: 'serendipity', meaning: '意外发现珍奇事物的本领', pos: 'n.' },
  { word: 'eloquent', meaning: '雄辩的，有说服力的', pos: 'adj.' },
  { word: 'meticulous', meaning: '一丝不苟的，细致的', pos: 'adj.' },
  { word: 'resilient', meaning: '有弹性的，适应力强的', pos: 'adj.' },
  { word: 'paradigm', meaning: '范例，典范', pos: 'n.' },
  { word: 'ambiguous', meaning: '模棱两可的，含糊的', pos: 'adj.' },
  { word: 'pragmatic', meaning: '务实的，实用主义的', pos: 'adj.' },
  { word: 'nuance', meaning: '细微差别，微妙之处', pos: 'n.' },
  { word: 'profound', meaning: '深刻的，意义深远的', pos: 'adj.' },
  { word: 'innovative', meaning: '创新的，革新的', pos: 'adj.' },
  { word: 'tenacious', meaning: '坚韧的，顽强的', pos: 'adj.' },
  { word: 'eloquence', meaning: '口才，雄辩', pos: 'n.' },
  { word: 'meticulousness', meaning: '一丝不苟，细致', pos: 'n.' },
  { word: 'perseverance', meaning: '毅力，坚持不懈', pos: 'n.' },
  { word: 'spontaneous', meaning: '自发的，即兴的', pos: 'adj.' },
  { word: 'intrinsic', meaning: '固有的，内在的', pos: 'adj.' },
  { word: 'extrinsic', meaning: '外在的，外部的', pos: 'adj.' },
  { word: 'conundrum', meaning: '难题，谜', pos: 'n.' },
  { word: 'dichotomy', meaning: '二分法，对立', pos: 'n.' },
  { word: 'juxtapose', meaning: '并列，并置', pos: 'v.' },
  { word: 'mitigate', meaning: '减轻，缓和', pos: 'v.' },
  { word: 'exacerbate', meaning: '使恶化，加剧', pos: 'v.' },
  { word: 'precarious', meaning: '不稳定的，危险的', pos: 'adj.' },
  { word: 'scrutinize', meaning: '仔细检查，审视', pos: 'v.' },
  { word: 'perfunctory', meaning: '敷衍的，马虎的', pos: 'adj.' },
  { word: 'vindicate', meaning: '证明...正确，辩护', pos: 'v.' },
  { word: 'impeccable', meaning: '无可挑剔的，完美的', pos: 'adj.' },
  { word: 'voracious', meaning: '贪婪的，如饥似渴的', pos: 'adj.' },
]

const exampleTemplates = [
  'The {word} nature of {context} requires careful attention.',
  'Many people underestimate the {word} impact of {context}.',
  'She is known for her {word} approach to {context}.',
  'The {word} concept has revolutionized the field of {context}.',
  'Understanding the {word} aspects of {context} is essential.',
]

const contexts = [
  'modern technology',
  'scientific research',
  'artificial intelligence',
  'climate change',
  'digital transformation',
  'human psychology',
  'business strategy',
  'educational development',
  'environmental protection',
  'social interaction',
]

let wordIdCounter = 1

export function generateWord(override?: Partial<MockWord>): MockWord {
  const wordData = faker.helpers.arrayElement(advancedWords)
  const context = faker.helpers.arrayElement(contexts)
  const example = faker.helpers.arrayElement(exampleTemplates)
    .replace('{word}', wordData.word)
    .replace('{context}', context)

  return {
    id: wordIdCounter++,
    word: wordData.word,
    phonetic: `/${faker.string.alpha(2)}-${faker.string.alpha(3)}-${faker.string.alpha(2)}/`,
    pos: wordData.pos,
    meaning: wordData.meaning,
    example,
    memoryTip: faker.helpers.maybe(() =>
      faker.word.words({ count: { min: 3, max: 8 } })
    ),
    createdAt: faker.date.recent({ days: 30 }).toISOString(),
    ...override,
  }
}

export function generateWordList(count: number): MockWord[] {
  const resetCounter = wordIdCounter
  wordIdCounter = 1
  const words: MockWord[] = []
  for (let i = 0; i < count; i++) {
    words.push(generateWord())
  }
  wordIdCounter = resetCounter
  return words
}

export function generateUser(override?: Partial<MockUser>): MockUser {
  return {
    id: faker.number.int({ min: 1, max: 1000 }),
    username: faker.internet.username(),
    email: faker.helpers.maybe(() => faker.internet.email()),
    role: faker.helpers.arrayElement(['USER', 'ADMIN']),
    ...override,
  }
}

export function generateReviewRecord(override?: Partial<MockReviewRecord>): MockReviewRecord {
  const word = generateWord()
  return {
    id: faker.number.int({ min: 1, max: 10000 }),
    wordId: word.id,
    word: word.word,
    meaning: word.meaning,
    result: faker.helpers.arrayElement(['known', 'vague', 'unknown']),
    proficiency: faker.number.int({ min: 0, max: 5 }),
    nextReviewAt: faker.date.soon({ days: 7 }).toISOString(),
    createdAt: faker.date.recent({ days: 14 }).toISOString(),
    ...override,
  }
}

export function generateReviewList(count: number): MockReviewRecord[] {
  return Array.from({ length: count }, () => generateReviewRecord())
}

export function generateQuizQuestion(override?: Partial<MockQuizQuestion>): MockQuizQuestion {
  const word = generateWord()
  const type = faker.helpers.arrayElement(['meaning', 'word'])
  const wrongOptions = generateWordList(3).map(w => type === 'meaning' ? w.meaning : w.word)
  const correctAnswer = type === 'meaning' ? word.meaning : word.word

  const options = faker.helpers.shuffle([correctAnswer, ...wrongOptions])

  return {
    wordId: word.id,
    word: word.word,
    type,
    question: type === 'meaning'
      ? `"${word.word}" 的中文释义是？`
      : `下列哪个单词表示"${word.meaning}"？`,
    options,
    correctAnswer,
    ...override,
  }
}

export function generateQuizQuestions(count: number): MockQuizQuestion[] {
  return Array.from({ length: count }, () => generateQuizQuestion())
}

export function generateMindMap(centerWordId: number, depth: number) {
  const nodes: MockMindMapNode[] = []
  const edges: MockMindMapEdge[] = []

  const centerWord = generateWord({ id: centerWordId })

  nodes.push({
    id: centerWord.id,
    word: centerWord.word,
    meaning: centerWord.meaning,
    category: 'center',
    depth: 0,
  })

  let currentNodeIds = [centerWord.id]

  for (let d = 1; d <= depth; d++) {
    const nextNodeIds: number[] = []
    const nodesToExpand = d === 1 ? currentNodeIds : faker.helpers.arrayElements(currentNodeIds, Math.min(3, currentNodeIds.length))

    for (const sourceId of nodesToExpand) {
      const childCount = faker.number.int({ min: 1, max: 3 })
      for (let i = 0; i < childCount; i++) {
        const childWord = generateWord()
        nodes.push({
          id: childWord.id,
          word: childWord.word,
          meaning: childWord.meaning,
          category: faker.helpers.arrayElement(['synonym', 'antonym', 'related']),
          depth: d,
        })

        const relIndex = faker.number.int({ min: 0, max: relationTypes.length - 1 })
        edges.push({
          source: sourceId,
          target: childWord.id,
          relationType: relationTypes[relIndex],
          label: relationLabels[relIndex],
        })

        nextNodeIds.push(childWord.id)
      }
    }
    currentNodeIds = nextNodeIds
  }

  return {
    centerWord: nodes[0],
    nodes,
    edges,
  }
}

export function generateStats() {
  return {
    totalWords: faker.number.int({ min: 50, max: 500 }),
    todayReviewCount: faker.number.int({ min: 0, max: 50 }),
    accuracy: faker.number.float({ min: 0.5, max: 0.98, fractionDigits: 2 }),
    streakDays: faker.number.int({ min: 0, max: 365 }),
  }
}

export function generateStudyPlan(override?: Partial<{ id: number; wordId: number; word: string; meaning: string; planType: string; createdAt: string }>) {
  const word = generateWord()
  return {
    id: faker.number.int({ min: 1, max: 1000 }),
    wordId: word.id,
    word: word.word,
    meaning: word.meaning,
    planType: faker.helpers.arrayElement(['vocabulary', 'review', 'exam']),
    createdAt: faker.date.recent({ days: 30 }).toISOString(),
    ...override,
  }
}

export function generateStudyPlanList(count: number) {
  return Array.from({ length: count }, () => generateStudyPlan())
}

export function apiResponse<T>(data: T, code = 0, message = 'ok') {
  return {
    code,
    message,
    data,
    traceId: faker.string.hexadecimal({ length: 16, prefix: '' }),
  }
}

export function apiError(code: number, message: string) {
  return apiResponse(null, code, message)
}

export function delay(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}
