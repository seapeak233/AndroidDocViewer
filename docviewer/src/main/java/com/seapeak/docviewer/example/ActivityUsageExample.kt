package com.seapeak.docviewer.example

import android.content.Context
import android.graphics.Color
import com.seapeak.docviewer.DocViewerActivity
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocPageConfig
import com.seapeak.docviewer.config.DocType

/**
 * DocViewerActivity 使用示例
 */
object ActivityUsageExample {
    
    /**
     * 基本使用 - 使用默认配置
     */
    fun basicUsage(context: Context) {
        val docConfig = DocConfig("file:///storage/emulated/0/Download/sample.pdf", DocType.PDF)
        DocViewerActivity.start(context, docConfig, "我的PDF文档")
    }
    
    /**
     * 从文件路径快速启动
     */
    fun quickStart(context: Context) {
        val filePath = "/storage/emulated/0/Download/sample.xlsx"
        DocViewerActivity.startWithFile(context, filePath, "Excel报表")
    }
    
    /**
     * 自定义主题配置
     */
    fun customTheme(context: Context) {
        val docConfig = DocConfig("file:///android_asset/sample.docx", DocType.WORD)
        
        val pageConfig = DocPageConfig(
            docConfig = docConfig,
            title = "Word文档预览",
            statusBarColor = Color.parseColor("#FF2196F3"),
            toolbarColor = Color.parseColor("#FF2196F3"),
            titleTextColor = Color.WHITE,
            iconTintColor = Color.WHITE,
            showBackButton = true,
            showMoreMenu = true,
            enableThirdPartyOpen = true,
            enableDownload = true,
            downloadToPublicDir = true
        )
        
        DocViewerActivity.start(context, pageConfig)
    }
    
    /**
     * 深色主题
     */
    fun darkTheme(context: Context) {
        val docConfig = DocConfig.fromFile("/path/to/document.pdf")!!
        val pageConfig = DocPageConfig.createDarkTheme(docConfig, "深色主题预览")
        DocViewerActivity.start(context, pageConfig)
    }
    
    /**
     * 浅色主题
     */
    fun lightTheme(context: Context) {
        val docConfig = DocConfig.fromAssets("sample.pptx", DocType.PPT)
        val pageConfig = DocPageConfig.createLightTheme(docConfig, "浅色主题预览")
        DocViewerActivity.start(context, pageConfig)
    }
    
    /**
     * 简洁模式（无更多菜单）
     */
    fun simpleMode(context: Context) {
        val docConfig = DocConfig("file:///android_asset/readme.md", DocType.MARKDOWN)
        val pageConfig = DocPageConfig.createSimple(docConfig, "只读模式")
        DocViewerActivity.start(context, pageConfig)
    }
    
    /**
     * 自定义颜色配置
     */
    fun customColors(context: Context) {
        val docConfig = DocConfig.fromFile("/path/to/spreadsheet.xlsx")!!
        
        val pageConfig = DocPageConfig(
            docConfig = docConfig,
            title = "自定义颜色",
            statusBarColor = Color.parseColor("#FF9C27B0"), // 紫色
            toolbarColor = Color.parseColor("#FF9C27B0"),
            titleTextColor = Color.WHITE,
            iconTintColor = Color.WHITE,
            showBackButton = true,
            showMoreMenu = true,
            enableThirdPartyOpen = false, // 禁用第三方打开
            enableDownload = true,
            downloadToPublicDir = false // 下载到应用私有目录
        )
        
        DocViewerActivity.start(context, pageConfig)
    }
    
    /**
     * 企业版配置（禁用下载和第三方打开）
     */
    fun enterpriseMode(context: Context) {
        val docConfig = DocConfig("file:///path/to/confidential.pdf", DocType.PDF)
        
        val pageConfig = DocPageConfig(
            docConfig = docConfig,
            title = "机密文档",
            statusBarColor = Color.parseColor("#FF424242"),
            toolbarColor = Color.parseColor("#FF424242"),
            titleTextColor = Color.WHITE,
            iconTintColor = Color.WHITE,
            showBackButton = true,
            showMoreMenu = false, // 禁用更多菜单
            enableThirdPartyOpen = false,
            enableDownload = false
        )
        
        DocViewerActivity.start(context, pageConfig)
    }
}