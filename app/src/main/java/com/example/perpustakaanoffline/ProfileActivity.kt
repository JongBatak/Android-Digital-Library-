package com.example.perpustakaanoffline

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.iterator

class ProfileActivity : AppCompatActivity() {

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { findViewById<ImageView>(R.id.iv_profile_pic).setImageURI(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageButton>(R.id.btn_profile_back).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.iv_profile_pic).setOnClickListener { pickImage.launch("image/*") }
        findViewById<ImageView>(R.id.btn_edit_avatar).setOnClickListener { pickImage.launch("image/*") }

        findViewById<View>(R.id.btn_logout).setOnClickListener { showLogoutConfirm() }

        bindMockData()
        playEntryAnimations()
    }

    private fun bindMockData() {
        findViewById<TextView>(R.id.tv_profile_name).text = "Ahmad Santoso"
        findViewById<TextView>(R.id.tv_profile_username).text = "@user123"

        bindStatCard(
            containerId = R.id.stat_books,
            background = R.drawable.bg_profile_stat_blue,
            icon = R.drawable.ic_book_icon,
            value = "12",
            label = getString(R.string.profile_books_read)
        )
        bindStatCard(
            containerId = R.id.stat_streak,
            background = R.drawable.bg_profile_stat_orange,
            icon = R.drawable.ic_fire,
            value = "7",
            label = getString(R.string.profile_day_streak)
        )
        bindStatCard(
            containerId = R.id.stat_rating,
            background = R.drawable.bg_profile_stat_purple,
            icon = R.drawable.ic_star_outline,
            value = "4.8",
            label = getString(R.string.profile_rating_avg)
        )

        findViewById<TextView>(R.id.tv_streak_days).text = "7"
        findViewById<TextView>(R.id.tv_streak_subtitle).text =
            getString(R.string.profile_streak_days, 7)

        setInfoCard(R.id.info_fullname, R.drawable.ic_profile, R.string.profile_label_full_name, "Ahmad Santoso", R.color.nav_icon_orange)
        setInfoCard(R.id.info_username, R.drawable.ic_menu, R.string.profile_label_username, "@user123", R.color.nav_icon_blue)
        setInfoCard(R.id.info_class, R.drawable.ic_book_icon, R.string.profile_label_class, "XII IPA 1", R.color.profile_stat_purple)
        setInfoCard(R.id.info_gender, R.drawable.ic_profile, R.string.profile_label_gender, "Laki-laki", R.color.green)
        setInfoCard(R.id.info_joined, R.drawable.ic_clock, R.string.profile_label_joined, "September 2024", R.color.red)

        val dayContainer = findViewById<LinearLayout>(R.id.view_profile_day_check_root)
        dayContainer?.let { container ->
            var count = 0
            container.iterator().forEach { child ->
                child.isSelected = count < 7
                child.isVisible = count < 7
                count++
            }
        }
    }

    private fun bindStatCard(containerId: Int, background: Int, icon: Int, value: String, label: String) {
        val include = findViewById<View>(containerId)
        val root = include.findViewById<View>(R.id.stat_card_root)
        root.setBackgroundResource(background)
        include.findViewById<ImageView>(R.id.iv_stat_icon).setImageResource(icon)
        include.findViewById<TextView>(R.id.tv_stat_value).text = value
        include.findViewById<TextView>(R.id.tv_stat_label).text = label
    }

    private fun setInfoCard(id: Int, icon: Int, titleRes: Int, value: String, tintColor: Int) {
        val include = findViewById<View>(id)
        include.findViewById<ImageView>(R.id.iv_info_icon).apply {
            setImageResource(icon)
            imageTintList = getColorStateList(tintColor)
        }
        include.findViewById<TextView>(R.id.tv_info_title).text = getString(titleRes)
        include.findViewById<TextView>(R.id.tv_info_value).text = value
        include.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(titleRes))
                .setMessage(value)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun playEntryAnimations() {
        val header = findViewById<View>(R.id.card_profile_header)
        val stats = findViewById<View>(R.id.stat_container)
        val streak = findViewById<View>(R.id.card_streak)
        val info = findViewById<View>(R.id.info_cards)

        listOf(header, stats, streak, info).forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 120).toLong())
                .setDuration(400)
                .start()
        }

        val pulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(findViewById(R.id.tv_streak_days), View.SCALE_X, 1f, 1.08f, 1f),
                ObjectAnimator.ofFloat(findViewById(R.id.tv_streak_days), View.SCALE_Y, 1f, 1.08f, 1f)
            )
            duration = 800
        }
        pulse.start()
    }

    private fun showLogoutConfirm() {
        AlertDialog.Builder(this)
            .setTitle(R.string.profile_logout)
            .setMessage(R.string.nav_manage_books_desc)
            .setPositiveButton(R.string.profile_logout) { _, _ -> finish() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
