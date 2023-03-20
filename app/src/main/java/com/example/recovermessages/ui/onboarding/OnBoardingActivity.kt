package com.example.recovermessages.ui.onboarding

import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.example.recovermessages.R
import com.example.recovermessages.utils.SharedPrefs

class OnBoardingActivity : IntroActivity() {

    private var sharedPrefs: SharedPrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = SharedPrefs(this)
        isButtonBackVisible = false

        if (sharedPrefs?.loadNightMode() == true) {
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.black_dim)
                    .backgroundDark(R.color.black)
                    .fragment(R.layout.fragment_intro2, R.style.Darktheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.black_dim)
                    .backgroundDark(R.color.black)
                    .fragment(R.layout.fragment_intro, R.style.Darktheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.black_dim)
                    .backgroundDark(R.color.black)
                    .fragment(R.layout.fragment_intro3, R.style.Darktheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.black_dim)
                    .backgroundDark(R.color.black)
                    .fragment(R.layout.fragment_intro4, R.style.Darktheme)
                    .build()
            )
        } else {

            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(R.layout.fragment_intro2, R.style.AppTheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(R.layout.fragment_intro, R.style.AppTheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(R.layout.fragment_intro3, R.style.AppTheme)
                    .build()
            )
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(R.layout.fragment_intro4, R.style.AppTheme)
                    .build()
            )
        }
    }
}