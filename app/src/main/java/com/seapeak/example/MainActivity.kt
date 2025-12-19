package com.seapeak.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.Toast
import com.seapeak.docviewer.DocViewerActivity
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocPageConfig
import com.seapeak.docviewer.config.DocType

class MainActivity : AppCompatActivity() {

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val fileName = getFileNameFromUri(selectedUri) ?: "文档"
            val docType = getDocTypeFromFileName(fileName)

            if (docType != null) {
                // 直接传递content URI，DocViewerFragment会处理
                val docConfig = DocConfig(selectedUri.toString(), docType)
                DocViewerActivity.start(this, DocPageConfig.createDefault(docConfig, fileName))
            } else {
                Toast.makeText(this, "不支持的文件类型", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_excel).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample3.xls", DocType.EXCEL)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig, "Excel文档"))
        }

        findViewById<View>(R.id.btn_word).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample2.docx", DocType.WORD)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig,"Word文档"))
        }

        findViewById<View>(R.id.btn_ppt).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample4.pptx", DocType.PPT)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig,"PPT文档"))
        }

        findViewById<View>(R.id.btn_pdf).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample.pdf", DocType.PDF)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig,"PDF文档"))
        }

        findViewById<View>(R.id.btn_txt).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample.txt", DocType.TXT)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig, "文本文档"))
        }

        findViewById<View>(R.id.btn_markdown).setOnClickListener {
            val docConfig = DocConfig("file:///android_asset/sample.md", DocType.MARKDOWN)
            DocViewerActivity.start(this, DocPageConfig.createSimple(docConfig, "Markdown文档"))
        }

        // 添加系统文件选择按钮
        findViewById<View>(R.id.btn_select_file).setOnClickListener {
            filePickerLauncher.launch("*/*")
        }
    }

    private fun getDocTypeFromFileName(fileName: String): DocType? {
        val lowerFileName = fileName.lowercase()
        return when {
            lowerFileName.endsWith(".pdf") -> DocType.PDF
            lowerFileName.endsWith(".doc") || lowerFileName.endsWith(".docx") -> DocType.WORD
            lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".xlsx") -> DocType.EXCEL
            lowerFileName.endsWith(".ppt") || lowerFileName.endsWith(".pptx") -> DocType.PPT
            lowerFileName.endsWith(".txt") -> DocType.TXT
            lowerFileName.endsWith(".md") -> DocType.MARKDOWN
            else -> null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) path.substring(cut + 1) else path
            }
        }
        return fileName
    }
}