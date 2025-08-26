# Markdown 文档示例

这是一个 **Markdown** 文档的示例，用于测试文档预览器的功能。

## 功能特点

我们的 Markdown 预览器支持以下功能：

- ✅ **实时预览**：支持 Markdown 到 HTML 的实时渲染
- ✅ **源码查看**：可以切换查看原始 Markdown 源码
- ✅ **复制功能**：支持复制渲染后的文本或源码
- ✅ **下载功能**：支持下载原始文件
- ✅ **响应式设计**：适配移动端和桌面端

## 语法支持

### 1. 标题

支持 1-6 级标题：

```markdown
# 一级标题
## 二级标题
### 三级标题
#### 四级标题
##### 五级标题
###### 六级标题
```

### 2. 文本格式

- **粗体文本**
- *斜体文本*
- ~~删除线~~
- `行内代码`

### 3. 列表

#### 无序列表
- 项目 1
- 项目 2
  - 子项目 2.1
  - 子项目 2.2
- 项目 3

#### 有序列表
1. 第一项
2. 第二项
3. 第三项

### 4. 代码块

```javascript
// JavaScript 示例代码
function helloWorld() {
    console.log("Hello, World!");
    return true;
}

const result = helloWorld();
```

```python
# Python 示例代码
def hello_world():
    print("Hello, World!")
    return True

result = hello_world()
```

### 5. 引用

> 这是一个引用文本。
> 
> 支持多行引用。
> 
> > 嵌套引用也是支持的。

### 6. 链接和图片

- [GitHub](https://github.com)
- [Google](https://google.com)

### 7. 表格

| 功能 | 支持情况 | 说明 |
|------|----------|------|
| Markdown 渲染 | ✅ | 基于 markdown-it |
| 语法高亮 | ✅ | 支持多种语言 |
| 表格渲染 | ✅ | 支持复杂表格 |
| 图片显示 | ✅ | 支持本地和网络图片 |

### 8. 分割线

---

## 技术实现

本 Markdown 预览器使用以下技术：

1. **markdown-it**：用于 Markdown 到 HTML 的转换
2. **WebView**：用于在 Android 应用中显示 HTML 内容
3. **CSS**：提供 GitHub 风格的样式
4. **JavaScript**：处理用户交互和功能逻辑

## 使用方法

```kotlin
// 在 Android 应用中使用
val docConfig = DocConfig(
    url = "file:///android_asset/sample.md",
    type = DocType.MARKDOWN
)

val fragment = DocViewerFragment.newInstance(docConfig)
```

## 注意事项

1. 确保 `markdown-it.min.js` 文件已正确放置在 assets 目录中
2. 文件路径需要使用正确的 URL 格式
3. 图片路径建议使用相对路径或完整的 URL

---

*这个示例展示了 Markdown 预览器的主要功能和语法支持。*

**最后更新时间：** 2024年12月

