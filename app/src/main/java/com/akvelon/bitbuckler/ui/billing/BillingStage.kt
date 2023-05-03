package com.akvelon.bitbuckler.ui.billing

//all the stages of billing flow
enum class BillingStage {
    SETUP_BILLING_CLIENT,
    QUERY_ALL_PURCHASES,
    QUERY_ALL_HISTORY,
    QUERY_PRODUCT_DETAILS,
    LAUNCH_PURCHASE_FLOW,
    UPDATING_PURCHASES,
    CONSUME_PURCHASE,
}