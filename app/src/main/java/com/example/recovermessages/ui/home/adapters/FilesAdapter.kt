package com.example.recovermessages.ui.home.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recovermessages.R
import com.example.recovermessages.ui.home.adapters.viewholders.SaverVH
import com.example.recovermessages.databinding.GridJtemBinding
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.ui.preview.ActivityImagePreview
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import timber.log.Timber
import java.io.File

class FilesAdapter(
    private val context: Context,
    private val filesList: MutableList<ModelFiles>
) : RecyclerView.Adapter<SaverVH>() {
    private var actionMode: ActionMode? = null
    private val selectedItems1: SparseBooleanArray = SparseBooleanArray()
    private val actionModeCallback: ActionModeCallback


    init {
        actionModeCallback = ActionModeCallback()
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): SaverVH {
        return SaverVH(
            GridJtemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SaverVH, i: Int) {
        val modelFiles = filesList[i]
        Timber.d("File type Adapter is %s", modelFiles.type)
        holder.binding.apply {

            if (modelFiles.type == "video") {
                playicon.show()
            } else {
                playicon.hide()
            }

            when (modelFiles.type) {
                "audio" -> Glide.with(context).load(modelFiles.file).centerCrop()
                    .placeholder(R.drawable.audio).into(icon)
                "image", "video" -> Glide.with(context).load(modelFiles.file).centerCrop()
                    .placeholder(R.drawable.placeholder).into(icon)
                "document" -> if (modelFiles.file.absolutePath.endsWith(".pdf")) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_pdf_files).into(icon)
                } else if (modelFiles.file.absolutePath.endsWith(".ppt")
                    || modelFiles.file.absolutePath.endsWith(".pptx")
                ) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_ppt_files).into(icon)
                } else if (modelFiles.file.absolutePath.endsWith(".doc")
                    || modelFiles.file.absolutePath.endsWith(".docx")
                ) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_word_files).into(icon)
                } else if (modelFiles.file.absolutePath.endsWith(".xls")
                    || modelFiles.file.absolutePath.endsWith(".xlsx")
                ) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_excel_files).into(icon)
                } else if (modelFiles.file.absolutePath.endsWith(".zip")) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_zip_files).into(icon)
                } else if (modelFiles.file.absolutePath.endsWith(".rar")) {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_rar_files).into(icon)
                } else {
                    Glide.with(context).load(modelFiles.file).centerCrop()
                        .placeholder(R.drawable.icon_all_docs).into(icon)
                }
            }

            shareID.setOnClickListener {
                shareImage(modelFiles)
            }


            delete.setOnClickListener {
                showDeleteDialog(i)
            }

            toggleCheckedIcon(holder.binding, i)


            cardview.setOnClickListener {
                if (selectedItemCount > 0) {
                    enableActionMode(i)
                } else {
                    val file = File(modelFiles.file.path)
                    val uri = FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        file
                    )
                    when (modelFiles.type) {
                        "image" -> try {
                            val intent = Intent(context, ActivityImagePreview::class.java)
                            intent.putExtra("imagepath", modelFiles.file.path)
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                "No App found to open this file",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        "video" -> try {
                            val intent = Intent()
                            intent.action = "android.intent.action.VIEW"
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            intent.setDataAndType(uri, "video/*")
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                "No App found to open this file",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        "audio" -> try {
                            val intent = Intent()
                            intent.action = "android.intent.action.VIEW"
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            intent.setDataAndType(uri, "audio/*")
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                "No App found to open this file",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        "document" -> try {
                            val mime = context.contentResolver.getType(uri)
                            // Open file with user selected app
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.setDataAndType(uri, mime)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            Toast.makeText(
                                context, "No App found to open this file",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            cardview.setOnLongClickListener {
                enableActionMode(i)
                false
            }

        }
    }

    private fun showDeleteDialog(i: Int) {
        val builder1 = AlertDialog.Builder(
            context
        )
        builder1.setMessage("Are you sure you want to delete this file?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog: DialogInterface, id: Int ->
            dialog.cancel()
            dialog.dismiss()
            deleteItem(i)
            notifyDataSetChanged()
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    private fun shareImage(modelFiles: ModelFiles) {
        val mainUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            modelFiles.file
        )
        val sharingIntent: Intent = Intent("android.intent.action.SEND")
        sharingIntent.type = "image/*"
        sharingIntent.putExtra("android.intent.extra.STREAM", mainUri)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No application found to open this file.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    private fun toggleCheckedIcon(binding: GridJtemBinding, position: Int) {
        if (selectedItems1[position, false]) {
            binding.cardview.background = ContextCompat.getDrawable(
                context,
                R.drawable.bg_selector
            )
        } else {
            binding.cardview.background = ContextCompat.getDrawable(
                context, R.drawable.bg_transparent
            )
        }
    }

    private fun toggleSelection1(pos: Int) {
        if (selectedItems1[pos, false]) {
            selectedItems1.delete(pos)
        } else {
            selectedItems1.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearSelections() {
        selectedItems1.clear()
        notifyDataSetChanged()
    }

    private val selectedItemCount: Int
        get() = selectedItems1.size()
    private val selectedItems: List<Int>
        get() {
            val items: MutableList<Int> = ArrayList(selectedItems1.size())
            for (i in 0 until selectedItems1.size()) {
                items.add(selectedItems1.keyAt(i))
            }
            return items
        }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = (context as AppCompatActivity).startSupportActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        toggleSelection1(position)
        val count = selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$count items selected"
            actionMode?.invalidate()
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_multiple_del, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val id = item.itemId
            if (id == R.id.action_trash) {
                val builderDelete = AlertDialog.Builder(
                    context
                )
                builderDelete.setMessage("Are you sure want to delete these file?")
                    .setPositiveButton("Yes") { dialog1: DialogInterface?, which1: Int ->
                        deleteSelection()
                        mode.finish()
                    }.setNegativeButton("No") { dialog1: DialogInterface, which1: Int ->
                        mode.finish()
                        dialog1.dismiss()
                    }.create().show()
                return true
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
        }
    }

    private fun deleteSelection() {
        val selectedItemPositions = selectedItems
        for (i in selectedItemPositions.indices.reversed()) {
            deleteItem(selectedItemPositions[i])
        }
        notifyDataSetChanged()
    }

    private fun deleteItem(position: Int) {
        try {
            val data = filesList[position]
            val file = data.file
            if (file.delete()) {
                filesList.removeAt(position)
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}