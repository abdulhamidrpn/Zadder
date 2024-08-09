package com.rpn.zadder.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.rpn.zadder.domain.model.NetworkStatus
import com.rpn.zadder.domain.repository.NetworkConnectivityObserver
import com.rpn.zadder.presentation.component.NetworkStatusBar
import com.rpn.zadder.presentation.navigation.NavGraphSetup
import com.rpn.zadder.presentation.theme.CustomGreen
import com.rpn.zadder.presentation.theme.ZadderTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var mRewardedAd: RewardedAd? = null
    }

    private lateinit var requestMultiplePermission: ActivityResultLauncher<Array<String>>

    @Inject
    lateinit var connectivityObserver: NetworkConnectivityObserver

    private fun permissionHandler() {
        requestMultiplePermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            var isGranted = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.forEach { (s, b) ->
                    isGranted = b
                }
            }

            /*if (!isGranted) {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMultiplePermission.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        } else {
            requestMultiplePermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun setupAd(){
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setupAd()
        permissionHandler()

        setContent {
            val status by connectivityObserver.networkStatus.collectAsState()
            var showMessageBar by rememberSaveable { mutableStateOf(false) }
            var message by rememberSaveable { mutableStateOf("") }
            var backgroundColor by remember { mutableStateOf(Color.Red) }

            LaunchedEffect(key1 = status) {
                when (status) {
                    NetworkStatus.Connected -> {
                        message = "Connected to Internet"
                        backgroundColor = CustomGreen
                        delay(timeMillis = 2000)
                        showMessageBar = false
                    }

                    NetworkStatus.Disconnected -> {
                        showMessageBar = true
                        message = "No Internet Connection"
                        backgroundColor = Color.Red
                    }
                }
            }

            ZadderTheme {
                val navController = rememberNavController()
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                val snackbarHostState = remember { SnackbarHostState() }

                var searchQuery by rememberSaveable { mutableStateOf("") }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    bottomBar = {
                        NetworkStatusBar(
                            showMessageBar = showMessageBar,
                            message = message,
                            backgroundColor = backgroundColor
                        )
                    }
                ) {
                    requestPermissions()
                    NavGraphSetup(
                        navController = navController,
                        scrollBehavior = scrollBehavior,
                        snackbarHostState = snackbarHostState,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                }
            }
        }
    }
}
