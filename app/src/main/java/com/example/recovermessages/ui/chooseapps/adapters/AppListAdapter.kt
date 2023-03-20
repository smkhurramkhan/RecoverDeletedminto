package com.example.recovermessages.ui.chooseapps.adapters

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recovermessages.R
import com.example.recovermessages.databinding.ItemAppListBinding
import com.example.recovermessages.ui.chooseapps.ChooseAppsActivity
import com.example.recovermessages.ui.chooseapps.vh.AppListVH
import timber.log.Timber

class AppListAdapter(
    private val listInfo: List<PackageInfo>,
    private val context: ChooseAppsActivity,
    private val addedList: MutableList<String>
) : RecyclerView.Adapter<AppListVH>() {
    private val checklist = ArrayList<Boolean>()
    private val packageManager: PackageManager


    init {
        for (i in listInfo.indices) {
            checklist.add(false)
        }
        packageManager = context.packageManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): AppListVH {
        return AppListVH(
            ItemAppListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: AppListVH, position: Int) {

        viewHolder.binding.apply {
            name.text =
                packageManager.getApplicationLabel(listInfo[position].applicationInfo).toString()

            icon.setImageDrawable(packageManager.getApplicationIcon(listInfo[position].applicationInfo))

            val contains = addedList.contains(listInfo[position].packageName)
            if (contains) {
                checklist[position] = contains
                addedList.remove(listInfo[position].packageName)
            } else {
                Timber.d("fall")
            }


            if (checklist[position]) {
                Glide.with(context).load(R.drawable.correct).into(check)
            } else {
                Glide.with(context).load(R.drawable.circle_stroke).into(check)
            }


            main.setOnClickListener {
                if (checklist[position]) {
                    checklist[position] = false
                    Glide.with(context).load(R.drawable.circle_stroke).into(check)
                } else {
                    Glide.with(context).load(R.drawable.correct).into(check)
                    checklist[position] = true

                }
                context.mAddToList(listInfo[position].packageName)
            }

        }

    }

    override fun getItemCount(): Int {
        return listInfo.size
    }
}