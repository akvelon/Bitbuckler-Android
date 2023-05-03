package com.akvelon.bitbuckler.ui.billing

import android.app.Activity
import android.util.Log
import com.akvelon.bitbuckler.ui.billing.services.SubscriptionsService
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BillingUseCase(
    private val subscriptionsService: SubscriptionsService,
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _isSubscriptionActive = MutableStateFlow(false)
    val isSubscriptionActive = _isSubscriptionActive.asStateFlow()

    init {
        subscriptionsService.billingStatusSubject
            .filter {
                it is BillingStatus.History
            }.map {
                when (it as BillingStatus.History) {
                    BillingStatus.History.HistoryEmpty -> false
                    BillingStatus.History.HistorySuccess -> true
                }
            }.onEach {
                Log.e("BILLING " + "subscriptions", "isActive: $it")
                _isSubscriptionActive.emit(it)
            }.launchIn(coroutineScope)
    }

    fun getStatusSubject() = subscriptionsService.billingStatusSubject

    fun getProductSubject() = subscriptionsService.productDetailsSubject

    fun launchBilling(activity: Activity, productDetails: ProductDetails, offerDetails: ProductDetails.SubscriptionOfferDetails) {
        subscriptionsService.launchBilling(activity, productDetails, offerDetails)
    }

    fun onPurchaseSuccess() {
        coroutineScope.launch {
            _isSubscriptionActive.emit(true)
        }
    }

    fun getProductDetails() {
        subscriptionsService.getProductDetails()
    }

    fun checkSubs() {
        subscriptionsService.startClient()
    }
}