/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 July 2021
 */

package com.akvelon.bitbuckler.ui.night

import android.os.Build
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import com.akvelon.bitbuckler.R

enum class NightModeType(
    val position: Int,
    @NightMode val value: Int,
    @StringRes val title: Int
) {
    NIGHT_MODE_NO(
        0,
        AppCompatDelegate.MODE_NIGHT_NO,
        R.string.night_mode_no
    ),
    NIGHT_MODE_YES(
        1,
        AppCompatDelegate.MODE_NIGHT_YES,
        R.string.night_mode_yes
    ),
    NIGHT_MODE_FOLLOW_SYSTEM(
        2,
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.night_mode_default
    ),
    NIGHT_MODE_AUTO_BATTERY(
        2,
        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
        R.string.night_mode_default
    );

    companion object {
        fun fromValue(@NightMode value: Int) = values().firstOrNull { it.value == value } ?: getDefaultMode()

        fun fromPosition(position: Int) = if (position == 2) {
            getDefaultMode()
        } else {
            values().firstOrNull { it.position == position } ?: getDefaultMode()
        }

        fun getDefaultMode() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NIGHT_MODE_FOLLOW_SYSTEM
            } else {
                NIGHT_MODE_AUTO_BATTERY
            }
    }
}
