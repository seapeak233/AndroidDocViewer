package com.seapeak.docviewer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocType
import com.seapeak.docviewer.utils.PermissionHelper
import java.io.File


class DocViewerFragment(private val docConfig: DocConfig) : Fragment(R.layout.doc_viewer_fragment) {

    private lateinit var webView: WebView

    companion object {
        fun newInstance(docConfig: DocConfig): DocViewerFragment {
            return DocViewerFragment(docConfig)
        }
        
        /**
         * 验证文件是否可以被WebView访问
         */
        fun validateFileAccess(filePath: String): Boolean {
            return PermissionHelper.isFileAccessible(filePath)
        }
        
        /**
         * 获取支持的文档类型
         */
        fun getSupportedExtensions(): List<String> {
            return listOf(
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "md", "markdown"
            ) + DocType.textExtensions
        }
        
        /**
         * 根据文件扩展名推断文档类型
         */
        fun getDocTypeFromExtension(filePath: String): DocType? {
            val extension = File(filePath).extension.lowercase()
            return DocType.fromExtension(extension)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.webView)

        initWebView()
        
        // 检查存储权限
        if (!PermissionHelper.hasStoragePermission(requireContext())) {
            Log.w("DocViewer", "Storage permission not granted, requesting...")
            PermissionHelper.requestStoragePermission(this)
        } else {
            loadDocument()
        }
    }
    
    private fun loadDocument() {
        webView.post {
            val url = when (docConfig.type) {
                DocType.EXCEL -> "file:///android_asset/excel/viewer.html"
                DocType.WORD -> "file:///android_asset/word/viewer.html"
                DocType.PPT -> "file:///android_asset/ppt/viewer.html"
                DocType.PDF -> "file:///android_asset/pdf/viewer.html"
                DocType.TXT -> "file:///android_asset/txt/viewer.html"
                DocType.MARKDOWN -> "file:///android_asset/markdown/viewer.html"
                else -> null
            }
            if (url == null) {
                activity?.finish()
            } else {
                webView.loadUrl("$url?file=${docConfig.url}")
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.handlePermissionResult(
            requestCode,
            permissions,
            grantResults,
            onPermissionGranted = {
                Log.i("DocViewer", "Storage permission granted")
                loadDocument()
            },
            onPermissionDenied = {
                Log.e("DocViewer", "Storage permission denied")
                // 可以显示提示信息或关闭页面
            }
        )
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PermissionHelper.handleActivityResult(
            requestCode,
            resultCode,
            onPermissionGranted = {
                Log.i("DocViewer", "Manage external storage permission granted")
                loadDocument()
            },
            onPermissionDenied = {
                Log.e("DocViewer", "Manage external storage permission denied")
                // 可以显示提示信息或关闭页面
            }
        )
    }

    override fun onDestroyView() {
        // 建议先移除父视图中的 webView 再销毁
        (webView.parent as? android.view.ViewGroup)?.removeView(webView)
        webView.apply {
            stopLoading() // 停止加载，防止销毁时仍在请求数据
            clearHistory()
            removeAllViews()
            destroy()
        }
        super.onDestroyView()
    }
    
    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings = webView.settings
        webSettings.allowFileAccess = true
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.setSupportZoom(true)
        
        // 增强配置以支持本地文件访问
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        
        // 性能优化配置
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.setGeolocationEnabled(false)

        // 对于Android 10+，需要额外配置
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            webSettings.isAlgorithmicDarkeningAllowed = false
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            webSettings.forceDark = WebSettings.FORCE_DARK_OFF
        }
        
        // 禁用不必要的功能以提升性能
        webSettings.setNeedInitialFocus(false)

        // 设置WebChromeClient处理JS弹窗
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                Log.d("DocViewer", "JS Alert: $message")
                
                // 创建自定义弹窗
                AlertDialog.Builder(requireContext())
                    .setTitle("提示")
                    .setMessage(message ?: "")
                    .setPositiveButton("确定") { _, _ ->
                        result?.confirm()
                    }
                    .setOnCancelListener {
                        result?.cancel()
                    }
                    .show()
                
                return true // 表示我们已经处理了这个弹窗
            }
            
            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                Log.d("DocViewer", "JS Confirm: $message")
                
                AlertDialog.Builder(requireContext())
                    .setTitle("确认")
                    .setMessage(message ?: "")
                    .setPositiveButton("确定") { _, _ ->
                        result?.confirm()
                    }
                    .setNegativeButton("取消") { _, _ ->
                        result?.cancel()
                    }
                    .setOnCancelListener {
                        result?.cancel()
                    }
                    .show()
                
                return true
            }
            
            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                Log.d("DocViewer", "JS Prompt: $message")
                
                val editText = android.widget.EditText(requireContext()).apply {
                    setText(defaultValue ?: "")
                }
                
                AlertDialog.Builder(requireContext())
                    .setTitle("输入")
                    .setMessage(message ?: "")
                    .setView(editText)
                    .setPositiveButton("确定") { _, _ ->
                        result?.confirm(editText.text.toString())
                    }
                    .setNegativeButton("取消") { _, _ ->
                        result?.cancel()
                    }
                    .setOnCancelListener {
                        result?.cancel()
                    }
                    .show()
                
                return true
            }
            
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.d("DocViewer", "Loading progress: $newProgress%")
                super.onProgressChanged(view, newProgress)
            }
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                Log.d("DocViewer", "Page title: $title")
                super.onReceivedTitle(view, title)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                Log.i("DocViewer", "shouldInterceptRequest: ${request?.url}")

                // 拦截文件请求，直接读取本地文件
                request?.url?.let { url ->
                    Log.d("DocViewer", "Intercepting request: $url")

                    // 处理content://协议
                    if (url.scheme == "content") {
                        try {
                            Log.d("DocViewer", "Handling content URI: $url")
                            val inputStream = requireContext().contentResolver.openInputStream(android.net.Uri.parse(url.toString()))

                            if (inputStream != null) {
                                // 从URI中获取文件扩展名
                                val mimeType = getMimeTypeFromUri(android.net.Uri.parse(url.toString()))
                                Log.d("DocViewer", "Serving content URI with MIME type: $mimeType")

                                return WebResourceResponse(
                                    mimeType,
                                    null,
                                    inputStream
                                )
                            } else {
                                Log.w("DocViewer", "Cannot open content URI: $url")
                                return WebResourceResponse(
                                    "text/plain",
                                    "UTF-8",
                                    "Cannot open content URI".byteInputStream()
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("DocViewer", "Error reading content URI: $url", e)
                            return WebResourceResponse(
                                "text/plain",
                                "UTF-8",
                                "Error: ${e.message}".byteInputStream()
                            )
                        }
                    }

                    // 只拦截非assets的file://请求
                    if (url.scheme == "file" && url.path?.startsWith("/android_asset/") != true) {
                        try {
                            val filePath = url.path!!
                            val file = File(filePath)

                            Log.d("DocViewer", "Trying to access file: ${file.absolutePath}")
                            Log.d("DocViewer", "File exists: ${file.exists()}, canRead: ${file.canRead()}")

                            if (file.exists() && file.canRead()) {
                                val mimeType = getMimeType(file.extension)
                                Log.d("DocViewer", "Serving file with MIME type: $mimeType")

                                return WebResourceResponse(
                                    mimeType,
                                    null, // 不指定编码，让系统自动处理
                                    file.inputStream()
                                )
                            } else {
                                Log.w("DocViewer", "File not accessible: ${file.absolutePath}")
                                // 返回404错误响应
                                return WebResourceResponse(
                                    "text/plain",
                                    "UTF-8",
                                    "File not found or not accessible".byteInputStream()
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("DocViewer", "Error reading file: ${url.path}", e)
                            // 返回错误响应
                            return WebResourceResponse(
                                "text/plain",
                                "UTF-8",
                                "Error: ${e.message}".byteInputStream()
                            )
                        }
                    }
                }

                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d("DocViewer", "shouldOverrideUrlLoading: ${request?.url}")
                return false // 让WebView处理URL加载
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                Log.e("DocViewer", "WebView error: ${error?.description} for ${request?.url}")
                super.onReceivedError(view, request, error)
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("DocViewer", "Page finished loading: $url")
                super.onPageFinished(view, url)
            }
            
            private fun getMimeType(extension: String): String {
                return when (extension.lowercase()) {
                    "pdf" -> "application/pdf"
                    "doc", "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    "xls", "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    "ppt", "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                    "txt" -> "text/plain"
                    "md" -> "text/markdown"
                    else -> "application/octet-stream"
                }
            }

            private fun getMimeTypeFromUri(uri: android.net.Uri): String {
                // 先尝试从ContentResolver获取MIME类型
                val contentType = requireContext().contentResolver.getType(uri)
                if (contentType != null) {
                    return contentType
                }

                // 如果无法获取，尝试从文件名推断
                val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (displayNameIndex != -1) {
                            val fileName = it.getString(displayNameIndex)
                            val extension = fileName.substringAfterLast('.', "")
                            if (extension.isNotEmpty()) {
                                return getMimeType(extension)
                            }
                        }
                    }
                }

                return "application/octet-stream"
            }
        }
    }

}
