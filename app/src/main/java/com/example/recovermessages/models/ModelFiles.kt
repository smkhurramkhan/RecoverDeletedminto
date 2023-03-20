package com.example.recovermessages.models

import java.io.File

class ModelFiles(
    var filename: String,
    var type: String,
    var size: String,
    var file: File,
    var isSelected: Boolean
)