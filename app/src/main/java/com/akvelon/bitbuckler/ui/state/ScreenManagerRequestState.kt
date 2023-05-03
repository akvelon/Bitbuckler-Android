package com.akvelon.bitbuckler.ui.state

interface ScreenManagerRequestState<T: ScreenValue> {
    fun  success(result: ScreenData<T>)

    fun failure(throwable: Throwable)
}