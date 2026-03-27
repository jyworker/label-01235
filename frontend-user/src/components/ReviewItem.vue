<template>
  <div class="review-item">
    <div class="review-header">
      <el-avatar :size="40" :src="review.avatar || ''">
        <el-icon :size="20"><User /></el-icon>
      </el-avatar>
      <div class="review-meta">
        <div class="review-author">{{ review.nickname || '匿名用户' }}</div>
        <div class="review-info">
          <el-rate
            :model-value="review.rating"
            disabled
            :colors="['#E8853D', '#E8853D', '#E8853D']"
          />
          <span class="review-time">{{ formatTime(review.createTime) }}</span>
        </div>
      </div>
      <div class="review-extra">
        <el-tag
          v-if="showStatus"
          :type="getStatusType(review.status)"
          size="small"
          class="status-tag"
        >
          {{ getStatusText(review.status) }}
        </el-tag>
        <el-button v-if="showDelete" type="danger" text size="small" @click="$emit('delete', review.id)">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </div>
    </div>

    <div class="review-scenic" v-if="showScenic && review.scenicSpotName">
      <el-icon :size="14"><Location /></el-icon>
      <span>{{ review.scenicSpotName }}</span>
    </div>

    <div class="review-content">{{ review.content }}</div>

    <div class="review-images" v-if="review.images && review.images.length">
      <el-image
        v-for="(img, idx) in review.images"
        :key="idx"
        :src="img"
        :preview-src-list="review.images"
        :initial-index="idx"
        fit="cover"
        class="review-img"
        lazy
      />
    </div>
  </div>
</template>

<script setup>
import { User, Delete, Location } from '@element-plus/icons-vue'

defineProps({
  review: {
    type: Object,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: false
  },
  showScenic: {
    type: Boolean,
    default: false
  },
  showStatus: {
    type: Boolean,
    default: false
  }
})

defineEmits(['delete'])

function getStatusType(status) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return time.split(' ')[0]
}
</script>

<style lang="scss" scoped>
.review-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--neutral-200);

  &:last-child {
    border-bottom: none;
  }
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  .el-avatar {
    background: var(--color-primary-bg);
    color: var(--color-primary);
    flex-shrink: 0;
  }
}

.review-extra {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;

  .status-tag {
    white-space: nowrap;
  }
}

.review-meta {
  flex: 1;
}

.review-author {
  font-size: 15px;
  font-weight: 600;
  color: var(--neutral-800);
  margin-bottom: 4px;
}

.review-info {
  display: flex;
  align-items: center;
  gap: 12px;

  :deep(.el-rate) {
    height: auto;

    .el-rate__icon {
      font-size: 14px !important;
      margin-right: 1px;
    }
  }

  .review-time {
    font-size: 13px;
    color: var(--neutral-400);
  }
}

.review-scenic {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-secondary);
  margin-bottom: 8px;
  padding: 4px 10px;
  background: rgba(45, 125, 154, 0.06);
  border-radius: var(--radius-sm);
  display: inline-flex;
}

.review-content {
  font-size: 14px;
  line-height: 1.7;
  color: var(--neutral-700);
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.review-img {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
}
</style>
