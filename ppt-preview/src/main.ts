/**
 * Vue 应用程序入口文件
 * 初始化 Vue 应用并挂载到 DOM
 */
import { createApp } from 'vue'
import App from './App.vue'
import './style.css'

// 创建 Vue 应用实例并挂载到 #app 元素
createApp(App).mount('#app')
