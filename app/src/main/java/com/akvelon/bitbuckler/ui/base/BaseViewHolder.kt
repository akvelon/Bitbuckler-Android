/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 *
 * Base class for ViewHolder pattern, implements LayoutContainer to call .findCacheViewById() inside bind() function
 *
 * See more about LayoutContainer and android-kotlin-extension here: https://kotlinlang.org/docs/tutorials/android-plugin.html#layoutcontainer-support
 */

package com.akvelon.bitbuckler.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(
    containerView: View
) : RecyclerView.ViewHolder(containerView)
