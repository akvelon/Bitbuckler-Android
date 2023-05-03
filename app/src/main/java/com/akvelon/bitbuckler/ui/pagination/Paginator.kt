/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 18 December 2020
 */

package com.akvelon.bitbuckler.ui.pagination

import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.extension.addAllWithIntersection

class Paginator<T>(
    private val requestFactory: (String?) -> Unit,
    private val viewController: ViewController<T>
): PaginatorRequestState<PagedResponse<T>> {

    override fun success(result: PagedResponse<T>) {
        nextPage = result.nextPage
        currentState.newData(result.values)
    }

    override fun failure(throwable: Throwable) {
        currentState.fail(throwable)
    }

    private var currentState: State<T> = Empty()

    private var nextPage: String? = null

    private var currentData = mutableListOf<T>()

    fun refresh() = currentState.refresh()

    fun loadNewPage() = currentState.loadNewPage()

    fun reset() = currentState.reset()

    fun release() = currentState.release()

    private fun loadPage(page: String?) {
        requestFactory(page)
    }

    interface ViewController<T> {

        fun showEmptyProgress(show: Boolean)

        fun showEmptyError(show: Boolean, error: Throwable? = null)

        fun showEmptyView(show: Boolean)

        fun showData(show: Boolean, data: List<T> = emptyList(), scrollToTop: Boolean = false)

        fun showErrorMessage(error: Throwable)

        fun showRefreshProgress(show: Boolean)

        fun showPageProgress(show: Boolean)

        fun showPageProgressError(show: Boolean, error: Throwable? = null)
    }

    private interface State<T> {

        fun refresh() {}

        fun loadNewPage() {}

        fun release() {}

        fun newData(data: List<T>) {}

        fun fail(error: Throwable) {}

        fun reset() {}
    }

    private inner class Empty : State<T> {

        override fun refresh() {
            currentState = EmptyProgress()
            viewController.showEmptyProgress(true)
            loadPage(nextPage)
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class EmptyProgress : State<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = if (nextPage == null) {
                    AllData()
                } else {
                    Data()
                }

                currentData.clear()
                currentData.addAll(data)

                viewController.apply {
                    showData(true, currentData, true)
                    showEmptyProgress(false)
                }
            } else {
                currentState = EmptyData()
                viewController.apply {
                    showEmptyView(true)
                    showEmptyProgress(false)
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
            loadPage(nextPage)
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
            loadPage(nextPage)
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class Data : State<T> {

        override fun refresh() {
            currentState = Refresh()
            viewController.showPageProgressError(false)
            viewController.showRefreshProgress(true)
            loadPage(null)
        }

        override fun loadNewPage() {
            currentState = PageProgress()
            viewController.showPageProgressError(false)
            viewController.showPageProgress(true)
            loadPage(nextPage)
        }

        override fun reset() {
            currentState = Empty()
            currentData.clear()
            nextPage = null
            viewController.showData(false)
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class Refresh : State<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = if (nextPage == null) {
                    AllData()
                } else {
                    Data()
                }

                currentData.clear()
                currentData.addAll(data)

                viewController.apply {
                    showRefreshProgress(false)
                    showData(true, currentData, true)
                }
            } else {
                currentState = EmptyData()
                currentData.clear()

                viewController.apply {
                    showData(false)
                    showRefreshProgress(false)
                    showEmptyView(true)
                }
            }
        }

        override fun reset() {
            currentState = Empty()
            currentData.clear()
            nextPage = null
            viewController.apply {
                showRefreshProgress(false)
                showData(false)
            }
        }

        override fun fail(error: Throwable) {
            currentState = Data()

            viewController.apply {
                showRefreshProgress(false)
                showErrorMessage(error)
            }
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class PageProgress : State<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = if (nextPage == null) {
                    AllData()
                } else {
                    Data()
                }

                currentData.addAllWithIntersection(data)

                viewController.apply {
                    showPageProgress(false)
                    showData(true, currentData, false)
                }
            } else {
                currentState = AllData()
                viewController.showPageProgress(false)
            }
        }

        override fun refresh() {
            currentState = Refresh()

            viewController.apply {
                showPageProgress(false)
                showRefreshProgress(true)
            }

            loadPage(null)
        }

        override fun reset() {
            currentState = Empty()
            currentData.clear()
            nextPage = null
            viewController.apply {
                showRefreshProgress(false)
                showData(false)
            }
        }

        override fun fail(error: Throwable) {
            currentState = Data()
            viewController.apply {
                showPageProgress(false)
                showPageProgressError(true, error)
            }
        }

        override fun release() {
            currentState = Released()
        }
    }

    private inner class AllData : State<T> {

        override fun refresh() {
            currentState = Refresh()
            viewController.showRefreshProgress(true)
            loadPage(null)
        }

        override fun reset() {
            currentState = Empty()
            currentData.clear()
            nextPage = null
            viewController.showData(false)
        }
    }

    private inner class Released : State<T>
}
