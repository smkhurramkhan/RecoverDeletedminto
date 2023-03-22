package com.example.recovermessages.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.example.recovermessages.databinding.ActivitySplashBinding
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.chooselanguage.ChooseLanguageActivity
import com.example.recovermessages.ui.main.MainActivity
import com.example.recovermessages.ui.permission.PermissionActivity
import com.example.recovermessages.utils.AppUtils
import com.example.recovermessages.utils.SharedPrefs

class SplashActivity : BaseActivity() {
    private lateinit var splashBinding: ActivitySplashBinding
    private var sharedPrefs: SharedPrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.setStatusBarColor(this)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        sharedPrefs = SharedPrefs(this)

        if (sharedPrefs?.firstRun == true) {
            val intent = Intent(this, ChooseLanguageActivity::class.java)
            intent.putExtra("fromStart", true)
            startActivity(intent)
            finish()
        } else {
            startIntentForMainActivity()
        }
    }


    private fun startIntentForMainActivity() {

        Handler(Looper.getMainLooper()).postDelayed({

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isNotificationListenerEnable
                    &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, PermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } else {
                if (isNotificationListenerEnable
                    &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, PermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

        }, 2000)


    }

    private val isNotificationListenerEnable: Boolean
        get() {
            val contentResolver = this.contentResolver
            val enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            val packageName = this.packageName

            return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(
                packageName
            ))
        }
}