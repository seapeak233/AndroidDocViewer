package com.seapeak.docviewer

import android.annotation.SuppressLint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import java.io.File


class DocViewerFragment : Fragment(R.layout.doc_viewer_fragment) {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.webView)

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

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                Log.i("chromium", "shouldInterceptRequest: " + request?.url)
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

//        webView.loadUrl("file:///android_asset/excel/viewer.html")
//        webView.loadUrl("file:///android_asset/word/viewer.html")
//        webView.loadUrl("file:///android_asset/pdf/viewer.html")
        webView.loadUrl("file:///android_asset/ppt/viewer.html")
    }

}