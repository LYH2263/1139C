<template>
  <div class="words-page page-container">
    <PageHeader 
      title="单词列表" 
      subtitle="浏览并管理您的个人词库。"
    >
      <template #actions>
        <el-button @click="resetSearch">
          <el-icon class="mr-1"><Refresh /></el-icon> 重置
        </el-button>
        <el-button type="primary" @click="handleSearch">
          <el-icon class="mr-1"><Search /></el-icon> 搜索
        </el-button>
      </template>
    </PageHeader>
    
    <!-- Search Section -->
    <SectionCard class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索单词或释义" 
            clearable 
            @keyup.enter="handleSearch"
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item label="词性">
          <el-select v-model="searchForm.pos" placeholder="全部词性" clearable style="width: 140px">
            <el-option label="名词 (Noun)" value="noun" />
            <el-option label="动词 (Verb)" value="verb" />
            <el-option label="形容词 (Adjective)" value="adjective" />
            <el-option label="副词 (Adverb)" value="adverb" />
          </el-select>
        </el-form-item>
      </el-form>
    </SectionCard>
    
    <!-- Words Table -->
    <SectionCard class="table-section" :body-style="{ padding: '0' }">
      <el-table 
        :data="wordList" 
        v-loading="loading" 
        stripe
        style="width: 100%"
      >
        <el-table-column prop="word" label="单词" min-width="120">
          <template #default="{ row }">
            <el-link type="primary" class="word-link" @click="$router.push(`/words/${row.id}`)">
              {{ row.word }}
            </el-link>
          </template>
        </el-table-column>
        
        <el-table-column prop="phonetic" label="音标" width="140">
           <template #default="{ row }">
            <span class="phonetic-text">{{ row.phonetic }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="pos" label="词性" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.pos" size="small" effect="plain">{{ formatPos(row.pos) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="meaning" label="释义" min-width="200" show-overflow-tooltip />
        
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/mindmap/${row.id}`)">
              <el-icon class="mr-1"><Share /></el-icon> 导图
            </el-button>
            <el-button link type="success" size="small" @click="addToPlan(row)">
              <el-icon class="mr-1"><Plus /></el-icon> 加入计划
            </el-button>
            <el-button link type="info" size="small" @click="$router.push(`/words/${row.id}`)">
              <el-icon class="mr-1"><View /></el-icon> 详情
            </el-button>
          </template>
        </el-table-column>
        
        <template #empty>
          <EmptyState 
            title="未找到单词" 
            description="请尝试更换搜索条件，或联系管理员添加。"
          />
        </template>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </SectionCard>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Share, Plus, View } from '@element-plus/icons-vue'
import type { Word } from '@/types'
import { wordApi } from '@/api/word'
import { statsApi } from '@/api/study'
import PageHeader from '@/components/ui/PageHeader.vue'
import SectionCard from '@/components/ui/SectionCard.vue'
import EmptyState from '@/components/ui/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const wordList = ref<Word[]>([])

const searchForm = reactive({
  keyword: '',
  pos: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const fetchWords = async () => {
  loading.value = true
  try {
    const res = await wordApi.getWords({
      keyword: searchForm.keyword || undefined,
      pos: searchForm.pos || undefined,
      page: pagination.page,
      size: pagination.size
    })
    wordList.value = res.list
    pagination.total = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchWords()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.pos = ''
  pagination.page = 1
  fetchWords()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchWords()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchWords()
}

const addToPlan = async (word: Word) => {
  try {
    await statsApi.createStudyPlan(word.id, 'TODAY')
    ElMessage.success(`已将 "${word.word}" 加入今日学习计划`)
  } catch (error) {
    console.error(error)
  }
}

const formatPos = (pos: string) => {
  const map: Record<string, string> = {
    noun: '名词',
    verb: '动词',
    adjective: '形容词',
    adverb: '副词'
  }
  return map[pos] || pos
}

onMounted(() => {
  fetchWords()
})
</script>

<style scoped>
.search-section {
  margin-bottom: var(--space-md);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-md);
}

.word-link {
  font-weight: 600;
  font-size: var(--font-size-base);
}

.phonetic-text {
  font-family: 'Times New Roman', serif;
  color: var(--c-text-secondary);
}

.pagination-wrapper {
  padding: var(--space-md);
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--c-border-light);
}

.mr-1 { margin-right: 4px; }
</style>
