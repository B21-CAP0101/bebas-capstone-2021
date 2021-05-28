package com.capstone101.core.data.network

sealed class NetworkStatus<out T> {
    data class Success<out T>(val data: T) : NetworkStatus<T>()
    data class Failed(val error: String) : NetworkStatus<Nothing>()
    object Empty : NetworkStatus<Nothing>()
}
