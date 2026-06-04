<template>
  <div class="mindmap-page page-container">
    <PageHeader 
      title="思维导图" 
      subtitle="可视化探索单词关联网络" 
    />
    
    <div class="mindmap-content">
      <!-- Controls -->
      <SectionCard class="controls-card" :body-style="{ padding: 'var(--space-md)' }">
        <div class="controls-wrapper">
          <div class="search-control">
            <el-input 
              v-model="centerWordId" 
              placeholder="输入中心单词 ID" 
              class="search-input"
              @keyup.enter="loadMindMap"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadMindMap" :loading="loading">
              生成导图
            </el-button>
          </div>
          
          <div class="depth-control">
            <span class="label">关联深度:</span>
            <el-radio-group v-model="depth" size="small">
              <el-radio-button :label="1">直接关联 (1度)</el-radio-button>
              <el-radio-button :label="2">扩展关联 (2度)</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </SectionCard>
      
      <!-- Graph Container -->
      <div class="graph-container" v-loading="loading">
        <div ref="chartRef" class="chart-canvas"></div>
        
        <div v-if="!mindMapData && !loading" class="empty-placeholder">
          <EmptyState 
            title="准备生成导图" 
            description="请输入单词 ID 并点击生成，探索词汇网络。"
            icon="Share"
          />
        </div>
        
        <div class="legend" v-if="mindMapData">
          <div class="legend-item">
            <span class="dot primary"></span> 中心词
          </div>
          <div class="legend-item">
            <span class="dot secondary"></span> 关联词
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import type { MindMapResponse } from '@/types'
import { mindMapApi } from '@/api/study'
import { ElMessage } from 'element-plus'
import { Search, Share } from '@element-plus/icons-vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import SectionCard from '@/components/ui/SectionCard.vue'
import EmptyState from '@/components/ui/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const chartRef = ref<HTMLDivElement>()
const chart = ref<echarts.ECharts | null>(null)
const loading = ref(false)
const centerWordId = ref<string>('')
const depth = ref(1)
const mindMapData = ref<MindMapResponse | null>(null)

const loadMindMap = async () => {
  if (!centerWordId.value) {
    ElMessage.warning('请输入单词 ID')
    return
  }
  
  loading.value = true
  try {
    mindMapData.value = await mindMapApi.getMindMap(Number(centerWordId.value), depth.value)
    nextTick(() => {
      renderChart()
    })
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const renderChart = () => {
  if (!chartRef.value || !mindMapData.value) return
  
  if (chart.value) {
    chart.value.dispose()
  }
  
  chart.value = echarts.init(chartRef.value)
  
  const { nodes, edges } = mindMapData.value
  
  // ECharts configuration
  const chartData = nodes.map(node => ({
    id: String(node.id),
    name: node.word,
    value: node.meaning,
    symbolSize: node.depth === 0 ? 60 : 40,
    itemStyle: {
      color: node.depth === 0 ? '#3b82f6' : '#10b981', // Using token values manually
      borderColor: '#fff',
      borderWidth: 2,
      shadowBlur: 10,
      shadowColor: 'rgba(0,0,0,0.1)'
    },
    label: {
      show: true,
      formatter: '{b}',
      fontSize: node.depth === 0 ? 14 : 12,
      color: '#fff' // Better contrast on colored nodes
    }
  }))
  
  const chartLinks = edges.map(edge => ({
    source: String(edge.source),
    target: String(edge.target),
    value: edge.label,
    label: {
      show: true,
      formatter: edge.label,
      fontSize: 10,
      color: '#94a3b8'
    },
    lineStyle: {
      curveness: 0.2,
      color: '#cbd5e1'
    }
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' },
      formatter: (params: any) => {
        if (params.dataType === 'node') {
          return `<div style="font-weight:bold;margin-bottom:4px;">${params.name}</div><div style="font-size:12px;color:#64748b;">${params.value}</div>`
        }
        return params.value
      }
    },
    series: [{
      type: 'graph',
      layout: 'force',
      data: chartData,
      links: chartLinks,
      roam: true,
      draggable: true,
      focusNodeAdjacency: true,
      force: {
        repulsion: 300,
        edgeLength: 120,
        gravity: 0.05
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 3,
          color: '#3b82f6'
        }
      }
    }]
  }
  
  chart.value.setOption(option)
  
  chart.value.on('click', (params: any) => {
    if (params.dataType === 'node') {
      const wordId = Number(params.data.id)
      router.push(`/words/${wordId}`)
      ElMessage.success({ message: `跳转至 "${params.name}" 详情`, type: 'success' })
    }
  })
}

watch(depth, () => {
  if (mindMapData.value) {
    loadMindMap()
  }
})

// Resize handler
const handleResize = () => {
    chart.value?.resize()
}

onMounted(() => {
  const wordId = route.params.wordId
  if (wordId) {
    centerWordId.value = String(wordId)
    loadMindMap()
  }
  
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.controls-card {
  margin-bottom: var(--space-md);
  background: white;
}

.controls-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-md);
}

.search-control {
  display: flex;
  gap: var(--space-sm);
  flex: 1;
  max-width: 400px;
}

.depth-control {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.label {
  font-size: var(--font-size-sm);
  color: var(--c-text-secondary);
}

.graph-container {
  height: calc(100vh - 250px);
  min-height: 500px;
  background: var(--c-bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--c-border-light);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}

.empty-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
}

.legend {
  position: absolute;
  bottom: var(--space-md);
  right: var(--space-md);
  background: rgba(255, 255, 255, 0.9);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: var(--c-text-secondary);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}

.dot.primary { background: #3b82f6; } /* var(--c-primary) */
.dot.secondary { background: #10b981; } /* var(--c-success) */

@media (max-width: 640px) {
  .controls-wrapper {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-control {
    max-width: none;
  }
  
  .depth-control {
    justify-content: flex-end;
  }
}
</style>
