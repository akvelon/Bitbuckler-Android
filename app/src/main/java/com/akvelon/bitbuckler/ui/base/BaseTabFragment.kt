/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 26 November 2020
 */

package com.akvelon.bitbuckler.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.AppActivity
import com.akvelon.bitbuckler.ui.screen.commit.CommitDetailsFragment
import com.akvelon.bitbuckler.ui.screen.pullrequest.PullRequestDetailsFragment
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator

abstract class BaseTabFragment :
    Fragment(R.layout.fragment_base_tab),
    BackButtonListener,
    RouterProvider {

    abstract val tabTag: String

    protected abstract val cicerone: Cicerone<Router>

    override val router
        get() = cicerone.router

    private val navigator: Navigator by lazy {
        object : AppNavigator(requireActivity(), R.id.tabContainer, childFragmentManager) {
            override fun setupFragmentTransaction(
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment?
            ) {
                when (nextFragment) {
                    is SlideFragmentTransition ->
                        fragmentTransaction.setCustomAnimations(
                            R.anim.slide_in_left, // entering view
                            android.R.anim.fade_out, // exiting view
                            android.R.anim.fade_in, // entering view
                            R.anim.slide_out_right // exiting view
                        )
                }
            }

            override fun back() {
                super.back()
                val currentFragment = fragmentManager.findFragmentById(containerId)
                onBack(currentFragment)
            }

            private fun onBack(currentFragment: Fragment?) {
                when (currentFragment) {
                    is PullRequestDetailsFragment, is CommitDetailsFragment ->
                        (parentFragment as BottomNavListener).setBottomNavVisibility(true)
                }
            }
        }
    }

    abstract fun backToRoot()

    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = childFragmentManager.findFragmentById(R.id.tabContainer)

        if (fragment is BackButtonListener) {
            fragment.onBackPressed()
        } else {
            (activity as AppActivity).router.exit()
        }
    }
}
