package com.example.perpustakaanoffline.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: ReadingStatEntity)

    @Query("SELECT * FROM reading_stats")
    fun observeStats(): Flow<List<ReadingStatEntity>>

    @Query(
        "SELECT books.title, SUM(reading_stats.minutes_read) AS total_minutes " +
            "FROM reading_stats INNER JOIN books ON books.id = reading_stats.book_id " +
            "WHERE reading_stats.account_id = :accountId " +
            "GROUP BY book_id ORDER BY total_minutes DESC LIMIT :limit")
    suspend fun topBooksForUser(accountId: Long, limit: Int = 5): List<TopBookStat>

    @Query("SELECT COUNT(DISTINCT book_id) FROM reading_stats WHERE account_id = :accountId")
    suspend fun countBooksRead(accountId: Long): Int

    @Query("SELECT IFNULL(SUM(minutes_read), 0) FROM reading_stats WHERE account_id = :accountId")
    suspend fun totalMinutesRead(accountId: Long): Int

    @Query(
        "SELECT LOWER(IFNULL(books.category, 'lainnya')) AS category, " +
            "SUM(reading_stats.minutes_read) AS totalMinutes " +
            "FROM reading_stats INNER JOIN books ON books.id = reading_stats.book_id " +
            "WHERE reading_stats.account_id = :accountId " +
            "GROUP BY LOWER(IFNULL(books.category, 'lainnya')) " +
            "ORDER BY totalMinutes DESC")
    suspend fun genreBreakdown(accountId: Long): List<GenreMinutes>

    @Query(
        "SELECT " +
            "SUM(CASE WHEN LOWER(IFNULL(books.category, '')) LIKE '%fiksi%' THEN reading_stats.minutes_read ELSE 0 END) AS fictionMinutes, " +
            "SUM(CASE WHEN LOWER(IFNULL(books.category, '')) LIKE '%fiksi%' THEN 0 ELSE reading_stats.minutes_read END) AS nonFictionMinutes " +
            "FROM reading_stats INNER JOIN books ON books.id = reading_stats.book_id " +
            "WHERE reading_stats.account_id = :accountId")
    suspend fun fictionSplit(accountId: Long): FictionSplit?

    @Query(
        "SELECT books.id AS bookId, books.title AS title, books.author AS author, books.category AS category, " +
            "MAX(reading_stats.last_read_at) AS lastReadAt " +
            "FROM reading_stats INNER JOIN books ON books.id = reading_stats.book_id " +
            "WHERE reading_stats.account_id = :accountId " +
            "GROUP BY books.id, books.title, books.author, books.category " +
            "ORDER BY lastReadAt DESC LIMIT :limit")
    suspend fun downloadedBooks(accountId: Long, limit: Int = 6): List<UserBookSummary>

    @Query(
        "SELECT books.title AS title, reading_stats.minutes_read AS minutesRead, reading_stats.last_read_at AS lastReadAt " +
            "FROM reading_stats INNER JOIN books ON books.id = reading_stats.book_id " +
            "WHERE reading_stats.account_id = :accountId " +
            "ORDER BY reading_stats.last_read_at DESC LIMIT :limit")
    suspend fun readingHistory(accountId: Long, limit: Int = 10): List<UserReadingHistory>
}

data class TopBookStat(
    val title: String,
    val total_minutes: Int
)

data class GenreMinutes(
    val category: String,
    val totalMinutes: Int
)

data class FictionSplit(
    val fictionMinutes: Int,
    val nonFictionMinutes: Int
)

data class UserBookSummary(
    val bookId: Long,
    val title: String,
    val author: String,
    val category: String?,
    val lastReadAt: Long
)

data class UserReadingHistory(
    val title: String,
    val minutesRead: Int,
    val lastReadAt: Long
)
