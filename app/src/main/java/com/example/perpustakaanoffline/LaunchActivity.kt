package com.example.perpustakaanoffline

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("perpus_prefs", MODE_PRIVATE)
        val loggedUser = prefs.getString(LoginActivity.KEY_SESSION_USER, null)
        val next = when {
            loggedUser.isNullOrEmpty() -> RegisterActivity::class.java
            prefs.getBoolean(LoginActivity.KEY_IS_ADMIN, false) -> AdminDashboardActivity::class.java
            else -> MainMenuActivity::class.java
        }
        startActivity(Intent(this, next))
        finish()
    }

    companion object {
        private const val ADMIN_USER = "adminPerpusDig"
    }
}
