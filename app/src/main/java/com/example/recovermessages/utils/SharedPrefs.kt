package com.example.recovermessages.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context?) {
    var mysharedpref: SharedPreferences? =null
    var editor: SharedPreferences.Editor? = null
    fun setNightModeState(state: Boolean?) {
        val editor = mysharedpref!!.edit()
        editor.putBoolean("NightMode", state!!)
        editor.commit()
    }

    fun loadNightMode(): Boolean {
        return mysharedpref!!.getBoolean("NightMode", false)
    }




    var language: String?
        get() = mysharedpref!!.getString("language", "")
        set(name) {
            editor!!.putString("language", name)
            editor!!.commit()
        }

    var isPremium: Boolean
        get() = mysharedpref!!.getBoolean("premium", false)
        set(flag) {
            editor?.putBoolean("premium", flag)
            editor?.commit()
        }

    var firstRun: Boolean
        get() = mysharedpref!!.getBoolean("firstRun", true)
        set(flag) {
            editor?.putBoolean("firstRun", flag)
            editor?.commit()
        }



    fun setLanguage(language:Boolean){
        editor?.putBoolean("setlanguage", language)
        editor?.commit()
    }
    fun getLanguage():Boolean{
        return mysharedpref!!.getBoolean("setlanguage", false)
    }


    init {
        if (context != null) {
            mysharedpref = context.getSharedPreferences("MeetPeopleProPrefs", Context.MODE_PRIVATE)
            editor = mysharedpref?.edit()
            editor?.apply()
        }
    }
}