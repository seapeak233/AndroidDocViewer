package com.seapeak.docviewer.config

import java.io.Serializable

data class DocConfig(val url: String, val type: DocType) : Serializable

enum class DocType {
    TXT, WORD, EXCEL, PPT, PDF, MARKDOWN
}
