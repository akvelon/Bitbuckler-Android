/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 06 May 2021
 */

package com.akvelon.bitbuckler.screen

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.text.KTextView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.screen.activity.ActivityListFragment
import com.kaspersky.kaspresso.screens.KScreen
import org.hamcrest.Matcher

object ActivityListScreen : KScreen<ActivityListScreen>() {

    override val layoutId = R.layout.fragment_activity_list
    override val viewClass = ActivityListFragment::class.java

    val recyclerViewActivity = KRecyclerView(
        {
            withId(R.id.recyclerViewActivity)
        },
        itemTypeBuilder = {
            itemType(::ActivityItem)
        }
    )

    class ActivityItem(parent: Matcher<View>) : KRecyclerItem<ActivityItem>(parent) {
        val title = KTextView(parent) { withId(R.id.title) }
        val author = KTextView(parent) { withId(R.id.author) }
    }
}
