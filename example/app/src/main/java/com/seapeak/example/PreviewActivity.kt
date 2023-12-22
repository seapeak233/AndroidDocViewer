package com.seapeak.example

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seapeak.docviewer.DocViewerFragment
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocType

class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val config: DocConfig = intent.getSerializableExtra("config") as DocConfig

        supportFragmentManager.beginTransaction().replace(
            R.id.frame, DocViewerFragment.newInstance(
                config
            )
        ).commit()
    }
}
