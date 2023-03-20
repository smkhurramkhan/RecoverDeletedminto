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
import java.util.*

class ViewModelDocs : ViewModel() {

    private var outputDataSet: MutableLiveData<List<ModelFiles>> = MutableLiveData()
    private var dataList: MutableList<ModelFiles> = mutableListOf()

    fun executeProcess() = viewModelScope.launch(Dispatchers.IO) {
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
        return fullPath.endsWith(".doc") ||
                fullPath.endsWith(".docx") ||
                fullPath.endsWith(".txt") ||
                fullPath.endsWith(".ppt") ||
                fullPath.endsWith(".pptx") ||
                fullPath.endsWith(".xls") ||
                fullPath.endsWith(".xlsx") ||
                fullPath.endsWith(".pdf") ||
                fullPath.endsWith(".zip") ||
                fullPath.endsWith(".rar")
    }

    private fun prepareAudioData() {
        val dir = File(Environment.getExternalStorageDirectory(), "Recover Deleted Messages")
        val mFilesList = dir.listFiles()
        var data: ModelFiles
        var filePath: String
        var fileName: String
        var fileSize: String?
        if (mFilesList != null) {
            for (selectedFile in mFilesList) {
                if (isAudioFile(selectedFile.name.lowercase(Locale.getDefault()))) {
                    try {
                        filePath = selectedFile.absolutePath
                        fileName = selectedFile.name
                        fileSize = ManagerMedia.getFileSize(selectedFile.length())
                        data = ModelFiles(
                            fileName,
                            "document",
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