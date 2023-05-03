/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 29 June 2021
 */

package com.akvelon.bitbuckler.screen

import com.agoda.kakao.text.KButton
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.screen.repository.details.RepositoryDetailsFragment
import com.kaspersky.kaspresso.screens.KScreen

object RepositoryDetailsScreen : KScreen<RepositoryDetailsScreen>() {

    override val layoutId = R.layout.fragment_repository_details
    override val viewClass = RepositoryDetailsFragment::class.java

    val backButton = KButton { withId(R.id.navigationButton) }
}
