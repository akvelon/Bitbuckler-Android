/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 23 March 2021
 */
package com.akvelon.bitbuckler.extension

import androidx.constraintlayout.motion.widget.MotionLayout

fun MotionLayout.doOnTransitionChange(action: (MotionLayout?, Int, Int, Float) -> Unit) {

    setTransitionListener(
        object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                action(p0, p1, p2, p3)
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        }
    )
}
