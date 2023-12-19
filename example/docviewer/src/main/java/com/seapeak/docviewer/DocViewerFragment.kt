package com.seapeak.docviewer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment


class DocViewerFragment : Fragment(R.layout.doc_viewer_fragment) {

    private lateinit var webView: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.webView)

        // 设置允许访问文件
        val webSettings = webView.settings
        webSettings.allowFileAccess = true
        webSettings.javaScriptEnabled = true

        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request?.url.toString().startsWith("file://")) {
                    return WebResourceResponse(
                        "application/x-javascript",
                        "utf-8",
                        activity?.assets?.open("pdf" + request?.url?.path)
                    )
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        // 加载 assets 目录下的 HTML 文件
        webView.loadUrl("file:///android_asset/pdf/viewer.html?file=https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/web/compressed.tracemonkey-pldi-09.pdf")
    }

}