package com.example.recovermessages.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.recovermessages.databinding.ActivityPermissionBinding
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.main.MainActivity
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions


class PermissionActivity : BaseActivity() {
    private lateinit var permissionBinding: ActivityPermissionBinding
    private var notificationResultLauncher: ActivityResultLauncher<Intent>? = null
    private var isCancelled = false
    private var isPermissionAllowed = false
    var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storagePermission33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionBinding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(permissionBinding.root)


        permissionBinding.notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isCancelled) {
                if (isNotificationListenerEnable) {
                    permissionBinding.notificationSwitch.isChecked = true
                } else {
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                    notificationResultLauncher?.launch(intent)
                }
            } else {
                permissionBinding.notificationSwitch.isChecked = false
                isCancelled = false
            }
        }


        permissionBinding.permissionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isPermissionAllowed) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Permissions.check(this, storagePermission33,
                        null /*rationale*/, null /*options*/, object : PermissionHandler() {
                            override fun onGranted() {
                                permissionBinding.permissionSwitch.isChecked = true
                            }

                            override fun onDenied(
                                context: Context?,
                                deniedPermissions: ArrayList<String>?
                            ) {
                                super.onDenied(context, deniedPermissions)
                                isPermissionAllowed = true
                                permissionBinding.permissionSwitch.isChecked = false

                            }
                        })
                }else{
                    Permissions.check(this, permissions,
                        null /*rationale*/, null /*options*/, object : PermissionHandler() {
                            override fun onGranted() {
                                permissionBinding.permissionSwitch.isChecked = true
                            }

                            override fun onDenied(
                                context: Context?,
                                deniedPermissions: ArrayList<String>?
                            ) {
                                super.onDenied(context, deniedPermissions)
                                isPermissionAllowed = true
                                permissionBinding.permissionSwitch.isChecked = false

                            }
                        })
                }


            } else {
                permissionBinding.permissionSwitch.isChecked = false
                isPermissionAllowed = false
            }
        }


        notificationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    permissionBinding.notificationSwitch.isChecked = isNotificationListenerEnable
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    isCancelled = true
                    permissionBinding.notificationSwitch.isChecked = false
                }
            }

        permissionBinding.nextButton.setOnClickListener {
            if (permissionBinding.permissionSwitch.isChecked &&
                permissionBinding.notificationSwitch.isChecked
            ) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please grant all permission", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (isNotificationListenerEnable) {
            permissionBinding.notificationSwitch.isChecked = true
        }
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