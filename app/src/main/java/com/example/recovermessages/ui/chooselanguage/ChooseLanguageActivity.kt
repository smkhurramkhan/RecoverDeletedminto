package com.example.recovermessages.ui.chooselanguage

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recovermessages.R
import com.example.recovermessages.databinding.ActivityChooseLanguageBinding
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.chooselanguage.adapter.ChooseLanguageAdapter
import com.example.recovermessages.ui.chooselanguage.model.LanguagesModel
import com.example.recovermessages.ui.home.HomeActivity
import com.example.recovermessages.utils.LanguageUtils


class ChooseLanguageActivity : BaseActivity() {
    private var languagesList = mutableListOf<LanguagesModel>()
    private var adapterLanguages: ChooseLanguageAdapter? = null
    private lateinit var chooseLanguageBinding: ActivityChooseLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseLanguageBinding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        languageUtils = LanguageUtils(this)
        initLanguageData()
        setContentView(chooseLanguageBinding.root)
        initRecycler()

    }

    private fun initRecycler() {
        chooseLanguageBinding.languagesRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        chooseLanguageBinding.languagesRecycler.layoutManager = LinearLayoutManager(this)
        adapterLanguages = ChooseLanguageAdapter(
            this,
            languagesList,
            onClick = { position ->
                if (languagesList[position].languageCode == "fr")
                    languageUtils?.setAppLocale("fr")
                else {
                    languageUtils?.setAppLocale("en")
                }
                sendIntent()
            }
        )
        chooseLanguageBinding.languagesRecycler.adapter = adapterLanguages
    }

    private fun initLanguageData() {
        languagesList.add(
            LanguagesModel(
                1, "en", "English", R.drawable.uk
            )
        )


        languagesList.add(
            LanguagesModel(
                2, "fr", "French", R.drawable.france
            )
        )


    }

    private fun sendIntent() {
        val nextScreen = Intent(this, HomeActivity::class.java)
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(nextScreen)
        finish()
    }

}