/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 17 December 2020
 */

package com.akvelon.bitbuckler.extension

import java.math.BigDecimal

fun Double.roundToTenth() =
    BigDecimal(this).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
