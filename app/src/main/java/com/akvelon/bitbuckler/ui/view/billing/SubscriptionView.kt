package com.akvelon.bitbuckler.ui.view.billing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.LayoutSubscriptionItemBinding
import com.akvelon.bitbuckler.ui.billing.utility.getRealPrice
import com.android.billingclient.api.ProductDetails

class SubscriptionView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.shape_donations_item_default)
    }

    fun setData(product: ProductDetails.SubscriptionOfferDetails, plan: SubscriptionPlans, choseListener: () -> Unit) {
        val price = product.pricingPhases.pricingPhaseList.firstOrNull()
        if (price != null)
            with(viewBinding) {
                tvPlan.setText(plan.title)
                tvPrice.text = price.getRealPrice(plan)
                btSubscribe.setOnClickListener {
                    choseListener.invoke()
                }
            }
    }

    private val layoutInflater = LayoutInflater.from(context)
    val viewBinding = LayoutSubscriptionItemBinding.inflate(layoutInflater, this, true)

    enum class SubscriptionPlans(@StringRes val title: Int) {
        MONTHLY(R.string.subscription_monthly), YEARLY(R.string.subscription_yearly)
    }
}