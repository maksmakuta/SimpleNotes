package com.makuta.simplenotes.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makuta.simplenotes.NotesApp.Companion.db
import com.makuta.simplenotes.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainVM : ViewModel() {

    private val _notes = MutableLiveData<List<String>>()
    val notes: LiveData<List<String>> = _notes
    private val _item = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _item

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.postValue(db.notes().all())
        }
    }

    fun delete(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = db.notes().pull(title)
            if (item != null) {
                db.notes().del(item)
            }
            load()
        }
    }

    fun rename(oldName: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = db.notes().pull(oldName)
            if (item != null) {
                item.title = newName
                db.notes().upd(item)
            }
            load()
        }
    }

    fun share(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = db.notes().pull(title)
            _item.postValue(item)
        }
    }

}