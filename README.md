# AndroidDocViewer

[English](README_EN.md) | 简体中文

[![JitPack](https://jitpack.io/v/seapeak233/AndroidDocViewer.svg)](https://jitpack.io/#seapeak233/AndroidDocViewer)

Android 文档本地预览解决方案

## 🎉 本次更新

### 新增 Activity 启动方式
不想自己包一层 Activity？现在可以直接用 `DocViewerActivity`：
- 一行代码启动预览
- 自动识别文件类型
- 带标题栏、返回按钮、更多菜单
- 支持第三方应用打开和下载到本地
- 可自定义标题栏颜色和功能开关

## 项目背景

自从腾讯 X5 的 TBSReader 收费后，寻找可用的原生离线文档预览方案成为了一个挑战。经过调研发现，各类原生方案都存在不同程度的局限性。

相比之下，JavaScript 生态系统在文档预览方面拥有更成熟的解决方案，且 WebView 天然支持缩放等交互行为，可以显著减少原生手势处理的开发工作量。

**核心思路**：将成熟的 JavaScript 文档预览方案打包为 HTML + CSS + JS，存放在 Android assets 目录中，通过 WebView 加载本地资源实现文档预览。

## 功能特性

### 🆕 新增 Activity 直接启动
- 无需手动创建 Fragment 和 Activity 容器
- 根据文件扩展名自动选择预览器
- 支持标题栏颜色、功能按钮等配置
- 内置第三方应用打开、文档下载功能

### 已支持的文档格式
- **PDF** - 基于 PDF.js，支持缩放、翻页
- **Word** (.docx) - 基于 docxjs，保持原始格式
- **PowerPoint** (.pptx) - 基于 pptx-preview，支持动画效果
- **Excel** (.xlsx) - 基于 SheetJS，支持多工作表
- **TXT** - 纯文本预览，支持编码自动识别
- **Markdown** - 基于 markdown-it，支持语法高亮

### 集成方式
- ✅ **Activity 直接启动**（推荐，本次更新重点）
- ✅ **Fragment 容器**
- ⏳ View 组件（计划中）

## 快速开始

### 添加依赖

```gradle
implementation 'com.github.seapeak233:AndroidDocViewer:<Tag>'
```

### 🚀 极简使用（推荐）

只需一行代码即可启动文档预览：

```kotlin
// 自动识别文件类型并启动预览
DocViewerActivity.startWithFile(context, "/path/to/document.pdf", "我的PDF文档")
```

### 基本用法

#### 方式一：Activity 直接启动 🆕

```kotlin
// 1. 快速启动（推荐）
DocViewerActivity.startWithFile(context, "/storage/emulated/0/Download/report.xlsx", "月度报表")

// 2. 使用 DocConfig 启动
val docConfig = DocConfig("file:///android_asset/sample.docx", DocType.WORD)
DocViewerActivity.start(context, docConfig, "Word文档")

// 3. 从文件路径创建配置
val docConfig = DocConfig.fromFile("/path/to/presentation.pptx")
DocViewerActivity.start(context, docConfig!!, "演示文稿")
```

#### 方式二：Fragment 嵌入

```kotlin
val config = DocConfig("file:///android_asset/sample.xlsx", DocType.EXCEL)
val fragment = DocViewerFragment.newInstance(config)

supportFragmentManager.beginTransaction()
    .replace(R.id.container, fragment)
    .commit()
```

## 🎨 个性化定制

### 自定义主题样式

```kotlin
val docConfig = DocConfig.fromFile("/path/to/document.pdf")!!

val pageConfig = DocPageConfig(
    docConfig = docConfig,
    title = "企业文档预览",
    statusBarColor = Color.parseColor("#FF2196F3"),    // 状态栏颜色
    toolbarColor = Color.parseColor("#FF2196F3"),      // 工具栏颜色
    titleTextColor = Color.WHITE,                      // 标题文字颜色
    iconTintColor = Color.WHITE,                       // 图标颜色
    showBackButton = true,                             // 显示返回按钮
    showMoreMenu = true,                               // 显示更多菜单
    enableThirdPartyOpen = true,                       // 启用第三方应用打开
    enableDownload = true,                             // 启用下载功能
    downloadToPublicDir = true                         // 下载到公共Download文件夹
)

DocViewerActivity.start(context, pageConfig)
```

### 快速配置

```kotlin
val docConfig = DocConfig.fromFile("/path/to/document.pdf")!!

// 深色配置
val darkConfig = DocPageConfig.createDarkTheme(docConfig, "文档预览")
DocViewerActivity.start(context, darkConfig)

// 浅色配置
val lightConfig = DocPageConfig.createLightTheme(docConfig, "文档预览")
DocViewerActivity.start(context, lightConfig)

// 简洁模式（无更多菜单）
val simpleConfig = DocPageConfig.createSimple(docConfig, "只读文档")
DocViewerActivity.start(context, simpleConfig)
```

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `title` | 标题栏显示的文字 | 根据文档类型自动生成 |
| `statusBarColor` | 状态栏背景色 | `#FF6200EE` |
| `toolbarColor` | 工具栏背景色 | `#FF6200EE` |
| `titleTextColor` | 标题文字颜色 | `Color.WHITE` |
| `iconTintColor` | 图标着色 | `Color.WHITE` |
| `showBackButton` | 是否显示返回按钮 | `true` |
| `showMoreMenu` | 是否显示更多菜单 | `true` |
| `enableThirdPartyOpen` | 是否启用第三方应用打开 | `true` |
| `enableDownload` | 是否启用下载功能 | `true` |
| `downloadToPublicDir` | 下载位置选择 | `true`(Download文件夹) |

## 📋 使用场景示例

### 场景一：企业文档管理（禁用下载和分享）

```kotlin
val pageConfig = DocPageConfig(
    docConfig = DocConfig.fromFile("/path/to/confidential.pdf")!!,
    title = "机密文档",
    showMoreMenu = false,           // 隐藏更多菜单
    enableThirdPartyOpen = false,   // 禁止第三方打开
    enableDownload = false          // 禁止下载
)
DocViewerActivity.start(context, pageConfig)
```

### 场景二：本地文件预览

```kotlin
// 预览本地下载的文件
val localFile = "/storage/emulated/0/Download/report.xlsx"
DocViewerActivity.startWithFile(context, localFile, "本地报表")
```

### 场景三：应用内帮助文档

```kotlin
// 从 assets 加载 Markdown 帮助文档
val docConfig = DocConfig.fromAssets("help.md", DocType.MARKDOWN)
val pageConfig = DocPageConfig.createSimple(docConfig, "使用帮助")
DocViewerActivity.start(context, pageConfig)
```

### 场景四：自定义品牌色

```kotlin
val pageConfig = DocPageConfig(
    docConfig = docConfig,
    title = "品牌文档",
    statusBarColor = Color.parseColor("#FF9C27B0"),  // 品牌紫色
    toolbarColor = Color.parseColor("#FF9C27B0"),
    titleTextColor = Color.WHITE,
    iconTintColor = Color.WHITE
)
DocViewerActivity.start(context, pageConfig)
```

## 📖 支持的文档类型

| 文档类型 | 枚举值 | 支持的扩展名 | 说明 |
|---------|--------|-------------|------|
| PDF | `DocType.PDF` | .pdf | 支持缩放、翻页 |
| Word | `DocType.WORD` | .doc, .docx | 保持原始格式 |
| Excel | `DocType.EXCEL` | .xls, .xlsx | 支持多工作表 |
| PowerPoint | `DocType.PPT` | .ppt, .pptx | 支持动画效果 |
| 文本 | `DocType.TXT` | .txt | 编码自动识别 |
| Markdown | `DocType.MARKDOWN` | .md | 支持语法高亮 |

## 💡 更多示例

完整的使用示例请参考：
- [MainActivity](https://github.com/seapeak233/AndroidDocViewer/blob/main/app/src/main/java/com/seapeak/example/MainActivity.kt) - Fragment 使用示例
- [PreviewActivity](https://github.com/seapeak233/AndroidDocViewer/blob/main/app/src/main/java/com/seapeak/example/PreviewActivity.kt) - Activity 使用示例
- [ActivityUsageExample](https://github.com/seapeak233/AndroidDocViewer/blob/main/docviewer/src/main/java/com/seapeak/docviewer/example/ActivityUsageExample.kt) - 各种场景的完整代码示例

## 预览效果

| Excel | PDF |
|-------|-----|
| ![Excel预览](static/excel_shot.png) | ![PDF预览](static/pdf_shot.png) |

| PowerPoint | Word |
|------------|------|
| ![PPT预览](static/ppt_shot.png) | ![Word预览](static/word_shot.png) |

## 版本更新

### 最新版本
- 新增 `DocViewerActivity` 直接启动方式
- 新增 `DocPageConfig` 页面配置选项
- 新增第三方应用打开功能
- 新增文档下载到本地功能
- 优化文件类型自动识别

### 迁移说明
之前使用 Fragment 的方式仍然支持，现在可以选择更简单的 Activity 方式：

```kotlin
// 之前的方式（仍然支持）
val fragment = DocViewerFragment.newInstance(docConfig)

// 新增的方式
DocViewerActivity.startWithFile(context, filePath, title)
```

## 🛠 技术依赖

本项目基于以下优秀的开源项目构建：

| 组件 | 用途 | 版本 | 仓库地址 |
|------|------|------|----------|
| SheetJS | Excel 文件解析 | 0.20.3 | [GitHub](https://github.com/SheetJS/sheetjs) |
| PDF.js | PDF 文件渲染 | 4.0.269 | [GitHub](https://github.com/mozilla/pdf.js) |
| pptx-preview | PowerPoint 预览 | 1.0.6 | [GitHub](https://github.com/501351981/pptx-preview) |
| docxjs | Word 文档解析 | 0.3.6 | [GitHub](https://github.com/VolodymyrBaydalka/docxjs) |
| markdown-it | Markdown 渲染 | 14.1.0 | [GitHub](https://github.com/markdown-it/markdown-it) |

## 许可证

本项目采用开源许可证，具体信息请查看 [LICENSE](LICENSE) 文件。
