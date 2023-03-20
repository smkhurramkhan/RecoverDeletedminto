package com.example.recovermessages.ui.messages

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recovermessages.databinding.ActivityMessegesBinding
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.models.DataModel
import com.example.recovermessages.ui.BaseActivity
import com.example.recovermessages.ui.messages.adapters.MessagesAdapter
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesActivity : BaseActivity() {
    private lateinit var messagesBinding: ActivityMessegesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messagesBinding = ActivityMessegesBinding.inflate(layoutInflater)
        setContentView(messagesBinding.root)

        val nameString = intent.getStringExtra("name")
        val packString = intent.getStringExtra("pack")

        nameString?.let { initToolbar(it) }

        loadData(nameString, packString)

        title = nameString

    }

    private fun initToolbar(name: String) {
        setSupportActionBar(messagesBinding.toolbar)
        messagesBinding.toolbar.title = name
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadData(name: String?, pack: String?) {
        lifecycleScope.launch {
            onPreExecute()
            val backgroundTask = doInBackground(name, pack)
            onPostExecute(backgroundTask)

        }
    }

    private suspend fun onPostExecute(backgroundList: List<DataModel>) {
        return withContext(Dispatchers.Main) {
            messagesBinding.progressBar.hide()

            val linearLayoutManager = LinearLayoutManager(this@MessagesActivity)
            linearLayoutManager.stackFromEnd = true
            messagesBinding.recyclerView.layoutManager = linearLayoutManager
            messagesBinding.recyclerView.adapter = MessagesAdapter(
                this@MessagesActivity,
                backgroundList
            )
        }
    }

    private suspend fun doInBackground(mString: String?, mPack: String?): List<DataModel> {
        return withContext(Dispatchers.IO) {
            AppDatabase(this@MessagesActivity)
                .getMsg(mString, mPack)
        }
    }

    private suspend fun onPreExecute() {
        return withContext(Dispatchers.Main) {
            messagesBinding.progressBar.show()
        }
    }
}