package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.LayoutDonationItemBinding
import com.android.billingclient.api.ProductDetails

class DonationsView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.selector_donations_item_background)

        setOnClickListener {
            isSelected = true
            selectListener.invoke(productDetails, this)
        }
    }

    private lateinit var productDetails: ProductDetails
    private lateinit var selectListener: (product: ProductDetails, view: View) -> Unit

    fun setOnSelectListener(selectListener: (product: ProductDetails, view: View) -> Unit) {
        this.selectListener = selectListener
    }

    fun unselect() {
        isSelected = false
    }

    fun select() {
        isSelected = true
        selectListener.invoke(productDetails, this)
    }

    fun setIcon(@DrawableRes icon: Int) {
        viewBinding.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
    }

    fun setData(product: ProductDetails) {
        this.productDetails = product

        with(viewBinding) {
            tvPrice.text = product.oneTimePurchaseOfferDetails?.formattedPrice
            tvTitle.text = product.name
        }
    }

    private val layoutInflater = LayoutInflater.from(context)
    val viewBinding = LayoutDonationItemBinding.inflate(layoutInflater, this, true)
}