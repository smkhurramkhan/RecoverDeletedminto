package com.example.recovermessages.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recovermessages.databinding.ActivityMainBinding
import com.example.recovermessages.ui.chooseapps.ChooseAppsActivity
import com.example.recovermessages.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSetupFinished()) {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@MainActivity, ChooseAppsActivity::class.java))
            finish()
        }
    }

    private fun checkSetupFinished(): Boolean {
        return getSharedPreferences("SETUP", 0).getBoolean("setup", false)
    }

}