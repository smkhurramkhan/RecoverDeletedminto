package com.example.recovermessages.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recovermessages.utils.LanguageUtils
import com.example.recovermessages.utils.SharedPrefs
import com.example.recovermessages.utils.ThemesUtils
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {
    var context: BaseActivity? = null
    var languageUtils: LanguageUtils? = null
    private lateinit var themesUtils: ThemesUtils
    private var sharedPrefs: SharedPrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = SharedPrefs(this)
        themesUtils = ThemesUtils(this)
        themesUtils.setTheme()
        languageUtils = LanguageUtils(this);
        languageUtils?.loadLocale();
        Timber.d("current theme is ${themesUtils.themeName}")

    }

}