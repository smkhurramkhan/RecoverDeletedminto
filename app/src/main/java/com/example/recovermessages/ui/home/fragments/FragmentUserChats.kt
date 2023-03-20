package com.example.recovermessages.ui.home.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recovermessages.databinding.UsersFragmentBinding
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.models.userModel
import com.example.recovermessages.ui.home.adapters.UsersAdapter
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FragmentUserChats : Fragment() {

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("onreceivelog %s", intent.getStringExtra("refresh"))
            updateList()
        }
    }


    private var list: List<userModel>? = null
    private var pack: String? = null
    private var usersAdapter: UsersAdapter? = null

    fun newInstance(str: String?): FragmentUserChats {
        val fragmentUserChats = FragmentUserChats()
        val bundle = Bundle()
        bundle.putString("pack", str)
        fragmentUserChats.arguments = bundle
        return fragmentUserChats
    }

    private lateinit var usersBinding: UsersFragmentBinding

    override fun onCreateView(
        layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?
    ): View {
        usersBinding = UsersFragmentBinding.inflate(layoutInflater)
        return usersBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("refresh"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }


    private fun loadData() {
        lifecycleScope.launch() {
            onPreExecute()
            doInBackground()
            onPostExecute()
        }
    }

    private suspend fun onPreExecute() {
        return withContext(Dispatchers.Main) {
            usersBinding.progressbar.show()
        }
    }

    private suspend fun doInBackground() {
        return withContext(Dispatchers.IO) {
            try {
                Thread.sleep(200)
                list = AppDatabase(context).getHomeList(
                    requireArguments().getString("pack")
                )
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }

    private suspend fun onPostExecute() {
        return withContext(Dispatchers.Main) {
            usersBinding.progressbar.hide()
            if (list?.isNotEmpty() == true) {
                usersBinding.usersRecycler.layoutManager = LinearLayoutManager(requireContext())
                usersAdapter = UsersAdapter(requireContext(), list?.toMutableList()!!, pack!!)
                usersBinding.usersRecycler.adapter = usersAdapter
                usersBinding.emptyText.hide()
            } else {
                usersBinding.emptyText.show()
            }
        }
    }


    private fun updateList() {
        lifecycleScope.launch {
            val updatedList = updateListDoInBackground()
            updateListInPostExecute(updatedList)
        }
    }

    private suspend fun updateListDoInBackground(): List<userModel>? {
        return withContext(Dispatchers.IO) {
            AppDatabase(context).getHomeList(requireArguments().getString("pack"))
        }
    }

    private suspend fun updateListInPostExecute(userModels: List<userModel>?) {
        return withContext(Dispatchers.Main) {
            if (userModels != null) {
                if (userModels.isNotEmpty()) {
                    usersBinding.usersRecycler.layoutManager =
                        LinearLayoutManager(requireContext())
                    usersBinding.usersRecycler.adapter = UsersAdapter(
                        requireContext(), userModels.toMutableList(), pack!!
                    )
                }
            }
        }
    }


}