package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.akvelon.bitbuckler.databinding.LayoutMessageDialogBinding
import com.akvelon.bitbuckler.ui.billing.BillingStatus

class MessageDialog(context: Context) : AlertDialog(context) {

    private lateinit var binding: LayoutMessageDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LayoutMessageDialogBinding.inflate(layoutInflater)
        this.setView(binding.root)
        super.onCreate(savedInstanceState)
    }

    fun showDialog(): MessageDialog {
        this.show()
        return this
    }

    fun setValues(status: BillingStatus) {
        setValues(title = status.title, message = status.message, icon = status.icon)
    }

    fun setValues(@StringRes title: Int, @StringRes message: Int, @DrawableRes icon: Int? = null): MessageDialog {
        binding.ivIcon.isVisible = icon != null
        if (icon != null)
            binding.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
        binding.tvSubtitle.setText(message)
        binding.tvTitle.setText(title)
        binding.btOk.setOnClickListener {
            dismiss()
        }
        return this
    }
}