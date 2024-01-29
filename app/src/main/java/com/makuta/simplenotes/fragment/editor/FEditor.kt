package com.makuta.simplenotes.fragment.editor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makuta.simplenotes.MainActivity
import com.makuta.simplenotes.NotesApp.Companion.BUNDLE_FILE
import com.makuta.simplenotes.R
import com.makuta.simplenotes.Utils.sha256
import com.makuta.simplenotes.databinding.DRenameBinding
import com.makuta.simplenotes.databinding.FEditBinding
import com.makuta.simplenotes.db.Note
import java.io.File

class FEditor : Fragment(), MenuProvider {

    private val vm: EditorVM by viewModels()
    private var noteTitle = ""
    private lateinit var binding: FEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        noteTitle = requireArguments().getString(BUNDLE_FILE, "")
        (requireActivity() as MainActivity).supportActionBar?.title = if (noteTitle.isEmpty())
            getString(R.string.title_new)
        else {
            vm.load(noteTitle)
            noteTitle
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_edit, menu)
        if (noteTitle.isEmpty()) {
            menu.findItem(R.id.menu_share).isVisible = false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                val shareBody = binding.content.text
                intent.setType("text/plain")
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    vm.note.value?.title ?: ""
                )
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(R.string.share_using)
                    )
                )
                return true
            }

            R.id.menu_save -> {
                if (vm.note.value != null) {
                    val f = File(requireContext().filesDir, vm.note.value!!.filename)
                    f.writeText(binding.content.text.toString())
                    findNavController().popBackStack()
                } else {
                    val dialogBinding = DRenameBinding.inflate(requireActivity().layoutInflater)
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.title_save_as)
                        .setView(dialogBinding.root)
                        .setPositiveButton(R.string.dialog_save) { d, _ ->
                            val t = dialogBinding.editor.text.toString()
                            if (t.isNotEmpty()) {
                                val newNote = Note(
                                    dialogBinding.editor.text.toString(),
                                    System.nanoTime().toString().sha256()
                                )
                                val f = File(requireContext().filesDir, newNote.filename)
                                f.writeText(binding.content.text.toString())
                                vm.save(newNote)
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
                }
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        vm.note.observe(this) {
            if (it != null) {
                loadNote(it.filename)
            }
        }
        vm.operation.observe(this) {
            if (it) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        vm.note.removeObservers(this)
        vm.operation.removeObservers(this)
    }

    private fun loadNote(file: String) {
        val f = File(requireContext().filesDir, file)
        if (f.exists()) {
            binding.content.setText(f.readText())
        } else {
            Toast.makeText(requireContext(), "Failed to load note", Toast.LENGTH_SHORT).show()
        }
    }
}
