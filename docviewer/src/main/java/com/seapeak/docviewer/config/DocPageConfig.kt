package com.seapeak.docviewer.config

import android.graphics.Color
import androidx.annotation.ColorInt
import java.io.Serializable

/**
 * 文档页面配置类
 * 用于配置 DocViewerActivity 的标题栏样式和功能
 */
data class DocPageConfig(
    val docConfig: DocConfig,
    val title: String? = null,
    @ColorInt val statusBarColor: Int = Color.parseColor("#FF6200EE"),
    @ColorInt val toolbarColor: Int = Color.parseColor("#FF6200EE"),
    @ColorInt val titleTextColor: Int = Color.WHITE,
    @ColorInt val iconTintColor: Int = Color.WHITE,
    val showBackButton: Boolean = true,
    val showMoreMenu: Boolean = true,
    val enableThirdPartyOpen: Boolean = true,
    val enableDownload: Boolean = true,
    val downloadToPublicDir: Boolean = true // true: Download文件夹, false: 应用私有目录
) : Serializable {
    
    companion object {
        /**
         * 创建默认配置
         */
        fun createDefault(docConfig: DocConfig, title: String? = null): DocPageConfig {
            return DocPageConfig(
                docConfig = docConfig,
                title = title
            )
        }
        
        /**
         * 创建深色主题配置
         */
        fun createDarkTheme(docConfig: DocConfig, title: String? = null): DocPageConfig {
            return DocPageConfig(
                docConfig = docConfig,
                title = title,
                statusBarColor = Color.parseColor("#FF121212"),
                toolbarColor = Color.parseColor("#FF1F1F1F"),
                titleTextColor = Color.WHITE,
                iconTintColor = Color.WHITE
            )
        }
        
        /**
         * 创建浅色主题配置
         */
        fun createLightTheme(docConfig: DocConfig, title: String? = null): DocPageConfig {
            return DocPageConfig(
                docConfig = docConfig,
                title = title,
                statusBarColor = Color.parseColor("#FFFFFFFF"),
                toolbarColor = Color.parseColor("#FFFFFFFF"),
                titleTextColor = Color.parseColor("#FF212121"),
                iconTintColor = Color.parseColor("#FF757575")
            )
        }
        
        /**
         * 创建简洁配置（无更多菜单）
         */
        fun createSimple(docConfig: DocConfig, title: String? = null): DocPageConfig {
            return DocPageConfig(
                docConfig = docConfig,
                title = title,
                showMoreMenu = false,
                enableThirdPartyOpen = false,
                enableDownload = false
            )
        }
    }
}