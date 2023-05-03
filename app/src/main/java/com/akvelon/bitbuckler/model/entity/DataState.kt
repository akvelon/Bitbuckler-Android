package com.akvelon.bitbuckler.model.entity

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    class Error(val noNetwork: Boolean) : DataState<Nothing>()
    class Success<T>(val data: T) : DataState<T>()
}
