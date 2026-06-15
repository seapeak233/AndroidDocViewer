<template>
  <div class="app">
    <!-- PPTX 预览容器 -->
    <div class="preview-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-overlay">
        <div class="spinner"></div>
      </div>
      <div ref="pptxContainer" class="pptx-container"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { init } from 'pptx-preview'

/**
 * 组件状态管理
 */
const pptxContainer = ref<HTMLDivElement>()
const selectedFile = ref<File | null>(null)
const loading = ref(false)
const currentSlide = ref(1)
const totalSlides = ref(0)

// PPTX 预览器实例
let pptxViewer: any = null

/**
 * 检查是否有URL参数传递的文件
 */
const checkAutoLoad = () => {
  const params = new URLSearchParams(window.location.search)
  const fileUrl = params.get('file')
  
  if (fileUrl) {
    loadFileFromUrl(fileUrl)
  }
}

/**
 * 从URL加载文件
 */
const loadFileFromUrl = async (url: string) => {
  try {
    loading.value = true
    
    console.log('从URL加载文件:', url)
    
    // 支持file://协议和http/https协议
    let arrayBuffer: ArrayBuffer
    
    if (url.startsWith('file://')) {
      // 使用XMLHttpRequest处理file://协议
      arrayBuffer = await loadFileWithXHR(url)
    } else {
      // 使用fetch处理http/https协议
      const response = await fetch(url)
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      arrayBuffer = await response.arrayBuffer()
    }
    
    // 创建File对象
    const fileName = url.split('/').pop() || 'document.pptx'
    const file = new File([arrayBuffer], fileName, {
      type: 'application/vnd.openxmlformats-officedocument.presentationml.presentation'
    })
    
    selectedFile.value = file
    
    // 自动开始预览
    await previewFile()
    
  } catch (err) {
    console.error('URL文件加载失败:', err)
    // 显示错误信息在容器中
    if (pptxContainer.value) {
      pptxContainer.value.innerHTML = `
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #666;">
          <div style="font-size: 48px; margin-bottom: 20px;">❌</div>
          <h3 style="margin-bottom: 15px; color: #333;">加载失败</h3>
          <p>${err instanceof Error ? err.message : '未知错误'}</p>
        </div>
      `
    }
  } finally {
    loading.value = false
  }
}

/**
 * 使用XMLHttpRequest加载file://协议的文件
 */
const loadFileWithXHR = (url: string): Promise<ArrayBuffer> => {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    
    xhr.onload = function() {
      if (xhr.status === 0 || xhr.status === 200) { // file://协议返回0
        resolve(xhr.response)
      } else {
        reject(new Error(`HTTP ${xhr.status}: ${xhr.statusText}`))
      }
    }
    
    xhr.onerror = function() {
      reject(new Error('文件加载失败'))
    }
    
    xhr.open('GET', url)
    xhr.responseType = 'arraybuffer'
    xhr.send()
  })
}





/**
 * 使用 FileReader 读取文件为 ArrayBuffer
 */
const readFileAsArrayBuffer = (file: File): Promise<ArrayBuffer> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (event) => {
      const arrayBuffer = event.target?.result as ArrayBuffer
      if (arrayBuffer && arrayBuffer.byteLength > 0) {
        resolve(arrayBuffer)
      } else {
        reject(new Error('文件读取失败或文件为空'))
      }
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取过程中发生错误'))
    }
    
    reader.readAsArrayBuffer(file)
  })
}

/**
 * 预览文件
 */
const previewFile = async () => {
  if (!selectedFile.value) {
    return
  }

  try {
    console.log('开始读取文件:', selectedFile.value.name, '大小:', selectedFile.value.size, 'bytes')
    
    // 使用 FileReader 读取文件为 ArrayBuffer
    const arrayBuffer = await readFileAsArrayBuffer(selectedFile.value)
    
    console.log('文件读取成功，ArrayBuffer 大小:', arrayBuffer.byteLength, 'bytes')
    
    // 等待 DOM 更新
    await new Promise(resolve => setTimeout(resolve, 200))
    
    if (pptxContainer.value) {
      console.log('初始化 PPTX 预览器...')
      
      // 清理之前的预览器实例
      if (pptxViewer) {
        try {
          pptxViewer.destroy?.()
        } catch (e) {
          console.warn('清理预览器时出错:', e)
        }
        pptxViewer = null
      }
      
      // 清空容器
      const container = pptxContainer.value
      container.innerHTML = ''
      
      // 再次等待确保容器完全渲染
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 检查容器尺寸
      const rect = container.getBoundingClientRect()
      console.log('容器尺寸:', rect.width, 'x', rect.height)
      
      try {
        // 初始化 PPTX 预览器
        pptxViewer = init(container, {
          width: rect.width,
          height: rect.height
        })
        
        console.log('预览器初始化完成，开始加载文件...')
        
        // 加载并预览文件
        const result = await pptxViewer.preview(arrayBuffer)
        
        console.log('文件预览完成，结果:', result)
        
        // 更新幻灯片信息
        totalSlides.value = result?.totalSlides || result?.slides?.length || 1
        currentSlide.value = 1
        
        console.log('幻灯片总数:', totalSlides.value)
        
      } catch (initError) {
        console.error('预览器初始化失败:', initError)
        
        // 显示错误信息
        container.innerHTML = `
          <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #666;">
            <div style="font-size: 48px; margin-bottom: 20px;">📄</div>
            <h3 style="margin-bottom: 15px; color: #333;">PPTX 文件已加载</h3>
            <p>文件包含不支持的复杂元素</p>
            <p style="margin-top: 20px; font-size: 14px; color: #999;">
              文件大小: ${(arrayBuffer.byteLength / 1024 / 1024).toFixed(2)} MB
            </p>
          </div>
        `
      }
    }
  } catch (err) {
    console.error('预览文件时出错:', err)
    
    // 显示错误信息在容器中
    if (pptxContainer.value) {
      pptxContainer.value.innerHTML = `
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; color: #666;">
          <div style="font-size: 48px; margin-bottom: 20px;">❌</div>
          <h3 style="margin-bottom: 15px; color: #333;">预览失败</h3>
          <p>${err instanceof Error ? err.message : '未知错误'}</p>
        </div>
      `
    }
  } finally {
    loading.value = false
  }
}



/**
 * 组件挂载时的初始化
 */
onMounted(() => {
  console.log('PPTX 预览器组件已加载')
  
  // 检查是否需要自动加载文件
  checkAutoLoad()
})
</script>

<style scoped>
/* 组件样式在下一个文件中定义 */
</style>
