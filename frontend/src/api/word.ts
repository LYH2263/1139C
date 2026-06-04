import api from './request'
import type { Word, WordListResponse } from '@/types'

export const wordApi = {
  getWords: (params?: { keyword?: string; pos?: string; page?: number; size?: number }): Promise<WordListResponse> => {
    return api.get('/words', { params })
  },

  getWordById: (id: number): Promise<Word> => {
    return api.get(`/words/${id}`)
  },

  createWord: (data: Partial<Word>): Promise<Word> => {
    return api.post('/admin/words', data)
  },

  updateWord: (id: number, data: Partial<Word>): Promise<Word> => {
    return api.put(`/admin/words/${id}`, data)
  },

  deleteWord: (id: number): Promise<void> => {
    return api.delete(`/admin/words/${id}`)
  },

  importWords: (file: File): Promise<{ importedCount: number; failedRows: string[] }> => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/admin/words/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
