package com.example.recovermessages.ui.home.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.recovermessages.R
import com.example.recovermessages.databinding.HomeFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import timber.log.Timber
import java.io.File

class FragmentHome : Fragment() {
    private var pack: String? = null
    private val tabIcons = intArrayOf(
        R.drawable.ic_chats, R.drawable.ic_images, R.drawable.ic_videos, R.drawable.ic_docs
    )
    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storagePermission33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )


    fun newInstance(str: String?): FragmentHome {
        val homeFragment = FragmentHome()
        val bundle = Bundle()
        bundle.putString("pack", str)
        homeFragment.arguments = bundle
        return homeFragment
    }

    private lateinit var homeBinding: HomeFragmentBinding
    override fun onCreateView(
        layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?
    ): View {
        homeBinding = HomeFragmentBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Permissions.check(requireContext(),
                storagePermission33,
                null /*rationale*/,
                null /*options*/,
                object : PermissionHandler() {
                    override fun onGranted() {
                        val externalStorageDirectory = Environment.getExternalStorageDirectory()
                        val file1 = File(
                            externalStorageDirectory,
                            requireContext().resources.getString(R.string.app_name)
                        )
                        if (!file1.exists()) {
                            file1.mkdirs()
                            Timber.d("directory  created")
                        } else {
                            Timber.d("directory already created")
                        }
                    }

                })
        } else {
            Permissions.check(
                requireContext(),
                permissions,
                null,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        val externalStorageDirectory = Environment.getExternalStorageDirectory()
                        val file1 = File(
                            externalStorageDirectory,
                            requireContext().resources.getString(R.string.app_name)
                        )
                        if (!file1.exists()) {
                            file1.mkdirs()
                            Timber.d("directory  created")
                        } else {
                            Timber.d("directory already created")
                        }
                    }
                })
        }

        return homeBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstTab = homeBinding.tab.newTab()
        firstTab.text = getString(R.string.frag_chats)
        val secondTab = homeBinding.tab.newTab()
        secondTab.text = getString(R.string.frag_images)
        val forthTab = homeBinding.tab.newTab()
        forthTab.text = getString(R.string.frag_videos)
        val fifthTab = homeBinding.tab.newTab()
        fifthTab.text = getString(R.string.frag_docs)
        homeBinding.tab.addTab(firstTab)
        homeBinding.tab.addTab(secondTab)
        homeBinding.tab.addTab(forthTab)
        homeBinding.tab.addTab(fifthTab)
        homeBinding.tab.setTabTextColors(
            ContextCompat.getColor(
                requireContext(), R.color.dinwhite
            ), ContextCompat.getColor(
                requireContext(), R.color.navy
            )
        )
        homeBinding.tab.setSelectedTabIndicatorHeight(0)
        setupTabIcons(homeBinding.tab)
        homeBinding.pager.adapter = FragAdapter(
            childFragmentManager
        )
        try {
            homeBinding.tab.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    try {
                        sendToCleanSaverAdapter()
                        sendToCleanHome()
                        homeBinding.pager.currentItem = tab.position
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            homeBinding.pager.addOnPageChangeListener(TabLayoutOnPageChangeListener(homeBinding.tab))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun setupTabIcons(tabs: TabLayout) {
        tabs.getTabAt(0)?.setIcon(tabIcons[0])
        tabs.getTabAt(1)?.setIcon(tabIcons[1])
        tabs.getTabAt(2)?.setIcon(tabIcons[2])
        tabs.getTabAt(3)?.setIcon(tabIcons[3])
    }


    private fun sendToCleanHome() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent("update").putExtra("update", 2))
    }

    private fun sendToCleanSaverAdapter() {
        Handler().postDelayed({
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("cleansaver"))
        }, 500)
    }

    private inner class FragAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(
        fragmentManager!!
    ) {
        override fun getCount(): Int {
            return 4
        }

        override fun getItem(i: Int): Fragment {
            return when (i) {
                0 -> {
                    FragmentUserChats().newInstance(pack)
                }
                1 -> {
                    FragmentImages().newInstance(pack)
                }
                2 -> {
                    FragmentVideos().newInstance(pack)
                }
                else -> {
                    FragmentDocs().newInstance(pack)
                }
            }
        }
    }
}