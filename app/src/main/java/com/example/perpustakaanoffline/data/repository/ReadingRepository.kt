package com.example.perpustakaanoffline.data.repository

import com.example.perpustakaanoffline.data.db.FictionSplit
import com.example.perpustakaanoffline.data.db.GenreMinutes
import com.example.perpustakaanoffline.data.db.ReadingStatDao
import com.example.perpustakaanoffline.data.db.ReadingStatEntity
import com.example.perpustakaanoffline.data.db.TopBookStat
import com.example.perpustakaanoffline.data.db.UserBookSummary
import com.example.perpustakaanoffline.data.db.UserReadingHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadingRepository(private val dao: ReadingStatDao) {
    fun observeStats(): Flow<List<ReadingStatEntity>> = dao.observeStats()

    fun observeTopBooks(limit: Int = 5): Flow<List<TopBookStat>> =
        dao.observeStats().map { emptyList<TopBookStat>() }

    suspend fun insert(stat: ReadingStatEntity) = dao.insert(stat)

    suspend fun topBooks(accountId: Long, limit: Int = 5): List<TopBookStat> =
        dao.topBooksForUser(accountId, limit)

    suspend fun countBooksRead(accountId: Long): Int = dao.countBooksRead(accountId)

    suspend fun totalMinutesRead(accountId: Long): Int = dao.totalMinutesRead(accountId)

    suspend fun genreBreakdown(accountId: Long): List<GenreMinutes> = dao.genreBreakdown(accountId)

    suspend fun fictionSplit(accountId: Long): FictionSplit? = dao.fictionSplit(accountId)

    suspend fun downloadedBooks(accountId: Long, limit: Int = 6): List<UserBookSummary> =
        dao.downloadedBooks(accountId, limit)

    suspend fun history(accountId: Long, limit: Int = 10): List<UserReadingHistory> =
        dao.readingHistory(accountId, limit)
}
