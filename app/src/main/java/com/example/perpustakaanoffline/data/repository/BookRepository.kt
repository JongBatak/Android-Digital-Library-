package com.example.perpustakaanoffline.data.repository

import com.example.perpustakaanoffline.data.db.BookDao
import com.example.perpustakaanoffline.data.db.BookEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(private val dao: BookDao) {
    fun observeBooks(): Flow<List<BookEntity>> = dao.observeBooks()

    suspend fun upsert(book: BookEntity): Long = dao.upsert(book)

    suspend fun remove(id: Long) = dao.deleteById(id)

    suspend fun getBook(id: Long): BookEntity? = dao.getBookById(id)

    fun observePreinstalledBooks(): Flow<List<BookEntity>> = dao.observePreinstalledBooks()
}
