package com.example.chat_app1204.data.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun uriToMultipart(context: Context, uri: Uri, partName: String = "images"): MultipartBody.Part {

    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = file
        .asRequestBody("image/*".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}