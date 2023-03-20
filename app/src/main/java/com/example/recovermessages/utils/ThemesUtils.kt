package com.example.recovermessages.utils

import android.content.Context
import com.example.recovermessages.R
import timber.log.Timber

class ThemesUtils(var context: Context) {
    private var pref: SharedPrefs = SharedPrefs(context)
    var themeName: Boolean = pref.loadNightMode()

    fun setTheme() {
        Timber.d("theme Name is $themeName")
        if (themeName) {
            context.setTheme(R.style.Darktheme)

        } else {
            context.setTheme(R.style.AppTheme)

        }

    }


}