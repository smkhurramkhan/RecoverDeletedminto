package com.example.recovermessages.ui.messages.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.recovermessages.databinding.ItemChatScreenBinding
import com.example.recovermessages.models.DataModel
import com.example.recovermessages.ui.messages.viewholder.MessagesVH

class MessagesAdapter(
    private val context: Context,
    private val list: List<DataModel>
) :
    RecyclerView.Adapter<MessagesVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MessagesVH {
        return MessagesVH(
            ItemChatScreenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: MessagesVH, i: Int) {
        val dataModel = list[i]

        viewHolder.binding.apply {
            tvMessage.text = dataModel.msg
            tvTime.text = dataModel.time


            msgParent.setOnClickListener {
                val equals = if (tvMessage.text == "\ud83d\udcf7 Photo") 1 else 0
                if (equals == 0) {
                    copyMsg(dataModel.msg)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(equals: Int): Int {
        return equals
    }

    private fun copyMsg(s: String) {
        try {
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText("msg", s))
            Toast.makeText(context, "Message Copied", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}