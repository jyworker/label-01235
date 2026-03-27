<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">景点管理</h1>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增景点
      </el-button>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索景点名称"
        clearable
        style="width: 240px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="queryParams.categoryId"
        placeholder="选择分类"
        clearable
        style="width: 160px"
        @change="handleSearch"
      >
        <el-option
          v-for="cat in categoryList"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
      <el-select
        v-model="queryParams.status"
        placeholder="状态"
        clearable
        style="width: 120px"
        @change="handleSearch"
      >
        <el-option label="已上架" :value="1" />
        <el-option label="已下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- Table -->
    <div class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
        row-key="id"
      >
        <template #empty>
          <el-empty description="暂无数据" :image-size="100" />
        </template>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="景点名称" min-width="160">
          <template #default="{ row }">
            <div class="scenic-name-cell">
              <img
                v-if="row.coverImage"
                :src="row.coverImage"
                class="scenic-thumb"
                alt=""
              />
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="ticketPrice" label="票价" width="100">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.ticketPrice ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="avgRating" label="评分" width="90">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-icon color="#E8853D"><StarFilled /></el-icon>
              <span>{{ row.avgRating ?? '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="visitCount" label="访问量" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, StarFilled } from '@element-plus/icons-vue'
import { getScenicList, deleteScenic, updateScenicStatus } from '@/api/scenic'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  categoryId: '',
  status: ''
})

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.categoryId === '') delete params.categoryId
    if (params.status === '') delete params.status
    if (params.keyword === '') delete params.keyword

    const res = await getScenicList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载景点列表失败', e)
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categoryList.value = Array.isArray(res.data) ? res.data : (res.data?.records || [])
    }
  } catch (e) {
    console.error('加载分类列表失败', e)
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  loadData()
}

function handleReset() {
  queryParams.keyword = ''
  queryParams.categoryId = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  loadData()
}

function handleAdd() {
  router.push('/scenic/add')
}

function handleEdit(row) {
  router.push(`/scenic/edit/${row.id}`)
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定要${action}「${row.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await updateScenicStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error(e?.response?.data?.message || e?.message || '操作失败')
    }
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.name}」吗？此操作不可撤销。`, '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })
    const res = await deleteScenic(row.id)
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
  loadCategories()
})
</script>

<style lang="scss" scoped>
.scenic-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.scenic-thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
  border: 1px solid var(--border-color);
}

.price-text {
  color: var(--color-accent);
  font-weight: 600;
}

.rating-cell {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}
</style>
