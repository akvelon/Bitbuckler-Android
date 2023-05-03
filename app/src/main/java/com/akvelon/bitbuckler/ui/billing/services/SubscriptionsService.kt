package com.akvelon.bitbuckler.ui.billing.services

import android.app.Activity
import android.content.Context
import com.akvelon.bitbuckler.ui.billing.BillingStage.*
import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.akvelon.bitbuckler.ui.billing.utility.*
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SubscriptionsService(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _billingStatusSubject = MutableSharedFlow<BillingStatus>()
    val billingStatusSubject = _billingStatusSubject.asSharedFlow()

    private val _productDetailsSubject = MutableSharedFlow<ProductDetails>()
    val productDetailsSubject = _productDetailsSubject.asSharedFlow()

    private val productIds = listOf(SUBSCRIPTION_PREMIUM)

    private var checkForStatusOnly = true
    private var waitingForPurchaseStatus: Boolean = false

    private val billingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (waitingForPurchaseStatus) {
            billingResult.onSuccess {
                acknowledgeIfNeeded(purchases)
                coroutineScope.launch {
                    _billingStatusSubject.emit(purchases.getPurchaseStatus())
                }
            }.onError {
                coroutineScope.launch {
                    _billingStatusSubject.emit(it.parse(UPDATING_PURCHASES))
                }
            }.log(UPDATING_PURCHASES, tag = TAG_BILLING_SUBS)
        }
        waitingForPurchaseStatus = false
    }

    private val queryPurchasesListener = PurchasesResponseListener { billingResult, purchases ->
        billingResult.onSuccess {
            acknowledgeIfNeeded(purchases)
            coroutineScope.launch {
                _billingStatusSubject.emit(
                    when {
                        purchases.isPreviousPurchasePending() -> BillingStatus.PreviousPurchasePending
                        purchases.isPreviousPurchaseActive() -> BillingStatus.History.HistorySuccess
                        else -> {
                            if (!checkForStatusOnly)
                                queryProductDetails()
                            BillingStatus.History.HistoryEmpty
                        }
                    }
                )
            }
        }.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(QUERY_ALL_PURCHASES))
            }
        }.log(QUERY_ALL_PURCHASES, tag = TAG_BILLING_SUBS)
    }

    private val acknowledgeResponseListener = AcknowledgePurchaseResponseListener {
        it.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(CONSUME_PURCHASE))
            }
        }.log(CONSUME_PURCHASE, tag = TAG_BILLING_SUBS)
    }

    private val productDetailsListener = ProductDetailsResponseListener { billingResult, productDetails ->
        billingResult.onSuccess {
            if (productDetails.isNotEmpty())
                coroutineScope.launch {
                    _productDetailsSubject.emit(productDetails.first())
                }
        }.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(QUERY_PRODUCT_DETAILS))
            }
        }.log(QUERY_PRODUCT_DETAILS, tag = TAG_BILLING_SUBS)
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            billingResult.onSuccess {
                queryPurchases()
            }.onError {
                if (!checkForStatusOnly)
                    coroutineScope.launch {
                        _billingStatusSubject.emit(it.parse(SETUP_BILLING_CLIENT))
                    }
            }.log(SETUP_BILLING_CLIENT, tag = TAG_BILLING_SUBS)
        }

        override fun onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    }

    private fun queryProductDetails() {
        val queryProductDetailsParams = BillingHelper.ProductsFlow.getQueryProductDetailsParams(productIds, isOneTime = false)
        billingClient.queryProductDetailsAsync(queryProductDetailsParams, productDetailsListener)
    }

    private fun queryPurchases() {
        val params = BillingHelper.PurchaseFlow.getAllPurchaseParams(isOneTime = false)
        billingClient.queryPurchasesAsync(params, queryPurchasesListener)
    }

    private fun acknowledgeIfNeeded(purchases: MutableList<Purchase>?) {
        purchases.orEmpty().filter {
            it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged
        }.map {
            BillingHelper.AcknowledgeFlow.getAcknowledgeParams(it)
        }.forEach {
            billingClient.acknowledgePurchase(it, acknowledgeResponseListener)
        }
    }

    fun startClient() {
        this.checkForStatusOnly = true
        billingClient.startConnection(billingClientStateListener)
    }

    fun getProductDetails() {
        this.checkForStatusOnly = false
        if (billingClient.isReady)
            queryPurchases()
        else
            billingClient.startConnection(billingClientStateListener)
    }

    fun launchBilling(activity: Activity, productDetails: ProductDetails, offerDetails: ProductDetails.SubscriptionOfferDetails) {
        val billingFlowParams = BillingHelper.PurchaseFlow.getSubsFLowParams(productDetails, offerDetails)
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        billingResult.onError {
            coroutineScope.launch {
                _billingStatusSubject.emit(it.parse(LAUNCH_PURCHASE_FLOW))
            }
        }.log(LAUNCH_PURCHASE_FLOW, tag = TAG_BILLING_SUBS)
        waitingForPurchaseStatus = true
    }
}