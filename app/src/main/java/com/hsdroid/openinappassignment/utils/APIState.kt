package com.hsdroid.openinappassignment.utils

sealed class APIState<out T> {
    data object EMPTY : APIState<Nothing>()
    data object LOADING : APIState<Nothing>()
    class SUCCESS<out T>(val data: T) : APIState<T>()
    class FAILURE(val t: Throwable) : APIState<Nothing>()
}