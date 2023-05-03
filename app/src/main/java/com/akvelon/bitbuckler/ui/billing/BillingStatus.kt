package com.akvelon.bitbuckler.ui.billing

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.akvelon.bitbuckler.R
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult

//statuses that will be sent to user
sealed class BillingStatus(
    @StringRes open val title: Int,
    @StringRes open val message: Int,
    @DrawableRes open val icon: Int?,
) {

    sealed class History(
        @StringRes override val title: Int,
        @StringRes override val message: Int,
        @DrawableRes override val icon: Int?,
    ) : BillingStatus(title, message, icon) {
        //when there is an successfully purchased item in history
        object HistorySuccess : History(title = R.string.donation_restore,
            message = R.string.donation_restore_message,
            icon = R.drawable.ic_donation_success)

        //when there is no successfully purchased item in history
        object HistoryEmpty : History(title = R.string.donation_empty_history,
            message = R.string.donation_empty_history_message,
            icon = null)
    }

    sealed class Purchase(
        @StringRes override val title: Int,
        @StringRes override val message: Int,
        @DrawableRes override val icon: Int? = null,
    ) : BillingStatus(title, message, icon) {
        //when item is purchased successfully
        object PurchaseSuccess : Purchase(title = R.string.donation_success,
            message = R.string.donation_success_message,
            icon = R.drawable.ic_donation_success)

        //when current purchase is pending
        object PurchasePending : Purchase(title = R.string.pending_purchase,
            message = R.string.pending_purchase_message,
            icon = R.drawable.ic_donation_success)
    }

    //when last purchase is still pending
    object PreviousPurchasePending : BillingStatus(title = R.string.previous_pending_purchase,
        message = R.string.previous_pending_purchase_message,
        icon = null)

    //when error happened on various stages
    class BillingError(val billingResult: BillingResult, val stage: BillingStage) : BillingStatus(
        title = R.string.donation_failed,
        message = R.string.donation_failed_message,
        icon = R.drawable.ic_failure
    ) {
        val shouldShowWarningToUser = when (stage) {
            BillingStage.SETUP_BILLING_CLIENT -> true
            BillingStage.QUERY_ALL_PURCHASES -> true
            BillingStage.QUERY_ALL_HISTORY -> true
            BillingStage.QUERY_PRODUCT_DETAILS -> true
            BillingStage.LAUNCH_PURCHASE_FLOW -> true
            BillingStage.UPDATING_PURCHASES -> billingResult.responseCode != BillingClient.BillingResponseCode.USER_CANCELED
            BillingStage.CONSUME_PURCHASE -> false
        }
    }
}
