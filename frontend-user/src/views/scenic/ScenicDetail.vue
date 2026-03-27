<template>
  <div class="scenic-detail-page">
    <!-- Skeleton Loading -->
    <template v-if="loading">
      <div class="detail-skeleton">
        <el-skeleton animated>
          <template #template>
            <el-skeleton-item variant="image" style="width: 100%; height: 360px;" />
            <div class="container" style="padding: 40px 24px;">
              <div style="max-width: 900px; margin: 0 auto;">
                <el-skeleton-item variant="h1" style="width: 40%; height: 36px;" />
                <el-skeleton-item variant="text" style="width: 30%; margin-top: 12px;" />
                <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 28px;">
                  <el-skeleton-item variant="rect" style="height: 80px; border-radius: 8px;" v-for="n in 4" :key="n" />
                </div>
                <el-skeleton-item variant="h3" style="width: 20%; margin-top: 40px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-top: 16px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-top: 8px;" />
                <el-skeleton-item variant="text" style="width: 80%; margin-top: 8px;" />
                <el-skeleton-item variant="text" style="width: 60%; margin-top: 8px;" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </template>

    <template v-else-if="scenic">
      <!-- Image Carousel -->
      <section class="detail-gallery" v-if="allImages.length > 1">
        <div class="scenic-gallery">
          <el-carousel :interval="5000" type="card" height="360px" indicator-position="outside">
            <el-carousel-item v-for="(img, index) in allImages" :key="img.id || img">
              <el-image
                :src="img"
                fit="cover"
                style="width: 100%; height: 100%; border-radius: 8px;"
                :preview-src-list="allImages"
                :initial-index="index"
              />
            </el-carousel-item>
          </el-carousel>
        </div>
      </section>
      <section class="detail-gallery" v-else-if="allImages.length === 1">
        <div class="scenic-cover">
          <el-image
            :src="allImages[0]"
            fit="cover"
            style="width: 100%; height: 400px; border-radius: 12px;"
          />
        </div>
      </section>
      <section class="detail-gallery" v-else>
        <div class="gallery-slide placeholder">
          <el-icon :size="60"><Picture /></el-icon>
          <span>暂无图片</span>
        </div>
      </section>

      <!-- Info Section -->
      <div class="container detail-body">
        <div class="detail-main">
          <!-- Title & Actions -->
          <div class="detail-header animate-in">
            <div class="title-area">
              <div class="title-row">
                <h1 class="scenic-name">{{ scenic.name }}</h1>
                <span v-if="scenic.isHot" class="hot-badge">
                  <el-icon><Sunny /></el-icon> 热门
                </span>
              </div>
              <div class="location-row">
                <el-icon><Location /></el-icon>
                <span>{{ scenic.address || (scenic.province + ' ' + scenic.city) }}</span>
              </div>
            </div>
            <div class="action-btns">
              <button
                class="fav-btn"
                :class="{ favorited: isFavorited }"
                @click="handleToggleFavorite"
              >
                <span class="heart-icon" :class="{ bounce: favAnimating }">
                  <el-icon :size="22">
                    <component :is="isFavorited ? 'StarFilled' : 'Star'" />
                  </el-icon>
                </span>
                <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
              </button>
              <el-button type="primary" size="large" round @click="goBooking">
                <el-icon><Ticket /></el-icon>
                立即预订
              </el-button>
            </div>
          </div>

          <!-- Quick Info -->
          <div class="quick-info animate-in animate-delay-1">
            <div class="info-card">
              <div class="info-label">门票</div>
              <div class="info-value price">
                <template v-if="scenic.ticketPrice > 0">
                  ¥{{ scenic.ticketPrice }}
                </template>
                <template v-else>免费</template>
              </div>
            </div>
            <div class="info-card">
              <div class="info-label">评分</div>
              <div class="info-value score-value">
                {{ scenic.avgRating && Number(scenic.avgRating) > 0 ? Number(scenic.avgRating).toFixed(1) : '暂无' }}
              </div>
            </div>
            <div class="info-card">
              <div class="info-label">开放时间</div>
              <div class="info-value">{{ scenic.openTime || '--' }} - {{ scenic.closeTime || '--' }}</div>
            </div>
            <div class="info-card">
              <div class="info-label">分类</div>
              <div class="info-value">{{ scenic.categoryName || '--' }}</div>
            </div>
          </div>

          <!-- Tips -->
          <div class="tips-box animate-in animate-delay-2" v-if="scenic.tips">
            <el-icon><InfoFilled /></el-icon>
            <span>{{ scenic.tips }}</span>
          </div>

          <!-- Description -->
          <div class="section-block animate-in animate-delay-2" v-if="scenic.description">
            <h2 class="block-title">景点简介</h2>
            <p class="desc-text">{{ scenic.description }}</p>
          </div>

          <!-- Detail Content (Rich Text) -->
          <div class="section-block animate-in animate-delay-3" v-if="scenic.detailContent">
            <h2 class="block-title">详细介绍</h2>
            <div class="rich-content" v-html="scenic.detailContent"></div>
          </div>

          <!-- 视频展示 -->
          <div v-if="scenic.videoUrl" class="video-section animate-in">
            <h3 class="section-title">景点视频</h3>
            <div class="video-wrapper">
              <video
                controls
                controlslist="nodownload"
                preload="metadata"
                playsinline
                :poster="coverImageUrl"
                class="scenic-video"
              >
                <source :src="fullVideoUrl" type="video/mp4">
                您的浏览器不支持视频播放
              </video>
            </div>
          </div>


          <!-- Reviews Section -->
          <div class="section-block animate-in">
            <div class="block-header">
              <h2 class="block-title">游客评价</h2>
              <span class="review-total">共 {{ reviewTotal }} 条评价</span>
            </div>

            <!-- Add Review -->
            <div class="add-review" v-if="userStore.isLoggedIn">
              <div class="review-form">
                <div class="form-row">
                  <span class="form-label">评分：</span>
                  <el-rate v-model="reviewForm.rating" :colors="['#E8853D', '#E8853D', '#E8853D']" />
                </div>
                <el-input
                  v-model="reviewForm.content"
                  type="textarea"
                  :rows="3"
                  placeholder="分享您的旅行体验..."
                  maxlength="500"
                  show-word-limit
                />
                <div class="form-actions">
                  <el-upload
                    v-model:file-list="reviewForm.imageFiles"
                    :auto-upload="false"
                    list-type="picture-card"
                    :limit="9"
                    accept="image/*"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                  <el-button type="primary" @click="submitReview" :loading="reviewSubmitting">
                    发表评论
                  </el-button>
                </div>
              </div>
            </div>
            <div class="login-hint" v-else>
              <router-link to="/login">登录</router-link> 后可以发表评论
            </div>

            <!-- Review List -->
            <div class="review-list" v-loading="reviewsLoading">
              <ReviewItem
                v-for="review in reviews"
                :key="review.id"
                :review="review"
              />
              <div v-if="!reviewsLoading && reviews.length === 0" class="empty-state">
                <div class="empty-state__icon">
                  <el-icon><ChatDotRound /></el-icon>
                </div>
                <div class="empty-state__title">暂无评价</div>
                <div class="empty-state__desc">成为第一个评价的人吧</div>
              </div>
            </div>

            <div class="pagination-wrap" v-if="reviewTotal > reviewParams.pageSize">
              <el-pagination
                v-model:current-page="reviewParams.pageNum"
                :page-size="reviewParams.pageSize"
                :total="reviewTotal"
                layout="prev, pager, next"
                background
                small
                @current-change="loadReviews"
              />
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getScenicDetail } from '@/api/scenic'
import { getReviewList, addReview, addReviewWithImages } from '@/api/review'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { toMediaUrl } from '@/utils/media'
import ReviewItem from '@/components/ReviewItem.vue'
import { Location, Picture, Sunny, Star, StarFilled, Ticket, InfoFilled, Plus, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const scenic = ref(null)
const isFavorited = ref(false)
const favAnimating = ref(false)

const reviews = ref([])
const reviewTotal = ref(0)
const reviewsLoading = ref(false)
const reviewSubmitting = ref(false)

const reviewParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const reviewForm = reactive({
  rating: 5,
  content: '',
  imageFiles: []
})

const allImages = computed(() => {
  if (!scenic.value) return []
  const imgs = []
  if (scenic.value.images && scenic.value.images.length) {
    imgs.push(...scenic.value.images)
  } else if (scenic.value.coverImage) {
    imgs.push(scenic.value.coverImage)
  }
  return imgs.map((img) => toMediaUrl(img)).filter(Boolean)
})
const coverImageUrl = computed(() => toMediaUrl(scenic.value?.coverImage))

// 视频地址转为同源绝对 URL，避免播放按钮不可用
const fullVideoUrl = computed(() => {
  return toMediaUrl(scenic.value?.videoUrl)
})


async function loadDetail() {
  loading.value = true
  try {
    const res = await getScenicDetail(route.params.id)
    scenic.value = res.data
  } catch (e) {
    ElMessage.error('加载景点详情失败')
  } finally {
    loading.value = false
  }
}

async function loadFavoriteStatus() {
  if (!userStore.isLoggedIn) {
    isFavorited.value = false
    return
  }
  try {
    const res = await checkFavorite(route.params.id)
    isFavorited.value = !!res.data?.favorited
  } catch (e) {
    isFavorited.value = false
  }
}

async function loadReviews() {
  reviewsLoading.value = true
  try {
    const res = await getReviewList({
      scenicSpotId: route.params.id,
      ...reviewParams
    })
    reviews.value = res.data?.records || []
    reviewTotal.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    reviewsLoading.value = false
  }
}

async function handleToggleFavorite() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  try {
    const res = await toggleFavorite(scenic.value.id)
    if (typeof res.data?.favorited === 'boolean') {
      isFavorited.value = res.data.favorited
    } else {
      isFavorited.value = !isFavorited.value
    }
    favAnimating.value = true
    setTimeout(() => { favAnimating.value = false }, 400)
    ElMessage.success(isFavorited.value ? '收藏成功' : '已取消收藏')
  } catch (e) {
    console.error(e)
  }
}

function goBooking() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: `/booking/${scenic.value.id}` } })
    return
  }
  router.push({ name: 'Booking', params: { id: scenic.value.id } })
}

async function submitReview() {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!reviewForm.rating) {
    ElMessage.warning('请选择评分')
    return
  }

  reviewSubmitting.value = true
  try {
    if (reviewForm.imageFiles.length) {
      const formData = new FormData()
      formData.append('scenicSpotId', route.params.id)
      formData.append('content', reviewForm.content)
      formData.append('rating', reviewForm.rating)
      reviewForm.imageFiles.forEach(file => {
        formData.append('images', file.raw)
      })
      await addReviewWithImages(formData)
    } else {
      await addReview({
        scenicSpotId: Number(route.params.id),
        content: reviewForm.content,
        rating: reviewForm.rating
      })
    }
    ElMessage.success('评论已提交，待审核后可见')
    reviewForm.content = ''
    reviewForm.rating = 5
    reviewForm.imageFiles = []
    reviewParams.pageNum = 1
    loadReviews()
  } catch (e) {
    console.error(e)
  } finally {
    reviewSubmitting.value = false
  }
}

onMounted(() => {
  loadDetail()
  loadFavoriteStatus()
  loadReviews()
})

watch(() => route.params.id, () => {
  loadDetail()
  loadFavoriteStatus()
  reviewParams.pageNum = 1
  loadReviews()
})

watch(() => userStore.isLoggedIn, () => {
  loadFavoriteStatus()
})
</script>

<style lang="scss" scoped>
.detail-gallery {
  background: var(--neutral-900);

  :deep(.el-carousel__indicators) {
    bottom: 16px;
  }

  :deep(.el-carousel__arrow) {
    background: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(4px);

    &:hover {
      background: rgba(0, 0, 0, 0.6);
    }
  }
}

.scenic-gallery {
  padding: 24px;
  background: var(--neutral-900);

  :deep(.el-carousel__item) {
    border-radius: 8px;
    overflow: hidden;
  }
}

.scenic-cover {
  padding: 24px;
  background: var(--neutral-900);
}

.gallery-slide {
  width: 100%;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &.placeholder {
    flex-direction: column;
    gap: 12px;
    color: var(--neutral-500);
    background: var(--neutral-200);
    font-size: 14px;
  }
}

.detail-body {
  padding: 40px 24px 80px;
}

.detail-main {
  max-width: 900px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.scenic-name {
  font-family: var(--font-serif);
  font-size: 30px;
  color: var(--neutral-900);
  font-weight: 700;
}

.hot-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: rgba(232, 133, 61, 0.1);
  color: var(--color-accent);
  font-size: 13px;
  font-weight: 600;
  border-radius: var(--radius-full);
}

.location-row {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--neutral-500);
}

.action-btns {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.fav-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #fff;
  border: 1px solid var(--neutral-300);
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: 14px;
  color: var(--neutral-600);
  transition: all 0.25s ease;

  &:hover {
    border-color: var(--color-accent);
    color: var(--color-accent);
  }

  &.favorited {
    border-color: var(--color-accent);
    color: var(--color-accent);
    background: rgba(232, 133, 61, 0.06);

    .heart-icon {
      color: var(--color-accent);
    }
  }

  .heart-icon {
    display: flex;
    transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

    &.bounce {
      transform: scale(1.3);
    }
  }
}

.quick-info {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.info-card {
  padding: 16px;
  background: var(--neutral-100);
  border-radius: var(--radius-md);
  text-align: center;

  .info-label {
    font-size: 13px;
    color: var(--neutral-500);
    margin-bottom: 6px;
  }

  .info-value {
    font-size: 15px;
    font-weight: 600;
    color: var(--neutral-800);

    &.price {
      color: var(--color-accent);
      font-size: 20px;
    }
  }

  .score-value {
    min-height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 700;
  }
}

.tips-box {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 14px 18px;
  background: rgba(91, 141, 184, 0.08);
  border-radius: var(--radius-md);
  margin-bottom: 32px;
  font-size: 14px;
  color: var(--color-info);
  line-height: 1.6;

  .el-icon {
    flex-shrink: 0;
    margin-top: 3px;
  }
}

.section-block {
  margin-bottom: 40px;
}

.block-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.block-title {
  font-family: var(--font-serif);
  font-size: 22px;
  color: var(--neutral-800);
  margin-bottom: 16px;
  position: relative;
  display: inline-block;

  &::after {
    content: '';
    position: absolute;
    bottom: -4px;
    left: 0;
    width: 32px;
    height: 2px;
    background: var(--color-primary);
    border-radius: 1px;
  }
}

.block-header .block-title {
  margin-bottom: 0;
}

.review-total {
  font-size: 14px;
  color: var(--neutral-500);
}

.desc-text {
  font-size: 15px;
  line-height: 1.8;
  color: var(--neutral-600);
}

.rich-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--neutral-700);

  :deep(img) {
    max-width: 100%;
    border-radius: var(--radius-md);
    margin: 12px 0;
  }

  :deep(p) {
    margin-bottom: 12px;
  }
}

/* Review form */
.add-review {
  margin-bottom: 32px;
  padding: 20px;
  background: var(--neutral-100);
  border-radius: var(--radius-md);
}

.review-form {
  .form-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .form-label {
      font-size: 14px;
      color: var(--neutral-600);
    }
  }

  .form-actions {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-top: 12px;
    gap: 12px;

    :deep(.el-upload--picture-card) {
      width: 64px;
      height: 64px;
    }

    :deep(.el-upload-list__item) {
      width: 64px;
      height: 64px;
    }
  }
}

.login-hint {
  padding: 16px;
  text-align: center;
  font-size: 14px;
  color: var(--neutral-500);
  background: var(--neutral-100);
  border-radius: var(--radius-md);
  margin-bottom: 24px;

  a {
    font-weight: 600;
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 视频展示 */
.video-section {
  margin-top: 24px;
  margin-bottom: 40px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.video-section .section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a2e;
}

.video-wrapper {
  border-radius: 8px;
  overflow: hidden;
  background: #000;
}

.scenic-video {
  width: 100%;
  max-height: 480px;
  display: block;
}


@media (max-width: 768px) {
  .quick-info {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-header {
    flex-direction: column;
  }

  .scenic-name {
    font-size: 24px;
  }

  .action-btns {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
