<template>
  <div class="booking-page">
    <div class="page-banner">
      <div class="container">
        <h1 class="page-title animate-in">预订门票</h1>
        <p class="page-desc animate-in animate-delay-1">填写预订信息，确认您的旅行计划</p>
      </div>
    </div>

    <div class="container page-body" v-loading="scenicLoading">
      <template v-if="scenic">
        <div class="booking-grid">
          <!-- Booking Form -->
          <div class="booking-form-section animate-in">
            <div class="form-card">
              <h2 class="form-title">预订信息</h2>

              <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-position="top"
                size="large"
                @submit.prevent="handleSubmit"
              >
                <el-form-item label="游览日期" prop="visitDate">
                  <el-date-picker
                    v-model="form.visitDate"
                    type="date"
                    placeholder="请选择游览日期"
                    :disabled-date="disabledDate"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item label="购票数量" prop="quantity">
                  <el-input-number
                    v-model="form.quantity"
                    :min="1"
                    :max="99"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item label="联系人" prop="contactName">
                  <el-input v-model="form.contactName" placeholder="请输入联系人姓名" :prefix-icon="User" />
                </el-form-item>

                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input v-model="form.contactPhone" placeholder="请输入联系电话" :prefix-icon="Phone" />
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    class="submit-btn"
                    :loading="submitting"
                    @click="handleSubmit"
                  >
                    确认预订
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>

          <!-- Order Summary -->
          <div class="order-summary animate-in animate-delay-1">
            <div class="summary-card">
              <h3 class="summary-title">订单摘要</h3>

              <!-- Scenic Info -->
              <div class="scenic-summary">
                <img
                  v-if="coverImageUrl"
                  :src="coverImageUrl"
                  class="scenic-cover"
                  @error="(e) => e.target.style.display = 'none'"
                />
                <div v-else class="scenic-cover placeholder">
                  <el-icon :size="24"><Picture /></el-icon>
                </div>
                <div class="scenic-info">
                  <h4 class="scenic-name">{{ scenic.name }}</h4>
                  <p class="scenic-location">
                    <el-icon :size="14"><Location /></el-icon>
                    {{ scenic.address || scenic.province + ' ' + scenic.city }}
                  </p>
                </div>
              </div>

              <div class="summary-divider"></div>

              <!-- Price Details -->
              <div class="price-details">
                <div class="price-row">
                  <span>门票单价</span>
                  <span>¥{{ scenic.ticketPrice || 0 }}</span>
                </div>
                <div class="price-row">
                  <span>购票数量</span>
                  <span>× {{ form.quantity }}</span>
                </div>
                <div class="price-row" v-if="form.visitDate">
                  <span>游览日期</span>
                  <span>{{ form.visitDate }}</span>
                </div>
              </div>

              <div class="summary-divider"></div>

              <div class="total-row">
                <span>合计</span>
                <span class="total-price">¥{{ totalAmount }}</span>
              </div>

              <!-- Notice -->
              <div class="notice">
                <el-icon :size="14"><WarningFilled /></el-icon>
                <span>
                  {{ isFreeOrder ? '当前为0元景点，下单后将自动支付' : '预订成功后请在30分钟内完成支付' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScenicDetail } from '@/api/scenic'
import { createOrder } from '@/api/order'
import { toMediaUrl } from '@/utils/media'
import { User, Phone, Location, Picture, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const scenicLoading = ref(true)
const submitting = ref(false)
const scenic = ref(null)

const form = reactive({
  visitDate: '',
  quantity: 1,
  contactName: '',
  contactPhone: ''
})

const rules = {
  visitDate: [{ required: true, message: '请选择游览日期', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入购票数量', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const totalAmount = computed(() => {
  if (!scenic.value) return '0.00'
  const price = Number(scenic.value.ticketPrice) || 0
  return (price * form.quantity).toFixed(2)
})

const isFreeOrder = computed(() => Number(totalAmount.value) === 0)
const coverImageUrl = computed(() => toMediaUrl(scenic.value?.coverImage))

function disabledDate(date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date < today
}

async function loadScenic() {
  scenicLoading.value = true
  try {
    const res = await getScenicDetail(route.params.id)
    scenic.value = res.data
  } catch (e) {
    ElMessage.error('加载景点信息失败')
  } finally {
    scenicLoading.value = false
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const res = await createOrder({
      scenicSpotId: scenic.value.id,
      quantity: form.quantity,
      visitDate: form.visitDate,
      contactName: form.contactName,
      contactPhone: form.contactPhone
    })
    if (res.code === 200) {
      if (res.data?.status === 1 && Number(res.data?.totalAmount ?? 0) === 0) {
        ElMessage.success('预订成功：该景点为0元，系统已自动完成支付')
      } else {
        ElMessage.success('预订成功')
      }
      router.push({ name: 'OrderDetail', params: { id: res.data.id } })
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadScenic()
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #1a6b4f 0%, #e8853d 100%);
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

.booking-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
  align-items: start;
}

.form-card {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  padding: 32px;
}

.form-title {
  font-family: var(--font-serif);
  font-size: 22px;
  color: var(--neutral-800);
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--neutral-200);
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-md);
  margin-top: 8px;
}

/* Summary */
.summary-card {
  background: #fff;
  border: 1px solid var(--neutral-200);
  border-radius: var(--radius-lg);
  padding: 24px;
  position: sticky;
  top: calc(var(--header-height) + 24px);
}

.summary-title {
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--neutral-800);
  margin-bottom: 20px;
}

.scenic-summary {
  display: flex;
  gap: 12px;
}

.scenic-cover {
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
  font-family: var(--font-serif);
  font-size: 15px;
  color: var(--neutral-800);
  margin-bottom: 4px;
  line-height: 1.4;
}

.scenic-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--neutral-400);
}

.summary-divider {
  height: 1px;
  background: var(--neutral-200);
  margin: 16px 0;
}

.price-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--neutral-600);
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: var(--neutral-800);

  .total-price {
    font-size: 26px;
    font-weight: 700;
    color: var(--color-accent);
  }
}

.notice {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 16px;
  padding: 10px 12px;
  background: rgba(212, 168, 67, 0.08);
  border-radius: var(--radius-sm);
  font-size: 12px;
  color: var(--color-warning);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--neutral-700);
}

@media (max-width: 768px) {
  .booking-grid {
    grid-template-columns: 1fr;
  }

  .summary-card {
    position: static;
  }

  .page-banner .page-title {
    font-size: 24px;
  }
}
</style>
