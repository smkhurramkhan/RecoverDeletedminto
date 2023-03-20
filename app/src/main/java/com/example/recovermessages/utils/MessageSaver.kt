package com.example.recovermessages.utils

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.recovermessages.R
import com.example.recovermessages.db.AppDatabase
import com.example.recovermessages.services.MultiLanguageComparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MessageSaver() {

    var mDelete = false
    var mContext: Context? = null


    fun saveToDB(
        context: Context,
        notificationTitle: String,
        notificationText: String,
        packageName: String
    ) {

        val comparator = MultiLanguageComparator()
        mDelete = comparator.contain(
            notificationText,
            context.getString(R.string.thismsgwasdeleted)
        )

        mContext = context

        GlobalScope.launch {
            onPreExecute()
            val mDoInBg =
                doInBackground(mDelete, context, notificationTitle, notificationText, packageName)
            onPostExecute(mDoInBg)

        }
    }

    private suspend fun onPreExecute() {
        return withContext(Dispatchers.Main) {
            Timber.d("onPreExecute")
        }
    }

    private suspend fun doInBackground(
        del: Boolean,
        context: Context,
        notificationTitle: String,
        notificationText: String,
        packageName: String
    ): Long {
        Timber.d("doInBackground")
        return withContext(Dispatchers.IO) {
            val n: Long? = null
            val s: String = packageName
            val s2: String = notificationTitle
            var value: Long?
            try {
                Timber.d("oldtitle: %s", s2)
                val s3: String = notificationText
                Timber.d("$s2 $s3")
                var s4 = s3
                Timber.d("testoldtitle: %s", s3)
                val comparator = MultiLanguageComparator()
                if (comparator.contain(
                        s3,
                        context.getString(R.string.thismsgwasdeleted)
                    )
                ) {
                    s4 = "\ud83d\udc46 new deleted message detected\u26a0"
                }
                val contains = if (s2.contains("messages):")) 1 else 0
                val s5: String
                if (contains != 0) {
                    s5 = s2.substring(0, s2.indexOf(" ("))
                    Timber.d("newtitle: %s", s5)
                } else if (s2.contains(":")) {
                    s5 = s2.substring(contains, s2.indexOf(":"))
                } else {
                    s5 = s2
                }
                value = n
                if (s5 != "WhatsApp") {
                    value = n
                    if (!s4.contains("new messages") && !s4.contains("deleted")) {
                        val format = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())
                        val simpleDateFormat = SimpleDateFormat("MMMM dd", Locale.US)
                        val substring = format.substring(11, 13)
                        val substring2 = format.substring(14, 16)
                        val sb4 = StringBuilder()
                        sb4.append(simpleDateFormat.format(Calendar.getInstance().time))
                        sb4.append(" ")
                        sb4.append(substring)
                        sb4.append(":")
                        sb4.append(substring2)
                        val string = sb4.toString()
                        val AppDatabase =
                            AppDatabase(context)
                        value = n
                        if (!AppDatabase.isPresent(s5, s4, s)) {
                            value = AppDatabase.addData(s5, s, s4, string, format)
                        }
                    } else if (s4.contains("deleted")) {
                        value = 1L
                    }
                }
            } catch (ex: Exception) {
                Timber.d("savemsg bg %s", ex.toString())
                value = n
            }
            value!!
        }
    }

    private suspend fun onPostExecute(mDoInBg: Long) {
        return withContext(Dispatchers.Main) {
            Timber.d("numadded: $mDoInBg")
            if (mDoInBg > 0L) {
                try {
                    if (mDelete) {
                        SendNotification().sendBackground(
                            mContext!!,
                            mContext?.getString(R.string.message_deleted),
                            mContext?.getString(R.string.message_was_deleted)
                        )
                    }
                    val intent = Intent("refresh")
                    intent.putExtra("refresh", "refresh")
                    LocalBroadcastManager.getInstance(mContext!!)
                        .sendBroadcast(intent)
                } catch (ex: java.lang.Exception) {
                    Timber.d("savemsg post%s", ex.toString())
                }
            }
        }
    }
}