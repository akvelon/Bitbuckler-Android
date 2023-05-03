/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 June 2021
 */

package com.akvelon.bitbuckler.screen

import com.agoda.kakao.dialog.KAlertDialog
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.screen.account.AccountFragment
import com.kaspersky.kaspresso.screens.KScreen

object AccountScreen : KScreen<AccountScreen>() {

    override val layoutId = R.layout.fragment_account
    override val viewClass = AccountFragment::class.java

    val displayName = KTextView { withId(R.id.displayName) }
    val avatar = KImageView { withId(R.id.accountAvatar) }
    val location = KTextView { withId(R.id.location) }
    val logout = KButton { withId(R.id.logout) }

    val confirmDialog = KAlertDialog()
}
