package com.example.recovermessages.ui.chooselanguage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recovermessages.databinding.ItemLanguageBinding
import com.example.recovermessages.ui.chooselanguage.adapter.vh.LanguagesVH
import com.example.recovermessages.ui.chooselanguage.model.LanguagesModel

class ChooseLanguageAdapter(
    val context: Context,
    private val languageList: List<LanguagesModel>,
    val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<LanguagesVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesVH {
        return LanguagesVH(
            ItemLanguageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LanguagesVH, position: Int) {
        val langModel = languageList[position]

        holder.binding.apply {
            flag.setImageResource(langModel.countryFlag)
            countryName.text = langModel.languageName
        }
        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }
}