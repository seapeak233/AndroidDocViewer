# ppt-preview

`ppt-preview` 是 AndroidDocViewer 中 PowerPoint 预览资源的前端壳工程，用来生成可放入 Android `assets` 的静态 HTML、CSS 和 JS 产物。

主库运行时不会直接依赖这个目录。Android 端实际加载的是：

```text
docviewer/src/main/assets/ppt/viewer.html
```

这个工程的职责是把 `pptx-preview` 封装成一个适合 WebView 加载的页面，并通过 URL 参数接收待预览文件：

```text
viewer.html?file=<encoded-file-url>
```

## 工作方式

页面启动后会读取 `file` 参数，拉取对应文件的二进制内容，然后调用 `pptx-preview` 渲染到全屏容器中。

当前入口文件：

```text
src/App.vue
```

核心流程：

1. 读取 `window.location.search` 中的 `file` 参数。
2. 对 `file://` 使用 `XMLHttpRequest` 读取，对 `http/https` 使用 `fetch` 读取。
3. 将文件内容转成 `ArrayBuffer`。
4. 初始化 `pptx-preview`，把幻灯片渲染到页面容器。

Android 端需要在 WebView 中开启本地文件访问能力，并拦截 `file://`、`content://` 等资源请求，把真实文件流返回给前端页面。如果要直接传 `content://`，前端也应显式使用 `XMLHttpRequest` 处理，避免部分 WebView 对 `fetch(content://...)` 支持不稳定。

## 本地开发

安装依赖：

```bash
npm install
```

启动开发服务：

```bash
npm run dev
```

开发时可以通过查询参数传入一个可访问的 PPTX 文件地址：

```text
http://localhost:5173/?file=http://localhost:5173/sample.pptx
```

如果直接传本地文件路径，需要注意浏览器和 WebView 对 `file://` 的访问限制不同，本地浏览器测试通过不代表 Android WebView 一定可用。

## 构建产物

生成静态文件：

```bash
npm run build
```

构建完成后，将 `dist` 中生成的 HTML、CSS、JS 文件同步到：

```text
../docviewer/src/main/assets/ppt/
```

并确保入口 HTML 文件名为：

```text
viewer.html
```

Android 端的 `DocViewerFragment` 当前固定加载 `file:///android_asset/ppt/viewer.html`，所以入口文件名不能随意改变。

## 发布前检查

更新 PPT 预览资源时，建议至少检查这些点：

1. `docviewer/src/main/assets/ppt/viewer.html` 能正确引用同目录下的 JS/CSS。
2. WebView 加载 `file:///android_asset/ppt/viewer.html?file=...` 时没有跨域或本地文件访问错误。
3. `file` 参数经过 URL 编码，避免路径中的空格、`#`、`&` 等字符导致参数解析错误。
4. `file://` 和 `content://` 来源的 PPTX 都能被 Android 端拦截并返回正确 MIME 类型。
5. 构建产物不依赖外部 CDN，避免离线预览失效。
6. 使用真实 `.pptx` 样例验证普通文本、图片、表格、背景和多页幻灯片。

## 注意事项

- `pptx-preview` 主要支持 `.pptx`。旧版 `.ppt` 不是同一种文件格式，即使 AndroidDocViewer 当前把 `.ppt` 和 `.pptx` 都归为 `DocType.PPT`，实际预览能力仍取决于前端库是否能解析。
- Vite 默认会给构建产物生成带 hash 的文件名。复制产物时要保证 `viewer.html` 中引用的文件一并复制过去。
- 如果希望产物长期稳定，建议在 `vite.config.ts` 中固定 `base: './'`，并让构建输出更贴近 `assets/ppt` 的目录结构。
- `pptx-preview` 对复杂动画、宏、嵌入媒体、特殊字体等内容的还原能力有限，应把它定位为轻量预览，不是 PowerPoint 完整替代品。
