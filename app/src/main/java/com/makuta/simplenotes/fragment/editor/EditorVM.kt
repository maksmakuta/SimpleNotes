package com.makuta.simplenotes.fragment.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makuta.simplenotes.NotesApp.Companion.db
import com.makuta.simplenotes.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorVM : ViewModel() {

    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note
    val operation = MutableLiveData<Boolean>()

    fun load(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _note.postValue(db.notes().pull(title))
        }
    }

    fun save(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.notes().put(note)
            operation.postValue(true)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.notes().del(note)
            operation.postValue(true)
        }
    }

}