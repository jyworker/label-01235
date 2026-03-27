<template>
  <div class="search-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">搜索结果</h1>
        <p class="page-desc animate-in animate-delay-1" v-if="keyword">
          关于 "<strong>{{ keyword }}</strong>" 的搜索结果
        </p>
      </div>
    </div>

    <div class="container page-body">
      <!-- Advanced Filters -->
      <div class="filter-panel animate-in">
        <div class="filter-row">
          <span class="filter-label">分类：</span>
          <div class="filter-tags">
            <span
              class="tag-item"
              :class="{ active: !filterParams.categoryId }"
              @click="filterParams.categoryId = ''; doSearch()"
            >全部</span>
            <span
              v-for="cat in categories"
              :key="cat.id"
              class="tag-item"
              :class="{ active: filterParams.categoryId == cat.id }"
              @click="filterParams.categoryId = cat.id; doSearch()"
            >{{ cat.name }}</span>
          </div>
        </div>
        <div class="filter-row">
          <span class="filter-label">价格：</span>
          <div class="price-range">
            <el-input-number
              v-model="filterParams.minPrice"
              :min="0"
              :step="10"
              placeholder="最低价"
              size="small"
              controls-position="right"
              @change="doSearch"
            />
            <span class="range-sep">—</span>
            <el-input-number
              v-model="filterParams.maxPrice"
              :min="0"
              :step="10"
              placeholder="最高价"
              size="small"
              controls-position="right"
              @change="doSearch"
            />
          </div>
        </div>
      </div>

      <!-- Results -->
      <div class="result-header">
        <span class="result-count">共找到 <strong>{{ total }}</strong> 个景点</span>
      </div>

      <div class="scenic-grid" v-loading="loading">
        <ScenicCard
          v-for="(item, idx) in list"
          :key="item.id"
          :scenic="item"
          class="animate-in"
          :class="'animate-delay-' + Math.min(idx + 1, 8)"
        />
        <el-empty v-if="!loading && list.length === 0" description="未找到相关景点" style="grid-column: 1 / -1">
          <template #description>
            <span>换个关键词试试</span>
          </template>
          <el-button type="primary" @click="$router.push('/scenic')">浏览全部景点</el-button>
        </el-empty>
      </div>

      <div class="pagination-wrap" v-if="total > queryParams.pageSize">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          layout="prev, pager, next, jumper"
          background
          @current-change="doSearch"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getScenicList, getCategories } from '@/api/scenic'
import ScenicCard from '@/components/ScenicCard.vue'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const keyword = ref('')

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12
})

const filterParams = reactive({
  categoryId: '',
  minPrice: undefined,
  maxPrice: undefined
})

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function doSearch() {
  loading.value = true
  try {
    const params = {
      keyword: keyword.value,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }
    if (filterParams.categoryId) params.categoryId = filterParams.categoryId
    if (filterParams.minPrice !== undefined && filterParams.minPrice !== null) params.minPrice = filterParams.minPrice
    if (filterParams.maxPrice !== undefined && filterParams.maxPrice !== null) params.maxPrice = filterParams.maxPrice

    const res = await getScenicList(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  queryParams.pageNum = 1
  doSearch()
})

onMounted(() => {
  keyword.value = route.query.keyword || ''
  loadCategories()
  doSearch()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #2d7d9a 0%, #1a6b4f 100%);
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
    color: rgba(255, 255, 255, 0.85);

    strong {
      color: #fff;
      font-weight: 600;
    }
  }
}

.page-body {
  padding: 32px 24px 64px;
}

.filter-panel {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-md);
  padding: 20px 24px;
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;

  &:not(:last-child) {
    margin-bottom: 12px;
  }
}

.filter-label {
  font-size: 14px;
  color: var(--neutral-500);
  font-weight: 500;
  flex-shrink: 0;
}

.filter-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  padding: 5px 14px;
  font-size: 13px;
  color: var(--neutral-600);
  background: var(--neutral-100);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    color: var(--color-primary);
    background: var(--color-primary-bg);
  }

  &.active {
    color: #fff;
    background: var(--color-primary);
  }
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;

  .range-sep {
    color: var(--neutral-400);
  }
}

.result-header {
  margin-bottom: 20px;

  .result-count {
    font-size: 14px;
    color: var(--neutral-500);

    strong {
      color: var(--color-primary);
    }
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

  .page-banner .page-title {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .scenic-grid {
    grid-template-columns: 1fr;
  }
}
</style>
