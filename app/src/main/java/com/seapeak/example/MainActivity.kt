package com.seapeak.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_excel).setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("config", DocConfig("file:///android_asset/sample3.xls", DocType.EXCEL))
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_word).setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("config", DocConfig("file:///android_asset/sample2.docx", DocType.WORD))
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_ppt).setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("config", DocConfig("file:///android_asset/sample4.pptx", DocType.PPT))
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_pdf).setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("config", DocConfig("file:///android_asset/sample.pdf", DocType.PDF))
            startActivity(intent)
        }
    }
}