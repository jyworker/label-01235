<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索用户名/昵称/手机号"
        clearable
        style="width: 280px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="queryParams.status"
        placeholder="状态"
        clearable
        style="width: 120px"
        @change="handleSearch"
      >
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
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
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info-cell">
              <div class="user-avatar">
                <img v-if="row.avatar" :src="row.avatar" alt="" />
                <el-icon v-else :size="20"><UserFilled /></el-icon>
              </div>
              <div class="user-detail">
                <span class="user-name">{{ row.nickname || row.username }}</span>
                <span class="user-account">@{{ row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200">
          <template #default="{ row }">
            <span>{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140">
          <template #default="{ row }">
            <span>{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
import { Search, UserFilled } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.keyword === '') delete params.keyword
    if (params.status === '') delete params.status

    const res = await getUserList(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载用户列表失败', e)
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

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户「${row.nickname || row.username}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await updateUserStatus(row.id, newStatus)
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

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
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

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--neutral-800);
}

.user-account {
  font-size: 12px;
  color: var(--neutral-400);
}
</style>
