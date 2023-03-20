package com.example.recovermessages.ui.home.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recovermessages.R
import com.example.recovermessages.databinding.ItemChatHeadersBinding
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.models.userModel
import com.example.recovermessages.ui.home.adapters.viewholders.UserVH
import com.example.recovermessages.ui.messages.MessagesActivity
import timber.log.Timber

class UsersAdapter(
    private val context: Context,
    private val list: MutableList<userModel>,
    private val pack: String
) : RecyclerView.Adapter<UserVH>() {
    private var actionMode: ActionMode? = null
    private val selectedItems1: SparseBooleanArray = SparseBooleanArray()
    private val actionModeCallback: ActionModeCallback
    private val db: AppDatabase


    init {
        actionModeCallback = ActionModeCallback()
        db = AppDatabase(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): UserVH {
        return UserVH(
            ItemChatHeadersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: UserVH, i: Int) {
        val userModel = list[i]

        viewHolder.binding.apply {
            name.text = userModel.name
            lastMessage.text = userModel.lastmsg
            messageTime.text = userModel.time
            toggleCheckedIcon(viewHolder.binding, i)

            layoutRoot.setOnClickListener {
                if (selectedItemCount > 0) {
                    enableActionMode(i)
                } else {
                    val intent = Intent(context, MessagesActivity::class.java)
                    intent.putExtra("name", userModel.name)
                    intent.putExtra("pack", pack)
                    context.startActivity(intent)
                }
            }

            layoutRoot.setOnLongClickListener {
                enableActionMode(i)
                false
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun toggleCheckedIcon(binding: ItemChatHeadersBinding, position: Int) {
        if (selectedItems1[position, false]) {
            binding.layoutRoot.background = ContextCompat.getDrawable(
                context,
                R.drawable.bg_selector
            )
        } else {
            binding.layoutRoot.background = ContextCompat.getDrawable(
                context,
                R.drawable.bg_transparent
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
                    .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                        deleteSelection()
                        mode.finish()
                    }.setNegativeButton("No") { dialog1: DialogInterface, _: Int ->
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
            val b = db.deleteSingleRow(list[position].name)
            if (b) {
                db.deleteMessageRow(list[position].name)
                list.removeAt(position)
            } else Timber.d("unable to delete this ----------------------")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}