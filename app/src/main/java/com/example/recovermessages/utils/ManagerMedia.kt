package com.example.recovermessages.utils

import android.media.MediaMetadataRetriever
import java.io.FileInputStream
import java.text.DecimalFormat

object ManagerMedia {
    fun getDuration(path: String?): String {
        var dur = "00:00"
        try {
            val hours: String
            val minutes: String
            val seconds: String
            val retriever = MediaMetadataRetriever()
            val inputStream = FileInputStream(path)
            retriever.setDataSource(inputStream.fd)
            val time =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInMillisec = time!!.toLong()
            val duration = timeInMillisec / 1000
            val hrs = duration / 3600
            val min = (duration - hrs * 3600) / 60
            val sec = duration - (hrs * 3600 + min * 60)
            hours = if (hrs < 1) {
                ""
            } else if (hrs > 9) {
                "$hrs:"
            } else {
                "0$hrs:"
            }
            minutes = if (min < 1) {
                "00:"
            } else if (min > 9) {
                "$min:"
            } else {
                "0$min:"
            }
            seconds = if (sec < 1) {
                "00"
            } else if (sec > 9) {
                sec.toString()
            } else {
                "0$sec"
            }
            dur = hours + "" + minutes + "" + seconds
        } catch (e: Exception) {
            e.printStackTrace()
            return dur
        }
        return dur
    }

    fun getFileSize(size: Long): String {
        val fileSize: String
        val b = size.toDouble()
        val kb = size / 1024.0
        val mb = size / 1024.0 / 1024.0
        val gb = size / 1024.0 / 1024.0 / 1024.0
        val tb = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.0")
        fileSize = if (tb > 1) {
            dec.format(tb) + " TB"
        } else if (gb > 1) {
            dec.format(gb) + " GB"
        } else if (mb > 1) {
            dec.format(mb) + " MB"
        } else if (kb > 1) {
            dec.format(kb) + " KB"
        } else {
            dec.format(b) + " Bytes"
        }
        return fileSize
    }
}