<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">评论管理</h1>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索评论内容/用户"
        clearable
        style="width: 260px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="queryParams.status"
        placeholder="审核状态"
        clearable
        style="width: 140px"
        @change="handleSearch"
      >
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已拒绝" :value="2" />
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
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="用户" width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="mini-avatar">
                <img v-if="row.avatar" :src="row.avatar" alt="" />
                <el-icon v-else :size="14"><UserFilled /></el-icon>
              </div>
              <span>{{ row.nickname || row.username || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="scenicSpotName" label="景点" width="160">
          <template #default="{ row }">
            <span>{{ row.scenicSpotName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="220">
          <template #default="{ row }">
            <div class="content-cell">
              <p class="review-text">{{ row.content }}</p>
              <div v-if="row.images && row.images.length" class="review-images">
                <img
                  v-for="(img, idx) in getImageList(row.images)"
                  :key="idx"
                  :src="img"
                  class="review-thumb"
                  alt=""
                />
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="90">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-icon color="#E8853D"><StarFilled /></el-icon>
              <span>{{ row.rating || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="statusMap[row.status]?.type || 'info'"
              size="small"
            >
              {{ statusMap[row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link size="small" @click="handleAudit(row, 1)">
                通过
              </el-button>
              <el-button type="warning" link size="small" @click="handleAudit(row, 2)">
                拒绝
              </el-button>
            </template>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, StarFilled, UserFilled } from '@element-plus/icons-vue'
import { getReviewList, auditReview, deleteReview } from '@/api/review'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const statusMap = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' }
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

function getImageList(images) {
  if (Array.isArray(images)) {
    return images.map(img => typeof img === 'string' ? img : img.imageUrl).filter(Boolean)
  }
  return []
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.keyword === '') delete params.keyword
    if (params.status === '') delete params.status

    const res = await getReviewList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载评论列表失败', e)
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
  queryParams.status = ''
  queryParams.pageNum = 1
  loadData()
}

async function handleAudit(row, status) {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(
      `确定要${action}这条评论吗？`,
      '审核确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await auditReview(row.id, status)
    if (res.code === 200) {
      ElMessage.success(`审核${action}成功`)
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？此操作不可撤销。', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })
    const res = await deleteReview(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--neutral-100);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
  color: var(--neutral-400);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.content-cell {
  padding: 4px 0;
}

.review-text {
  font-size: 13px;
  color: var(--neutral-700);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.review-images {
  display: flex;
  gap: 6px;
  margin-top: 6px;
}

.review-thumb {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  object-fit: cover;
  border: 1px solid var(--border-color);
}

.rating-cell {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}
</style>
