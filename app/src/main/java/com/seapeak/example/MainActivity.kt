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
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val fileName = getFileNameFromUri(selectedUri) ?: "temp_file"
                val docType = getDocTypeFromFileName(fileName)

                if (docType != null) {
                    // 将content URI的内容复制到缓存文件，避免权限问题
                    val tempFile = copyUriToTempFile(selectedUri, fileName)
                    if (tempFile != null) {
                        val docConfig = DocConfig("file://${tempFile.absolutePath}", docType)
                        DocViewerActivity.start(this, DocPageConfig.createDefault(docConfig, fileName))
                    } else {
                        Toast.makeText(this, "文件读取失败", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "不支持的文件类型", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "打开文件失败：${e.message}", Toast.LENGTH_SHORT).show()
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
        return DocType.fromExtension(File(fileName).extension)
    }

    private fun copyUriToTempFile(uri: Uri, fileName: String): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(cacheDir, fileName)

            FileOutputStream(tempFile).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
