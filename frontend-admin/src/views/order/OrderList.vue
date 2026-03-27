<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="queryParams.orderNo"
        placeholder="搜索订单号"
        clearable
        style="width: 220px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索用户/景点"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="queryParams.status"
        placeholder="订单状态"
        clearable
        style="width: 140px"
        @change="handleSearch"
      >
        <el-option
          v-for="item in statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <template #empty>
          <el-empty description="暂无数据" :image-size="100" />
        </template>
        <el-table-column prop="orderNo" label="订单号" min-width="190">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户" min-width="110">
          <template #default="{ row }">
            <span>{{ row.nickname || row.username || row.contactName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="景点" min-width="150">
          <template #default="{ row }">
            <span>{{ row.scenicSpotName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="游玩日期" width="115">
          <template #default="{ row }">
            <span>{{ row.visitDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status !== 3 && row.status !== 4"
              type="warning"
              link
              size="small"
              @click="handleUpdateStatus(row)"
            >
              更新状态
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- Detail Dialog -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="560px"
      destroy-on-close
      :append-to-body="true"
      :z-index="2000"
    >
      <div class="order-detail" v-if="currentOrder">
        <div class="detail-section">
          <h4 class="section-title">订单信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号</span>
              <span class="detail-value">{{ currentOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态</span>
              <el-tag :type="getStatusType(currentOrder.status)" size="small">
                {{ getStatusLabel(currentOrder.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="detail-label">下单时间</span>
              <span class="detail-value">{{ currentOrder.createTime }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">支付时间</span>
              <span class="detail-value">{{ currentOrder.payTime || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">景点信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">景点名称</span>
              <span class="detail-value">{{ currentOrder.scenicSpotName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">游玩日期</span>
              <span class="detail-value">{{ currentOrder.visitDate || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">购买数量</span>
              <span class="detail-value">{{ currentOrder.quantity }} 张</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">总金额</span>
              <span class="detail-value amount-text">¥{{ currentOrder.totalAmount ?? 0 }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">联系人信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">联系人</span>
              <span class="detail-value">{{ currentOrder.contactName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ currentOrder.contactPhone || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Update Status Dialog -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新订单状态"
      width="420px"
      :close-on-click-modal="false"
      destroy-on-close
      :append-to-body="true"
      :z-index="2000"
    >
      <el-form label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(statusForm.currentStatus)" size="small">
            {{ getStatusLabel(statusForm.currentStatus) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.newStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option
              v-for="item in getAvailableStatuses(statusForm.currentStatus)"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="statusSubmitting" @click="confirmUpdateStatus">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail, updateOrderStatus } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const statusDialogVisible = ref(false)
const statusSubmitting = ref(false)

const statusOptions = [
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 },
  { label: '已退款', value: 4 }
]

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  orderNo: '',
  status: ''
})

const statusForm = reactive({
  orderId: null,
  currentStatus: null,
  newStatus: null
})

function getStatusLabel(status) {
  const map = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消', 4: '已退款' }
  return map[status] || '未知'
}

function getStatusType(status) {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }
  return map[status] || 'info'
}

function getAvailableStatuses(currentStatus) {
  const all = [
    { label: '待支付', value: 0 },
    { label: '已支付', value: 1 },
    { label: '已完成', value: 2 },
    { label: '已取消', value: 3 },
    { label: '已退款', value: 4 }
  ]
  return all.filter(s => s.value !== currentStatus)
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.keyword === '') delete params.keyword
    if (params.orderNo === '') delete params.orderNo
    if (params.status === '') delete params.status

    const res = await getOrderList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载订单列表失败', e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.keyword = ''
  queryParams.orderNo = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  loadData()
}

async function handleViewDetail(row) {
  try {
    const res = await getOrderDetail(row.id)
    if (res.code === 200) {
      currentOrder.value = res.data
      detailVisible.value = true
    } else {
      ElMessage.error(res.message || '获取订单详情失败')
    }
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  }
}

function handleUpdateStatus(row) {
  statusForm.orderId = row.id
  statusForm.currentStatus = row.status
  statusForm.newStatus = null
  statusDialogVisible.value = true
}

async function confirmUpdateStatus() {
  if (statusForm.newStatus === null) {
    ElMessage.warning('请选择新状态')
    return
  }

  statusSubmitting.value = true
  try {
    const res = await updateOrderStatus(statusForm.orderId, statusForm.newStatus)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      statusDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '状态更新失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '状态更新失败')
  } finally {
    statusSubmitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.order-no {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: var(--neutral-600);
}

.amount-text {
  color: var(--color-accent);
  font-weight: 600;
}

// Order Detail
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  background: var(--neutral-50);
  border-radius: 8px;
  padding: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--neutral-800);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: var(--neutral-400);
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: var(--neutral-700);
  font-weight: 500;
}
</style>
