package com.akvelon.bitbuckler.ui.billing.utility

import com.android.billingclient.api.*

sealed class BillingHelper {

    fun getProductType(isOneTime: Boolean): String {
        return if (isOneTime)
            BillingClient.ProductType.INAPP
        else
            BillingClient.ProductType.SUBS
    }

    object AcknowledgeFlow : BillingHelper() {
        fun getAcknowledgeParams(purchase: Purchase): AcknowledgePurchaseParams {
            return AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        }
    }

    object ConsumeFlow : BillingHelper() {
        fun getConsumeParams(purchase: Purchase): ConsumeParams {
            return ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        }
    }

    object ProductsFlow : BillingHelper() {
        private fun getProductDetailsParams(
            productIds: List<String>,
            isOneTime: Boolean,
        ): List<QueryProductDetailsParams.Product> {
            return productIds.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(getProductType(isOneTime))
                    .build()
            }
        }

        fun getQueryProductDetailsParams(productIds: List<String>, isOneTime: Boolean): QueryProductDetailsParams {
            val productDetailsParams = getProductDetailsParams(productIds, isOneTime)
            return QueryProductDetailsParams.newBuilder()
                .setProductList(productDetailsParams)
                .build()
        }
    }

    object PurchaseFlow : BillingHelper() {
        fun getSubsFLowParams(
            productDetails: ProductDetails,
            offer: ProductDetails.SubscriptionOfferDetails,
        ): BillingFlowParams {
            val productDetailsParamsList = getSubsFlowOfferDetails(productDetails, offer)
            return BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParamsList))
                .build()
        }

        private fun getSubsFlowOfferDetails(
            productDetails: ProductDetails,
            offer: ProductDetails.SubscriptionOfferDetails,
        ): BillingFlowParams.ProductDetailsParams {
            return BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offer.offerToken)
                .build()
        }

        fun getBillingFLowParams(productDetails: ProductDetails): BillingFlowParams {
            val productDetailsParamsList = getBillingFlowProductDetails(productDetails)
            return BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParamsList))
                .build()
        }

        private fun getBillingFlowProductDetails(productDetails: ProductDetails): BillingFlowParams.ProductDetailsParams {
            return BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        }

        fun getAllPurchaseParams(isOneTime: Boolean): QueryPurchasesParams {
            return QueryPurchasesParams.newBuilder()
                .setProductType(getProductType(isOneTime))
                .build()
        }

        fun getAllHistoryParams(isOneTime: Boolean): QueryPurchaseHistoryParams {
            return QueryPurchaseHistoryParams.newBuilder()
                .setProductType(getProductType(isOneTime))
                .build()
        }
    }
}