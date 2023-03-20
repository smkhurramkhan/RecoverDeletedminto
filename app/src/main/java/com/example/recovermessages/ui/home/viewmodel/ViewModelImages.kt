package com.example.recovermessages.ui.home.viewmodel

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.utils.ManagerMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ViewModelImages : ViewModel() {

    var outputDataSet: MutableLiveData<List<ModelFiles>> = MutableLiveData()
    private var dataList: MutableList<ModelFiles> = mutableListOf()

    fun exectueProcess() = viewModelScope.launch(Dispatchers.IO) {
        val job = launch {
            doInBackground()
        }
        job.invokeOnCompletion {
            postExecute()
        }

    }

    private fun postExecute() {
        outputDataSet.postValue(dataList)
    }


    private fun doInBackground() {
        prepareAudioData()
    }

    val inputAudioDataSet: MutableLiveData<List<ModelFiles>>
        get() = outputDataSet


    private fun isAudioFile(fullPath: String): Boolean {
        return fullPath.endsWith(".jpg") ||
                fullPath.endsWith(".jpeg") ||
                fullPath.endsWith(".png")
    }

    private fun prepareAudioData() {
        val dir = File(Environment.getExternalStorageDirectory(), "Recover Deleted Messages")
        val FileList = dir.listFiles()
        var data: ModelFiles
        var filePath: String
        var fileName: String
        var fileFormat: String
        var fileSize: String?
        var fileDuration: String?
        if (FileList != null) {
            for (selectedFile in FileList) {
                if (isAudioFile(selectedFile.name.lowercase(Locale.getDefault()))) {
                    try {
                        filePath = selectedFile.absolutePath
                        fileName = selectedFile.name
                        fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1)
                        fileSize = ManagerMedia.getFileSize(selectedFile.length())
                        fileDuration = ManagerMedia.getDuration(filePath)
                        val lastModDate = Date(selectedFile.lastModified())
                        val lastModified = Date(lastModDate.toString())
                        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
                        val formattedDateString = formatter.format(lastModified)
                        val dateTime = SimpleDateFormat("dd/MMM/yyyy hh:mm a")
                        val formattedDateTime = dateTime.format(lastModified)
                        data = ModelFiles(
                            fileName,
                            "image",
                            fileSize,
                            File(filePath),
                            false

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