package com.example.perpustakaanoffline.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaanoffline.data.repository.AccountRepository
import com.example.perpustakaanoffline.data.repository.BookRepository
import com.example.perpustakaanoffline.data.repository.ReadingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDashboardViewModel(
    private val accountRepository: AccountRepository,
    private val readingRepository: ReadingRepository,
    private val bookRepository: BookRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDashboardState())
    val uiState: StateFlow<UserDashboardState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val account = accountRepository.findByUsername(username)
                ?: return@launch
            val accountId = account.id
            val topBooks = readingRepository.topBooks(accountId)
            val booksRead = readingRepository.countBooksRead(accountId)
            val minutesRead = readingRepository.totalMinutesRead(accountId)
            val genreBreakdown = readingRepository.genreBreakdown(accountId)
            val fictionSplit = readingRepository.fictionSplit(accountId)
            val downloaded = readingRepository.downloadedBooks(accountId)
            val history = readingRepository.history(accountId)

            val favoriteGenre = genreBreakdown.maxByOrNull { it.totalMinutes }?.category ?: "-"
            val fictionPercent = fictionSplit?.let {
                val total = (it.fictionMinutes + it.nonFictionMinutes).coerceAtLeast(1)
                (it.fictionMinutes * 100 / total)
            } ?: 0
            val nonFictionPercent = 100 - fictionPercent

            _uiState.value = UserDashboardState(
                fullName = account.fullName,
                booksRead = booksRead,
                downloads = downloaded.size,
                minutesRead = minutesRead,
                favoriteGenre = favoriteGenre,
                topBooks = topBooks,
                fictionPercent = fictionPercent,
                nonFictionPercent = nonFictionPercent,
                downloadedBooks = downloaded,
                history = history
            )
        }
    }
}

 data class UserDashboardState(
    val fullName: String = "",
    val booksRead: Int = 0,
    val downloads: Int = 0,
    val minutesRead: Int = 0,
    val favoriteGenre: String = "-",
    val topBooks: List<com.example.perpustakaanoffline.data.db.TopBookStat> = emptyList(),
    val fictionPercent: Int = 0,
    val nonFictionPercent: Int = 0,
    val downloadedBooks: List<com.example.perpustakaanoffline.data.db.UserBookSummary> = emptyList(),
    val history: List<com.example.perpustakaanoffline.data.db.UserReadingHistory> = emptyList()
)

