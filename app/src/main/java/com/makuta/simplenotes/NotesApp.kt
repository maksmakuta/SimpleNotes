package com.makuta.simplenotes

import android.app.Application
import androidx.room.Room
import com.makuta.simplenotes.db.NotesDB

class NotesApp : Application() {

    companion object {
        const val BUNDLE_FILE = "bundle_file"

        lateinit var db: NotesDB
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, NotesDB::class.java, "notes").build()
    }

    override fun onTerminate() {
        super.onTerminate()
        db.close()
    }

}