package com.example.recovermessages.ui.home.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recovermessages.models.CacheFiles
import com.example.recovermessages.utils.ManagerMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ViewModelCache(application: Application) : AndroidViewModel(application) {

    private var dataList: MutableList<CacheFiles> = mutableListOf()
    var dataObserver: MutableLiveData<List<CacheFiles>> = MutableLiveData()

    fun execute() {
        viewModelScope.launch(Dispatchers.IO) {
            prepareData()
            postData()
        }
    }

    private fun postData() {
        dataObserver.postValue(dataList)

    }

    private fun isCacheFile(fullPath: String): Boolean {
        return fullPath.endsWith(".cached")
    }

    private fun prepareData() {
        val dir = File(
            Environment.getExternalStorageDirectory(),
            "Recover Deleted Messages/.Cached Files"
        )
        val FileList = dir.listFiles()
        var data: CacheFiles
        var filePath: String
        var fileName: String
        var fileFormat: String
        var fileSize: String?
        var fileDuration: String?
        if (FileList != null) {
            for (selectedFile in FileList) {
                if (isCacheFile(selectedFile.name.lowercase(Locale.getDefault()))) {
                    try {

                        filePath = selectedFile.absolutePath
                        fileName = selectedFile.name
                        fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1)
                        fileSize = ManagerMedia.getFileSize(selectedFile.length())
                        //fileDuration = ManagerMedia.getDuration(filePath)

                        val file = File(filePath)

                        val lastModDate = Date(selectedFile.lastModified())
                        val lastModified = Date(lastModDate.toString())
                        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
                        val formattedDateString = formatter.format(lastModified)
                        val dateTime = SimpleDateFormat("dd/MMM/yyyy hh:mm a")
                        val formattedDateTime = dateTime.format(lastModified)
                        data = CacheFiles(
                            fileName,
                            "cache",
                            fileSize,
                            file,
                            false,
                            file.lastModified()
                        )
                        dataList.add(data)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        }
    }
}