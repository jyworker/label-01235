<template>
  <div class="scenic-card" @click="goDetail">
    <div class="card-cover">
      <img
        v-if="coverImageUrl"
        :src="coverImageUrl"
        :alt="scenic.name"
        loading="lazy"
        @error="onImgError"
      />
      <div v-else class="cover-placeholder">
        <el-icon :size="40"><Picture /></el-icon>
      </div>
      <div class="card-badges">
        <span v-if="scenic.isHot" class="badge hot">热门</span>
        <span v-if="scenic.ticketPrice === 0 || scenic.ticketPrice === '0.00'" class="badge free">免费</span>
      </div>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ scenic.name }}</h3>
      <div class="card-location">
        <el-icon :size="14"><Location /></el-icon>
        <span>{{ scenic.province }}{{ scenic.city ? ' · ' + scenic.city : '' }}</span>
      </div>
      <div class="card-footer">
        <div class="card-rating" v-if="scenic.avgRating">
          <el-rate
            :model-value="scenic.avgRating"
            disabled
            show-score
            text-color="var(--color-accent)"
            score-template="{value}"
            :colors="['#E8853D', '#E8853D', '#E8853D']"
            :size="'small'"
          />
          <span class="review-count">{{ scenic.reviewCount || 0 }}条评价</span>
        </div>
        <div class="card-price">
          <template v-if="scenic.ticketPrice > 0">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ scenic.ticketPrice }}</span>
            <span class="price-unit">起</span>
          </template>
          <span v-else class="price-free">免费</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Picture } from '@element-plus/icons-vue'
import { toMediaUrl } from '@/utils/media'

const props = defineProps({
  scenic: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const coverImageUrl = computed(() => toMediaUrl(props.scenic.coverImage || props.scenic.scenicSpotCover))

function goDetail() {
  router.push({ name: 'ScenicDetail', params: { id: props.scenic.id } })
}

function onImgError(e) {
  e.target.style.display = 'none'
  e.target.parentElement.classList.add('img-error')
}
</script>

<style lang="scss" scoped>
.scenic-card {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);

    .card-cover img {
      transform: scale(1.05);
    }
  }
}

.card-cover {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: var(--neutral-100);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  &.img-error {
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--neutral-300);
  }
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--neutral-100), var(--neutral-200));
  color: var(--neutral-400);
}

.card-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 6px;
}

.badge {
  padding: 3px 10px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(8px);

  &.hot {
    background: rgba(232, 133, 61, 0.9);
    color: #fff;
  }

  &.free {
    background: rgba(46, 139, 87, 0.9);
    color: #fff;
  }
}

.card-body {
  padding: 16px;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--neutral-800);
  margin-bottom: 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--neutral-500);
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 1;
  overflow: hidden;

  :deep(.el-rate) {
    height: auto;

    .el-rate__icon {
      font-size: 14px !important;
      margin-right: 2px;
    }

    .el-rate__text {
      font-size: 13px;
      font-weight: 600;
    }
  }

  .review-count {
    font-size: 12px;
    color: var(--neutral-400);
    white-space: nowrap;
  }
}

.card-price {
  white-space: nowrap;

  .price-symbol {
    font-size: 13px;
    color: var(--color-accent);
    font-weight: 600;
  }

  .price-value {
    font-size: 20px;
    color: var(--color-accent);
    font-weight: 700;
    line-height: 1;
  }

  .price-unit {
    font-size: 12px;
    color: var(--neutral-500);
    margin-left: 2px;
  }

  .price-free {
    font-size: 15px;
    color: var(--color-success);
    font-weight: 600;
  }
}
</style>
