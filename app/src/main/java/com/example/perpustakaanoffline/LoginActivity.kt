package com.example.perpustakaanoffline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("perpus_prefs", MODE_PRIVATE) }
    private val repo by lazy {
        val app = application as PerpusApp
        app.container.accountRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvNoAccount = findViewById<TextView>(R.id.tvNoAccount)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Login gagal")
                    .setMessage("Lengkapi username & password")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                runCatching { repo.findByUsername(username) }
                    .onFailure { showError("Gagal membaca database: ${it.localizedMessage}") }
                    .onSuccess { account ->
                        if (username == ADMIN_USER && password == ADMIN_PASS) {
                            saveSession(username, true)
                            openAdmin()
                            return@launch
                        }

                        if (account == null || account.password != password) {
                            showError("Username atau password salah")
                            return@launch
                        }
                        saveSession(username, account.isAdmin)
                        openMainMenu()
                    }
            }
        }

        tvNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun saveSession(username: String, isAdmin: Boolean) {
        prefs.edit()
            .putString(KEY_SESSION_USER, username)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }

    private fun openAdmin() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun openMainMenu() {
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Login gagal")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        private const val ADMIN_USER = "adminPerpusDig"
        private const val ADMIN_PASS = "admin123"
        const val KEY_SESSION_USER = "logged_in_user"
        const val KEY_IS_ADMIN = "is_admin"
    }
}
