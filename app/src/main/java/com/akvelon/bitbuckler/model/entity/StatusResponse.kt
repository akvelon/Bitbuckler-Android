package com.akvelon.bitbuckler.model.entity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class StatusResponse(val status: Boolean) {

    companion object {
        suspend fun executeRequestAndParseResponse(request: suspend () -> Response<Void>): StatusResponse {
            return withContext(Dispatchers.IO) {
                try {
                    val response = request.invoke()
                    if (response.isSuccessful)
                        StatusResponse(status = true)
                    else
                        StatusResponse(status = false)
                } catch (throwable: Exception) {
                    StatusResponse(status = false)
                }
            }
        }
    }
}

