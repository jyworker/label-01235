<template>
  <div class="order-detail-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">订单详情</h1>
      </div>
    </div>

    <div class="container page-body" v-loading="loading">
      <template v-if="order">
        <!-- Status Banner -->
        <div class="status-banner animate-in" :class="'status-' + order.status">
          <div class="status-icon">
            <el-icon :size="32">
              <component :is="statusIcon" />
            </el-icon>
          </div>
          <div class="status-info">
            <h2 class="status-text">{{ getStatusText(order.status) }}</h2>
            <p class="status-hint">{{ getStatusHint(order.status) }}</p>
          </div>
          <div class="status-actions">
            <el-button
              v-if="order.status === 0"
              type="primary"
              round
              @click="handlePay"
            >立即支付</el-button>
            <el-button
              v-if="order.status === 0"
              round
              @click="handleCancel"
            >取消订单</el-button>
          </div>
        </div>

        <!-- Order Info -->
        <div class="info-card animate-in animate-delay-1">
          <h3 class="card-title">订单信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单编号</span>
              <span class="info-value mono">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ order.createTime }}</span>
            </div>
            <div class="info-item" v-if="order.payTime">
              <span class="info-label">支付时间</span>
              <span class="info-value">{{ order.payTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">订单状态</span>
              <span class="info-value">
                <el-tag :type="getStatusType(order.status)" size="small">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>

        <!-- Scenic Info -->
        <div class="info-card animate-in animate-delay-2">
          <h3 class="card-title">景点信息</h3>
          <div class="scenic-row">
            <img
              v-if="orderCoverImage"
              :src="orderCoverImage"
              class="scenic-img"
              @error="(e) => e.target.style.display = 'none'"
            />
            <div class="scenic-detail">
              <h4 class="scenic-name" @click="$router.push(`/scenic/${order.scenicSpotId}`)">
                {{ order.scenicSpotName || '景点' }}
              </h4>
              <div class="scenic-meta">
                <span>游览日期：{{ order.visitDate }}</span>
                <span>数量：{{ order.quantity }} 张</span>
              </div>
            </div>
            <div class="scenic-price">
              <span class="price-value">¥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- Contact Info -->
        <div class="info-card animate-in animate-delay-3">
          <h3 class="card-title">联系信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">联系人</span>
              <span class="info-value">{{ order.contactName || '--' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ order.contactPhone || '--' }}</span>
            </div>
          </div>
        </div>

        <!-- Back -->
        <div class="back-link animate-in">
          <el-button text type="primary" @click="$router.push('/orders')">
            <el-icon><ArrowLeft /></el-icon>返回订单列表
          </el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, payOrder, cancelOrder } from '@/api/order'
import { toMediaUrl } from '@/utils/media'
import { ArrowLeft, Clock, CircleCheck, Warning, CircleClose, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const order = ref(null)

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '已使用',
  3: '已取消',
  4: '已退款'
}

const statusTypeMap = {
  0: 'warning',
  1: 'success',
  2: 'info',
  3: 'danger',
  4: 'info'
}

const statusHints = {
  0: '请尽快完成支付，以确认您的预订',
  1: '支付成功，请在游览日期前往景点',
  2: '感谢您的游览，欢迎再次光临',
  3: '订单已取消',
  4: '退款已处理'
}

const statusIcons = {
  0: 'Clock',
  1: 'CircleCheck',
  2: 'CircleCheck',
  3: 'CircleClose',
  4: 'Refresh'
}

const statusIcon = computed(() => {
  return statusIcons[order.value?.status] || 'Clock'
})
const orderCoverImage = computed(() => toMediaUrl(order.value?.coverImage || order.value?.scenicSpotCover))

function getStatusText(status) {
  return statusMap[status] || '未知'
}

function getStatusType(status) {
  return statusTypeMap[status] || 'info'
}

function getStatusHint(status) {
  return statusHints[status] || ''
}

async function loadOrder() {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch (e) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${order.value.totalAmount}？`, '确认支付', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消'
    })
    await payOrder(order.value.id)
    ElMessage.success('支付成功')
    loadOrder()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '暂不取消',
      type: 'warning'
    })
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    loadOrder()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #1a6b4f 0%, #2d7d9a 100%);
  padding: 40px 0 32px;
  text-align: center;

  .page-title {
    font-family: var(--font-serif);
    font-size: 28px;
    color: #fff;
  }
}

.page-body {
  padding: 32px 24px 64px;
  max-width: 800px;
  margin: 0 auto;
}

.status-banner {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: var(--radius-lg);
  margin-bottom: 24px;
  flex-wrap: wrap;

  &.status-0 {
    background: linear-gradient(135deg, rgba(212, 168, 67, 0.1), rgba(232, 133, 61, 0.08));
    .status-icon { color: var(--color-warning); }
  }
  &.status-1 {
    background: linear-gradient(135deg, rgba(46, 139, 87, 0.1), rgba(26, 107, 79, 0.08));
    .status-icon { color: var(--color-success); }
  }
  &.status-2 {
    background: linear-gradient(135deg, rgba(91, 141, 184, 0.1), rgba(45, 125, 154, 0.08));
    .status-icon { color: var(--color-info); }
  }
  &.status-3 {
    background: linear-gradient(135deg, rgba(199, 84, 80, 0.1), rgba(199, 84, 80, 0.05));
    .status-icon { color: var(--color-error); }
  }
  &.status-4 {
    background: linear-gradient(135deg, rgba(91, 141, 184, 0.1), rgba(91, 141, 184, 0.05));
    .status-icon { color: var(--color-info); }
  }
}

.status-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  flex-shrink: 0;
}

.status-info {
  flex: 1;

  .status-text {
    font-family: var(--font-serif);
    font-size: 22px;
    color: var(--neutral-800);
    margin-bottom: 4px;
  }

  .status-hint {
    font-size: 14px;
    color: var(--neutral-500);
  }
}

.status-actions {
  display: flex;
  gap: 8px;
}

.info-card {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 16px;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--neutral-800);
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--neutral-200);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  .info-label {
    font-size: 13px;
    color: var(--neutral-400);
    margin-bottom: 4px;
    display: block;
  }

  .info-value {
    font-size: 15px;
    color: var(--neutral-800);
    font-weight: 500;

    &.mono {
      font-family: monospace;
    }
  }
}

.scenic-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.scenic-img {
  width: 100px;
  height: 72px;
  object-fit: cover;
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.scenic-detail {
  flex: 1;

  .scenic-name {
    font-family: var(--font-serif);
    font-size: 17px;
    color: var(--color-primary);
    cursor: pointer;
    margin-bottom: 6px;

    &:hover {
      text-decoration: underline;
    }
  }

  .scenic-meta {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: var(--neutral-500);
  }
}

.scenic-price .price-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-accent);
}

.back-link {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .scenic-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .status-banner {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
