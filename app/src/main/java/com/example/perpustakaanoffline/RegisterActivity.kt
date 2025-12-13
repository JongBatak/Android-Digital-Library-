package com.example.perpustakaanoffline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.perpustakaanoffline.data.db.AccountEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("perpus_prefs", MODE_PRIVATE) }
    private val repo by lazy {
        val app = application as PerpusApp
        app.container.accountRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val etFullName = findViewById<EditText>(R.id.etNamaLengkap)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etClass = findViewById<EditText>(R.id.etKelas)
        val genderGroup = findViewById<RadioGroup>(R.id.GenderRadio)
        val btnRegister = findViewById<Button>(R.id.btnRegisterAction)
        val tvHaveAccount = findViewById<TextView>(R.id.tvHaveAccount)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val selectedGender = when (genderGroup.checkedRadioButtonId) {
                R.id.radioMale -> "male"
                R.id.radioFemale -> "female"
                else -> null
            }

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                etUsername.error = "Isi data wajib"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    repo.upsert(
                        AccountEntity(
                            username = username,
                            password = password,
                            fullName = fullName,
                            className = etClass.text.toString().ifBlank { null },
                            gender = selectedGender,
                            isAdmin = username == ADMIN_USER
                        )
                    )
                }.onFailure {
                    AlertDialog.Builder(this@RegisterActivity)
                        .setTitle("Gagal daftar")
                        .setMessage(it.localizedMessage)
                        .setPositiveButton("OK", null)
                        .show()
                }.onSuccess {
                    saveSession(username, username == ADMIN_USER)
                    if (username == ADMIN_USER && password == ADMIN_PASS) {
                        openAdmin()
                    } else {
                        openMainMenu()
                    }
                }
            }
        }

        tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveSession(username: String, isAdmin: Boolean) {
        prefs.edit()
            .putString(LoginActivity.KEY_SESSION_USER, username)
            .putBoolean(LoginActivity.KEY_IS_ADMIN, isAdmin)
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

    companion object {
        private const val ADMIN_USER = "adminPerpusDig"
        private const val ADMIN_PASS = "admin123"
    }
}
