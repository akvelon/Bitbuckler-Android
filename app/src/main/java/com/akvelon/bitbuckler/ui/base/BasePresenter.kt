/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.ui.base

import android.util.Log
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.*
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenterScope

open class BasePresenter<V : MvpView>(
    protected val router: Router
) : MvpPresenter<V>() {

    private val jobs by lazy { mutableListOf<Job>() }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        Log.e("coroutineExceptionHandler", throwable.toString())
    }

    fun asyncOnDefault(
        exceptionHandler: CoroutineExceptionHandler = coroutineExceptionHandler,
        action: suspend CoroutineScope.() -> Unit
    ): Deferred<Unit> {
        val deferred =
            presenterScope.async(Dispatchers.Default + exceptionHandler) { action() }
        jobs.add(deferred)
        return deferred
    }

    fun launchOnDefault(
        exceptionHandler: CoroutineExceptionHandler = coroutineExceptionHandler,
        action: suspend CoroutineScope.() -> Unit
    ): Job {
        val job =
            presenterScope.launch(Dispatchers.Default + exceptionHandler) { action() }
        jobs.add(job)
        return job
    }

    fun launchOnIO(
        exceptionHandler: CoroutineExceptionHandler = coroutineExceptionHandler,
        action: suspend CoroutineScope.() -> Unit
    ): Job {
        val job =
            presenterScope.launch(Dispatchers.IO + exceptionHandler) { action() }
        jobs.add(job)
        return job
    }

    fun launchOnMain(
        exceptionHandler: CoroutineExceptionHandler = coroutineExceptionHandler,
        action: suspend CoroutineScope.() -> Unit
    ): Job {
        val job =
            presenterScope.launch(Dispatchers.Main + exceptionHandler) { action() }
        jobs.add(job)
        return job
    }

    suspend fun <T> switchToUi(action: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.Main) { action() }

    suspend fun <T> switchToIO(action: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO) { action() }

    suspend fun <T> switchToDefault(action: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.Default) { action() }

    private fun cancelJobs() =
        jobs.filter { it.isActive }
            .forEach { job -> job.cancel() }


    override fun onDestroy() {
        cancelJobs()
    }

    open fun onBackPressed() {
        router.exit()
    }
}
