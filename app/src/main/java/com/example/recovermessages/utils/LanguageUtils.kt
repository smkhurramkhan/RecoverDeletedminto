package com.example.recovermessages.utils

import android.content.Context
import android.content.res.Configuration
import com.example.recovermessages.MyApplication
import java.util.*

class LanguageUtils(var context: Context) {
    var pref: SharedPrefs = SharedPrefs(context)
    fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            MyApplication.instance?.resources?.displayMetrics
        )
        pref.language = language
    }

    fun loadLocale() {
        val language = pref.language
        if (language != null) {
            setAppLocale(language)
        }
    }

}