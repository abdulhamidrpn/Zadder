package com.rpn.zadder.domain.model

sealed class NetworkStatus {
    data object Connected: NetworkStatus()
    data object Disconnected: NetworkStatus()
}