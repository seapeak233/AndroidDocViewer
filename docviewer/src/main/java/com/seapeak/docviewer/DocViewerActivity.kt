package com.seapeak.docviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.appbar.AppBarLayout
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocPageConfig
import com.seapeak.docviewer.config.DocType
import com.seapeak.docviewer.utils.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DocViewerActivity : AppCompatActivity() {

    private lateinit var pageConfig: DocPageConfig
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var appBarLayout: AppBarLayout

    companion object {
        private const val EXTRA_PAGE_CONFIG = "extra_page_config"

        fun start(context: Context, pageConfig: DocPageConfig) {
            val intent = Intent(context, DocViewerActivity::class.java).apply {
                putExtra(EXTRA_PAGE_CONFIG, pageConfig)
            }
            context.startActivity(intent)
        }

        fun start(context: Context, docConfig: DocConfig, title: String? = null) {
            start(context, DocPageConfig.createDefault(docConfig, title))
        }

        fun startWithFile(context: Context, filePath: String, title: String? = null) {
            val docType = DocViewerFragment.getDocTypeFromExtension(filePath)
            if (docType == null) {
                Toast.makeText(context, "不支持的文件格式", Toast.LENGTH_SHORT).show()
                return
            }

            val docConfig = DocConfig(filePath, docType)
            start(context, docConfig, title ?: File(filePath).nameWithoutExtension)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)

        // 适配 SDK 33/34 的 getSerializableExtra·
        pageConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_PAGE_CONFIG, DocPageConfig::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_PAGE_CONFIG) as? DocPageConfig
        } ?: run {
            Log.e("DocViewerActivity", "DocPageConfig not found")
            finish()
            return
        }

        initViews()
        setupToolbar()
        loadFragment()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout)
        setSupportActionBar(toolbar)
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = pageConfig.title ?: getDefaultTitle()
            setDisplayHomeAsUpEnabled(pageConfig.showBackButton)
        }

        // 状态栏与 Toolbar 颜色适配
        window.statusBarColor = pageConfig.statusBarColor
        appBarLayout.setBackgroundColor(pageConfig.toolbarColor)
        toolbar.setTitleTextColor(pageConfig.titleTextColor)

        // 使用 setTint 替代弃用的 setColorFilter
        if (pageConfig.showBackButton) {
            toolbar.navigationIcon?.setTint(pageConfig.iconTintColor)
        }
    }

    private fun getDefaultTitle(): String {
        return when (pageConfig.docConfig.type) {
            DocType.PDF -> "PDF预览"
            DocType.WORD -> "Word预览"
            DocType.EXCEL -> "Excel预览"
            DocType.PPT -> "PPT预览"
            DocType.TXT -> "文本预览"
            DocType.MARKDOWN -> "Markdown预览"
        }
    }

    private fun loadFragment() {
        val fragment = DocViewerFragment.newInstance(pageConfig.docConfig)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (pageConfig.showMoreMenu) {
            menuInflater.inflate(R.menu.doc_viewer_menu, menu)
            menu?.let { menuItems ->
                for (i in 0 until menuItems.size()) {
                    menuItems.getItem(i).icon?.setTint(pageConfig.iconTintColor)
                }
            }
            menu?.findItem(R.id.action_open_with)?.isVisible = pageConfig.enableThirdPartyOpen
            menu?.findItem(R.id.action_download)?.isVisible = pageConfig.enableDownload
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // SDK 34 推荐使用 dispatcher 处理返回
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_open_with -> {
                openWithThirdParty()
                true
            }
            R.id.action_download -> {
                downloadFile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openWithThirdParty() {
        try {
            val cleanUrl = pageConfig.docConfig.url.removePrefix("file://")
            val file = File(cleanUrl)
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
                return
            }

            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val mimeType = getMimeType(file.extension) ?: "*/*"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "选择应用打开")
            startActivity(chooser)
        } catch (e: Exception) {
            Log.e("DocViewerActivity", "Error opening", e)
            Toast.makeText(this, "无法打开文件: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFile() {
        // 注意：在 SDK 33+ 中，这里需要检查的是 READ_MEDIA_... 权限，而非 READ_EXTERNAL_STORAGE
        if (!PermissionHelper.hasStoragePermission(this)) {
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sourceFile = File(pageConfig.docConfig.url.removePrefix("file://"))
                if (!sourceFile.exists()) {
                    showToast("源文件不存在")
                    return@launch
                }

                val targetDir = if (pageConfig.downloadToPublicDir) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                } else {
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                }

                if (targetDir == null) {
                    showToast("无法访问目录")
                    return@launch
                }

                if (!targetDir.exists()) targetDir.mkdirs()

                var targetFile = File(targetDir, sourceFile.name)
                var counter = 1
                while (targetFile.exists()) {
                    targetFile = File(targetDir, "${sourceFile.nameWithoutExtension}_$counter.${sourceFile.extension}")
                    counter++
                }

                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                showToast("已下载到: ${targetFile.absolutePath}")

                // 刷新媒体库
                if (pageConfig.downloadToPublicDir) {
                    val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    scanIntent.data = Uri.fromFile(targetFile)
                    sendBroadcast(scanIntent)
                }
            } catch (e: Exception) {
                showToast("下载失败: ${e.message}")
            }
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@DocViewerActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(extension: String): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }
}