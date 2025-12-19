package com.seapeak.docviewer.config

import java.io.File
import java.io.Serializable

data class DocConfig(val url: String, val type: DocType) : Serializable {
    
    companion object {
        /**
         * 从文件路径创建DocConfig
         */
        fun fromFile(filePath: String): DocConfig? {
            val file = File(filePath)
            val docType = getDocTypeFromExtension(file.extension) ?: return null
            val url = if (filePath.startsWith("file://")) filePath else "file://$filePath"
            return DocConfig(url, docType)
        }
        
        /**
         * 从Assets文件创建DocConfig
         */
        fun fromAssets(assetPath: String, docType: DocType): DocConfig {
            val url = "file:///android_asset/$assetPath"
            return DocConfig(url, docType)
        }
        
        /**
         * 根据文件扩展名推断文档类型
         */
        private fun getDocTypeFromExtension(extension: String): DocType? {
            return when (extension.lowercase()) {
                "pdf" -> DocType.PDF
                "doc", "docx" -> DocType.WORD
                "xls", "xlsx" -> DocType.EXCEL
                "ppt", "pptx" -> DocType.PPT
                "txt" -> DocType.TXT
                "md" -> DocType.MARKDOWN
                else -> null
            }
        }
    }
}

enum class DocType {
    TXT, WORD, EXCEL, PPT, PDF, MARKDOWN
}
