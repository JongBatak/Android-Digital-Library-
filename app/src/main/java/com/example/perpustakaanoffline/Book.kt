package com.example.perpustakaanoffline

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val category: String,
    val coverResId: Int,
    val description: String? = null,
    val format: String = "EPUB",
    val filePath: String? = null
)