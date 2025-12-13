package com.example.perpustakaanoffline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        setupStatCard(
            includeId = R.id.card_total_books,
            value = "24",
            label = getString(R.string.stat_total_books),
            iconRes = R.drawable.ic_book_icon,
            backgroundRes = R.drawable.bg_stat_card_orange
        )
        setupStatCard(
            includeId = R.id.card_total_users,
            value = "156",
            label = getString(R.string.stat_total_users),
            iconRes = R.drawable.ic_user_group,
            backgroundRes = R.drawable.bg_stat_card_blue
        )
        setupStatCard(
            includeId = R.id.card_total_reads,
            value = "994",
            label = getString(R.string.stat_total_reads),
            iconRes = R.drawable.ic_stats_bar,
            backgroundRes = R.drawable.bg_stat_card_purple
        )

        initNavCard(
            R.id.item_manage_books,
            R.drawable.ic_book_icon,
            R.color.nav_icon_orange,
            R.string.nav_manage_books_title,
            R.string.nav_manage_books_desc
        ) { startActivity(Intent(this, AdminBooksActivity::class.java)) }

        initNavCard(
            R.id.item_manage_users,
            R.drawable.ic_user_group,
            R.color.nav_icon_blue,
            R.string.nav_manage_users_title,
            R.string.nav_manage_users_desc
        ) { startActivity(Intent(this, AdminUsersActivity::class.java)) }

        initNavCard(
            R.id.item_view_stats,
            R.drawable.ic_stats_bar,
            R.color.nav_icon_purple,
            R.string.nav_stats_title,
            R.string.nav_stats_desc
        ) { startActivity(Intent(this, AdminStatsActivity::class.java)) }

        findViewById<ImageButton>(R.id.btn_dashboard_action).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setupStatCard(
        includeId: Int,
        value: String,
        label: String,
        iconRes: Int,
        backgroundRes: Int
    ) {
        val card = findViewById<View>(includeId)
        val container = card.findViewById<View>(R.id.stat_card_container)
        val valueView = card.findViewById<TextView>(R.id.tv_stat_value)
        val labelView = card.findViewById<TextView>(R.id.tv_stat_label)
        val iconView = card.findViewById<ImageView>(R.id.iv_stat_icon)
        container.setBackgroundResource(backgroundRes)
        valueView.text = value
        labelView.text = label
        iconView.setImageResource(iconRes)
    }

    private fun initNavCard(
        includeId: Int,
        iconRes: Int,
        iconTint: Int,
        titleRes: Int,
        subtitleRes: Int,
        onClick: () -> Unit
    ) {
        val card = findViewById<View>(includeId)
        card.findViewById<ImageView>(R.id.iv_nav_icon).apply {
            setImageResource(iconRes)
            imageTintList = getColorStateList(iconTint)
        }
        card.findViewById<TextView>(R.id.tv_nav_title).text = getString(titleRes)
        card.findViewById<TextView>(R.id.tv_nav_subtitle).text = getString(subtitleRes)
        card.setOnClickListener { onClick() }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Keluar dari akun admin?")
            .setPositiveButton("Logout") { _, _ ->
                val prefs = getSharedPreferences("perpus_prefs", MODE_PRIVATE)
                prefs.edit()
                    .remove(LoginActivity.KEY_SESSION_USER)
                    .remove(LoginActivity.KEY_IS_ADMIN)
                    .apply()
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
