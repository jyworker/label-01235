<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="isEdit ? '编辑景点' : '新增景点'"
    width="720px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="top"
    >
      <div class="form-grid">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入景点名称" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categoryList"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="简要描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入景点简要描述"
        />
      </el-form-item>

      <el-form-item label="详情内容" prop="detailContent">
        <div class="rich-editor-wrapper">
          <Toolbar
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            mode="default"
            style="border-bottom: 1px solid var(--border-color, #E8E5DE)"
          />
          <Editor
            v-model="form.detailContent"
            :defaultConfig="editorConfig"
            mode="default"
            style="height: 280px; overflow-y: hidden"
            @onCreated="handleEditorCreated"
          />
        </div>
      </el-form-item>

      <el-form-item label="封面图片" prop="coverImage">
        <div class="upload-area">
          <el-upload
            class="cover-upload"
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <div v-if="form.coverImage" class="cover-preview">
              <img :src="form.coverImage" alt="" />
              <div class="cover-mask">
                <el-icon :size="20"><RefreshRight /></el-icon>
                <span>更换图片</span>
              </div>
            </div>
            <div v-else class="cover-placeholder">
              <el-icon :size="28"><Plus /></el-icon>
              <span>上传封面</span>
            </div>
          </el-upload>
        </div>
      </el-form-item>

      <el-form-item label="更多图片">
        <el-upload
          v-model:file-list="imageFileList"
          :http-request="handleImageUpload"
          list-type="picture-card"
          accept="image/*"
          :limit="9"
          :on-exceed="handleExceed"
        >
          <el-icon :size="20"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <div class="form-grid">
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="如: 浙江" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="如: 杭州" />
        </el-form-item>
      </div>

      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入详细地址" />
      </el-form-item>

      <div class="form-grid">
        <el-form-item label="经度" prop="longitude">
          <el-input-number
            v-model="form.longitude"
            :precision="6"
            :step="0.001"
            :min="-180"
            :max="180"
            style="width: 100%"
            placeholder="如: 120.148583"
          />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number
            v-model="form.latitude"
            :precision="6"
            :step="0.001"
            :min="-90"
            :max="90"
            style="width: 100%"
            placeholder="如: 30.242845"
          />
        </el-form-item>
      </div>

      <div class="form-grid form-grid-3">
        <el-form-item label="票价" prop="ticketPrice">
          <el-input-number
            v-model="form.ticketPrice"
            :precision="2"
            :step="10"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开放时间" prop="openTime">
          <el-time-picker
            v-model="form.openTime"
            placeholder="请选择开放时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
            @change="handleTimeChange"
          />
        </el-form-item>
        <el-form-item label="关闭时间" prop="closeTime">
          <el-time-picker
            v-model="form.closeTime"
            placeholder="请选择关闭时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
            @change="handleTimeChange"
          />
        </el-form-item>
      </div>

      <el-form-item label="景点视频" prop="videoUrl">
        <div class="video-upload-row">
          <el-input
            v-model="form.videoUrl"
            placeholder="视频链接或上传视频（支持 MP4）"
            clearable
            style="flex: 1"
          />
          <el-upload
            class="video-upload-btn"
            :show-file-list="false"
            :http-request="handleVideoUpload"
            accept="video/mp4,video/quicktime,.mp4,.mov"
          >
            <el-button type="primary" plain size="default">上传视频</el-button>
          </el-upload>
        </div>
      </el-form-item>

      <el-form-item label="游玩提示">
        <el-input
          v-model="form.tips"
          type="textarea"
          :rows="2"
          placeholder="如: 建议游玩时间3-4小时"
        />
      </el-form-item>

      <el-form-item label="热门推荐">
        <el-switch
          v-model="form.isHot"
          :active-value="1"
          :inactive-value="0"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ submitting ? '提交中...' : '确 定' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, RefreshRight } from '@element-plus/icons-vue'
import { addScenic, updateScenic } from '@/api/scenic'
import { uploadFile } from '@/api/file'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const props = defineProps({
  visible: Boolean,
  scenicData: Object,
  categoryList: Array
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const imageFileList = ref([])

// WangEditor 配置
const editorRef = shallowRef()
const toolbarConfig = {
  excludeKeys: ['fullScreen', 'group-video']
}
const editorConfig = {
  placeholder: '请输入景点详情内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await uploadFile(file)
          if (res.code === 200 && res.data) {
            const url = res.data.url || res.data
            insertFn(url, file.name, url)
          } else {
            ElMessage.error(res.message || '图片上传失败')
          }
        } catch (e) {
          ElMessage.error(e?.response?.data?.message || e?.message || '图片上传失败')
        }
      }
    }
  }
}

function handleEditorCreated(editor) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})

const isEdit = computed(() => !!props.scenicData?.id)

const defaultForm = {
  name: '',
  categoryId: '',
  description: '',
  detailContent: '',
  videoUrl: '',
  coverImage: '',
  images: [],
  province: '',
  city: '',
  address: '',
  longitude: null,
  latitude: null,
  ticketPrice: 0,
  openTime: '',
  closeTime: '',
  tips: '',
  isHot: 0
}

const form = ref({ ...defaultForm })

function toMinutes(time) {
  if (!time || typeof time !== 'string' || !time.includes(':')) return null
  const [h, m] = time.split(':').map(Number)
  if (Number.isNaN(h) || Number.isNaN(m)) return null
  return h * 60 + m
}

function validateOpenTime(rule, value, callback) {
  if (form.value.closeTime && !value) {
    callback(new Error('已选择关闭时间，请先选择开放时间'))
    return
  }
  const openMinutes = toMinutes(value)
  const closeMinutes = toMinutes(form.value.closeTime)
  if (openMinutes !== null && closeMinutes !== null && openMinutes >= closeMinutes) {
    callback(new Error('开放时间需早于关闭时间'))
    return
  }
  callback()
}

function validateCloseTime(rule, value, callback) {
  if (value && !form.value.openTime) {
    callback(new Error('请先选择开放时间'))
    return
  }
  const openMinutes = toMinutes(form.value.openTime)
  const closeMinutes = toMinutes(value)
  if (openMinutes !== null && closeMinutes !== null && closeMinutes <= openMinutes) {
    callback(new Error('关闭时间需晚于开放时间'))
    return
  }
  callback()
}

const rules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入简要描述', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  ticketPrice: [{ required: true, message: '请输入票价', trigger: 'blur' }],
  openTime: [{ validator: validateOpenTime, trigger: 'change' }],
  closeTime: [{ validator: validateCloseTime, trigger: 'change' }]
}

function handleTimeChange() {
  if (!formRef.value) return
  formRef.value.validateField(['openTime', 'closeTime'])
}

watch(() => props.visible, (val) => {
  if (val) {
    if (props.scenicData) {
      form.value = { ...defaultForm, ...props.scenicData }
      // Populate image file list from existing images
      if (props.scenicData.images && Array.isArray(props.scenicData.images)) {
        imageFileList.value = props.scenicData.images.map((img, idx) => {
          const url = typeof img === 'string' ? img : img.imageUrl
          return { name: `image-${idx}`, url }
        })
      } else {
        imageFileList.value = []
      }
    } else {
      form.value = { ...defaultForm }
      imageFileList.value = []
    }
  }
})

async function handleVideoUpload({ file }) {
  const MAX_MB = 200
  const sizeMb = file.size / 1024 / 1024
  if (file.size > MAX_MB * 1024 * 1024) {
    ElMessage.error(`视频文件不能超过 ${MAX_MB}MB，当前文件约 ${sizeMb.toFixed(1)}MB`)
    return
  }
  try {
    const res = await uploadFile(file)
    if (res.code === 200 && res.data) {
      form.value.videoUrl = typeof res.data === 'string' ? res.data : (res.data.url || res.data)
      ElMessage.success('视频上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    if (e?.response?.status === 413) {
      ElMessage.error(`服务器当前上传限制过小，${sizeMb.toFixed(1)}MB 视频已被拒绝，请联系管理员调整后再试`)
    } else {
      ElMessage.error('视频上传失败')
    }
  }
}

async function handleCoverUpload({ file }) {
  try {
    const res = await uploadFile(file)
    if (res.code === 200 && res.data) {
      form.value.coverImage = res.data.url || res.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '上传失败')
  }
}

async function handleImageUpload({ file, onSuccess, onError }) {
  try {
    const res = await uploadFile(file)
    if (res.code === 200 && res.data) {
      const url = res.data.url || res.data
      onSuccess({ url })
      ElMessage.success('图片上传成功')
    } else {
      onError()
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    onError()
    ElMessage.error(e?.response?.data?.message || e?.message || '上传失败')
  }
}

function handleExceed() {
  ElMessage.warning('最多上传9张图片')
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const images = imageFileList.value.map(f => {
      if (f.response?.url) return f.response.url
      if (f.url) return f.url
      return ''
    }).filter(Boolean)

    const data = {
      ...form.value,
      images: images.filter((url) => typeof url === 'string' && !url.startsWith('blob:')),
      isHot: Number(form.value.isHot) || 0
    }

    let res
    if (isEdit.value) {
      res = await updateScenic(data)
    } else {
      res = await addScenic(data)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      emit('update:visible', false)
      emit('success')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}

.form-grid-3 {
  grid-template-columns: 1fr 1fr 1fr;
}

.video-upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.video-upload-btn {
  flex-shrink: 0;
}

.upload-area {
  width: 100%;
}

.cover-upload {
  :deep(.el-upload) {
    width: 320px;
    aspect-ratio: 8 / 5;
    border: 1px dashed var(--border-color);
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--color-primary);
    }
  }
}

.cover-preview {
  width: 100%;
  height: 100%;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.cover-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;

  .cover-preview:hover & {
    opacity: 1;
  }
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--neutral-400);
  font-size: 13px;
}

.rich-editor-wrapper {
  border: 1px solid var(--border-color, #E8E5DE);
  border-radius: 8px;
  overflow: hidden;
  width: 100%;

  :deep(.w-e-toolbar) {
    background-color: #FAFAF8;
    border-radius: 8px 8px 0 0;
  }

  :deep(.w-e-text-container) {
    background-color: #fff;
    border-radius: 0 0 8px 8px;
  }
}
</style>
