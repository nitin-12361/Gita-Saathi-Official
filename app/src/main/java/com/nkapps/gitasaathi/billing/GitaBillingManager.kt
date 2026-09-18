package com.nkapps.gitasaathi.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.nkapps.gitasaathi.ads.GitaAdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GoldPlan {
    MONTHLY,
    YEARLY
}

/**
 * GitaBillingManager manages Google Play In-App Billing (Subscriptions).
 * Handles:
 * - Connecting to Google Play BillingClient
 * - Querying subscription products (gita_gold_monthly, gita_gold_yearly)
 * - Restoring active purchases automatically on startup
 * - Launching official Google Play payment flow (UPI, cards, GPay)
 * - Acknowledging purchases and enabling Gita Saathi Gold (Ad-Free)
 */
object GitaBillingManager : PurchasesUpdatedListener {
    private const val TAG = "GitaBillingManager"

    // Official Google Play Console Subscription Product IDs
    const val PRODUCT_ID_MONTHLY = "gita_gold_monthly"
    const val PRODUCT_ID_YEARLY = "gita_gold_yearly"

    private var billingClient: BillingClient? = null
    private var appContext: Context? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _monthlyPrice = MutableStateFlow("₹101")
    val monthlyPrice: StateFlow<String> = _monthlyPrice.asStateFlow()

    private val _yearlyPrice = MutableStateFlow("₹501")
    val yearlyPrice: StateFlow<String> = _yearlyPrice.asStateFlow()

    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    fun init(context: Context) {
        if (billingClient != null) return
        appContext = context.applicationContext

        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(this)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        startConnection()
    }

    private fun startConnection() {
        val client = billingClient ?: return
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Google Play BillingClient setup finished successfully")
                    _isConnected.value = true
                    queryProductDetails()
                    restorePurchases(null)
                } else {
                    Log.w(TAG, "Billing setup returned response code: ${billingResult.responseCode}, debugMessage: ${billingResult.debugMessage}")
                    _isConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected. Will attempt reconnect on next action.")
                _isConnected.value = false
            }
        })
    }

    /**
     * Queries Play Console for available subscription details to fetch real localized prices.
     */
    fun queryProductDetails() {
        val client = billingClient ?: return
        if (!_isConnected.value) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = queryProductDetailsResult.productDetailsList
                productDetailsList.forEach { details ->
                    productDetailsMap[details.productId] = details
                    val offer = details.subscriptionOfferDetails?.firstOrNull()
                    val pricingPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
                    val formattedPrice = pricingPhase?.formattedPrice

                    if (formattedPrice != null) {
                        if (details.productId == PRODUCT_ID_MONTHLY) {
                            _monthlyPrice.value = formattedPrice
                        } else if (details.productId == PRODUCT_ID_YEARLY) {
                            _yearlyPrice.value = formattedPrice
                        }
                    }
                }
                Log.d(TAG, "Fetched ${productDetailsList.size} subscription products from Google Play")
            } else {
                Log.w(TAG, "Failed to query product details: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Launches the official Google Play subscription purchase flow.
     */
    fun launchBillingFlow(
        activity: Activity,
        plan: GoldPlan,
        onFallbackOrError: (String) -> Unit
    ) {
        val client = billingClient
        val targetProductId = when (plan) {
            GoldPlan.MONTHLY -> PRODUCT_ID_MONTHLY
            GoldPlan.YEARLY -> PRODUCT_ID_YEARLY
        }

        if (client == null || !_isConnected.value) {
            startConnection()
            onFallbackOrError("Google Play Billing से संपर्क नहीं हो सका। कृपया इंटरनेट जांचें।")
            return
        }

        val productDetails = productDetailsMap[targetProductId]
        val offerToken = productDetails?.subscriptionOfferDetails?.firstOrNull()?.offerToken

        if (productDetails == null || offerToken == null) {
            Log.w(TAG, "ProductDetails or offerToken not found for $targetProductId. Products may not be published on Play Console yet.")
            onFallbackOrError("Google Play पर यह प्लान अभी उपलब्ध हो रहा है। कृपया कुछ समय बाद पुनः प्रयास करें।")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = client.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "launchBillingFlow failed: ${result.debugMessage}")
            onFallbackOrError("भुगतान प्रक्रिया शुरू करने में त्रुटि: ${result.debugMessage}")
        }
    }

    /**
     * Google Play callback when a purchase is completed, canceled, or pending.
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        val context = appContext ?: return
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase, context)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "User canceled the purchase flow.")
        } else {
            Log.w(TAG, "onPurchasesUpdated error code: ${billingResult.responseCode}, ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase, context: Context) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Activate Gold premium status in app
            GitaAdManager.setPremiumStatus(context, true)
            com.nkapps.gitasaathi.firebase.GitaCrashlytics.setCustomKey("is_gold_premium", true)
            com.nkapps.gitasaathi.firebase.GitaAnalytics.logGoldPurchase(purchase.products.joinToString())
            Log.i(TAG, "Active purchase verified for products: ${purchase.products}. Gold status activated!")

            // Acknowledge purchase if not already acknowledged
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                scope.launch {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { ackResult ->
                        if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.i(TAG, "Purchase successfully acknowledged with Google Play.")
                        } else {
                            Log.w(TAG, "Failed to acknowledge purchase: ${ackResult.debugMessage}")
                        }
                    }
                }
            }
        }
    }

    /**
     * Restores previous purchases (called on startup or when user taps 'Restore Purchases').
     */
    fun restorePurchases(onResult: ((Boolean, String) -> Unit)? = null) {
        val client = billingClient
        val context = appContext

        if (client == null || !_isConnected.value || context == null) {
            onResult?.invoke(false, "Google Play Store से कनेक्ट नहीं हो सका।")
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var foundActiveSubscription = false
                for (purchase in purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        foundActiveSubscription = true
                        handlePurchase(purchase, context)
                    }
                }

                if (foundActiveSubscription) {
                    onResult?.invoke(true, "आपकी Gold सदस्यता सफलतापूर्वक पुनर्स्थापित कर दी गई है! 👑")
                } else {
                    onResult?.invoke(false, "इस Google खाते पर कोई सक्रिय Gold सदस्यता नहीं मिली।")
                }
            } else {
                onResult?.invoke(false, "खरीदारी खोजने में समस्या: ${billingResult.debugMessage}")
            }
        }
    }
}
