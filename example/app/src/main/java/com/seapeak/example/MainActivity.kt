package com.seapeak.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seapeak.docviewer.DocViewerFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame, DocViewerFragment()).commit()
    }
}