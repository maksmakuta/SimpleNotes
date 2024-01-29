package com.makuta.simplenotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makuta.simplenotes.databinding.INoteBinding

class NotesAdapter(
    private val listener: OnNoteActionListener? = null
) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private val data = ArrayList<String>()

    fun addData(items: List<String>) {
        data.clear()
        data.addAll(items)
        notifyItemRangeChanged(0, items.size)
    }

    fun del(item: String) {
        val i = data.indexOf(item)
        if (i >= 0) {
            data.remove(item)
            notifyItemRemoved(i)
        }
    }

    fun clear() {
        val c = data.size
        data.clear()
        notifyItemRangeRemoved(0, c)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = INoteBinding.inflate(layoutInflater, parent, false)
        return NotesHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.text.text = data[position]
        if (listener != null) {
            holder.root.setOnClickListener {
                listener.onClick(data[position])
            }
            holder.root.setOnLongClickListener {
                listener.onLongClick(data[position], holder.root)
                true
            }
        }
    }

    override fun getItemCount(): Int = data.size

    class NotesHolder(binding: INoteBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val text = binding.title
    }

    interface OnNoteActionListener {
        fun onClick(name: String)
        fun onLongClick(name: String, view: View)
    }
}