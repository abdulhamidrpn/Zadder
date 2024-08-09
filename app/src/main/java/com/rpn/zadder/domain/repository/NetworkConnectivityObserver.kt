package com.rpn.zadder.domain.repository

import com.rpn.zadder.domain.model.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {
    val networkStatus: StateFlow<NetworkStatus>
}