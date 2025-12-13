package com.example.perpustakaanoffline.data

import android.content.Context
import com.example.perpustakaanoffline.data.db.PerpusDatabase
import com.example.perpustakaanoffline.data.repository.AccountRepository
import com.example.perpustakaanoffline.data.repository.BookRepository
import com.example.perpustakaanoffline.data.repository.ReadingRepository

class AppContainer(context: Context) {
    private val database = PerpusDatabase.create(context)

    val accountRepository = AccountRepository(database.accountDao())
    val bookRepository = BookRepository(database.bookDao())
    val readingRepository = ReadingRepository(database.readingStatDao())
}

