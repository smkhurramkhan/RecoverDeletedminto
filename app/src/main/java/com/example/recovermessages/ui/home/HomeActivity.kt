package com.example.recovermessages.ui.home

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.example.recovermessages.R
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.models.CacheFiles
import com.example.recovermessages.services.NotifyListener
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.chooseapps.ChooseAppsActivity
import com.example.recovermessages.ui.chooselanguage.ChooseLanguageActivity
import com.example.recovermessages.ui.home.fragments.FragmentHome
import com.example.recovermessages.ui.home.fragments.FragmentUserChats
import com.example.recovermessages.ui.home.viewmodel.ViewModelCache
import com.example.recovermessages.ui.onboarding.OnBoardingActivity
import com.example.recovermessages.utils.SharedPrefs
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var pager: ViewPager
    private lateinit var viewmodel: ViewModelCache
    private lateinit var cacheList: MutableList<CacheFiles>
    var aSwitch: SwitchCompat? = null
    private var sharedPrefs: SharedPrefs? = null
    private fun ask() {
        try {
            if (Build.VERSION.SDK_INT >= 22) {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            } else {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createFolder() {
        try {
            val file = File(
                Environment.getExternalStorageDirectory().absolutePath, getString(R.string.app_name)
            )
            if (!file.exists()) {
                val b = file.mkdir()
                Timber.d("value of %s", b)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun navigationRelated() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null

        val menu = navigationView.menu
        aSwitch = MenuItemCompat.getActionView(menu.findItem(R.id.nav_switch))
            .findViewById(R.id.switch_id)
        if (sharedPrefs?.loadNightMode() == true) {
            aSwitch?.isChecked = true
        }

        aSwitch?.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            Timber.d("switch state is ${isChecked}")
            sharedPrefs?.setNightModeState(isChecked)
            updateTheme()
        }
    }

    private fun setupPager() {
        try {
            pager = findViewById(R.id.pager)
            val tabLayout = findViewById<TabLayout>(R.id.tab)
            val allPackages = AppDatabase(this).allPackages
            for (i in allPackages.indices) {
                val packageManager = packageManager
                val packageInfo: PackageInfo =
                    packageManager.getPackageInfo(allPackages[i], PackageManager.GET_META_DATA)
                tabLayout.addTab(
                    tabLayout.newTab()
                        .setText(packageManager.getApplicationLabel(packageInfo.applicationInfo))
                        .setIcon(packageManager.getApplicationIcon(packageInfo.applicationInfo))
                )
            }
            tabLayout.isTabIndicatorFullWidth = true
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.dinwhite),
                ContextCompat.getColor(this, R.color.white)
            )
            pager.adapter = FragAdapter(
                supportFragmentManager, allPackages
            )
            if (intent.getIntExtra("pos", 0) == 1) {
                pager.currentItem = 1
            }
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    pager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            pager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
            findViewById<View>(R.id.progressBar2).visibility = View.GONE
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private class FragAdapter(
        fragmentManager: FragmentManager?, private val packs: ArrayList<String>
    ) : FragmentStatePagerAdapter(
        fragmentManager!!
    ) {
        override fun getItem(i: Int): Fragment {
            var fragment: Fragment? = null
            for (i2 in packs.indices) {
                fragment = if (packs[i].contains("com.whatsapp")) {
                    val fragmentHome_w = FragmentHome().newInstance(packs[i])
                    fragmentHome_w
                } else if (packs[i].contains("com.whatsapp.w4b")) {
                    val fragmentHome_wb = FragmentHome().newInstance(packs[i])
                    fragmentHome_wb
                } else if (packs[i].contains("com.gbwhatsapp")) {
                    val fragmentHome_gb = FragmentHome().newInstance(packs[i])
                    fragmentHome_gb
                } else {
                    FragmentUserChats().newInstance(packs[i])
                }
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return packs.size
        }
    }

    private fun toggleNotificationListenerService() {
        val packageManager = packageManager
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotifyListener::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotifyListener::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val isDrawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START)
        if (isDrawerOpen) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = SharedPrefs(this)
        setContentView(R.layout.activity_home)
        cacheList = ArrayList()
        Handler().postDelayed({
            navigationRelated()
            tryReconnectService()
            setupPager()
        }, 1)
        val relativeLayout = findViewById<FrameLayout>(R.id.mainContainer)
        viewmodel = ViewModelProvider(this).get(ViewModelCache::class.java)
        viewmodel.execute()
        viewmodel.dataObserver.observe(this) {
            cacheList.addAll(it)

            var diffInHours: Long = 0
            for (i in 0 until cacheList.lastIndex) {
                val diffInMilliSec =
                    System.currentTimeMillis() - cacheList.get(i).file.lastModified()

                diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec)

                Timber.d("difference in diffInMin %s", diffInHours)

            }




            if (diffInHours >= 2) {
                lifecycleScope.launch(Dispatchers.IO) {
                    for (i in 0 until cacheList.lastIndex) {
                        val file = cacheList[i].file
                        file.delete()
                    }
                }
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId) {
            R.id.share -> {
                val intent = Intent("android.intent.action.SEND")
                intent.type = "text/plain"
                val sb =
                    resources.getString(R.string.share) + " https://play.google.com/store/apps/details?id=" + packageName
                intent.putExtra("android.intent.extra.TEXT", sb)
                startActivity(intent)
            }
            R.id.rate -> {
                val intent = Intent("android.intent.action.VIEW")
                val sb = "https://play.google.com/store/apps/details?id=$packageName"
                intent.data = Uri.parse(sb)
                startActivity(intent)
            }
            R.id.contact -> {
                val intent3 = Intent(
                    "android.intent.action.SENDTO", Uri.fromParts(
                        "mailto", getString(
                            R.string.email
                        ), null
                    )
                )
                intent3.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name))
                startActivity(intent3)
            }
            R.id.help -> {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }
            R.id.restart -> {
                if (!isNotificationListenerEnable)
                    ask()
                else
                    Toast.makeText(this, "Permission already granted", Toast.LENGTH_LONG).show()
            }
            R.id.change_lang -> {
                startActivity(Intent(this, ChooseLanguageActivity::class.java))
            }
            R.id.manage -> {
                val intent = Intent(this, ChooseAppsActivity::class.java)
                intent.putExtra("key", 1)
                finish()
                startActivity(intent)
            }
        }
        (findViewById<View>(R.id.drawer_layout) as DrawerLayout).closeDrawer(
            GravityCompat.START
        )
        return true
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        val PERMISSION_REQUEST_CODE = 111
        if (i == PERMISSION_REQUEST_CODE && iArr.isNotEmpty()) {
            if (iArr[0] == 0) {
                createFolder()
                val intent = Intent(getString(R.string.files))
                intent.putExtra(
                    getString(R.string.files), getString(R.string.remove_permission_framgent)
                )
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            } else if (iArr[0] == RESULT_OK) {
                val format = String.format(
                    getString(R.string.format_request_permision), getString(
                        R.string.app_name
                    )
                )
                val alertDialogBuilder = AlertDialog.Builder(this@HomeActivity)
                alertDialogBuilder.setTitle("Permission Required!" as CharSequence)
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setMessage(format as CharSequence).setNeutralButton(
                    "Grant" as CharSequence
                ) { dialogInterface, n ->
                    ActivityCompat.requestPermissions(
                        this@HomeActivity, arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"), 111
                    )
                }.setNegativeButton(
                    "Cancel" as CharSequence
                ) { dialogInterface: DialogInterface, n: Int ->
                    dialogInterface.dismiss()
                    finish()
                }
                alertDialogBuilder.show()
            }
        }
    }

    private fun tryReconnectService() {
        toggleNotificationListenerService()
        if (Build.VERSION.SDK_INT >= 24) {
            NotificationListenerService.requestRebind(
                ComponentName(
                    this, NotifyListener::class.java
                )
            )
        }
    }

    private fun updateTheme() {
        val intent = Intent(this@HomeActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
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