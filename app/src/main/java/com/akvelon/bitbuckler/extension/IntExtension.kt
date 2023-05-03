/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 18 December 2020
 */

package com.akvelon.bitbuckler.extension

infix fun Int.divide(divider: Int) = (this.toDouble() / divider)

infix fun Long.divide(divider: Long) = (this.toDouble() / divider)
