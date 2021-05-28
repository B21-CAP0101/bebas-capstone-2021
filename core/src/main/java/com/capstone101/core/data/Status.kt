package com.capstone101.core.data

sealed class Status<out T>(val data: T? = null, val error: String? = null) {
    class Success<out T>(data: T) : Status<T>(data)
    class Loading<T> : Status<T>()
    class Error<T>(data: T, error: String) : Status<T>(data, error)
}