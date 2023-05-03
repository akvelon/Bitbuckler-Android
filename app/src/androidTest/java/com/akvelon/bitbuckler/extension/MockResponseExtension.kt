/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 01 July 2021
 */

package com.akvelon.bitbuckler.extension

import com.akvelon.bitbuckler.FileReader
import okhttp3.mockwebserver.MockResponse

fun MockResponse.setBodyFromFile(fileName: String) =
    this.setBody(FileReader.readStringFromFile(fileName))
