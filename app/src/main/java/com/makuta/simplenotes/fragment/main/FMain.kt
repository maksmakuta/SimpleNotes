package com.makuta.simplenotes.fragment.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makuta.simplenotes.MainActivity
import com.makuta.simplenotes.NotesApp.Companion.BUNDLE_FILE
import com.makuta.simplenotes.R
import com.makuta.simplenotes.Utils.gone
import com.makuta.simplenotes.Utils.visible
import com.makuta.simplenotes.adapter.NotesAdapter
import com.makuta.simplenotes.databinding.DRenameBinding
import com.makuta.simplenotes.databinding.FMainBinding
import java.io.File

class FMain : Fragment(), NotesAdapter.OnNoteActionListener, PopupMenu.OnMenuItemClickListener {

    private val vm: MainVM by viewModels()
    private lateinit var binding: FMainBinding
    private lateinit var notesAdapter: NotesAdapter
    private var currItem: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.setTitle(R.string.app_name)
        notesAdapter = NotesAdapter(this)

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = notesAdapter

        //notesAdapter.addData((1..10).map { "Item $it" })

        binding.fabNew.setOnClickListener {
            findNavController().navigate(
                R.id.action_FMain_to_FEditor,
                bundleOf(
                    BUNDLE_FILE to ""
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        vm.notes.observe(this) {
            if (!it.isNullOrEmpty()) {
                notesAdapter.addData(it)
                binding.list.visible()
                binding.empty.gone()
            } else {
                binding.list.gone()
                binding.empty.visible()
            }
        }
        vm.note.observe(this) {
            if (it != null) {
                val f = File(requireContext().filesDir, it.filename)
                val intent = Intent(Intent.ACTION_SEND)
                val shareBody = f.readText()
                intent.setType("text/plain")
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    it.title
                )
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(R.string.share_using)
                    )
                )
            }
        }
        vm.load()
    }

    override fun onStop() {
        super.onStop()
        vm.notes.removeObservers(this)
        vm.note.removeObservers(this)
    }

    override fun onClick(name: String) {
        findNavController().navigate(
            R.id.action_FMain_to_FEditor,
            bundleOf(
                BUNDLE_FILE to name
            )
        )
    }

    override fun onLongClick(name: String, view: View) {
        currItem = name
        val popupMenu = PopupMenu(requireContext(), view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }
        popupMenu.inflate(R.menu.menu_popup)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.menu_del -> {
                    if (!currItem.isNullOrEmpty()) {
                        vm.delete(currItem!!)
                        notesAdapter.del(currItem!!)
                    }
                    return true
                }

                R.id.menu_share -> {
                    if (!currItem.isNullOrEmpty()) {
                        vm.share(currItem!!)
                    }
                    return true
                }

                R.id.menu_rename -> {
                    val dialogBinding = DRenameBinding.inflate(requireActivity().layoutInflater)
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.title_rename)
                        .setView(dialogBinding.root)
                        .setPositiveButton(R.string.dialog_rename) { d, _ ->
                            val t = dialogBinding.editor.text.toString()
                            if (t.isNotEmpty()) {
                                vm.rename(currItem!!, t)
                                d.dismiss()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.err_empty,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                            d.dismiss()
                        }
                        .show()
                    return true
                }
            }
        }
        return false
    }

}