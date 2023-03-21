package com.example.recovermessages.ui.chooseapps

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recovermessages.R
import com.example.recovermessages.databinding.ActivityChooseAppsBinding
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.chooseapps.adapters.AppListAdapter
import com.example.recovermessages.ui.home.HomeActivity
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import com.example.recovermessages.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ChooseAppsActivity : BaseActivity() {
    private var addedPackages = mutableListOf<String>()
    private var addedPackagesToCompare = mutableListOf<String>()
    private var appListAdapter: AppListAdapter? = null
    private var appPackList = mutableListOf<String>()
    private var asking = false
    private var handler: Handler? = null
    private var key = 0
    private var utils: Utils? = null

    private lateinit var binding: ActivityChooseAppsBinding

    private fun notSystemApps(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo.flags and 1 != 0
    }

    private fun saveSetup() {
        val edit = getSharedPreferences("SETUP", 0).edit()
        edit.putBoolean("setup", true)
        edit.apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun saveToDB() {
        backgroundTask()
    }

    private fun setUpAppList() {
        binding.main.removeAllViews()
        binding.progress.show()

        AsyncLayoutInflater(this).inflate(
            R.layout.app_list_recycler,
            LinearLayout(this)
        ) { view: View, i: Int, viewGroup: ViewGroup? ->
            binding.progress.hide()
            binding.main.addView(view)
            setupAppListRecycler(
                view.findViewById(R.id.recycle),
                findViewById(R.id.button)
            )
        }
    }

    private fun setupAppListRecycler(recyclerView: RecyclerView, button: Button) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val includeList = ArrayList<String>()
        includeList.add("com.whatsapp")
        includeList.add("com.whatsapp.w4b")
        includeList.add("com.gbwhatsapp")
        includeList.add("com.facebook.lite")
        includeList.add("com.facebook.orca")
        includeList.add("com.facebook.mlite")
        includeList.add("com.instagram.android")
        includeList.add("com.instagram.lite")
        includeList.add("org.telegram.messenger")
        Thread {
            val arrayList: MutableList<PackageInfo> = ArrayList()
            val arrayList2: MutableList<PackageInfo> = ArrayList()
            val packageManager = packageManager
            val packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            val installedPackages: MutableList<PackageInfo> = ArrayList()
            for (info in packages) {
                if (includeList.contains(info.packageName)) {
                    installedPackages.add(info)
                }
            }
            var i = 0
            while (i < installedPackages.size) {
                if (!(notSystemApps(installedPackages[i]) ||
                            arrayList.contains(installedPackages[i]))
                ) {
                    arrayList.add(installedPackages[i])
                }
                i++
            }
            if (key == 1) {
                for (str2 in AppDatabase(
                    this
                ).allPackages) {
                    try {
                        arrayList2.add(
                            packageManager.getPackageInfo(
                                str2,
                                PackageManager.GET_PERMISSIONS
                            )
                        )
                        appPackList.add(str2)
                        addedPackages.add(str2)
                        addedPackagesToCompare.add(str2)
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }
            }
            appListAdapter = AppListAdapter(
                arrayList,
                this@ChooseAppsActivity, addedPackages
            )
            handler?.post {
                recyclerView.adapter = appListAdapter
                binding.progress.hide()
                button.setOnClickListener {
                    if (appPackList.size > 0) {
                        saveToDB()
                    } else {
                        Toast.makeText(
                            this,
                            "Please choose atLeast one application", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }.start()
    }

    private fun setupNotificationPermission() {
        val alertDialog = AlertDialog.Builder(this@ChooseAppsActivity)
        alertDialog.setTitle("Notification Access")
        alertDialog.setMessage(Html.fromHtml("Notification access allows " + getString(R.string.app_name) + " to read and backup deleted messages &amp; media."))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Enable") { dialogInterface: DialogInterface?, n: Int ->
            asking = true
            try {
                if (Build.VERSION.SDK_INT >= 22) {
                    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                } else {
                    try {
                        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                    } catch (ex: Exception) {
                        finish()
                        startActivity(Intent(this@ChooseAppsActivity, HomeActivity::class.java))
                    }
                }
            } catch (ex2: Exception) {
                finish()
                startActivity(Intent(this@ChooseAppsActivity, HomeActivity::class.java))
            }
        }
        alertDialog.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        val alertDialog1 = alertDialog.create()
        alertDialog1.show()
    }

    private fun termsSetup() {
        val alertDialog = AlertDialog.Builder(this@ChooseAppsActivity)
        alertDialog.setTitle("Terms & Conditions")
        alertDialog.setMessage(Html.fromHtml(getString(R.string.app_name) + " is a backup &amp; utility app. It is designed to provide backup services for notifications, specific location of storage &amp; some utility features. This app uses common Android APIs to achieve services. It's not designed to interfere with other apps or services.<br>However the use of app may be incompatible with terms of use of other apps. If this kind of incompatibility occurs, you shall not use this app.<br><br>Using this app you accept its <a href='#'>Terms &amp; Conditions</a> and <a href='#'>Privacy Policy</a>"))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Agree") { dialogInterface: DialogInterface, n: Int -> dialogInterface.cancel() }
        alertDialog.setNegativeButton("Disagree") { dialog: DialogInterface?, which: Int -> finish() }
        val alertDialog1 = alertDialog.create()
        alertDialog1.show()
        (alertDialog1.findViewById<View>(android.R.id.message) as TextView).movementMethod =
            LinkMovementMethod.getInstance()
    }

    fun mAddToList(str: String) {
        if (appPackList.contains(str)) {
            appPackList.remove(str)
        } else {
            appPackList.add(str)
        }
        val sb = " size " + appPackList.size
        Timber.d("addlistlog %s", sb)
    }

    override fun onBackPressed() {
        if (key == 1) {
            finish()
            super.onBackPressed()
            startActivity(Intent(this, HomeActivity::class.java))
            return
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        utils = Utils(this)
        key = intent.getIntExtra("key", 0)
        if (key == 1) {
            setUpAppList()
        } else {
            setUpAppList()
            termsSetup()
        }
    }


    private fun backgroundTask() {
        lifecycleScope.launch {
            onPreExecute()
            doInBackground()
            onPostExecute()
        }
    }

    private suspend fun onPreExecute() {
        withContext(Dispatchers.Main) {
            Timber.d("OnPreExecute")
        }
    }

    private suspend fun doInBackground() {
        withContext(Dispatchers.IO) {
            val appDatabase = AppDatabase(this@ChooseAppsActivity)
            if (key == 1) {
                val sb = "size  " + addedPackagesToCompare.size
                Timber.d("keylog %s", sb)
                val list = ArrayList<String>()
                val iterator: Iterator<String> = addedPackagesToCompare.iterator()
                var i: Int
                while ((if (iterator.hasNext()) 1 else 0).also { i = it } != 0) {
                    val s = iterator.next()
                    if (!appPackList.contains(s)) {
                        list.add(s)
                        Timber.d("keylog %s", s)
                    } else {
                        val sb2 = "dont remove $s"
                        Timber.d("keylog %s", sb2)
                    }
                }
                while (i < appPackList.size) {
                    appDatabase.addPackages(appPackList[i])
                    ++i
                }
                appDatabase.removePackageAndMsg(list)
            } else {
                for (j in appPackList.indices) {
                    appDatabase.addPackages(appPackList[j])
                }
            }
        }
    }

    private suspend fun onPostExecute() {
        withContext(Dispatchers.Main) {
            if (key == 1) {
                finish()
                val intent = Intent(getString(R.string.noti_obserb))
                intent.putExtra(getString(R.string.noti_obserb), "update")
                LocalBroadcastManager.getInstance(this@ChooseAppsActivity).sendBroadcast(intent)
                startActivity(
                    Intent(
                        this@ChooseAppsActivity,
                        HomeActivity::class.java
                    )
                )
            } else {
                if (!isNotificationListenerEnable)
                    setupNotificationPermission()
                else
                startActivity(Intent(this@ChooseAppsActivity, HomeActivity::class.java))
                finish()
            }
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

    override fun onResume() {
        super.onResume()
        if (asking && utils?.isNotificationEnabled == true) {
            saveSetup()
            val intent = Intent(getString(R.string.noti_obserb))
            intent.putExtra(getString(R.string.noti_obserb), "update")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }
}