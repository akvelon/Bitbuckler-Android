package com.akvelon.bitbuckler.ui.screen.privacypolicy

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

class PrivacyPolicyWebClient(
    private val onLoadingFinished: () -> Unit
): WebViewClient() {
    private var loadingFinished = true
    private var redirect = false

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, urlNewString: String?): Boolean {
        if (!loadingFinished) {
            redirect = true
        }
        loadingFinished = false
        view.loadUrl(urlNewString!!)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, facIcon: Bitmap?) {
        loadingFinished = false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (!redirect) {
            loadingFinished = true
            onLoadingFinished.invoke()
        }
        if (loadingFinished && !redirect) {
            //HIDE LOADING IT HAS FINISHED
        } else {
            redirect = false
        }
    }
}