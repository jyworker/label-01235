<template>
  <div class="star-rating" :class="{ 'is-vertical': vertical }">
    <div class="star-rating-row">
      <el-rate
        :model-value="score"
        disabled
        :colors="['#E8853D', '#E8853D', '#E8853D']"
        :size="size"
      />
      <span v-if="showScore" class="score-text" :style="{ fontSize: scoreSize }">
        {{ score ? score.toFixed(1) : '暂无' }}
      </span>
    </div>
    <span v-if="showCount" class="count-text">
      （{{ count }} 条评价）
    </span>
  </div>
</template>

<script setup>
defineProps({
  score: {
    type: Number,
    default: 0
  },
  count: {
    type: Number,
    default: 0
  },
  size: {
    type: String,
    default: 'default'
  },
  scoreSize: {
    type: String,
    default: '14px'
  },
  showScore: {
    type: Boolean,
    default: true
  },
  showCount: {
    type: Boolean,
    default: false
  },
  vertical: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  &.is-vertical {
    flex-direction: column;
    align-items: center;
    gap: 6px;

    .count-text {
      margin-left: 0;
    }
  }

  .star-rating-row {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  :deep(.el-rate) {
    height: auto;

    .el-rate__icon {
      margin-right: 2px;
    }
  }

  .score-text {
    font-weight: 700;
    color: var(--color-accent);
  }

  .count-text {
    font-size: 13px;
    color: var(--neutral-400);
    margin-left: 2px;
  }
}
</style>
