package com.example.perpustakaanoffline.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookFormat {
    EPUB,
    ZIM,
    PDF,
    OTHER
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "class_name") val className: String?,
    @ColumnInfo(name = "gender") val gender: String?,
    @ColumnInfo(name = "is_admin") val isAdmin: Boolean = false
)

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "format") val format: BookFormat,
    @ColumnInfo(name = "file_path") val filePath: String?,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    @ColumnInfo(name = "is_preinstalled") val isPreinstalled: Boolean = true,
    @ColumnInfo(name = "description") val description: String? = null
)

@Entity(tableName = "reading_stats")
data class ReadingStatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "book_id") val bookId: Long,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "minutes_read") val minutesRead: Int,
    @ColumnInfo(name = "last_read_at") val lastReadAt: Long
)

