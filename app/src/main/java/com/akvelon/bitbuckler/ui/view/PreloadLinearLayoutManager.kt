/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Mikhail Suendukov (mikhail.suendukov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreloadLinearLayoutManager(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    LinearLayoutManager(context, attrs, defStyleAttr, defStyleRes) {
    var loadData: (() -> Unit)? = null

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        if (findLastCompletelyVisibleItemPosition() >= itemCount - 1) {
            loadData?.invoke()
        }
    }
}
