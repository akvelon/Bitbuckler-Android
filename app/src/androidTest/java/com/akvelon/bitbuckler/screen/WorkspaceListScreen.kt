/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 09 June 2021
 */

package com.akvelon.bitbuckler.screen

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.text.KTextView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.screen.workspace.WorkspaceListFragment
import com.kaspersky.kaspresso.screens.KScreen
import org.hamcrest.Matcher

object WorkspaceListScreen : KScreen<WorkspaceListScreen>() {
    override val layoutId = R.layout.fragment_workspace_list
    override val viewClass = WorkspaceListFragment::class.java

    val recyclerViewRepositoryCards = KRecyclerView(
        {
            withId(R.id.recyclerViewRepositoryCards)
        },
        itemTypeBuilder = {
            itemType(::RecentRepoItem)
        }
    )

    class RecentRepoItem(parent: Matcher<View>) : KRecyclerItem<RecentRepoItem>(parent) {
        val name = KTextView(parent) { withId(R.id.name) }
    }
}
