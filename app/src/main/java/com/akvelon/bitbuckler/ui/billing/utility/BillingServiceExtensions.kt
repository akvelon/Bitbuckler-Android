package com.akvelon.bitbuckler.ui.billing.utility

import android.util.Log
import com.akvelon.bitbuckler.ui.billing.BillingStage
import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.akvelon.bitbuckler.ui.view.billing.SubscriptionView
import com.android.billingclient.api.*
import java.math.BigDecimal
import java.math.MathContext

fun BillingResult.onSuccess(callback: () -> Unit): BillingResult {
    if (responseCode == BillingClient.BillingResponseCode.OK)
        callback.invoke()
    return this
}

fun BillingResult.onError(callback: (BillingResult) -> Unit): BillingResult {
    if (responseCode != BillingClient.BillingResponseCode.OK)
        callback.invoke(this)
    return this
}

fun BillingResult.log(stage: BillingStage, tag: String) {
    val message = if (responseCode == BillingClient.BillingResponseCode.OK)
        "OK"
    else
        "ERROR $responseCode : $debugMessage"
    Log.e("BILLING " + "$tag: " + stage.name, message)
}

fun BillingResult.parse(stage: BillingStage): BillingStatus.BillingError {
    return BillingStatus.BillingError(this, stage)
}

fun List<Purchase>?.isPreviousPurchasePending(): Boolean {
    return orEmpty().any { it.purchaseState == Purchase.PurchaseState.PENDING }
}

fun List<Purchase>?.isPreviousPurchaseActive(): Boolean {
    return orEmpty().any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
}

fun List<Purchase>?.getPurchaseStatus(): BillingStatus {
    return if (orEmpty().all { it.purchaseState == Purchase.PurchaseState.PURCHASED })
        BillingStatus.Purchase.PurchaseSuccess
    else
        BillingStatus.Purchase.PurchasePending
}

fun List<PurchaseHistoryRecord>?.getHistoryStatus(): BillingStatus {
    return if (isNullOrEmpty())
        BillingStatus.History.HistoryEmpty
    else
        BillingStatus.History.HistorySuccess
}

fun ProductDetails.PricingPhase.getRealPrice(plan: SubscriptionView.SubscriptionPlans): String {
    val formattedPrice = when (plan) {
        SubscriptionView.SubscriptionPlans.YEARLY -> BigDecimal(priceAmountMicros)
            .divide(BigDecimal(12), MathContext.DECIMAL128)
        SubscriptionView.SubscriptionPlans.MONTHLY -> BigDecimal(priceAmountMicros)
    }
        .divide(BigDecimal(1000000), MathContext.DECIMAL128)
        .setScale(2, BigDecimal.ROUND_HALF_UP)
    return "$priceCurrencyCode $formattedPrice"
}