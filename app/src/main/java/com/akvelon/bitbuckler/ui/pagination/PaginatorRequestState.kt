package com.akvelon.bitbuckler.ui.pagination

interface PaginatorRequestState<T> {
    fun  success(result: T)

    fun failure(throwable: Throwable)
}