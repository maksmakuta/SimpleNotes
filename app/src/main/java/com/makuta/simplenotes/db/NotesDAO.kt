package com.makuta.simplenotes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDAO {

    @Query("SELECT title FROM notes")
    suspend fun all(): List<String>

    @Query("SELECT * FROM notes WHERE title = (:title)")
    suspend fun pull(title: String): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upd(note: Note)

    @Delete
    suspend fun del(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun size(): Int
}