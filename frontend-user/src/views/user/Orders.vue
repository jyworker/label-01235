<template>
  <div class="orders-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">我的订单</h1>
        <p class="page-desc animate-in animate-delay-1">管理您的旅行预订</p>
      </div>
    </div>

    <div class="container page-body">
      <!-- Status Tabs -->
      <div class="status-tabs animate-in">
        <span
          v-for="tab in statusTabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: currentStatus === tab.value }"
          @click="switchStatus(tab.value)"
        >{{ tab.label }}</span>
      </div>

      <!-- Order List -->
      <div class="order-list" v-loading="loading">
        <div
          v-for="(order, idx) in list"
          :key="order.id"
          class="order-card animate-in"
          :class="'animate-delay-' + Math.min(idx + 1, 6)"
        >
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)" size="small" round>
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <div class="order-body" @click="goDetail(order.id)">
            <div class="order-scenic">
              <img
                v-if="orderCover(order)"
                :src="orderCover(order)"
                class="scenic-thumb"
                @error="(e) => e.target.style.display = 'none'"
              />
              <div v-else class="scenic-thumb placeholder">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="scenic-info">
                <h3 class="scenic-name">{{ order.scenicSpotName || '景点' }}</h3>
                <p class="order-meta">
                  <span>游览日期：{{ order.visitDate }}</span>
                  <span>数量：{{ order.quantity }} 张</span>
                </p>
                <p class="order-contact" v-if="order.contactName">
                  联系人：{{ order.contactName }} {{ order.contactPhone }}
                </p>
              </div>
            </div>
            <div class="order-amount">
              <span class="amount-label">订单金额</span>
              <span class="amount-value">¥{{ order.totalAmount }}</span>
            </div>
          </div>

          <div class="order-footer">
            <span class="order-time">{{ order.createTime }}</span>
            <div class="order-actions">
              <el-button
                v-if="order.status === 0"
                type="primary"
                size="small"
                @click="handlePay(order)"
              >立即支付</el-button>
              <el-button
                v-if="order.status === 0"
                size="small"
                @click="handleCancel(order)"
              >取消订单</el-button>
              <el-button
                size="small"
                text
                type="primary"
                @click="goDetail(order.id)"
              >查看详情</el-button>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && list.length === 0" description="还没有订单记录">
          <el-button type="primary" @click="$router.push('/scenic')">去预订景点门票</el-button>
        </el-empty>
      </div>

      <div class="pagination-wrap" v-if="total > params.pageSize">
        <el-pagination
          v-model:current-page="params.pageNum"
          :page-size="params.pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadOrders"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyOrders, payOrder, cancelOrder } from '@/api/order'
import { toMediaUrl } from '@/utils/media'
import { Tickets, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const currentStatus = ref('')

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: '0' },
  { label: '已支付', value: '1' },
  { label: '已使用', value: '2' },
  { label: '已取消', value: '3' },
  { label: '已退款', value: '4' }
]

const params = reactive({
  pageNum: 1,
  pageSize: 10
})

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

function getStatusText(status) {
  return statusMap[status] || '未知'
}

function getStatusType(status) {
  return statusTypeMap[status] || 'info'
}

// 订单列表接口返回 scenicSpotCover，兼容 coverImage
function orderCover(order) {
  return toMediaUrl(order.scenicSpotCover || order.coverImage || '')
}

function switchStatus(value) {
  currentStatus.value = value
  params.pageNum = 1
  loadOrders()
}

function goDetail(id) {
  router.push({ name: 'OrderDetail', params: { id } })
}

async function loadOrders() {
  loading.value = true
  try {
    const queryParams = { ...params }
    if (currentStatus.value !== '') {
      queryParams.status = currentStatus.value
    }
    const res = await getMyOrders(queryParams)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handlePay(order) {
  try {
    await ElMessageBox.confirm(`确认支付订单 ${order.orderNo}，金额 ¥${order.totalAmount}？`, '确认支付', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'info'
    })
    await payOrder(order.id)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm(`确认取消订单 ${order.orderNo}？`, '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '暂不取消',
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadOrders()
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

.status-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-md);
  padding: 6px;
  overflow-x: auto;
}

.tab-item {
  padding: 8px 20px;
  font-size: 14px;
  color: var(--neutral-600);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;

  &:hover {
    color: var(--color-primary);
    background: var(--color-primary-bg);
  }

  &.active {
    color: #fff;
    background: var(--color-primary);
    font-weight: 600;
  }
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 320px;
}

.order-card {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
  }
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: var(--neutral-100);
  border-bottom: 1px solid var(--neutral-200);

  .order-no {
    font-size: 13px;
    color: var(--neutral-500);
    font-family: monospace;
  }
}

.order-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  cursor: pointer;
  gap: 24px;
}

.order-scenic {
  display: flex;
  gap: 16px;
  flex: 1;
}

.scenic-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  flex-shrink: 0;

  &.placeholder {
    background: var(--neutral-100);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--neutral-300);
  }
}

.scenic-info {
  flex: 1;
  min-width: 0;
}

.scenic-name {
  font-size: 16px;
  font-family: var(--font-serif);
  color: var(--neutral-800);
  margin-bottom: 6px;
}

.order-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--neutral-500);
  margin-bottom: 4px;
}

.order-contact {
  font-size: 13px;
  color: var(--neutral-400);
}

.order-amount {
  text-align: right;
  flex-shrink: 0;

  .amount-label {
    display: block;
    font-size: 12px;
    color: var(--neutral-400);
    margin-bottom: 4px;
  }

  .amount-value {
    font-size: 22px;
    font-weight: 700;
    color: var(--color-accent);
  }
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid var(--neutral-200);

  .order-time {
    font-size: 13px;
    color: var(--neutral-400);
  }

  .order-actions {
    display: flex;
    gap: 8px;
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .order-body {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-amount {
    text-align: left;
    width: 100%;
    padding-top: 12px;
    border-top: 1px dashed var(--neutral-200);
  }

  .order-footer {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .page-banner .page-title {
    font-size: 24px;
  }
}
</style>
