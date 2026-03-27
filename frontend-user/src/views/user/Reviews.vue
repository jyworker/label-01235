<template>
  <div class="reviews-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">我的评论</h1>
        <p class="page-desc animate-in animate-delay-1">您发表过的旅行评价</p>
      </div>
    </div>

    <div class="container page-body">
      <div class="review-list" v-loading="loading">
        <ReviewItem
          v-for="(review, idx) in list"
          :key="review.id"
          :review="review"
          :show-delete="true"
          :show-scenic="true"
          :show-status="true"
          class="animate-in"
          :class="'animate-delay-' + Math.min(idx + 1, 6)"
          @delete="handleDelete"
        />

        <el-empty v-if="!loading && list.length === 0" description="还没有发表评论">
          <el-button type="primary" @click="$router.push('/scenic')">去探索景点并评论</el-button>
        </el-empty>
      </div>

      <div class="pagination-wrap" v-if="total > params.pageSize">
        <el-pagination
          v-model:current-page="params.pageNum"
          :page-size="params.pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadReviews"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyReviews, deleteReview } from '@/api/review'
import ReviewItem from '@/components/ReviewItem.vue'
import { ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const params = reactive({
  pageNum: 1,
  pageSize: 10
})

async function loadReviews() {
  loading.value = true
  try {
    const res = await getMyReviews(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReview(id)
    ElMessage.success('删除成功')
    loadReviews()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadReviews()
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
    color: rgba(255, 255, 255, 0.8);
  }
}

.page-body {
  padding: 32px 24px 64px;
  max-width: 800px;
  margin: 0 auto;
}

.review-list {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  padding: 4px 24px;
  min-height: 320px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .page-banner .page-title {
    font-size: 24px;
  }

  .review-list {
    padding: 4px 16px;
  }
}
</style>
