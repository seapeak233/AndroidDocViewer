# AndroidDocViewer
Android文档本地预览方案
<持续开发中...>

## 前言
自从腾讯X5的TBSReader收费之后，我一直在搜索可用的离线预览方案，经过一番查找，各类方案都有或多或少的缺失。
在转换思路之后发现，JS的方案对比原生来说，成熟度高太多了，且浏览器可缩放的行为可以极大的减少原生手势相关的工作量
所以，我这边的思路是将各类文档预览，可用的JS方案打包成html+css+js，存放到Android的assets目录，使用webView加载本地html

## TODO LIST
- [x] 支持pdf
- [x] 支持word
- [x] 支持ppt
- [ ] 支持excel
