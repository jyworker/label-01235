<template>
  <div class="page-container dashboard">
    <!-- Header -->
    <div class="page-header">
      <h1 class="page-title">数据看板</h1>
      <span class="header-date">{{ currentDate }}</span>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div
        v-for="(card, index) in statsCards"
        :key="card.key"
        class="stat-card"
        :style="{ animationDelay: `${index * 80}ms` }"
      >
        <div class="stat-icon" :style="{ background: card.bgColor }">
          <el-icon :size="24" :color="card.iconColor">
            <component :is="card.icon" />
          </el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-label">{{ card.label }}</span>
          <span class="stat-value">{{ card.prefix }}{{ animatedValues[card.key] ?? 0 }}{{ card.suffix }}</span>
        </div>
        <div class="stat-trend" v-if="card.subLabel">
          <span class="sub-label">{{ card.subLabel }}</span>
          <span class="sub-value">{{ overview[card.subKey] ?? 0 }}</span>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="charts-row">
      <!-- Visit Trend - ECharts Line -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">近7天访问量</h3>
        </div>
        <div class="chart-body">
          <div ref="visitChartRef" style="width: 100%; height: 280px;"></div>
        </div>
      </div>

      <!-- Order Trend - ECharts Bar -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">近7天订单趋势</h3>
        </div>
        <div class="chart-body">
          <div ref="orderChartRef" style="width: 100%; height: 280px;"></div>
        </div>
      </div>
    </div>

    <!-- Bottom Row -->
    <div class="bottom-row">
      <!-- Top Scenic -->
      <div class="chart-card list-card">
        <div class="chart-header">
          <h3 class="chart-title">热门景点 TOP10</h3>
        </div>
        <div class="chart-body">
          <div class="rank-list" v-if="topScenic.length">
            <div
              v-for="(item, idx) in topScenic"
              :key="idx"
              class="rank-item"
              :style="{ animationDelay: `${idx * 60}ms` }"
            >
              <span class="rank-num" :class="{ top3: idx < 3 }">{{ idx + 1 }}</span>
              <span class="rank-name">{{ item.name }}</span>
              <div class="rank-bar-bg">
                <div
                  class="rank-bar-fill"
                  :style="{
                    width: `${getTopPercent(item, topScenic)}%`,
                    background: idx < 3 ? 'var(--color-primary)' : 'var(--color-secondary)'
                  }"
                ></div>
              </div>
              <span class="rank-count">{{ item.visitCount || item.count || 0 }}</span>
            </div>
          </div>
          <div v-else class="chart-empty">暂无数据</div>
        </div>
      </div>

      <!-- Category Distribution - ECharts Pie -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">分类分布</h3>
        </div>
        <div class="chart-body">
          <div ref="categoryChartRef" style="width: 100%; height: 300px;"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, reactive, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getVisitTrend, getOrderTrend, getTopScenic, getCategoryDistribution } from '@/api/stats'
import { User, Place, Tickets, Money } from '@element-plus/icons-vue'

const overview = ref({})
const visitTrend = ref([])
const orderTrend = ref([])
const topScenic = ref([])
const categoryDist = ref([])

const animatedValues = reactive({
  userCount: 0,
  scenicCount: 0,
  orderCount: 0,
  totalRevenue: 0
})

// ECharts refs
const visitChartRef = ref(null)
const orderChartRef = ref(null)
const categoryChartRef = ref(null)
let visitChart = null
let orderChart = null
let categoryChart = null

const currentDate = computed(() => {
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return now.toLocaleDateString('zh-CN', options)
})

const statsCards = computed(() => [
  {
    key: 'userCount',
    label: '用户总数',
    icon: 'User',
    bgColor: 'var(--color-primary-lighter)',
    iconColor: 'var(--color-primary)',
    prefix: '',
    suffix: '',
    subLabel: '今日访问',
    subKey: 'todayVisit'
  },
  {
    key: 'scenicCount',
    label: '景点总数',
    icon: 'Place',
    bgColor: 'var(--color-secondary-lighter)',
    iconColor: 'var(--color-secondary)',
    prefix: '',
    suffix: '',
    subLabel: null,
    subKey: null
  },
  {
    key: 'orderCount',
    label: '订单总数',
    icon: 'Tickets',
    bgColor: 'var(--color-accent-lighter)',
    iconColor: 'var(--color-accent)',
    prefix: '',
    suffix: '',
    subLabel: '今日订单',
    subKey: 'todayOrder'
  },
  {
    key: 'totalRevenue',
    label: '总收入',
    icon: 'Money',
    bgColor: '#FEF3CD',
    iconColor: '#D4A843',
    prefix: '¥',
    suffix: '',
    subLabel: null,
    subKey: null
  }
])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const parts = dateStr.split('-')
  return `${parts[1] || ''}/${parts[2] || ''}`
}

function getTopPercent(item, arr) {
  const vals = arr.map(i => i.visitCount || i.count || 0)
  const max = Math.max(...vals, 1)
  return ((item.visitCount || item.count || 0) / max) * 100
}

function animateValue(key, target) {
  const duration = 1200
  const startTime = Date.now()
  const tick = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    animatedValues[key] = Math.round(eased * target)
    if (progress < 1) requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
}

// ========== ECharts Init Functions ==========
function initVisitChart(data) {
  if (!visitChartRef.value) return
  visitChart = echarts.init(visitChartRef.value)
  visitChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => formatDate(d.date)),
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#999' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#999' },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      name: '访问量',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(26, 107, 79, 0.3)' },
          { offset: 1, color: 'rgba(26, 107, 79, 0.05)' }
        ])
      },
      lineStyle: { color: '#1A6B4F', width: 2 },
      itemStyle: { color: '#1A6B4F' },
      data: data.map(d => d.count || d.value || 0)
    }]
  })
}

function initOrderChart(data) {
  if (!orderChartRef.value) return
  orderChart = echarts.init(orderChartRef.value)
  orderChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => formatDate(d.date)),
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#999' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#999' },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      name: '订单数',
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#2D7D9A' },
          { offset: 1, color: '#5BB5D5' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      data: data.map(d => d.count || d.value || 0)
    }]
  })
}

function initCategoryChart(data) {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center' },
    color: ['#1A6B4F', '#2D7D9A', '#E8853D', '#D4A843', '#C75450', '#5B8DB8', '#2E8B57', '#8B5CF6', '#EC4899', '#6366F1'],
    series: [{
      name: '景点分类',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: data.map(d => ({ value: d.count || d.value || 0, name: d.name || d.categoryName }))
    }]
  })
}

function handleResize() {
  visitChart?.resize()
  orderChart?.resize()
  categoryChart?.resize()
}

async function loadData() {
  try {
    const [overviewRes, visitRes, orderRes, topRes, catRes] = await Promise.all([
      getOverview(),
      getVisitTrend(),
      getOrderTrend(),
      getTopScenic(),
      getCategoryDistribution()
    ])

    if (overviewRes.code === 200 && overviewRes.data) {
      overview.value = overviewRes.data
      animateValue('userCount', overviewRes.data.userCount || 0)
      animateValue('scenicCount', overviewRes.data.scenicCount || 0)
      animateValue('orderCount', overviewRes.data.orderCount || 0)
      animateValue('totalRevenue', overviewRes.data.totalRevenue || 0)
    }

    if (visitRes.code === 200) {
      visitTrend.value = Array.isArray(visitRes.data) ? visitRes.data : []
    }

    if (orderRes.code === 200) {
      orderTrend.value = Array.isArray(orderRes.data) ? orderRes.data : []
    }

    if (topRes.code === 200) {
      topScenic.value = Array.isArray(topRes.data) ? topRes.data : []
    }

    if (catRes.code === 200) {
      categoryDist.value = Array.isArray(catRes.data) ? catRes.data : []
    }

    // Initialize ECharts after data is loaded
    await nextTick()
    if (visitTrend.value.length) initVisitChart(visitTrend.value)
    if (orderTrend.value.length) initOrderChart(orderTrend.value)
    if (categoryDist.value.length) initCategoryChart(categoryDist.value)
  } catch (e) {
    console.error('加载看板数据失败', e)
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  visitChart?.dispose()
  orderChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard {
  max-width: 1600px;
  margin: 0 auto;
}

.header-date {
  font-size: 14px;
  color: var(--neutral-400);
}

// ==================== Stats Grid ====================
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  transition: all 0.25s ease;
  animation: fadeSlideIn 0.5s both;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 80px;
    height: 80px;
    background: radial-gradient(circle, rgba(0,0,0,0.015) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(20px, -20px);
  }

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--neutral-500);
  font-weight: 500;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--neutral-900);
  font-family: var(--font-display);
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.stat-trend {
  position: absolute;
  bottom: 12px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.sub-label {
  color: var(--neutral-400);
}

.sub-value {
  color: var(--color-primary);
  font-weight: 600;
}

// ==================== Chart Cards ====================
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  animation: fadeSlideIn 0.5s 0.2s both;
}

.chart-header {
  padding: 20px 24px 0;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--neutral-800);
}

.chart-body {
  padding: 20px 24px 24px;
}

.chart-empty {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--neutral-400);
  font-size: 14px;
}

// ==================== Rank List ====================
.list-card .chart-body {
  padding-top: 12px;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  animation: fadeSlideIn 0.4s both;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--neutral-500);
  background: var(--neutral-100);
  flex-shrink: 0;

  &.top3 {
    background: var(--color-primary);
    color: #fff;
  }
}

.rank-name {
  width: 100px;
  font-size: 13px;
  color: var(--neutral-700);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-shrink: 0;
}

.rank-bar-bg {
  flex: 1;
  height: 8px;
  background: var(--neutral-100);
  border-radius: 4px;
  overflow: hidden;
}

.rank-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.rank-count {
  font-size: 13px;
  font-weight: 600;
  color: var(--neutral-600);
  min-width: 40px;
  text-align: right;
}

// ==================== Responsive ====================
@media (max-width: 1280px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .charts-row,
  .bottom-row {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 560px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
