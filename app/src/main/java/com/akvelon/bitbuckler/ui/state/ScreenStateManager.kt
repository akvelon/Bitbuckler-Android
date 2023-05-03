/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 28 January 2021
 */

package com.akvelon.bitbuckler.ui.state

class ScreenStateManager<T : ScreenValue>(
    private val requestFactory: () -> Unit,
    private val viewController: ViewController<T>
): ScreenManagerRequestState<T> {

    private var currentState: State<T> = Empty()

    fun refresh() = currentState.refresh()

    fun release() = currentState.release()

    override fun success(result: ScreenData<T>) {
        currentState.newData(result.value)
    }

    override fun failure(throwable: Throwable) {
        currentState.fail(throwable)
    }

    private fun loadData() {
        requestFactory()
    }

    interface ViewController<T : ScreenValue> {

        fun showEmptyProgress(show: Boolean)

        fun showEmptyError(show: Boolean, error: Throwable? = null)

        fun showEmptyView(show: Boolean)

        fun showData(data: T)

        fun showErrorMessage(error: Throwable)

        fun showRefreshProgress(show: Boolean)
    }

    interface State<T : ScreenValue> {

        fun refresh() {}

        fun release() {}

        fun newData(data: T) {}

        fun fail(error: Throwable) {}
    }

    private inner class Empty : State<T> {

        override fun refresh() {
            currentState = EmptyProgress()
            viewController.showEmptyProgress(true)
            loadData()
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class EmptyProgress : State<T> {

        override fun newData(data: T) {
            if (data.isEmpty) {
                currentState = EmptyData()
                viewController.apply {
                    showEmptyProgress(false)
                    showEmptyView(true)
                }
            } else {
                currentState = Data()
                viewController.apply {
                    showEmptyProgress(false)
                    showData(data)
                }
            }
        }

        override fun fail(error: Throwable) {
            currentState = EmptyError()
            viewController.apply {
                showEmptyError(true, error)
                showEmptyProgress(false)
            }
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class EmptyError : State<T> {

        override fun refresh() {
            currentState = EmptyProgress()
            viewController.apply {
                showEmptyError(false)
                showEmptyProgress(true)
            }
            loadData()
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class EmptyData : State<T> {

        override fun refresh() {
            currentState = EmptyProgress()
            viewController.apply {
                showEmptyView(false)
                showEmptyProgress(true)
            }
            loadData()
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class Data : State<T> {

        override fun refresh() {
            currentState = Refresh()
            viewController.showRefreshProgress(true)
            loadData()
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class Refresh : State<T> {

        override fun newData(data: T) {
            currentState = Data()
            viewController.apply {
                showRefreshProgress(false)
                showData(data)
            }
        }

        override fun fail(error: Throwable) {
            currentState = Data()

            viewController.apply {
                showRefreshProgress(false)
                showErrorMessage(error)
            }
        }
    }

    private inner class Released : State<T>
}
