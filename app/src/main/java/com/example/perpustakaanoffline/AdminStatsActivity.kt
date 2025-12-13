package com.example.perpustakaanoffline

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AdminStatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_stats)

        findViewById<ImageButton>(R.id.btn_header_back).setOnClickListener { finish() }
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_header_action).apply {
            text = getString(R.string.label_export)
            setOnClickListener {
                // TODO export stats
            }
        }
    }
}

