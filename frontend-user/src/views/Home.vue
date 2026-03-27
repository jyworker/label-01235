<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-overlay"></div>
        <div class="hero-pattern"></div>
      </div>
      <div class="hero-content container">
        <h1 class="hero-title animate-in">发现世界之美</h1>
        <p class="hero-subtitle animate-in animate-delay-1">探索令人心旷神怡的自然风光与人文景观</p>
        <div class="hero-search animate-in animate-delay-2">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索你想去的景点..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon :size="20"><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="hero-stats animate-in animate-delay-3">
          <div class="stat-item">
            <span class="stat-num">500+</span>
            <span class="stat-label">精选景点</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">10万+</span>
            <span class="stat-label">真实评价</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">99%</span>
            <span class="stat-label">满意度</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="categories-section">
      <div class="container">
        <div class="section-header animate-in">
          <h2 class="section-title">探索分类</h2>
          <p class="section-subtitle">选择您感兴趣的旅行主题</p>
        </div>
        <div class="category-grid" v-loading="categoriesLoading">
          <div
            v-for="(cat, idx) in categories"
            :key="cat.id"
            class="category-card animate-in"
            :class="'animate-delay-' + (idx + 1)"
            @click="goCategory(cat.id)"
          >
            <div class="cat-icon">
              <el-icon :size="28"><component :is="getCategoryIcon(cat.name)" /></el-icon>
            </div>
            <span class="cat-name">{{ cat.name }}</span>
          </div>
          <div v-if="!categoriesLoading && categories.length === 0" class="empty-hint">
            暂无分类数据
          </div>
        </div>
      </div>
    </section>

    <!-- Recommend Section -->
    <section class="recommend-section" v-if="recommendList.length > 0">
      <div class="container">
        <div class="section-header animate-in">
          <h2 class="section-title">为你推荐</h2>
          <p class="section-subtitle">根据您的偏好精选的景点</p>
        </div>
        <div class="scenic-grid" v-loading="recommendLoading">
          <ScenicCard
            v-for="(item, idx) in recommendList"
            :key="item.id"
            :scenic="item"
            class="animate-in"
            :class="'animate-delay-' + (idx + 1)"
          />
        </div>
      </div>
    </section>

    <!-- Hot Scenic -->
    <section class="hot-section">
      <div class="container">
        <div class="section-header animate-in">
          <h2 class="section-title">热门推荐</h2>
          <p class="section-subtitle">最受旅行者喜爱的景点</p>
        </div>
        <!-- Skeleton Loading -->
        <template v-if="hotLoading">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="n in 4" :key="n">
              <el-card shadow="hover" style="margin-bottom: 20px;">
                <el-skeleton :rows="4" animated>
                  <template #template>
                    <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
                    <div style="padding: 14px;">
                      <el-skeleton-item variant="h3" style="width: 60%;" />
                      <el-skeleton-item variant="text" style="width: 100%; margin-top: 12px;" />
                      <el-skeleton-item variant="text" style="width: 40%; margin-top: 8px;" />
                    </div>
                  </template>
                </el-skeleton>
              </el-card>
            </el-col>
          </el-row>
        </template>

        <!-- Actual Content -->
        <template v-else>
          <div class="scenic-grid">
            <ScenicCard
              v-for="(item, idx) in hotList"
              :key="item.id"
              :scenic="item"
              class="animate-in"
              :class="'animate-delay-' + (idx + 1)"
            />
            <el-empty v-if="hotList.length === 0" description="暂无热门景点" style="grid-column: 1 / -1">
              <template #description>
                <span>精彩景点即将上线，敬请期待</span>
              </template>
            </el-empty>
          </div>
        </template>
        <div class="section-more animate-in" v-if="hotList.length > 0">
          <router-link to="/scenic" class="more-btn">
            查看全部景点
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotScenic, getCategories, getRecommended } from '@/api/scenic'
import ScenicCard from '@/components/ScenicCard.vue'
import { Search, ArrowRight, Sunrise, Sunny, Ship, House, OfficeBuilding, Trophy, MapLocation } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const categories = ref([])
const hotList = ref([])
const recommendList = ref([])
const categoriesLoading = ref(false)
const hotLoading = ref(false)
const recommendLoading = ref(false)

const categoryIcons = {
  '自然风光': 'Sunny',
  '历史古迹': 'OfficeBuilding',
  '主题乐园': 'Trophy',
  '海岛沙滩': 'Ship',
  '山岳': 'Sunrise',
  '古镇': 'House',
  '城市观光': 'MapLocation',
  default: 'MapLocation'
}

function getCategoryIcon(name) {
  for (const [key, icon] of Object.entries(categoryIcons)) {
    if (name && name.includes(key)) return icon
  }
  return categoryIcons.default
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value.trim() } })
  }
}

function goCategory(id) {
  router.push({ name: 'ScenicList', query: { categoryId: id } })
}

async function loadCategories() {
  categoriesLoading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    categoriesLoading.value = false
  }
}

async function loadHotScenic() {
  hotLoading.value = true
  try {
    const res = await getHotScenic()
    hotList.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    hotLoading.value = false
  }
}

async function loadRecommend() {
  recommendLoading.value = true
  try {
    const res = await getRecommended()
    if (res.code === 200) {
      recommendList.value = res.data || []
    }
  } catch (e) {
    // 静默处理，推荐不影响页面主体
  } finally {
    recommendLoading.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadHotScenic()
  loadRecommend()
})
</script>

<style lang="scss" scoped>
/* ===== Hero ===== */
.hero {
  position: relative;
  min-height: 520px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0d3b2e 0%, #1a5c45 30%, #2d7d9a 70%, #1a6b4f 100%);

  .hero-overlay {
    position: absolute;
    inset: 0;
    background:
      radial-gradient(ellipse at 20% 50%, rgba(232, 133, 61, 0.15) 0%, transparent 50%),
      radial-gradient(ellipse at 80% 20%, rgba(45, 125, 154, 0.2) 0%, transparent 50%);
  }

  .hero-pattern {
    position: absolute;
    inset: 0;
    opacity: 0.06;
    background-image:
      radial-gradient(circle at 25% 25%, #fff 1px, transparent 1px),
      radial-gradient(circle at 75% 75%, #fff 1px, transparent 1px);
    background-size: 50px 50px;
  }
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 80px 24px 60px;
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 16px;
  letter-spacing: 2px;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 40px;
  font-weight: 400;
}

.hero-search {
  max-width: 600px;
  margin: 0 auto 40px;

  :deep(.el-input__wrapper) {
    border-radius: 28px 0 0 28px !important;
    padding: 6px 6px 6px 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
    border: none !important;

    &:hover,
    &.is-focus {
      box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2) !important;
    }
  }

  :deep(.el-input-group__append) {
    background: var(--color-primary);
    border: none;
    border-radius: 0 28px 28px 0 !important;
    padding: 0 24px;
    box-shadow: none;

    .el-button {
      color: #fff;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  :deep(.el-input__inner) {
    font-size: 16px;
    height: 48px;
  }
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
}

.stat-item {
  text-align: center;

  .stat-num {
    display: block;
    font-size: 28px;
    font-weight: 700;
    color: #fff;
    font-family: var(--font-serif);
  }

  .stat-label {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    margin-top: 4px;
  }
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

/* ===== Categories ===== */
.categories-section {
  padding: 64px 0 48px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 12px;
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
    border-color: var(--color-primary);

    .cat-icon {
      background: var(--color-primary);
      color: #fff;
    }
  }

  .cat-icon {
    width: 56px;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-primary-bg);
    color: var(--color-primary);
    border-radius: var(--radius-md);
    transition: all 0.25s ease;
  }

  .cat-name {
    font-size: 14px;
    font-weight: 500;
    color: var(--neutral-700);
  }
}

.empty-hint {
  grid-column: 1 / -1;
  text-align: center;
  padding: 32px;
  color: var(--neutral-400);
  font-size: 14px;
}

/* ===== Recommend ===== */
.recommend-section {
  padding: 48px 0;
  background: #fff;
}

/* ===== Hot Scenic ===== */
.hot-section {
  padding: 48px 0 80px;
  background: var(--neutral-100);
}

.scenic-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.section-more {
  text-align: center;
  margin-top: 40px;
}

.more-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 32px;
  color: var(--color-primary);
  font-weight: 600;
  font-size: 15px;
  border: 2px solid var(--color-primary);
  border-radius: var(--radius-full);
  text-decoration: none;
  transition: all 0.25s ease;

  &:hover {
    background: var(--color-primary);
    color: #fff;
  }
}

/* ===== Responsive ===== */
@media (max-width: 1024px) {
  .scenic-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .hero-stats {
    gap: 20px;
  }

  .stat-item .stat-num {
    font-size: 22px;
  }

  .scenic-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .scenic-grid {
    grid-template-columns: 1fr;
  }

  .hero {
    min-height: 420px;
  }

  .hero-content {
    padding: 60px 16px 40px;
  }
}
</style>
