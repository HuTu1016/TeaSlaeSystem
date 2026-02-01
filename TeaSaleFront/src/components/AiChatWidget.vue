<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ChatDotRound, Close, Position } from '@element-plus/icons-vue'
import request from '@/utils/request'

const isOpen = ref(false)
const isJumping = ref(false)
const isWatering = ref(false)
const message = ref('')
const messages = ref([])
const loading = ref(false)
const chatBody = ref(null)
const floatBall = ref(null)
const chatWindow = ref(null)

// 弹窗拖拽相关
const isWindowDragging = ref(false)
const windowPosition = ref({ x: 0, y: 0 })
let windowDragStart = { x: 0, y: 0 }
let windowStartPos = { x: 0, y: 0 }

// 弹窗尺寸可调整
const windowSize = ref({ width: 400, height: 550 })
const isResizing = ref(false)
let resizeStart = { x: 0, y: 0 }
let sizeStart = { width: 0, height: 0 }
let resizeDirection = ''

// 打字机效果
const typingText = ref('')
const isTyping = ref(false)

// 是否已加载历史对话
const historyLoaded = ref(false)

// 拖拽相关
const isDragging = ref(false)
const position = ref({ x: 0, y: 0 })
const isLeftSide = ref(false)

let jumpInterval = null
let dragStartPos = { x: 0, y: 0 }
let ballStartPos = { x: 0, y: 0 }

// 从localStorage恢复位置
const restorePosition = () => {
  const saved = localStorage.getItem('teaPetPosition')
  if (saved) {
    const pos = JSON.parse(saved)
    position.value = pos
    isLeftSide.value = pos.x < window.innerWidth / 2
  } else {
    // 默认右下角
    position.value = { 
      x: window.innerWidth - 90, 
      y: window.innerHeight - 90 
    }
  }
}

// 保存位置到localStorage
const savePosition = () => {
  localStorage.setItem('teaPetPosition', JSON.stringify(position.value))
}

// 拖拽开始
const onDragStart = (e) => {
  if (isOpen.value) return
  isDragging.value = true
  const clientX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX
  const clientY = e.type === 'touchstart' ? e.touches[0].clientY : e.clientY
  dragStartPos = { x: clientX, y: clientY }
  ballStartPos = { ...position.value }
}

// 拖拽移动
const onDragMove = (e) => {
  if (!isDragging.value) return
  e.preventDefault()
  const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX
  const clientY = e.type === 'touchmove' ? e.touches[0].clientY : e.clientY
  
  let newX = ballStartPos.x + (clientX - dragStartPos.x)
  let newY = ballStartPos.y + (clientY - dragStartPos.y)
  
  // 边界限制
  newX = Math.max(0, Math.min(window.innerWidth - 60, newX))
  newY = Math.max(0, Math.min(window.innerHeight - 60, newY))
  
  position.value = { x: newX, y: newY }
}

// 拖拽结束
const onDragEnd = () => {
  if (!isDragging.value) return
  
  // 检查是否真的拖拽过（移动距离超过5px）
  const moveDistance = Math.abs(position.value.x - ballStartPos.x) + Math.abs(position.value.y - ballStartPos.y)
  if (moveDistance > 5) {
    hasDragged = true
  }
  
  isDragging.value = false
  
  // 吸附到两侧
  const threshold = 80
  if (position.value.x < threshold) {
    position.value.x = 10
    isLeftSide.value = true
  } else if (position.value.x > window.innerWidth - threshold - 60) {
    position.value.x = window.innerWidth - 70
    isLeftSide.value = false
  }
  
  savePosition()
}

// 计算样式
const ballStyle = computed(() => ({
  left: `${position.value.x}px`,
  top: `${position.value.y}px`,
  right: 'auto',
  bottom: 'auto'
}))

// 聊天窗口位置
const chatWindowStyle = computed(() => {
  if (isLeftSide.value) {
    return { left: '0', right: 'auto' }
  }
  return { right: '0', left: 'auto' }
})

// 每10秒浇水动画
const startWateringAnimation = () => {
  jumpInterval = setInterval(() => {
    if (!isOpen.value) {
      // 浇水动画
      isWatering.value = true
      setTimeout(() => {
        isWatering.value = false
        // 茶宠跳动并显示提示语
        isJumping.value = true
        showRandomTip()
        setTimeout(() => {
          isJumping.value = false
        }, 600)
      }, 1500)
    }
  }, 10000)
}

onMounted(async () => {
  restorePosition()
  restoreWindowSize()
  startWateringAnimation()
  
  // 添加全局事件监听（茶宠拖拽）
  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
  document.addEventListener('touchmove', onDragMove, { passive: false })
  document.addEventListener('touchend', onDragEnd)
  
  // 添加全局事件监听（弹窗拖拽）
  document.addEventListener('mousemove', onWindowDragMove)
  document.addEventListener('mouseup', onWindowDragEnd)
  document.addEventListener('touchmove', onWindowDragMove, { passive: false })
  document.addEventListener('touchend', onWindowDragEnd)
  
  // 添加全局事件监听（弹窗尺寸调整）
  document.addEventListener('mousemove', onResizeMove)
  document.addEventListener('mouseup', onResizeEnd)
  
  // 尝试加载历史对话
  await loadChatHistory()
})

onUnmounted(() => {
  if (jumpInterval) {
    clearInterval(jumpInterval)
  }
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  document.removeEventListener('touchmove', onDragMove)
  document.removeEventListener('touchend', onDragEnd)
  document.removeEventListener('mousemove', onWindowDragMove)
  document.removeEventListener('mouseup', onWindowDragEnd)
  document.removeEventListener('touchmove', onWindowDragMove)
  document.removeEventListener('touchend', onWindowDragEnd)
  document.removeEventListener('mousemove', onResizeMove)
  document.removeEventListener('mouseup', onResizeEnd)
})

// 恢复弹窗尺寸
const restoreWindowSize = () => {
  const saved = localStorage.getItem('chatWindowSize')
  if (saved) {
    windowSize.value = JSON.parse(saved)
  }
}

// 保存弹窗尺寸
const saveWindowSize = () => {
  localStorage.setItem('chatWindowSize', JSON.stringify(windowSize.value))
}

// 弹窗尺寸调整开始
const onResizeStart = (e, direction) => {
  e.preventDefault()
  e.stopPropagation()
  isResizing.value = true
  resizeDirection = direction
  resizeStart = { x: e.clientX, y: e.clientY }
  sizeStart = { ...windowSize.value }
}

// 弹窗尺寸调整移动
const onResizeMove = (e) => {
  if (!isResizing.value) return
  const deltaX = e.clientX - resizeStart.x
  const deltaY = e.clientY - resizeStart.y
  
  let newWidth = sizeStart.width
  let newHeight = sizeStart.height
  
  // 根据方向调整尺寸
  if (resizeDirection.includes('right')) {
    newWidth = Math.max(320, Math.min(800, sizeStart.width + deltaX))
  }
  if (resizeDirection.includes('left')) {
    newWidth = Math.max(320, Math.min(800, sizeStart.width - deltaX))
  }
  if (resizeDirection.includes('bottom')) {
    newHeight = Math.max(400, Math.min(900, sizeStart.height + deltaY))
  }
  if (resizeDirection.includes('top')) {
    newHeight = Math.max(400, Math.min(900, sizeStart.height - deltaY))
  }
  
  windowSize.value = { width: newWidth, height: newHeight }
}

// 弹窗尺寸调整结束
const onResizeEnd = () => {
  if (isResizing.value) {
    isResizing.value = false
    saveWindowSize()
  }
}

// 加载历史对话
const loadChatHistory = async () => {
  try {
    const res = await request({
      url: '/ai/history',
      method: 'get'
    })
    if (res && res.length > 0) {
      messages.value = res
      historyLoaded.value = true
    } else {
      // 无历史记录，显示欢迎消息
      messages.value.push({
        role: 'assistant',
        content: '您好！我是茶小韵，茗韵茶业的智能顾问~🍵\n有什么关于茶叶的问题都可以问我哦！\n\n您可以去茶叶商城看看我们的产品，或者到领券中心领取优惠券哦~'
      })
    }
    scrollToBottom()
  } catch (e) {
    console.error('加载历史对话失败', e)
    // 显示欢迎消息
    messages.value.push({
      role: 'assistant',
      content: '您好！我是茶小韵，茗韵茶业的智能顾问~🍵\n有什么关于茶叶的问题都可以问我哦！'
    })
  }
}

// 保存对话历史到Redis
const saveChatHistory = async () => {
  try {
    await request({
      url: '/ai/history/save',
      method: 'post',
      data: { messages: messages.value }
    })
  } catch (e) {
    console.error('保存对话历史失败', e)
  }
}

const tipMessage = ref('')
const showTip = ref(false)

const tipMessages = [
  '想了解茶叶吗？点我咨询~',
  '今日有新茶上架，来看看？',
  '送礼选茶？我来帮您推荐~',
  '不知道买什么茶？问我就对了！',
  '茶叶冲泡有技巧，点我告诉您~'
]

let hasDragged = false

const handleBallClick = () => {
  // 如果刚刚拖拽过，不触发点击
  if (hasDragged) {
    hasDragged = false
    return
  }
  // 单击打开弹窗
  isOpen.value = true
  // 打开后滚动到最底部
  scrollToBottom()
}

const closeChat = () => {
  // 关闭前保存对话历史
  if (messages.value.length > 1) {
    saveChatHistory()
  }
  isOpen.value = false
  // 重置弹窗位置
  windowPosition.value = { x: 0, y: 0 }
}

// 弹窗拖拽开始
const onWindowDragStart = (e) => {
  isWindowDragging.value = true
  const clientX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX
  const clientY = e.type === 'touchstart' ? e.touches[0].clientY : e.clientY
  windowDragStart = { x: clientX, y: clientY }
  windowStartPos = { ...windowPosition.value }
}

// 弹窗拖拽移动
const onWindowDragMove = (e) => {
  if (!isWindowDragging.value) return
  e.preventDefault()
  const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX
  const clientY = e.type === 'touchmove' ? e.touches[0].clientY : e.clientY
  
  windowPosition.value = {
    x: windowStartPos.x + (clientX - windowDragStart.x),
    y: windowStartPos.y + (clientY - windowDragStart.y)
  }
}

// 弹窗拖拽结束
const onWindowDragEnd = () => {
  isWindowDragging.value = false
}

// 打字机效果
const typeWriter = (text, callback) => {
  isTyping.value = true
  typingText.value = ''
  let index = 0
  const timer = setInterval(() => {
    if (index < text.length) {
      typingText.value += text[index]
      index++
      scrollToBottom()
    } else {
      clearInterval(timer)
      isTyping.value = false
      if (callback) callback()
    }
  }, 30)
}

// 解析消息中的关键词并转换为可点击链接
const parseMessage = (text) => {
  // 导航类关键词
  const navKeywords = [
    { pattern: /茶叶商城/g, path: '/products', type: 'nav' },
    { pattern: /领券中心/g, path: '/coupon/center', type: 'nav' },
    { pattern: /会员中心/g, path: '/user/wallet', type: 'nav' }
  ]
  
  // 茶叶名称关键词（点击后搜索）
  const teaKeywords = /龙井|碧螺春|毛峰|铁观音|大红袍|金骏眉|正山小种|普洱|白毫银针|绿茶|红茶|乌龙茶|白茶|黑茶|花茶/g
  
  let result = text
  
  // 处理导航关键词
  navKeywords.forEach(({ pattern, path }) => {
    result = result.replace(pattern, (match) => {
      return `<span class="keyword-link" data-path="${path}" data-type="nav">${match}</span>`
    })
  })
  
  // 处理茶叶名称关键词（搜索）
  result = result.replace(teaKeywords, (match) => {
    return `<span class="keyword-link" data-keyword="${match}" data-type="search">${match}</span>`
  })
  
  return result
}

// 显示随机提示语
const showRandomTip = () => {
  const randomIndex = Math.floor(Math.random() * tipMessages.length)
  tipMessage.value = tipMessages[randomIndex]
  showTip.value = true
  setTimeout(() => {
    showTip.value = false
  }, 3000)
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!message.value.trim() || loading.value) return

  const userMessage = message.value.trim()
  message.value = ''

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage
  })
  scrollToBottom()

  loading.value = true

  try {
    // 构建历史消息（最近10条）
    const history = messages.value.slice(-10).map(m => ({
      role: m.role === 'assistant' ? 'assistant' : 'user',
      content: m.content
    }))

    const res = await request({
      url: '/ai/chat',
      method: 'post',
      data: {
        message: userMessage,
        history: history.slice(0, -1) // 排除刚添加的用户消息
      }
    })

    const replyText = res?.reply || '抱歉，我暂时无法回答，请稍后再试~'
    // 添加空消息用于打字机效果
    messages.value.push({
      role: 'assistant',
      content: '',
      isTyping: true
    })
    const msgIndex = messages.value.length - 1
    
    // 打字机效果
    let index = 0
    const timer = setInterval(() => {
      if (index < replyText.length) {
        messages.value[msgIndex].content += replyText[index]
        index++
        scrollToBottom()
      } else {
        clearInterval(timer)
        messages.value[msgIndex].isTyping = false
        loading.value = false
        // 保存对话历史到Redis
        saveChatHistory()
      }
    }, 30)
    
  } catch (e) {
    console.error(e)
    messages.value.push({
      role: 'assistant',
      content: '网络似乎有点问题，请稍后再试哦~'
    })
    loading.value = false
  } finally {
    scrollToBottom()
  }
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// 选择选项
const selectOption = (value, msgIndex) => {
  // 隐藏该消息的选项
  messages.value[msgIndex].showOptions = false
  // 发送选中的选项作为用户消息
  message.value = value
  sendMessage()
}

// 处理关键词点击
const handleKeywordClick = (e) => {
  if (e.target.classList.contains('keyword-link')) {
    const type = e.target.dataset.type
    
    if (type === 'nav') {
      // 导航类：直接跳转
      const path = e.target.dataset.path
      if (path) {
        // 先保存对话历史，再跳转
        saveChatHistory()
        closeChat()
        window.location.href = path
      }
    } else if (type === 'search') {
      // 茶叶名称：跳转到商品页并搜索，同时在搜索框显示关键词
      const keyword = e.target.dataset.keyword
      if (keyword) {
        // 先保存对话历史，再跳转
        saveChatHistory()
        closeChat()
        // 保存搜索关键词到localStorage，供搜索框读取
        localStorage.setItem('searchKeyword', keyword)
        window.location.href = `/products?keyword=${encodeURIComponent(keyword)}`
      }
    }
  }
}

// 监听路由变化，自动收起弹窗
import { watch } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
watch(() => route.path, () => {
  if (isOpen.value) {
    closeChat()
  }
})
</script>

<template>
  <div class="ai-chat-widget">
    <!-- 浇水茶壶（壶嘴在左侧，从右往左倒水） -->
    <div 
      v-show="isWatering && !isOpen" 
      class="teapot-watering"
      :style="{ left: `${position.x + 50}px`, top: `${position.y - 70}px` }"
    >
      <svg viewBox="0 0 80 60" class="teapot-svg">
        <!-- 茶壶身体 -->
        <ellipse cx="45" cy="35" rx="25" ry="20" fill="#8B4513"/>
        <!-- 茶壶盖 -->
        <rect x="30" y="12" width="30" height="6" rx="3" fill="#654321"/>
        <circle cx="45" cy="10" r="4" fill="#654321"/>
        <!-- 茶壶嘴（在左侧） -->
        <path d="M 25 30 Q 10 25 5 35 Q 10 40 20 38" fill="#8B4513"/>
        <!-- 茶壶把手（在右侧） -->
        <path d="M 70 25 Q 80 35 70 45" stroke="#654321" stroke-width="5" fill="none"/>
        <!-- 水流（从左侧壶嘴流出） -->
        <path class="water-stream" d="M 5 38 Q 8 50 15 60" stroke="#87CEEB" stroke-width="3" fill="none" stroke-linecap="round"/>
        <circle class="water-drop" cx="15" cy="58" r="3" fill="#87CEEB"/>
      </svg>
    </div>

    <!-- 茶宠容器（包含木质托盘和茶宠） -->
    <div 
      v-show="!isOpen"
      class="tea-pet-container"
      :style="ballStyle"
      @mousedown="onDragStart"
      @touchstart="onDragStart"
    >
      <!-- 云朵形提示框（在茶宠左侧） -->
      <transition name="tip-fade">
        <div v-show="showTip" class="cloud-tip">
          <svg viewBox="0 0 220 70" class="cloud-svg">
            <path d="M 25 55 Q 5 55 5 42 Q 5 28 25 28 Q 22 15 45 15 Q 65 3 105 15 Q 145 3 175 15 Q 200 8 208 28 Q 215 28 215 42 Q 215 55 195 55 Q 200 65 180 60 Z" fill="#8a9a5b" opacity="0.95"/>
            <!-- 小尾巴指向右边茶宠 -->
            <path d="M 205 42 L 220 38 L 212 48 Z" fill="#8a9a5b" opacity="0.95"/>
          </svg>
          <span class="cloud-text">{{ tipMessage }}</span>
        </div>
      </transition>

      <!-- 茶宠（会跳动） -->
      <div 
        ref="floatBall"
        class="float-ball" 
        :class="{ jumping: isJumping, dragging: isDragging }"
        @click.stop="handleBallClick"
      >
        <div class="tea-pet-icon">
          <svg viewBox="0 0 100 100" class="tea-pet-svg">
            <!-- 茶宠身体 -->
            <ellipse cx="50" cy="60" rx="35" ry="28" fill="#f5deb3"/>
            <!-- 茶宠头部 -->
            <circle cx="50" cy="35" r="25" fill="#f5deb3"/>
            <!-- 眼睛 -->
            <circle cx="40" cy="32" r="5" fill="#333"/>
            <circle cx="60" cy="32" r="5" fill="#333"/>
            <circle cx="42" cy="30" r="2" fill="#fff"/>
            <circle cx="62" cy="30" r="2" fill="#fff"/>
            <!-- 嘴巴微笑 -->
            <path d="M 42 42 Q 50 50 58 42" stroke="#333" stroke-width="2" fill="none" stroke-linecap="round"/>
            <!-- 腮红 -->
            <ellipse cx="32" cy="38" rx="5" ry="3" fill="#ffb6c1" opacity="0.6"/>
            <ellipse cx="68" cy="38" rx="5" ry="3" fill="#ffb6c1" opacity="0.6"/>
            <!-- 茶叶装饰 -->
            <ellipse cx="50" cy="18" rx="8" ry="4" fill="#8a9a5b" transform="rotate(-20, 50, 18)"/>
            <ellipse cx="58" cy="15" rx="6" ry="3" fill="#6a7a3b" transform="rotate(20, 58, 15)"/>
          </svg>
        </div>
      </div>

      <!-- 木质托盘（不跳动，在底部） -->
      <div class="wooden-tray">
        <svg viewBox="0 0 120 40" class="tray-svg">
          <defs>
            <linearGradient id="woodGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#6B4226"/>
              <stop offset="20%" style="stop-color:#8B5A2B"/>
              <stop offset="50%" style="stop-color:#A0522D"/>
              <stop offset="80%" style="stop-color:#8B4513"/>
              <stop offset="100%" style="stop-color:#5D3A1A"/>
            </linearGradient>
            <linearGradient id="woodShadow" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" style="stop-color:#8B4513"/>
              <stop offset="100%" style="stop-color:#4A2810"/>
            </linearGradient>
          </defs>
          <!-- 托盘底部阴影 -->
          <ellipse cx="60" cy="32" rx="55" ry="8" fill="#2A1A0A" opacity="0.3"/>
          <!-- 托盘侧面（立体感） -->
          <path d="M 5 25 Q 5 35 60 35 Q 115 35 115 25 L 115 20 Q 115 30 60 30 Q 5 30 5 20 Z" fill="url(#woodShadow)"/>
          <!-- 托盘主体顶面 -->
          <ellipse cx="60" cy="20" rx="55" ry="12" fill="url(#woodGradient)"/>
          <!-- 托盘内凹效果 -->
          <ellipse cx="60" cy="18" rx="48" ry="8" fill="#7A4A2A" opacity="0.4"/>
          <!-- 木纹装饰 -->
          <path d="M 20 18 Q 40 15 60 18 Q 80 21 100 18" stroke="#5D3A1A" stroke-width="0.5" fill="none" opacity="0.3"/>
          <path d="M 25 22 Q 45 19 65 22 Q 85 25 95 22" stroke="#5D3A1A" stroke-width="0.5" fill="none" opacity="0.3"/>
        </svg>
      </div>
    </div>

    <!-- 聊天窗口 -->
    <transition name="chat-slide">
      <div 
        v-show="isOpen" 
        ref="chatWindow"
        class="chat-window" 
        :class="{ dragging: isWindowDragging || isResizing }"
        :style="{ 
          ...chatWindowStyle,
          width: windowSize.width + 'px',
          height: windowSize.height + 'px',
          transform: `translate(${windowPosition.x}px, ${windowPosition.y}px)`
        }"
      >
        <div 
          class="chat-header"
          @mousedown="onWindowDragStart"
          @touchstart="onWindowDragStart"
        >
          <div class="header-info">
            <div class="avatar header-avatar">
              <svg viewBox="0 0 100 100" class="avatar-svg">
                <ellipse cx="50" cy="60" rx="35" ry="28" fill="#f5deb3"/>
                <circle cx="50" cy="35" r="25" fill="#f5deb3"/>
                <circle cx="40" cy="32" r="5" fill="#333"/>
                <circle cx="60" cy="32" r="5" fill="#333"/>
                <circle cx="42" cy="30" r="2" fill="#fff"/>
                <circle cx="62" cy="30" r="2" fill="#fff"/>
                <path d="M 42 42 Q 50 50 58 42" stroke="#333" stroke-width="2" fill="none" stroke-linecap="round"/>
                <ellipse cx="32" cy="38" rx="5" ry="3" fill="#ffb6c1" opacity="0.6"/>
                <ellipse cx="68" cy="38" rx="5" ry="3" fill="#ffb6c1" opacity="0.6"/>
                <ellipse cx="50" cy="18" rx="8" ry="4" fill="#8a9a5b" transform="rotate(-20, 50, 18)"/>
                <ellipse cx="58" cy="15" rx="6" ry="3" fill="#6a7a3b" transform="rotate(20, 58, 15)"/>
              </svg>
            </div>
            <div class="info">
              <div class="name">茶小韵</div>
              <div class="status">茗韵茶业智能顾问 · 拖动标题栏移动窗口</div>
            </div>
          </div>
          <div class="header-actions">
            <span v-if="historyLoaded" class="history-badge" title="已加载今日历史对话">📜</span>
            <el-icon class="close-btn" @click.stop="closeChat"><Close /></el-icon>
          </div>
        </div>

        <div class="chat-body" ref="chatBody">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="message"
            :class="msg.role"
          >
            <div class="msg-avatar" v-if="msg.role === 'assistant'">🍵</div>
            <div class="msg-content">
              <div class="msg-text" v-html="parseMessage(msg.content)" @click="handleKeywordClick"></div>
              <!-- 选项按钮 -->
              <div v-if="msg.showOptions && msg.options" class="option-buttons">
                <button 
                  v-for="(opt, optIndex) in msg.options" 
                  :key="optIndex"
                  class="option-btn"
                  @click="selectOption(opt.value, index)"
                >
                  {{ opt.label }}
                </button>
              </div>
            </div>
          </div>

          <div v-if="loading" class="message assistant">
            <div class="msg-avatar">🍵</div>
            <div class="msg-content">
              <div class="msg-text typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <div class="input-wrapper">
            <input 
              v-model="message"
              type="text"
              placeholder="请输入您的问题..."
              @keydown="handleKeydown"
              :disabled="loading"
            />
            <el-button 
              type="primary" 
              :icon="Position" 
              circle 
              size="small"
              @click="sendMessage"
              :loading="loading"
              class="send-btn"
            />
          </div>
          <div class="quick-questions">
            <span class="quick-btn" @click="message = '推荐一款适合送礼的茶叶'; sendMessage()">🎁 送礼推荐</span>
            <span class="quick-btn" @click="message = '有什么优惠活动吗'; sendMessage()">💰 优惠活动</span>
            <span class="quick-btn" @click="message = '茶叶怎么冲泡'; sendMessage()">🫖 冲泡方法</span>
          </div>
          <div class="nav-links">
            <a class="nav-link" @click="saveChatHistory(); $router.push('/products'); closeChat()">🛒 茶叶商城</a>
            <a class="nav-link" @click="saveChatHistory(); $router.push('/coupon/center'); closeChat()">🎫 领券中心</a>
            <a class="nav-link" @click="saveChatHistory(); $router.push('/user/wallet'); closeChat()">👤 会员中心</a>
          </div>
        </div>
        <!-- 边框调整区域 -->
        <div class="resize-edge resize-top" @mousedown="(e) => onResizeStart(e, 'top')"></div>
        <div class="resize-edge resize-right" @mousedown="(e) => onResizeStart(e, 'right')"></div>
        <div class="resize-edge resize-bottom" @mousedown="(e) => onResizeStart(e, 'bottom')"></div>
        <div class="resize-edge resize-left" @mousedown="(e) => onResizeStart(e, 'left')"></div>
        <div class="resize-corner resize-top-left" @mousedown="(e) => onResizeStart(e, 'top-left')"></div>
        <div class="resize-corner resize-top-right" @mousedown="(e) => onResizeStart(e, 'top-right')"></div>
        <div class="resize-corner resize-bottom-left" @mousedown="(e) => onResizeStart(e, 'bottom-left')"></div>
        <div class="resize-corner resize-bottom-right" @mousedown="(e) => onResizeStart(e, 'bottom-right')"></div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.ai-chat-widget {
  position: fixed;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  z-index: 9999;
  pointer-events: none;
}

/* 浇水茶壶 */
.teapot-watering {
  position: fixed;
  width: 80px;
  height: 60px;
  z-index: 9998;
  pointer-events: none;
  animation: teapot-pour 1.5s ease-in-out;
}

.teapot-svg {
  width: 100%;
  height: 100%;
}

.water-stream {
  animation: water-flow 0.5s ease-in-out infinite;
}

.water-drop {
  animation: drop-fall 0.5s ease-in-out infinite;
}

@keyframes teapot-pour {
  0% { transform: translateY(-20px) rotate(0deg); opacity: 0; }
  20% { transform: translateY(0) rotate(-15deg); opacity: 1; }
  80% { transform: translateY(0) rotate(-15deg); opacity: 1; }
  100% { transform: translateY(-20px) rotate(0deg); opacity: 0; }
}

@keyframes water-flow {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

@keyframes drop-fall {
  0% { transform: translateY(0); opacity: 1; }
  100% { transform: translateY(10px); opacity: 0; }
}

/* 茶宠容器 */
.tea-pet-container {
  position: fixed;
  display: flex;
  flex-direction: column;
  align-items: center;
  pointer-events: auto;
  cursor: grab;
}

/* 云朵形提示框 */
.cloud-tip {
  position: absolute;
  right: 70px;
  top: -5px;
  width: 220px;
  height: 70px;
  pointer-events: none;
}

.cloud-svg {
  width: 100%;
  height: 100%;
}

.cloud-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-55%, -55%);
  color: white;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

/* 木质托盘 */
.wooden-tray {
  width: 100px;
  height: 35px;
  margin-top: -10px;
}

.tray-svg {
  width: 100%;
  height: 100%;
}

.tip-fade-enter-active,
.tip-fade-leave-active {
  transition: all 0.3s ease;
}

.tip-fade-enter-from,
.tip-fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}

/* 悬浮小球（茶宠） */
.float-ball {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8a9a5b 0%, #6a7a3b 100%);
  box-shadow: 0 4px 20px rgba(138, 154, 91, 0.4);
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
  position: relative;
}

.float-ball:hover {
  box-shadow: 0 6px 25px rgba(138, 154, 91, 0.5);
}

.float-ball.dragging {
  cursor: grabbing;
  transform: scale(1.05);
  transition: none;
}

.float-ball.open {
  background: linear-gradient(135deg, #666 0%, #444 100%);
}

.float-ball.jumping {
  animation: jump 0.6s ease;
}

@keyframes jump {
  0%, 100% { transform: translateY(0); }
  25% { transform: translateY(-15px); }
  50% { transform: translateY(-5px); }
  75% { transform: translateY(-10px); }
}

.ball-icon {
  font-size: 28px;
  color: white;
}

.tea-pet-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tea-pet-svg {
  width: 100%;
  height: 100%;
}

.ball-tip {
  position: absolute;
  right: 70px;
  background: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  opacity: 0;
  transition: opacity 0.3s;
}

.float-ball:hover .ball-tip {
  opacity: 1;
}

/* 聊天窗口 */
.chat-window {
  position: fixed;
  bottom: 50px;
  right: 30px;
  min-width: 320px;
  min-height: 400px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  pointer-events: auto;
}

/* 边框调整区域 */
.resize-edge {
  position: absolute;
  background: transparent;
}

.resize-top {
  top: 0;
  left: 10px;
  right: 10px;
  height: 6px;
  cursor: ns-resize;
}

.resize-bottom {
  bottom: 0;
  left: 10px;
  right: 10px;
  height: 6px;
  cursor: ns-resize;
}

.resize-left {
  left: 0;
  top: 10px;
  bottom: 10px;
  width: 6px;
  cursor: ew-resize;
}

.resize-right {
  right: 0;
  top: 10px;
  bottom: 10px;
  width: 6px;
  cursor: ew-resize;
}

/* 角落调整区域 */
.resize-corner {
  position: absolute;
  width: 12px;
  height: 12px;
  background: transparent;
}

.resize-top-left {
  top: 0;
  left: 0;
  cursor: nwse-resize;
}

.resize-top-right {
  top: 0;
  right: 0;
  cursor: nesw-resize;
}

.resize-bottom-left {
  bottom: 0;
  left: 0;
  cursor: nesw-resize;
}

.resize-bottom-right {
  bottom: 0;
  right: 0;
  cursor: nwse-resize;
}

.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.3s ease;
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

/* 头部 */
.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #8a9a5b 0%, #6a7a3b 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.header-avatar {
  padding: 4px;
  overflow: hidden;
}

.avatar-svg {
  width: 100%;
  height: 100%;
}

.info .name {
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.info .status {
  color: rgba(255,255,255,0.8);
  font-size: 12px;
}

.close-btn {
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255,255,255,0.2);
}

/* 消息区域 */
.chat-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f8f9fa;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 32px;
  height: 32px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.msg-content {
  max-width: 70%;
}

.msg-text {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message.assistant .msg-text {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.message.user .msg-text {
  background: linear-gradient(135deg, #8a9a5b 0%, #6a7a3b 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

/* 关键词高亮链接 - AI回复中为绿色 */
.message.assistant :deep(.keyword-link) {
  color: #6a7a3b;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.2s;
}

.message.assistant :deep(.keyword-link:hover) {
  color: #8a9a5b;
}

/* 关键词高亮链接 - 用户消息中为白色 */
.message.user :deep(.keyword-link) {
  color: white;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
  transition: opacity 0.2s;
}

.message.user :deep(.keyword-link:hover) {
  opacity: 0.8;
}

/* 弹窗拖拽样式 */
.chat-window.dragging {
  transition: none;
  user-select: none;
}

.chat-header {
  cursor: move;
}

/* 打字动画 */
.typing {
  display: flex;
  gap: 4px;
  padding: 16px !important;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #ccc;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

/* 底部输入区 */
.chat-footer {
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #eee;
}

.input-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.input-wrapper input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.2s;
}

.input-wrapper input:focus {
  border-color: #8a9a5b;
}

.send-btn {
  background: linear-gradient(135deg, #8a9a5b 0%, #6a7a3b 100%) !important;
  border: none !important;
}

.quick-questions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-btn {
  padding: 6px 12px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #e0e0e0;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.history-badge {
  font-size: 16px;
  cursor: help;
}

.close-btn {
  font-size: 20px;
  color: white;
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
}

/* 选项按钮 */
.option-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.option-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #8a9a5b 0%, #6a7a3b 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 3px 10px rgba(138, 154, 91, 0.4);
}

/* 底部导航链接 */
.nav-links {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.nav-link {
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
  text-decoration: none;
}

.nav-link:hover {
  color: #8a9a5b;
}
</style>
