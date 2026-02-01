<template>
  <div class="search-input-container" ref="containerRef">
    <el-input 
      v-model="keyword" 
      :placeholder="placeholder"
      class="search-input"
      :prefix-icon="Search"
      @focus="handleFocus"
      @blur="handleBlur"
      @input="handleInput"
      @keyup.enter="handleSearch"
      clearable
    />
    
    <!-- 搜索下拉面板 -->
    <transition name="dropdown-fade">
      <div v-show="showDropdown" class="search-dropdown">
        <!-- 搜索建议（有输入时显示） -->
        <div v-if="keyword && suggestions.length > 0" class="dropdown-section">
          <div class="section-header">
            <span class="section-title">搜索建议</span>
          </div>
          <div class="suggestion-list">
            <div 
              v-for="(item, index) in suggestions" 
              :key="'sug-' + index"
              class="suggestion-item"
              @mousedown.prevent="selectSuggestion(item)"
            >
              <el-icon class="item-icon"><Search /></el-icon>
              <span class="item-text" v-html="highlightKeyword(item)"></span>
            </div>
          </div>
        </div>

        <!-- 热门搜索（无输入时显示） -->
        <div v-if="!keyword && hotWords.length > 0" class="dropdown-section">
          <div class="section-header">
            <span class="section-title">🔥 热门搜索</span>
          </div>
          <div class="hot-words-list">
            <span 
              v-for="(item, index) in hotWords" 
              :key="'hot-' + index"
              class="hot-word-tag"
              @mousedown.prevent="selectHotWord(item)"
            >
              {{ item.keyword }}
            </span>
          </div>
        </div>

        <!-- 搜索历史 -->
        <div v-if="!keyword && searchHistory.length > 0" class="dropdown-section">
          <div class="section-header">
            <span class="section-title">搜索历史</span>
            <span class="clear-btn" @mousedown.prevent="clearHistory">清空</span>
          </div>
          <div class="history-list">
            <div 
              v-for="(item, index) in searchHistory" 
              :key="'his-' + index"
              class="history-item"
              @mousedown.prevent="selectHistory(item)"
            >
              <el-icon class="item-icon"><Clock /></el-icon>
              <span class="item-text">{{ item }}</span>
              <el-icon class="delete-icon" @mousedown.stop.prevent="removeHistory(index)"><Close /></el-icon>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!keyword && hotWords.length === 0 && searchHistory.length === 0" class="empty-state">
          <span>暂无搜索记录</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Clock, Close } from '@element-plus/icons-vue'
import { getSuggestions, getHotWords } from '@/api/search'

const props = defineProps({
  placeholder: {
    type: String,
    default: '搜索好茶...'
  }
})

const router = useRouter()
const containerRef = ref(null)

// 状态
const keyword = ref('')
const showDropdown = ref(false)
const suggestions = ref([])
const hotWords = ref([])
const searchHistory = ref([])

// 防抖定时器
let debounceTimer = null

// 本地存储Key
const HISTORY_KEY = 'tea_search_history'

/**
 * 加载搜索历史
 */
const loadHistory = () => {
  try {
    const saved = localStorage.getItem(HISTORY_KEY)
    if (saved) {
      searchHistory.value = JSON.parse(saved)
    }
  } catch (e) {
    console.error('加载搜索历史失败', e)
  }
}

/**
 * 保存搜索历史
 */
const saveHistory = (kw) => {
  if (!kw || !kw.trim()) return
  const trimmed = kw.trim()
  
  // 移除重复项
  const filtered = searchHistory.value.filter(h => h !== trimmed)
  // 添加到开头
  filtered.unshift(trimmed)
  // 限制数量
  searchHistory.value = filtered.slice(0, 10)
  
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
  } catch (e) {
    console.error('保存搜索历史失败', e)
  }
}

/**
 * 清空历史
 */
const clearHistory = () => {
  searchHistory.value = []
  localStorage.removeItem(HISTORY_KEY)
}

/**
 * 移除单条历史
 */
const removeHistory = (index) => {
  searchHistory.value.splice(index, 1)
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
  } catch (e) {
    console.error('保存搜索历史失败', e)
  }
}

/**
 * 加载热搜词
 */
const loadHotWords = async () => {
  try {
    const res = await getHotWords(10)
    if (res && Array.isArray(res)) {
      hotWords.value = res
    }
  } catch (e) {
    console.error('加载热搜词失败', e)
  }
}

/**
 * 获取搜索建议
 */
const fetchSuggestions = async (kw) => {
  if (!kw || kw.trim().length < 1) {
    suggestions.value = []
    return
  }
  
  try {
    const res = await getSuggestions(kw.trim(), 8)
    if (res && Array.isArray(res)) {
      suggestions.value = res
    }
  } catch (e) {
    console.error('获取搜索建议失败', e)
    suggestions.value = []
  }
}

/**
 * 高亮关键词
 */
const highlightKeyword = (text) => {
  if (!keyword.value || !text) return text
  const regex = new RegExp(`(${keyword.value})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

/**
 * 处理聚焦
 */
const handleFocus = () => {
  showDropdown.value = true
  if (!keyword.value) {
    loadHotWords()
  }
}

/**
 * 处理失焦
 */
const handleBlur = () => {
  // 延迟关闭，允许点击下拉项
  setTimeout(() => {
    showDropdown.value = false
  }, 200)
}

/**
 * 处理输入
 */
const handleInput = (val) => {
  // 防抖获取建议
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    fetchSuggestions(val)
  }, 300)
}

/**
 * 执行搜索
 */
const doSearch = (kw) => {
  if (!kw || !kw.trim()) return
  
  saveHistory(kw)
  showDropdown.value = false
  keyword.value = kw.trim()
  
  router.push({ 
    path: '/products', 
    query: { keyword: kw.trim() } 
  })
}

/**
 * 回车搜索
 */
const handleSearch = () => {
  doSearch(keyword.value)
}

/**
 * 选择搜索建议
 */
const selectSuggestion = (item) => {
  doSearch(item)
}

/**
 * 选择热词
 */
const selectHotWord = (item) => {
  doSearch(item.keyword)
}

/**
 * 选择历史
 */
const selectHistory = (item) => {
  doSearch(item)
}

onMounted(() => {
  loadHistory()
  
  // 从localStorage读取AI对话传来的搜索关键词
  const savedKeyword = localStorage.getItem('searchKeyword')
  if (savedKeyword) {
    keyword.value = savedKeyword
    // 读取后清除，避免重复使用
    localStorage.removeItem('searchKeyword')
  }
  
  // 从URL参数读取搜索关键词
  const urlParams = new URLSearchParams(window.location.search)
  const urlKeyword = urlParams.get('keyword')
  if (urlKeyword) {
    keyword.value = urlKeyword
  }
})
</script>

<style scoped>
.search-input-container {
  position: relative;
  width: 280px;
}

.search-input :deep(.el-input__wrapper) {
  width: 100%;
  border-radius: 99px;
  background-color: #f2f4f7;
  box-shadow: none;
  border: none;
  padding: 4px 15px;
  transition: all 0.3s;
}

.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #e0e0e0;
}

/* 下拉面板 */
.search-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  width: 360px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  z-index: 2000;
  padding: 12px 0;
  max-height: 400px;
  overflow-y: auto;
}

.dropdown-section {
  padding: 0 16px;
  margin-bottom: 12px;
}

.dropdown-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #666;
}

.clear-btn {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.2s;
}

.clear-btn:hover {
  color: #333;
}

/* 热词标签 */
.hot-words-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-word-tag {
  display: inline-block;
  padding: 6px 14px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-word-tag:hover {
  background: #e8f5e9;
  color: #2e7d32;
}

/* 建议/历史列表 */
.suggestion-list,
.history-list {
  display: flex;
  flex-direction: column;
}

.suggestion-item,
.history-item {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;
}

.suggestion-item:hover,
.history-item:hover {
  background: #f5f5f5;
}

.item-icon {
  font-size: 14px;
  color: #999;
  margin-right: 10px;
  flex-shrink: 0;
}

.item-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-text :deep(.highlight) {
  color: #2e7d32;
  font-weight: 600;
}

.delete-icon {
  font-size: 14px;
  color: #ccc;
  cursor: pointer;
  opacity: 0;
  transition: all 0.2s;
}

.history-item:hover .delete-icon {
  opacity: 1;
}

.delete-icon:hover {
  color: #f56c6c;
}

/* 空状态 */
.empty-state {
  padding: 30px;
  text-align: center;
  color: #999;
  font-size: 13px;
}

/* 动画 */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.2s ease;
}

.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
