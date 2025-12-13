package com.example.perpustakaanoffline.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AdminStat(
    val label: String,
    val value: Int,
    val deltaPercent: Float,
)

data class AdminBook(
    val id: Long,
    val title: String,
    val author: String,
    val category: String,
    val format: String,
)

enum class Gender { MALE, FEMALE }

data class AdminAccount(
    val id: Long,
    val name: String,
    val username: String,
    val gender: Gender,
)

data class BookAnalytics(
    val title: String,
    val minutesRead: Int,
    val popularity: Float,
)

data class AdminUiState(
    val stats: List<AdminStat> = emptyList(),
    val books: List<AdminBook> = emptyList(),
    val accounts: List<AdminAccount> = emptyList(),
    val analytics: List<BookAnalytics> = emptyList(),
)

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<AdminUiState> = _uiState

    fun addRandomBook() {
        viewModelScope.launch {
            val nextId = System.currentTimeMillis()
            val newBook = AdminBook(
                id = nextId,
                title = "Judul ${nextId % 100}",
                author = "Penulis ${Random.nextInt(1, 50)}",
                category = listOf("Fiksi", "Sains", "Pelajaran").random(),
                format = listOf("EPUB", "ZIM", "PDF").random()
            )
            _uiState.update { it.copy(books = it.books + newBook) }
        }
    }

    fun deleteBook(id: Long) {
        _uiState.update { it.copy(books = it.books.filterNot { book -> book.id == id }) }
    }

    fun refreshStats() {
        _uiState.update { state ->
            state.copy(
                stats = state.stats.map { stat ->
                    stat.copy(
                        value = (stat.value * randomFloat(0.95f, 1.15f)).toInt(),
                        deltaPercent = randomFloat(-5f, 12f)
                    )
                }
            )
        }
    }

    private fun randomFloat(min: Float, max: Float): Float {
        return min + Random.nextFloat() * (max - min)
    }

    private fun buildInitialState(): AdminUiState {
        val stats = listOf(
            AdminStat("Total Buku", 240, 5.2f),
            AdminStat("Pembaca Aktif", 128, 3.8f),
            AdminStat("Buku Dibaca", 78, 7.1f),
        )
        val books = listOf(
            AdminBook(1, "Laskar Pelangi", "Andrea Hirata", "Fiksi", "EPUB"),
            AdminBook(2, "Bumi Manusia", "Pramoedya A. Toer", "Sejarah", "PDF"),
            AdminBook(3, "Fisika Modern", "Y. Surya", "Sains", "ZIM"),
        )
        val accounts = listOf(
            AdminAccount(1, "Ahmad Santoso", "ahmad01", Gender.MALE),
            AdminAccount(2, "Dewi Lestari", "dewi02", Gender.FEMALE),
            AdminAccount(3, "Rika Putri", "rika03", Gender.FEMALE),
        )
        val analytics = listOf(
            BookAnalytics("Laskar Pelangi", 320, 0.92f),
            BookAnalytics("Bumi Manusia", 280, 0.81f),
            BookAnalytics("Fisika Modern", 150, 0.65f),
        )
        return AdminUiState(stats, books, accounts, analytics)
    }
}
