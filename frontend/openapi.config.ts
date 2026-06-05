import type { Config } from 'openapi-typescript'

const config: Config = {
  input: 'http://localhost:8080/v3/api-docs',
  output: 'src/types/api.d.ts',
  version: '3.x',
  propertiesRequiredByDefault: false,
  additionalProperties: false,
  enumValues: true,
  rawSchema: false,
  exportType: true,
  auth: {
    authType: 'bearer',
    token: process.env.OPENAPI_API_TOKEN,
  },
}

export default config
