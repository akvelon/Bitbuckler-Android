/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 07 June 2021
 */

package com.akvelon.bitbuckler.mock

import okhttp3.mockwebserver.MockWebServer

object MockServer {
    private val server = MockWebServer()

    fun setUp() {
        server.dispatcher = Dispatcher()
        server.start(8080)
    }

    fun shutDown() = server.shutdown()
}
