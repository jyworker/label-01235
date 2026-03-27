<template>
  <div class="favorites-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">我的收藏</h1>
        <p class="page-desc animate-in animate-delay-1">您收藏的心仪景点</p>
      </div>
    </div>

    <div class="container page-body">
      <div class="scenic-grid" v-loading="loading">
        <div
          v-for="(item, idx) in list"
          :key="item.favoriteId || item.id"
          class="fav-card animate-in"
          :class="'animate-delay-' + Math.min(idx + 1, 8)"
        >
          <ScenicCard :scenic="item" />
          <button class="unfav-btn" @click.stop="handleUnfavorite(item)">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <el-empty v-if="!loading && list.length === 0" description="还没有收藏景点哦" style="grid-column: 1 / -1">
          <el-button type="primary" @click="$router.push('/scenic')">去发现精彩景点</el-button>
        </el-empty>
      </div>

      <div class="pagination-wrap" v-if="total > params.pageSize">
        <el-pagination
          v-model:current-page="params.pageNum"
          :page-size="params.pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadFavorites"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyFavorites, toggleFavorite } from '@/api/favorite'
import ScenicCard from '@/components/ScenicCard.vue'
import { Star, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const params = reactive({
  pageNum: 1,
  pageSize: 12
})

async function loadFavorites() {
  loading.value = true
  try {
    const res = await getMyFavorites(params)
    const records = res.data?.records || res.data || []
    // 统一为 ScenicCard 所需结构：id 为景点ID，name 为景点名称
    list.value = records.map((item) => ({
      ...item,
      favoriteId: item.id,
      id: item.scenicSpotId || item.id,
      name: item.name || item.scenicSpotName || '未命名景点'
    }))
    total.value = res.data?.total || records.length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleUnfavorite(item) {
  try {
    const scenicName = item.name || item.scenicSpotName || '该景点'
    await ElMessageBox.confirm(`确定取消收藏「${scenicName}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await toggleFavorite(item.scenicSpotId || item.id)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #e8853d 0%, #1a6b4f 100%);
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

.scenic-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  min-height: 320px;
}

.fav-card {
  position: relative;

  .unfav-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    z-index: 2;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.5);
    border: none;
    color: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transform: scale(0.8);
    transition: all 0.2s ease;

    &:hover {
      background: var(--color-error);
    }
  }

  &:hover .unfav-btn {
    opacity: 1;
    transform: scale(1);
  }
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
