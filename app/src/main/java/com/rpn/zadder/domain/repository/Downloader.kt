package com.rpn.zadder.domain.repository

import android.net.Uri

interface Downloader {
    fun downloadFile(url: String, fileName: String?, onComplete: (Uri?) -> Unit)
    suspend fun setWallpaper(imageUri: Uri)
}