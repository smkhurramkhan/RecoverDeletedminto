package com.example.recovermessages.utils

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.example.recovermessages.R

object AppUtils {


    fun setStatusBarColor(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.statusBarColor =
                activity.resources.getColor(R.color.black, activity.theme)
        } else
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.black)
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }


}