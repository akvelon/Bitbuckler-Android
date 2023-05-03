package com.akvelon.bitbuckler.ui.billing.services

import android.app.Activity
import android.content.Context
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.billing.*
import com.akvelon.bitbuckler.ui.billing.BillingStage.*
import com.akvelon.bitbuckler.ui.billing.utility.*
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED
import com.android.billingclient.api.Purchase.PurchaseState.PURCHASED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BillingService(private val context: Context, private val analytics: AnalyticsProvider) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _billingStatusSubject = MutableSharedFlow<BillingStatus>()
    val billingStatusSubject = _billingStatusSubject.asSharedFlow()

    private val _productDetailsSubject = MutableSharedFlow<List<ProductDetails>>()
    val productDetailsSubject = _productDetailsSubject.asSharedFlow()

    private val productIds = listOf(CUP_OF_TEA, CUP_OF_COFFEE, BROWNIE, LUNCH)

    private val billingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    private var screenTag: String = ""
    private var waitingForPurchaseStatus: Boolean = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (waitingForPurchaseStatus) {
            billingResult.onSuccess {
                consumePurchasesIfNeeded(purchases)
                coroutineScope.launch {
                    _billingStatusSubject.emit(purchases.getPurchaseStatus())
                    analytics.report(AnalyticsEvent.DonationEvents.DonationScreenComplimentSuccess)
                }
            }.onError {
                coroutineScope.launch {
                    _billingStatusSubject.emit(it.parse(UPDATING_PURCHASES))
                    if (it.responseCode == USER_CANCELED)
                        analytics.report(AnalyticsEvent.DonationEvents.DonationScreenComplimentCancelled)
                    else
                        analytics.report(AnalyticsEvent.DonationEvents.DonationScreenComplimentFailed)
                }
            }.log(UPDATING_PURCHASES, tag = TAG_BILLING_PRODUCT + screenTag)
        }
        waitingForPurchaseStatus = false
    }

    private val purchaseHistoryResponseListener = PurchaseHistoryResponseListener { billingResult, purchases ->
        billingResult.onSuccess {
            coroutineScope.launch {
                val status = purchases.getHistoryStatus()
                if (status == BillingStatus.History.HistorySuccess)
                    analytics.report(AnalyticsEvent.DonationEvents.DonationScreenRestored)
                _billingStatusSubject.emit(status)
            }
        }.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(QUERY_ALL_HISTORY))
            }
        }.log(QUERY_ALL_HISTORY, tag = TAG_BILLING_PRODUCT + screenTag)
    }

    private val consumeResponseListener = ConsumeResponseListener { billingResult: BillingResult, _: String ->
        billingResult.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(CONSUME_PURCHASE))
            }
        }.log(CONSUME_PURCHASE, tag = TAG_BILLING_PRODUCT + screenTag)
    }

    private val queryPurchasesListener = PurchasesResponseListener { billingResult, purchases ->
        billingResult.onSuccess {
            consumePurchasesIfNeeded(purchases)
            if (purchases.isPreviousPurchasePending())
                coroutineScope.launch {
                    _billingStatusSubject.emit(BillingStatus.PreviousPurchasePending)
                }
            else
                queryProductDetails()
        }.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(QUERY_ALL_PURCHASES))
            }
        }.log(QUERY_ALL_PURCHASES, tag = TAG_BILLING_PRODUCT + screenTag)
    }

    private val productDetailsListener = ProductDetailsResponseListener { billingResult, productDetails ->
        billingResult.onSuccess {
            coroutineScope.launch {
                _productDetailsSubject.emit(productDetails)
            }
        }.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(QUERY_PRODUCT_DETAILS))
            }
        }.log(QUERY_PRODUCT_DETAILS, tag = TAG_BILLING_PRODUCT + screenTag)
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            billingResult.onSuccess {
                queryPurchases()
            }.onError {
                coroutineScope.launch {
                    _billingStatusSubject.emit(it.parse(SETUP_BILLING_CLIENT))
                }
            }.log(SETUP_BILLING_CLIENT, tag = TAG_BILLING_PRODUCT + screenTag)
        }

        override fun onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    }

    private fun queryProductDetails() {
        val queryProductDetailsParams = BillingHelper.ProductsFlow.getQueryProductDetailsParams(productIds, isOneTime = true)
        billingClient.queryProductDetailsAsync(queryProductDetailsParams, productDetailsListener)
    }

    private fun queryPurchases() {
        val params = BillingHelper.PurchaseFlow.getAllPurchaseParams(isOneTime = true)
        billingClient.queryPurchasesAsync(params, queryPurchasesListener)
    }

    fun queryAllHistory() {
        val params = BillingHelper.PurchaseFlow.getAllHistoryParams(isOneTime = true)
        billingClient.queryPurchaseHistoryAsync(params, purchaseHistoryResponseListener)
    }

    fun startClient(screenTag: String) {
        this.screenTag = screenTag
        if (billingClient.isReady)
            queryPurchases()
        else
            billingClient.startConnection(billingClientStateListener)
    }

    fun launchBilling(activity: Activity, productDetails: ProductDetails) {
        val billingFlowParams = BillingHelper.PurchaseFlow.getBillingFLowParams(productDetails)
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        billingResult.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(LAUNCH_PURCHASE_FLOW))
            }
        }.log(LAUNCH_PURCHASE_FLOW, tag = TAG_BILLING_PRODUCT + screenTag)
        analytics.report(AnalyticsEvent.DonationEvents.DonationScreenComplimentClicked)
        waitingForPurchaseStatus = true
    }

    private fun consumePurchasesIfNeeded(purchases: MutableList<Purchase>?) {
        purchases.orEmpty().filter {
            it.purchaseState == PURCHASED
        }.map {
            BillingHelper.ConsumeFlow.getConsumeParams(it)
        }.forEach {
            billingClient.consumeAsync(it, consumeResponseListener)
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}