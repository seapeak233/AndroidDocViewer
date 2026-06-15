/**
 * Vite 环境类型声明文件
 * 定义 Vue 单文件组件和 CSS 模块的类型
 */

/// <reference types="vite/client" />

// 声明 Vue 单文件组件模块
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 声明 CSS 模块
declare module '*.css' {
  const content: string
  export default content
}

// 声明 PPTX-Preview 模块类型
declare module 'pptx-preview' {
  export interface PreviewerOptions {
    width?: number
    height?: number
    [key: string]: any
  }
  
  export interface PreviewResult {
    totalSlides?: number
    [key: string]: any
  }
  
  export interface PptxViewer {
    preview(arrayBuffer: ArrayBuffer): Promise<PreviewResult>
    goToSlide?(slideIndex: number): void
    destroy?(): void
    [key: string]: any
  }
  
  export function init(container: HTMLElement, options?: PreviewerOptions): PptxViewer
}
