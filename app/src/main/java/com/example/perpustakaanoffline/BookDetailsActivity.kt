package com.example.perpustakaanoffline

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class BookDetailsActivity : AppCompatActivity() {
    private var currentSizeSp = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_book_details)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_detail_title)
        val tvToolbarAuthor = findViewById<TextView>(R.id.tv_detail_author)
        val ivCover = findViewById<ImageView>(R.id.iv_detail_cover)
        val tvTitle = findViewById<TextView>(R.id.tv_detail_book_title)
        val tvAuthor = findViewById<TextView>(R.id.tv_detail_book_author_full)
        val chipFormat = findViewById<com.google.android.material.chip.Chip>(R.id.chip_detail_description)
        val tvContent = findViewById<TextView>(R.id.tv_book_content)
        val btnZoomIn = findViewById<ImageView>(R.id.iv_zoom_in)
        val btnZoomOut = findViewById<ImageView>(R.id.iv_zoom_out)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.book_title_sample)
        val author = intent.getStringExtra(EXTRA_AUTHOR) ?: getString(R.string.book_author_sample)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: getString(R.string.book_content_sample)
        val format = intent.getStringExtra(EXTRA_FORMAT) ?: "EPUB"
        val coverRes = intent.getIntExtra(EXTRA_COVER_RES, R.drawable.book_cover_placeholder)

        tvToolbarTitle.text = title
        tvToolbarAuthor.text = author
        tvTitle.text = title
        tvAuthor.text = author
        chipFormat.text = format
        tvContent.text = description
        ivCover.setImageResource(coverRes)

        if (savedInstanceState != null) {
            currentSizeSp = savedInstanceState.getFloat(KEY_SIZE, 16f)
        }
        tvContent.textSize = currentSizeSp

        btnZoomIn.setOnClickListener {
            currentSizeSp = (currentSizeSp + 2f).coerceAtMost(40f)
            tvContent.textSize = currentSizeSp
        }
        btnZoomOut.setOnClickListener {
            currentSizeSp = (currentSizeSp - 2f).coerceAtLeast(10f)
            tvContent.textSize = currentSizeSp
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(KEY_SIZE, currentSizeSp)
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
        const val EXTRA_TITLE = "extra_book_title"
        const val EXTRA_AUTHOR = "extra_book_author"
        const val EXTRA_DESCRIPTION = "extra_book_description"
        const val EXTRA_FORMAT = "extra_book_format"
        const val EXTRA_COVER_RES = "extra_book_cover"
        private const val KEY_SIZE = "currentSizeSp"
    }
}
