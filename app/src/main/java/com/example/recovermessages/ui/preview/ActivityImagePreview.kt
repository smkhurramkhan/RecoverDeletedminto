package com.example.recovermessages.ui.preview

import android.os.Bundle
import androidx.core.content.FileProvider
import com.example.recovermessages.databinding.ActivityShowImageBinding
import com.example.recovermessages.ui.BaseActivity
import java.io.File

class ActivityImagePreview : BaseActivity() {
    private var vaultImagePath: String? = null
    private lateinit var binding: ActivityShowImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("imagepath") != null) {
            vaultImagePath = intent.getStringExtra("imagepath")
        } else finish()

        val shareFile = vaultImagePath?.let { File(it) }
        val photoURI = shareFile?.let {
            FileProvider.getUriForFile(
                this, "$packageName.provider",
                it
            )
        }
        binding.imgShow.setImageURI(photoURI)
    }
}