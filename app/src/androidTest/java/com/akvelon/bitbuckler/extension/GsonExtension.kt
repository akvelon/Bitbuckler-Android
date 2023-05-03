/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 29 June 2021
 */

package com.akvelon.bitbuckler.extension

import com.akvelon.bitbuckler.FileReader
import com.google.gson.Gson

inline fun <reified T> Gson.getObjectFromFile(fileName: String) =
    fromJson<T>(FileReader.readStringFromFile(fileName))
