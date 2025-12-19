package com.seapeak.docviewer

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        
        /**
         * 启动文档预览Activity
         */
        fun start(context: Context, pageConfig: DocPageConfig) {
            val intent = Intent(context, DocViewerActivity::class.java).apply {
                putExtra(EXTRA_PAGE_CONFIG, pageConfig)
            }
            context.startActivity(intent)
        }
        
        /**
         * 快速启动文档预览Activity（使用默认配置）
         */
        fun start(context: Context, docConfig: DocConfig, title: String? = null) {
            start(context, DocPageConfig.createDefault(docConfig, title))
        }
        
        /**
         * 从文件路径启动文档预览
         */
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
        
        // 获取配置
        pageConfig = intent.getSerializableExtra(EXTRA_PAGE_CONFIG) as? DocPageConfig
            ?: run {
                Log.e("DocViewerActivity", "DocPageConfig not found in intent")
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
        // 设置标题
        supportActionBar?.apply {
            title = pageConfig.title ?: getDefaultTitle()
            setDisplayHomeAsUpEnabled(pageConfig.showBackButton)
        }
        
        // 设置颜色
        window.statusBarColor = pageConfig.statusBarColor
        appBarLayout.setBackgroundColor(pageConfig.toolbarColor)
        toolbar.setTitleTextColor(pageConfig.titleTextColor)
        
        // 设置返回按钮颜色
        if (pageConfig.showBackButton) {
            toolbar.navigationIcon?.setColorFilter(
                pageConfig.iconTintColor,
                PorterDuff.Mode.SRC_IN
            )
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
            
            // 设置菜单图标颜色
            menu?.let { menuItems ->
                for (i in 0 until menuItems.size()) {
                    val menuItem = menuItems.getItem(i)
                    menuItem.icon?.setColorFilter(
                        pageConfig.iconTintColor,
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }
            
            // 根据配置显示/隐藏菜单项
            menu?.findItem(R.id.action_open_with)?.isVisible = pageConfig.enableThirdPartyOpen
            menu?.findItem(R.id.action_download)?.isVisible = pageConfig.enableDownload
        }
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
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
            val file = File(pageConfig.docConfig.url.removePrefix("file://"))
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
                return
            }
            
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            val mimeType = getMimeType(file.extension) ?: "*/*"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "选择应用打开")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "没有找到可以打开此文件的应用", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("DocViewerActivity", "Error opening with third party", e)
            Toast.makeText(this, "打开失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun downloadFile() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            Toast.makeText(this, "需要存储权限才能下载文件", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sourceFile = File(pageConfig.docConfig.url.removePrefix("file://"))
                if (!sourceFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DocViewerActivity, "源文件不存在", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                
                val targetDir = if (pageConfig.downloadToPublicDir) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                } else {
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                }
                
                if (targetDir == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DocViewerActivity, "无法访问下载目录", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
                
                val targetFile = File(targetDir, sourceFile.name)
                var counter = 1
                var finalTargetFile = targetFile
                
                // 处理文件名冲突
                while (finalTargetFile.exists()) {
                    val nameWithoutExt = sourceFile.nameWithoutExtension
                    val extension = sourceFile.extension
                    finalTargetFile = File(targetDir, "${nameWithoutExt}_${counter}.${extension}")
                    counter++
                }
                
                // 复制文件
                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(finalTargetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DocViewerActivity,
                        "文件已下载到: ${finalTargetFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                
                // 通知媒体扫描器
                if (pageConfig.downloadToPublicDir) {
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScanIntent.data = Uri.fromFile(finalTargetFile)
                    sendBroadcast(mediaScanIntent)
                }
                
            } catch (e: Exception) {
                Log.e("DocViewerActivity", "Error downloading file", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DocViewerActivity,
                        "下载失败: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun getMimeType(extension: String): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }
}