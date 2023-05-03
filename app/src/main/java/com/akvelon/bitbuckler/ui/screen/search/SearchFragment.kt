/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 15 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentSearchBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.hideKeyboard
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.showKeyboard
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.search.adapter.SearchResultsAdapter
import com.akvelon.bitbuckler.ui.screen.start.StartFragment.Companion.CUSTOM_TABS_PACKAGE
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class SearchFragment :
    MvpAppCompatFragment(R.layout.fragment_search),
    SearchView,
    BackButtonListener {

    private lateinit var resultsAdapter: SearchResultsAdapter

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private lateinit var serviceConnection: CustomTabsServiceConnection
    private var session: CustomTabsSession? = null

    private val searchPresenter: SearchPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(WORKSPACE_ARG)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultsAdapter = SearchResultsAdapter(
            searchPresenter::onResultClick,
            searchPresenter::onResultRepositoryClick,
            searchPresenter::loadNextResultsPage
        )

        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0L)
                session = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        CustomTabsClient.bindCustomTabsService(
            requireContext(),
            CUSTOM_TABS_PACKAGE,
            serviceConnection
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            results.apply {
                setHasFixedSize(true)
                adapter = this@SearchFragment.resultsAdapter
                addEndlessOnScrollListener {
                    searchPresenter.loadNextResultsPage()
                }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener {
                    searchPresenter.refreshResults()
                }
            }

            errorView.retry.setOnClickListener {
                searchPresenter.refreshResults()
            }

            toolbar.setNavigationOnClickListener { onBackPressed() }

            searchBar.setOnEditorActionListener { _, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    onQueryEntered()
                    handled = true
                }
                handled
            }
            searchBar.requestFocus()
            binding.root.showKeyboard()
        }
    }

    override fun openSearchPage(searchUri: Uri) {
        CustomTabsIntent.Builder(session)
            .build()
            .launchUrl(
                requireContext(),
                searchUri
            )
    }

    override fun onResume() {
        super.onResume()

        // refresh results after search enabled
        if (binding.errorView.root.isVisible)
            searchPresenter.refreshResults()
    }

    override fun showEmptyProgress(show: Boolean) {
        binding.progressEmptyListView.root.setVisible(show)

        binding.swipeToRefresh.apply {
            post {
                isRefreshing = false
            }
        }
    }

    override fun setNoInternetConnectionError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun setUnknownError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    override fun setSearchIsNotEnabledError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_search_off)
            errorTitle.text = getString(R.string.search_not_enabled_title)
            errorSubtitle.text = getString(R.string.search_not_enabled_subtitle)
            retry.text = getString(R.string.search_not_enabled_enable_button)
            retry.setOnClickListener {
                searchPresenter.onEnablePressed()
            }
        }

    override fun showErrorView(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showEmptyView(show: Boolean) {
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showErrorMessage(error: Throwable) {
        binding.results.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.results.post {
            resultsAdapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.results.post {
            resultsAdapter.showProgressError(show)
        }
    }

    override fun showResults(show: Boolean, results: List<CodeSearchResult>) {
        binding.results.setVisible(show)

        resultsAdapter.setData(results)
    }

    override fun showNoResultsView(show: Boolean) {
        binding.noResultsView.root.setVisible(show)
    }

    override fun onBackPressed() {
        binding.root.hideKeyboard()
        searchPresenter.onBackPressed()
    }

    private fun onQueryEntered() {
        with(searchPresenter) {
            setQuery(
                binding.searchBar.text.toString()
            )
            refreshResults()
        }
        binding.root.hideKeyboard()
        binding.searchBar.clearFocus()
    }

    companion object {
        const val WORKSPACE_ARG = "workspace_arg"

        fun newInstance(workspace: Workspace) = SearchFragment().apply {
            arguments = bundleOf(
                WORKSPACE_ARG to workspace
            )
        }
    }
}
