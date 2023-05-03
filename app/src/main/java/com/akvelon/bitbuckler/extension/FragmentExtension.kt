/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 21 December 2020
 */

package com.akvelon.bitbuckler.extension

import androidx.fragment.app.Fragment

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.argument(key: String) = requireArguments().get(key) as T
