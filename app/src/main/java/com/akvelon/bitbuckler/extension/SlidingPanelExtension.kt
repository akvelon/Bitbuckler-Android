/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 13 January 2021
 */

package com.akvelon.bitbuckler.extension

import com.sothree.slidinguppanel.SlidingUpPanelLayout

fun SlidingUpPanelLayout.anchor() {
    this.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
}

fun SlidingUpPanelLayout.collapse() {
    this.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
}

fun SlidingUpPanelLayout.isAnchoredOrExpanded() =
    this.panelState == SlidingUpPanelLayout.PanelState.ANCHORED ||
        this.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
