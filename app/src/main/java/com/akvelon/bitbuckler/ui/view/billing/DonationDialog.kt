package com.akvelon.bitbuckler.ui.view.billing

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.LayoutDonationsBinding
import com.android.billingclient.api.ProductDetails

class DonationDialog(context: Context) : AlertDialog(context) {

    private lateinit var binding: LayoutDonationsBinding
    private lateinit var actionListener: (DialogActions) -> Unit
    private var notifyDismiss: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LayoutDonationsBinding.inflate(layoutInflater)
        this.setView(binding.root)
        super.onCreate(savedInstanceState)
    }

    fun showDialog(): DonationDialog {
        this.show()
        return this
    }

    fun bind(products: List<ProductDetails>): DonationDialog {
        val cards = listOf(binding.cardFirst, binding.cardSecond, binding.cardThird, binding.cardFourth)

        val unselectOtherCards = fun(view: View) {
            cards.filter { it.id != view.id }.forEach { it.unselect() }
        }

        var selectedProduct: ProductDetails? = null

        val productSelectListener: (ProductDetails, View) -> Unit = { product, view ->
            selectedProduct = product
            unselectOtherCards(view)
        }

        cards.forEach { it.setOnSelectListener(productSelectListener) }

        val icons = listOf(
            R.drawable.ic_donation_tea,
            R.drawable.ic_donation_coffee,
            R.drawable.ic_donation_brownie,
            R.drawable.ic_donation_lunch
        )

        products.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros }.forEachIndexed { index, productDetails ->
            with(cards[index]) {
                setData(productDetails)
                setIcon(icons[index])
            }
        }

        cards.first().select()

        with(binding) {
            btPurchase.setOnClickListener {
                notifyDismiss = false
                dismiss()
                actionListener.invoke(DialogActions.LaunchBilling(selectedProduct ?: return@setOnClickListener))
            }
            btRestore.setOnClickListener {
                notifyDismiss = false
                dismiss()
                actionListener.invoke(DialogActions.CheckHistory)
            }
            btCancel.setOnClickListener {
                dismiss()
            }
            checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
                actionListener.invoke(DialogActions.ToggleReminder(isChecked))
            }
        }
        return this
    }

    fun setType(type: PopupType): DonationDialog {
        binding.btRestore.isVisible = type == PopupType.GIVE_A_GIFT
        binding.checkBoxReminder.isVisible = type == PopupType.SELF_POPUP
        return this
    }

    fun setActionListener(actionListener: (DialogActions) -> Unit): DonationDialog {
        this.actionListener = actionListener
        return this
    }

    fun setDismissListener(dismissListener: () -> Unit) {
        setOnDismissListener {
            if (notifyDismiss)
                dismissListener.invoke()
        }
    }

    enum class PopupType {
        SELF_POPUP, GIVE_A_GIFT
    }

    sealed class DialogActions {
        object CheckHistory : DialogActions()
        class LaunchBilling(val selectedProduct: ProductDetails) : DialogActions()
        class ToggleReminder(val enabled: Boolean) : DialogActions()
    }
}