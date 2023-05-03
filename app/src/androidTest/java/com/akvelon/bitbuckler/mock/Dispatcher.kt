/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 25 May 2021
 */

package com.akvelon.bitbuckler.mock

import com.akvelon.bitbuckler.extension.setBodyFromFile
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class Dispatcher : Dispatcher() {

    @Override
    override fun dispatch(request: RecordedRequest): MockResponse {
        val response = MockResponse().setResponseCode(200)
        request.path?.let {
            if (request.method == GET) {
                return when (true) {
                    it.startsWith("/$ver/pullrequests/") ->
                        response.setBodyFromFile(PRS_FILE_NAME)
                    it.startsWith("/$ver/repositories/akvelon-mobile/bitbuckler") ->
                        response.setBodyFromFile(REPOSITORY_FILE_NAME)
                    else -> response
                }
            }
        }
        return response
    }

    companion object {
        private const val PRS_FILE_NAME = "response-pullrequests-selected-user.json"

        private const val REPOSITORY_FILE_NAME = "response-repository.json"

        private const val GET = "GET"

        private const val ver = "2.0"
    }
}
