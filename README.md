当前版本：[![](https://jitpack.io/v/seapeak233/AndroidDocViewer.svg)](https://jitpack.io/#seapeak233/AndroidDocViewer)

# AndroidDocViewer
Android文档本地预览方案<持续优化中...>

## 前言
自从腾讯X5的TBSReader收费之后，我一直在搜索可用的离线预览方案，经过一番查找，各类方案都有或多或少的缺失。
在转换思路之后发现，JS的方案对比原生来说，成熟度高太多了，且浏览器可缩放的行为可以极大的减少原生手势相关的工作量
所以，我这边的思路是将各类文档预览，可用的JS方案打包成html+css+js，存放到Android的assets目录，使用webView加载本地html

## TODO LIST
- [x] 支持pdf
- [x] 支持word
- [x] 支持ppt
- [x] 支持excel
- [ ] 支持txt
- [ ] 样式优化

## 使用
* 引入aar:
```
implementation 'com.github.seapeak233:AndroidDocViewer:<Tag>'
```
* 提供了一个Fragment容器，将DocConfig内容放入其中
```
// DocConfig(:url(文件链接), :type(预览类型DocType))
// DocType 的类型有五种 TXT, WORD, EXCEL, PPT, PDF

val config = DocConfig("file:///android_asset/sample3.xls", DocType.EXCEL)
val fragment = DocViewerFragment.newInstance(config)
// 将DocViewerFragment放入Activity提供的容器中即可预览
```

## 实际截图
![Excel预览](https://github.com/seapeak233/AndroidDocViewer/blob/main/static/excel_shot.png)
![Pdf预览](https://github.com/seapeak233/AndroidDocViewer/blob/main/static/pdf_shot.png)
![PPT预览](https://github.com/seapeak233/AndroidDocViewer/blob/main/static/ppt_shot.png)
![Word预览](https://github.com/seapeak233/AndroidDocViewer/blob/main/static/word_shot.png)

## 非常感谢方案思路
[sheetjs](https://github.com/SheetJS/sheetjs)
[pdfjs](https://github.com/mozilla/pdf.js)
[pptxjs](https://github.com/meshesha/PPTXjs)
[docxjs](https://github.com/VolodymyrBaydalka/docxjs)
