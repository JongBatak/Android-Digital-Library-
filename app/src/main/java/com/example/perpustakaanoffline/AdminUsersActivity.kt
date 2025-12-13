package com.example.perpustakaanoffline

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AdminUsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_users)

        findViewById<ImageButton>(R.id.btn_header_back).setOnClickListener { finish() }
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_header_action).apply {
            text = getString(R.string.label_add_user)
            setOnClickListener {
                // TODO open add account form
            }
        }
    }
}

