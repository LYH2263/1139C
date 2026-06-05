import { diff } from 'openapi-diff'
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const projectRoot = path.resolve(__dirname, '..')

const CURRENT_SPEC_PATH = path.join(projectRoot, 'openapi.json')
const PREV_SPEC_PATH = path.join(projectRoot, 'openapi-prev.json')

function exitWithError(message) {
  console.error('\n❌ ' + message)
  process.exit(1)
}

function loadSpec(filePath) {
  if (!fs.existsSync(filePath)) {
    return null
  }
  try {
    const content = fs.readFileSync(filePath, 'utf-8')
    return JSON.parse(content)
  } catch (e) {
    exitWithError(`Failed to parse spec file: ${filePath}\nError: ${e.message}`)
  }
}

function formatBreakingChanges(breakingChanges) {
  if (!breakingChanges || breakingChanges.length === 0) {
    return ''
  }

  const grouped = {
    endpoints: [],
    parameters: [],
    properties: [],
    responses: [],
    other: [],
  }

  breakingChanges.forEach(change => {
    const action = change.action || change.type
    const code = change.code || change.action || change.type
    const path = change.path || change.sourceSpecPath || ''
    const info = change.info || change.message || JSON.stringify(change)

    if (code.includes('path') || path.includes('paths')) {
      grouped.endpoints.push({ action, code, path, info })
    } else if (code.includes('parameter') || path.includes('parameters')) {
      grouped.parameters.push({ action, code, path, info })
    } else if (code.includes('property') || code.includes('schema') || path.includes('properties')) {
      grouped.properties.push({ action, code, path, info })
    } else if (code.includes('response') || path.includes('responses')) {
      grouped.responses.push({ action, code, path, info })
    } else {
      grouped.other.push({ action, code, path, info })
    }
  })

  let output = '\n📋 Breaking Changes Summary:\n'
  output += '═'.repeat(80) + '\n\n'

  if (grouped.endpoints.length > 0) {
    output += '🔌 Endpoint Changes:\n'
    grouped.endpoints.forEach(c => {
      output += `  • [${c.action.toUpperCase()}] ${c.path}\n`
      output += `    ${c.info}\n`
    })
    output += '\n'
  }

  if (grouped.parameters.length > 0) {
    output += '📦 Parameter Changes:\n'
    grouped.parameters.forEach(c => {
      output += `  • [${c.action.toUpperCase()}] ${c.path}\n`
      output += `    ${c.info}\n`
    })
    output += '\n'
  }

  if (grouped.properties.length > 0) {
    output += '🏷️  Property/Schema Changes:\n'
    grouped.properties.forEach(c => {
      output += `  • [${c.action.toUpperCase()}] ${c.path}\n`
      output += `    ${c.info}\n`
    })
    output += '\n'
  }

  if (grouped.responses.length > 0) {
    output += '📤 Response Changes:\n'
    grouped.responses.forEach(c => {
      output += `  • [${c.action.toUpperCase()}] ${c.path}\n`
      output += `    ${c.info}\n`
    })
    output += '\n'
  }

  if (grouped.other.length > 0) {
    output += '📝 Other Changes:\n'
    grouped.other.forEach(c => {
      output += `  • [${c.action.toUpperCase()}] ${c.code}: ${c.info}\n`
    })
    output += '\n'
  }

  output += '═'.repeat(80) + '\n'
  output += `⚠️  Total Breaking Changes: ${breakingChanges.length}\n`

  return output
}

function formatNonBreakingChanges(nonBreakingChanges) {
  if (!nonBreakingChanges || nonBreakingChanges.length === 0) {
    return ''
  }

  let output = '\n✨ Non-Breaking Changes:\n'
  output += '─'.repeat(80) + '\n\n'

  nonBreakingChanges.forEach(change => {
    const info = change.info || change.message || JSON.stringify(change)
    const path = change.path || change.sourceSpecPath || ''
    output += `  • ${path}\n`
    output += `    ${info}\n`
  })

  output += `\nTotal Non-Breaking Changes: ${nonBreakingChanges.length}\n`
  return output
}

async function main() {
  console.log('🔍 API Change Detection Starting...')
  console.log('─'.repeat(80))

  if (!fs.existsSync(CURRENT_SPEC_PATH)) {
    console.log(`⚠️  Current spec not found at: ${CURRENT_SPEC_PATH}`)
    console.log('💡 Please run: npm run api:fetch')
    exitWithError('Cannot proceed without current OpenAPI spec')
  }

  if (!fs.existsSync(PREV_SPEC_PATH)) {
    console.log(`⚠️  Previous spec not found at: ${PREV_SPEC_PATH}`)
    console.log('💡 This is the first run, no baseline for comparison')
    console.log('💡 After reviewing, save current spec as baseline: npm run api:save')
    process.exit(0)
  }

  const currentSpec = loadSpec(CURRENT_SPEC_PATH)
  const prevSpec = loadSpec(PREV_SPEC_PATH)

  console.log(`📄 Current spec version: ${currentSpec.info?.version || 'unknown'}`)
  console.log(`📄 Previous spec version: ${prevSpec.info?.version || 'unknown'}`)
  console.log()

  try {
    const result = await diff(prevSpec, currentSpec)

    const breakingChanges = result.breakingChanges || []
    const nonBreakingChanges = result.nonBreakingChanges || []

    console.log(formatNonBreakingChanges(nonBreakingChanges))
    console.log(formatBreakingChanges(breakingChanges))

    if (breakingChanges.length > 0) {
      console.log('\n❌ BREAKING CHANGES DETECTED!')
      console.log('💡 Please address these changes before merging.')
      console.log('💡 If this is intentional, update the baseline: npm run api:save')
      process.exit(1)
    } else {
      console.log('\n✅ No breaking changes detected!')
      console.log('💡 You can update the baseline: npm run api:save')
      process.exit(0)
    }
  } catch (e) {
    exitWithError(`Diff failed: ${e.message}\n${e.stack}`)
  }
}

main().catch(e => exitWithError(e.message))
