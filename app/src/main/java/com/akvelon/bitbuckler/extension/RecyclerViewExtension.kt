/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 09 February 2021
 */

package com.akvelon.bitbuckler.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.bitbuckler.ui.view.PreloadLinearLayoutManager

fun RecyclerView.addEndlessOnScrollListener(onEndScroll: () -> Unit) {
    clearOnScrollListeners()
    // Adding a listener for loading data until recycler view becomes scrollable
    (layoutManager as PreloadLinearLayoutManager).loadData = onEndScroll
    addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    with((layoutManager as LinearLayoutManager)) {
                        if (childCount + findFirstVisibleItemPosition() >= itemCount) {
                            onEndScroll()
                        }
                    }
                }
            }
        }
    )
}

fun RecyclerView.addHorizontalEndlessOnScrollListener(onEndScroll: () -> Unit) {
    clearOnScrollListeners()
    // Adding a listener for loading data until recycler view becomes scrollable
    (layoutManager as PreloadLinearLayoutManager).loadData = onEndScroll
    addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dx > 0) {
                    with((layoutManager as LinearLayoutManager)) {
                        if (childCount + findFirstVisibleItemPosition() >= itemCount) {
                            onEndScroll()
                        }
                    }
                }
            }
        }
    )
}
