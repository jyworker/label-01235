<template>
  <div class="scenic-list-page">
    <!-- Page Header -->
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">探索景点</h1>
        <p class="page-desc animate-in animate-delay-1">发现令您心动的旅行目的地</p>
      </div>
    </div>

    <div class="container page-body">
      <!-- Filters -->
      <div class="filter-bar animate-in">
        <!-- Category Tags -->
        <div class="filter-categories">
          <span
            class="cat-tag"
            :class="{ active: !queryParams.categoryId }"
            @click="selectCategory(null)"
          >全部</span>
          <span
            v-for="cat in categories"
            :key="cat.id"
            class="cat-tag"
            :class="{ active: queryParams.categoryId == cat.id }"
            @click="selectCategory(cat.id)"
          >{{ cat.name }}</span>
        </div>

        <!-- Sort & Rating Filter -->
        <div class="filter-row">
          <div class="filter-sort">
            <span class="sort-label">排序：</span>
            <span
              v-for="item in sortOptions"
              :key="item.value"
              class="sort-tag"
              :class="{ active: queryParams.sortBy === item.value }"
              @click="selectSort(item.value)"
            >{{ item.label }}</span>
          </div>
          <div class="filter-rating">
            <span class="sort-label">评分：</span>
            <span
              v-for="item in ratingOptions"
              :key="item.value"
              class="sort-tag"
              :class="{ active: queryParams.minRating === item.value }"
              @click="selectRating(item.value)"
            >{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- List with stable container to prevent layout flash -->
      <div class="scenic-grid" v-loading="loading" element-loading-background="rgba(255,255,255,0.7)">
        <template v-if="!loading && list.length === 0">
          <el-empty description="暂无符合条件的景点" style="grid-column: 1 / -1">
            <el-button type="primary" @click="handleResetFilters">重置筛选条件</el-button>
          </el-empty>
        </template>
        <template v-else>
          <ScenicCard
            v-for="(item, idx) in list"
            :key="item.id"
            :scenic="item"
          />
        </template>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrap" v-if="total > queryParams.pageSize">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          layout="prev, pager, next, jumper"
          background
          @current-change="loadList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScenicList, getCategories } from '@/api/scenic'
import ScenicCard from '@/components/ScenicCard.vue'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])

const sortOptions = [
  { label: '综合', value: '' },
  { label: '评分', value: 'rating' },
  { label: '人气', value: 'visit' },
  { label: '价格', value: 'price' },
  { label: '最新', value: 'latest' }
]

const ratingOptions = [
  { label: '不限', value: '' },
  { label: '4星以上', value: 4 },
  { label: '3星以上', value: 3 },
  { label: '2星以上', value: 2 }
]

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  categoryId: route.query.categoryId || '',
  sortBy: '',
  minRating: ''
})

function selectCategory(id) {
  queryParams.categoryId = id || ''
  queryParams.pageNum = 1
  loadList()
}

function selectSort(value) {
  queryParams.sortBy = value
  queryParams.pageNum = 1
  loadList()
}

function selectRating(value) {
  queryParams.minRating = value
  queryParams.pageNum = 1
  loadList()
}

function handleResetFilters() {
  queryParams.categoryId = ''
  queryParams.sortBy = ''
  queryParams.minRating = ''
  queryParams.pageNum = 1
  loadList()
}

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (!params.categoryId) delete params.categoryId
    if (!params.sortBy) delete params.sortBy
    if (!params.minRating) delete params.minRating
    const res = await getScenicList(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

watch(() => route.query.categoryId, (val) => {
  if (val) {
    queryParams.categoryId = val
    queryParams.pageNum = 1
    loadList()
  }
})

onMounted(() => {
  loadCategories()
  loadList()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #1a6b4f 0%, #2d7d9a 100%);
  padding: 48px 0 40px;
  text-align: center;

  .page-title {
    font-family: var(--font-serif);
    font-size: 32px;
    color: #fff;
    margin-bottom: 8px;
  }

  .page-desc {
    font-size: 15px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.page-body {
  padding: 32px 24px 64px;
}

.filter-bar {
  margin-bottom: 32px;
}

.filter-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.cat-tag {
  padding: 6px 18px;
  font-size: 14px;
  color: var(--neutral-600);
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &.active {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: #fff;
  }
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-sort,
.filter-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.sort-label {
  font-size: 14px;
  color: var(--neutral-500);
}

.sort-tag {
  padding: 4px 14px;
  font-size: 13px;
  color: var(--neutral-600);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    color: var(--color-primary);
    background: var(--color-primary-bg);
  }

  &.active {
    color: var(--color-primary);
    font-weight: 600;
    background: var(--color-primary-bg);
  }
}

.scenic-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  min-height: 400px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 48px;
}

@media (max-width: 1024px) {
  .scenic-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .scenic-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .page-banner {
    padding: 32px 0;

    .page-title {
      font-size: 24px;
    }
  }
}

@media (max-width: 480px) {
  .scenic-grid {
    grid-template-columns: 1fr;
  }
}
</style>
