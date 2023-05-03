/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 26 May 2021
 */

package com.akvelon.bitbuckler

import androidx.test.platform.app.InstrumentationRegistry

object FileReader {
    fun readStringFromFile(fileName: String): String {
        val bufferReader =
            (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App)
                .assets.open(fileName).bufferedReader()

        return bufferReader.use { it.readText() }
    }
}
