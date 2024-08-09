package com.rpn.zadder.presentation.full_image_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rpn.zadder.data.helpers.DownloadWorker
import com.rpn.zadder.data.helpers.DefaultWallpaperSetter
import com.rpn.zadder.domain.model.DownloadFile
import com.rpn.zadder.domain.model.UnsplashImage
import com.rpn.zadder.domain.repository.ImageRepository
import com.rpn.zadder.presentation.navigation.Routes
import com.rpn.zadder.presentation.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class FullImageViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val wallpaperSetter: DefaultWallpaperSetter,
    private val workManager: WorkManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val TAG = "FullImageViewModel"

    private val imageId = savedStateHandle.toRoute<Routes.FullImageScreen>().imageId

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _downloadStatus = MutableStateFlow<WorkInfo?>(null)
    val downloadStatus: StateFlow<WorkInfo?> = _downloadStatus


    var image: UnsplashImage? by mutableStateOf(null)
        private set

    init {
        getImage()
    }

    private fun getImage() {
        viewModelScope.launch {
            try {
                val result = repository.getImage(imageId)
                image = result
            } catch (e: UnknownHostException) {
                _snackbarEvent.send(
                    SnackbarEvent(message = "No Internet connection. Please check you network.")
                )
            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong.")
                )
            }
        }
    }

    private fun sendSnackMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvent.send(SnackbarEvent(message))
        }
    }

    fun downloadImage(url: String?, image: UnsplashImage?, isSetWallPaper: Boolean = false) {
        if (url == null) {
            sendSnackMessage("Something went wrong.")
            return
        }

        val title = image?.description?.take(30) ?: "New Image"
        val id = image?.id ?: ""
        val downloadFile = DownloadFile(
            id = id,
            name = title,
            type = "JPG",
            url = url,
            downloadedUri = null
        )

        sendSnackMessage("Downloading...")
        startDownloadingFile(
            file = downloadFile,
            success = { filePath ->

                if (isSetWallPaper) {
                    try {
                        wallpaperSetter.setWallpaper(filePath)
                        sendSnackMessage("Wallpaper set successfully.")
                    } catch (e: Exception) {
                        sendSnackMessage("Failed to set wallpaper.")
                    }
                } else {
                    sendSnackMessage("Download completed successfully")
                }
            },
            failed = {
                Log.d(TAG, "Downloading failed!")
            },
            running = {
                Log.d(TAG, "Downloading...")
            }
        )
    }

    private fun startDownloadingFile(
        file: DownloadFile,
        success: (String) -> Unit,
        failed: (String) -> Unit,
        running: () -> Unit
    ) {
        val data = Data.Builder()

        data.apply {
            putString(DownloadWorker.KEY_FILE_NAME, file.name)
            putString(DownloadWorker.KEY_FILE_URL, file.url)
            putString(DownloadWorker.KEY_FILE_TYPE, file.type)
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val fileDownloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            "oneFileDownloadWork_${System.currentTimeMillis()}",
            ExistingWorkPolicy.KEEP,
            fileDownloadWorker
        )

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(fileDownloadWorker.id).collect { it ->
                _downloadStatus.value = it
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val filePath = it.outputData.getString(DownloadWorker.KEY_FILE_URI) ?: ""
                        Log.d(TAG, "Download succeeded. File path: $filePath")
                        success(filePath)
                        // Use filePath to set wallpaper
                    }

                    WorkInfo.State.RUNNING -> {
                        val progress = it.progress.getInt("progress", 0)
                        Log.d(TAG, "Downloading... $progress%")
                        running()
                    }

                    WorkInfo.State.FAILED -> {
                        Log.d(TAG, "Download failed")
                        failed("Downloading failed!")
                    }

                    else -> {
                        failed("Something went wrong")
                    }
                }
            }
        }

    }
}