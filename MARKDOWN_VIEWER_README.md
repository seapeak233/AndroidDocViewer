# Markdown 预览器实现说明

## 概述
基于现有的文档预览器架构，我成功实现了一个功能完整的 Markdown 预览器，仿照项目中其他 viewer 的结构和设计风格。

## 实现的文件

### 1. 核心预览器页面
**文件位置**: `docviewer/src/main/assets/markdown/viewer.html`

**功能特点**:
- 🎨 **GitHub 风格样式**: 使用类似 GitHub 的 Markdown 渲染样式
- 🔄 **双模式预览**: 支持渲染预览模式和源码查看模式
- 📱 **响应式设计**: 适配移动端和桌面端
- 📋 **复制功能**: 支持复制渲染后的文本或原始源码
- 💾 **下载功能**: 支持下载原始 Markdown 文件
- ℹ️ **文件信息**: 显示文件大小、字符数等信息

### 2. JavaScript 库
**文件位置**: `docviewer/src/main/assets/markdown/markdown-it.min.js`
- 使用 `markdown-it` 库进行 Markdown 到 HTML 的转换
- 支持表格、代码块、链接等完整的 Markdown 语法

### 3. 示例文件
**文件位置**: `app/src/main/assets/sample.md`
- 创建了一个功能完整的 Markdown 示例文档
- 展示了各种 Markdown 语法的渲染效果

## 技术架构

### HTML 结构
```html
<!DOCTYPE html>
<html>
<head>
    <!-- 样式定义 -->
</head>
<body>
    <!-- 工具栏 -->
    <div class="toolbar">
        <div class="toolbar-left">
            <h2>Markdown 预览器</h2>
        </div>
        <div class="toolbar-right">
            <button onclick="toggleView()">切换到源码</button>
            <button onclick="copyContent()">复制内容</button>
            <button onclick="downloadFile()">下载</button>
        </div>
    </div>
    
    <!-- 内容区域 -->
    <div class="container">
        <div class="content-wrapper">
            <!-- 文件信息 -->
            <div class="file-info">...</div>
            
            <!-- Markdown 渲染内容 -->
            <div id="markdownContent" class="markdown-content">
                <!-- 动态渲染的内容 -->
            </div>
        </div>
    </div>
    
    <!-- JavaScript 逻辑 -->
    <script src="markdown-it.min.js"></script>
    <script>
        // MarkdownViewer 类实现
    </script>
</body>
</html>
```

### JavaScript 核心类

```javascript
class MarkdownViewer {
    constructor() {
        // 初始化 markdown-it 实例
        this.md = window.markdownit({
            html: true,         // 启用 HTML 标签
            breaks: true,       // 转换换行到 <br>
            linkify: true,      // 自动转换链接
            typographer: true,  // 启用排版替换
        });
    }
    
    // 核心方法
    loadFile()          // 加载文件
    renderMarkdown()    // 渲染 Markdown
    toggleView()        // 切换预览/源码模式
    copyContent()       // 复制内容
    downloadFile()      // 下载文件
}
```

### CSS 样式特点

1. **GitHub 风格**: 参考 GitHub 的 Markdown 渲染样式
2. **响应式设计**: 支持移动端和桌面端适配
3. **美观的排版**: 合理的间距、字体和颜色搭配
4. **代码高亮**: 代码块使用单独的样式

## 支持的 Markdown 语法

- ✅ **标题** (H1-H6)
- ✅ **文本格式** (粗体、斜体、删除线)
- ✅ **列表** (有序和无序列表)
- ✅ **代码** (行内代码和代码块)
- ✅ **引用** (单行和多行引用)
- ✅ **链接** (文本链接和自动链接)
- ✅ **表格** (支持对齐)
- ✅ **分割线**
- ✅ **图片** (本地和网络图片)
- ✅ **HTML 标签** (部分安全的 HTML)

## 使用方法

### 在 Android 应用中使用

```kotlin
// 创建 DocConfig
val docConfig = DocConfig(
    url = "file:///android_asset/sample.md",
    type = DocType.MARKDOWN
)

// 创建 Fragment
val fragment = DocViewerFragment.newInstance(docConfig)

// 添加到 Activity
supportFragmentManager.beginTransaction()
    .replace(R.id.container, fragment)
    .commit()
```

### 配置要求

1. 确保 `markdown-it.min.js` 文件在 `assets/markdown/` 目录中
2. 确保 `viewer.html` 文件在 `assets/markdown/` 目录中
3. DocViewerFragment 已支持 `DocType.MARKDOWN`

## 文件结构

```
docviewer/src/main/assets/markdown/
├── viewer.html           # 主预览页面
└── markdown-it.min.js    # Markdown 解析库

app/src/main/assets/
├── sample.md            # 示例 Markdown 文件
└── sample.txt           # 示例文本文件
```

## 特色功能

### 1. 双模式预览
- **渲染模式**: 显示美观的 HTML 渲染结果
- **源码模式**: 显示原始 Markdown 代码，便于编辑和调试

### 2. 工具栏功能
- **模式切换**: 一键切换预览和源码模式
- **复制功能**: 智能复制，根据当前模式复制相应内容
- **下载功能**: 直接下载原始 Markdown 文件

### 3. 文件信息显示
- 文件名
- 文件大小 (KB)
- 字符数
- 编码格式

### 4. 样式优化
- GitHub 风格的渲染样式
- 代码块语法高亮支持
- 表格美化
- 响应式布局

## 扩展建议

1. **语法高亮**: 可以集成 Prism.js 或 highlight.js 实现代码语法高亮
2. **目录导航**: 自动生成文档目录树
3. **全文搜索**: 增加搜索功能，类似文本预览器
4. **主题切换**: 支持明暗主题切换
5. **数学公式**: 集成 MathJax 支持数学公式渲染

## 兼容性

- ✅ Android WebView
- ✅ 现代浏览器 (Chrome, Firefox, Safari, Edge)
- ✅ 移动端浏览器
- ✅ 支持文件协议 (file://)

这个 Markdown 预览器完全遵循了项目的架构设计，与现有的其他文档预览器保持一致的用户体验和代码风格。
