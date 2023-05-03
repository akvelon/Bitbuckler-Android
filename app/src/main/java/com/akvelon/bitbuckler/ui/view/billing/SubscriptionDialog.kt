package com.akvelon.bitbuckler.ui.view.billing

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.akvelon.bitbuckler.databinding.LayoutSubscriptionsBinding
import com.akvelon.bitbuckler.ui.billing.utility.TAG_SUBS_PLAN_MONTHLY
import com.akvelon.bitbuckler.ui.billing.utility.TAG_SUBS_PLAN_YEARLY
import com.android.billingclient.api.ProductDetails

class SubscriptionDialog(context: Context) : AlertDialog(context) {

    private lateinit var binding: LayoutSubscriptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LayoutSubscriptionsBinding.inflate(layoutInflater)
        this.setView(binding.root)
        super.onCreate(savedInstanceState)
    }

    fun showDialog(): SubscriptionDialog {
        this.show()
        return this
    }

    fun setData(productDetails: ProductDetails, choseListener: (ProductDetails.SubscriptionOfferDetails) -> Unit) {
        if (productDetails.subscriptionOfferDetails?.size == 2) {
            val monthly = productDetails.subscriptionOfferDetails!!.find { it.offerTags.contains(TAG_SUBS_PLAN_MONTHLY) }!!
            val yearly = productDetails.subscriptionOfferDetails!!.find { it.offerTags.contains(TAG_SUBS_PLAN_YEARLY) }!!
            with(binding) {
                cvMonthly.setData(monthly, SubscriptionView.SubscriptionPlans.MONTHLY) {
                    choseListener.invoke(monthly)
                    dismiss()
                }
                cvYearly.setData(yearly, SubscriptionView.SubscriptionPlans.YEARLY) {
                    choseListener.invoke(yearly)
                    dismiss()
                }

                btCancel.setOnClickListener {
                    dismiss()
                }

            }
        }
    }
}