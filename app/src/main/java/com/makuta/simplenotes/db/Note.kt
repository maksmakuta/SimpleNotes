package com.makuta.simplenotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    var title: String,
    val filename: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null
}
