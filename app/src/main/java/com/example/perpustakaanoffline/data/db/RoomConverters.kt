package com.example.perpustakaanoffline.data.db

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun fromFormat(format: BookFormat): String = format.name

    @TypeConverter
    fun toFormat(raw: String): BookFormat = runCatching { BookFormat.valueOf(raw) }
        .getOrDefault(BookFormat.OTHER)
}

