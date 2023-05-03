/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.extension

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.akvelon.bitbuckler.ui.glide.GlideApp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

fun ImageView.loadSquare(url: String, @DrawableRes placeholderResId: Int = 0) =
    GlideApp.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .transition(
            withCrossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
        )
        .transform(CenterInside(), RoundedCorners(10))
        .apply {
            if (placeholderResId != 0) {
                placeholder(placeholderResId)
            }
        }
        .into(this)

fun ImageView.loadCircle(url: String, @DrawableRes placeholderResId: Int = 0) =
    GlideApp.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .circleCrop()
        .apply {
            if (placeholderResId != 0) {
                placeholder(placeholderResId)
            }
        }
        .into(this)

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}
