package com.example.perpustakaanoffline

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perpustakaanoffline.dashboard.UserDashboardState
import com.example.perpustakaanoffline.dashboard.UserDashboardViewModel
import com.example.perpustakaanoffline.data.AppContainer
import com.example.perpustakaanoffline.data.db.UserBookSummary
import com.example.perpustakaanoffline.data.db.UserReadingHistory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: UserDashboardViewModel
    private lateinit var downloadedAdapter: UserDownloadedAdapter
    private lateinit var historyAdapter: UserHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val username = intent.getStringExtra(LoginActivity.KEY_SESSION_USER)
            ?: getSharedPreferences("perpus_prefs", MODE_PRIVATE)
                .getString(LoginActivity.KEY_SESSION_USER, null)
            ?: return finish()

        val container = (application as PerpusApp).container
        val factory = UserDashboardViewModelFactory(container, username)
        viewModel = ViewModelProvider(this, factory)[UserDashboardViewModel::class.java]

        downloadedAdapter = UserDownloadedAdapter()
        historyAdapter = UserHistoryAdapter()

        findViewById<RecyclerView>(R.id.rv_downloaded_books).apply {
            layoutManager = LinearLayoutManager(this@UserDashboardActivity, RecyclerView.HORIZONTAL, false)
            adapter = downloadedAdapter
        }
        findViewById<RecyclerView>(R.id.rv_reading_history).apply {
            layoutManager = LinearLayoutManager(this@UserDashboardActivity)
            adapter = historyAdapter
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                bindHeader(state)
                bindStat(state)
                downloadedAdapter.submitList(state.downloadedBooks)
                historyAdapter.submitList(state.history)
            }
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun bindHeader(state: UserDashboardState) {
        findViewById<TextView>(R.id.tv_dashboard_name).text = state.fullName
    }

    private fun bindStat(state: UserDashboardState) {
        bindStatCard(R.id.stat_books_read, R.drawable.ic_book_icon, R.drawable.bg_stat_card_orange,
            state.booksRead.toString(), getString(R.string.stat_books_read))
        bindStatCard(R.id.stat_books_downloaded, R.drawable.ic_download, R.drawable.bg_stat_card_blue,
            state.downloads.toString(), getString(R.string.stat_books_downloaded))
        bindStatCard(R.id.stat_favorite, R.drawable.ic_heart_outline, R.drawable.bg_stat_card_purple,
            state.favoriteGenre, getString(R.string.stat_favorite_genre))
        val formatted = formatMinutes(state.minutesRead)
        bindStatCard(R.id.stat_reading_time, R.drawable.ic_clock, R.drawable.bg_stat_card_green,
            formatted.value, formatted.label)
    }

    private fun bindStatCard(includeId: Int, icon: Int, background: Int, primaryText: String, secondaryText: String) {
        val include = findViewById<androidx.cardview.widget.CardView>(includeId)
        val container = include.findViewById<android.view.View>(R.id.stat_card_container)
        val tvValue = include.findViewById<TextView>(R.id.tv_stat_value)
        val tvLabel = include.findViewById<TextView>(R.id.tv_stat_label)
        val ivIcon = include.findViewById<ImageView>(R.id.iv_stat_icon)
        container.setBackgroundResource(background)
        tvValue.text = primaryText
        tvLabel.text = secondaryText
        ivIcon.setImageResource(icon)
    }

    private fun formatMinutes(minutes: Int): StatText {
        val hours = minutes / 60
        val remaining = minutes % 60
        val value = if (hours > 0) getString(R.string.stat_hour_minute_value, hours, remaining)
        else getString(R.string.stat_minute_value, remaining)
        return StatText(value, getString(R.string.stat_reading_time_label))
    }

    data class StatText(val value: String, val label: String)
}

private class UserDashboardViewModelFactory(
    private val container: AppContainer,
    private val username: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDashboardViewModel(
                container.accountRepository,
                container.readingRepository,
                container.bookRepository,
                username
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private class UserDownloadedAdapter : RecyclerView.Adapter<UserDownloadedAdapter.ViewHolder>() {
    private val items = mutableListOf<UserBookSummary>()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun submitList(list: List<UserBookSummary>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: UserBookSummary) {
            itemView.findViewById<TextView>(R.id.tv_book_title).text = item.title
            itemView.findViewById<TextView>(R.id.tv_book_author).text = item.author
        }
    }
}

private class UserHistoryAdapter : RecyclerView.Adapter<UserHistoryAdapter.ViewHolder>() {
    private val items = mutableListOf<UserReadingHistory>()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun submitList(list: List<UserReadingHistory>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: UserReadingHistory) {
            itemView.findViewById<TextView>(R.id.tv_history_title).text = item.title
            itemView.findViewById<TextView>(R.id.tv_history_time).text =
                itemView.context.getString(R.string.stat_minute_value, item.minutesRead)
        }
    }
}
