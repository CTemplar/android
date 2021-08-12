package mobileapp.ctemplar.com.ctemplarapp.billing;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BillingController implements PurchasesUpdatedListener,
        BillingClientStateListener, SkuDetailsResponseListener, PurchasesResponseListener {
    private static final int RECONNECT_DELAY = 5000;
    private static BillingController instance;
    private final Application app;
    private final Handler handler;
    private BillingClient billingClient;

    private static final List<String> LIST_OF_SKUS = Collections.unmodifiableList(
            Arrays.asList(BillingConstants.ALL_SUBSCRIPTIONS));

    // Key - sku (productId)
    private final MutableLiveData<Map<String, SkuDetails>> skuDetailMap = new MutableLiveData<>();
    private final MutableLiveData<List<Purchase>> subscriptionPurchases = new MutableLiveData<>();

    private BillingController(Application app) {
        this.app = app;
        Looper looper = Looper.myLooper();
        this.handler = new Handler(looper != null ? looper : Looper.getMainLooper());
    }

    public static BillingController getInstance(Application app) {
        if (instance == null) {
            instance = new BillingController(app);
        }
        return instance;
    }

    public LiveData<Map<String, SkuDetails>> getSkuDetails() {
        return skuDetailMap;
    }

    public LiveData<List<Purchase>> getSubscriptionPurchases() {
        return subscriptionPurchases;
    }

    public void init() {
        if (billingClient != null) {
            Timber.e("Billing client is already initiated");
            return;
        }
        billingClient = BillingClient.newBuilder(app)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        if (!billingClient.isReady()) {
            Timber.d("Starting billing client connection...");
            billingClient.startConnection(this);
        }
    }

    public void dispose() {
        if (billingClient == null) {
            Timber.e("Dispose failed: Billing client not initialized.");
            return;
        }
        Timber.d("Disposing");
        if (billingClient.isReady()) {
            billingClient.endConnection();
        }
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Timber.d("onBillingSetupFinished: " + responseCode + " " + debugMessage);
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            requestData();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected");
        billingClient.startConnection(this);
        handler.postDelayed(this::reconnect, RECONNECT_DELAY);
    }

    private void reconnect() {
        if (billingClient == null) {
            return;
        }
        Timber.i("Reconnecting...");
        if (!billingClient.isReady()) {
            billingClient.startConnection(this);
        }
    }

    private void requestData() {
        requestSkuDetails();
        requestSubscriptionPurchases();
    }

    @Override
    public void onSkuDetailsResponse(@NotNull BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                Timber.i("onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                handleSkuDetailsResponse(skuDetailsList);
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
            case BillingClient.BillingResponseCode.ERROR:
                Timber.e("onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                Timber.i("onSkuDetailsResponse: " + responseCode + " " + debugMessage);
                break;
            // These response codes are not expected.
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            default:
                Timber.wtf("onSkuDetailsResponse: " + responseCode + " " + debugMessage);
        }
    }

    private void requestSubscriptionPurchases() {
        Timber.d("requestSubscriptionPurchases");
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, this);
    }

    @Override
    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult,
                                         @NonNull List<Purchase> list) {
        handlePurchases(list);
    }

    @Override
    public void onPurchasesUpdated(@NotNull BillingResult billingResult, List<Purchase> purchases) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Timber.d("onPurchasesUpdated: %s %s", responseCode, debugMessage);
        switch (responseCode) {
            case BillingClient.BillingResponseCode.OK:
                if (purchases == null) {
                    Timber.d("onPurchasesUpdated: null purchase list");
                }
                handlePurchases(purchases);
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                Timber.i("onPurchasesUpdated: User canceled the purchase");
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                Timber.i("onPurchasesUpdated: The user already owns this item");
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                Timber.e("onPurchasesUpdated: DEVELOPER_ERROR");
                break;
        }
    }

    private void handlePurchases(List<Purchase> purchasesList) {
        if (purchasesList != null) {
            Timber.d("handlePurchases: " + purchasesList.size() + " purchase(s)");
        } else {
            Timber.d("handlePurchases: with no purchases");
        }
        if (isUnchangedPurchaseList(purchasesList)) {
            Timber.d("handlePurchases: Purchase list has not changed");
            return;
        }
        subscriptionPurchases.postValue(purchasesList);
        if (purchasesList != null) {
            // TODO
        }
    }

    private void handleSkuDetailsResponse(List<SkuDetails> skuDetailsList) {
        final int expectedCount = LIST_OF_SKUS.size();
        if (skuDetailsList == null) {
            skuDetailMap.postValue(Collections.emptyMap());
            Timber.e("onSkuDetailsResponse: " +
                    "Expected " + expectedCount);
        } else {
            Map<String, SkuDetails> newSkusDetailList = new HashMap<>();
            for (SkuDetails skuDetails : skuDetailsList) {
                newSkusDetailList.put(skuDetails.getSku(), skuDetails);
            }
            skuDetailMap.postValue(newSkusDetailList);
            int skuDetailsCount = newSkusDetailList.size();
            if (skuDetailsCount == expectedCount) {
                Timber.i("onSkuDetailsResponse: Found " + skuDetailsCount + " SkuDetails");
            } else {
                Timber.e("onSkuDetailsResponse: " +
                        "Expected " + expectedCount + ", " +
                        "Found " + skuDetailsCount);
            }
        }
    }

    private boolean isUnchangedPurchaseList(List<Purchase> purchasesList) {
        List<Purchase> currentPurchases = subscriptionPurchases.getValue();
        // Null check
        if (currentPurchases == purchasesList) {
            return true;
        }
        if (currentPurchases == null || purchasesList == null) {
            return false;
        }
        if (currentPurchases.size() != purchasesList.size()) {
            return false;
        }
        MAIN:
        for (Purchase purchase : purchasesList) {
            for (Purchase currentPurchase : currentPurchases) {
                if (currentPurchase.toString().equals(purchase.toString())) {
                    continue MAIN;
                }
            }
            return false;
        }
        return true;
    }

    private void requestSkuDetails() {
        Timber.d("requestSkuDetails");
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.SUBS)
                .setSkusList(LIST_OF_SKUS)
                .build();
        billingClient.querySkuDetailsAsync(params, this);
    }

    public int launchBillingFlow(Activity activity, BillingFlowParams params) {
        BillingResult billingResult = billingClient.launchBillingFlow(activity, params);
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        Timber.d("launchBillingFlow: BillingResponse " + responseCode + " " + debugMessage);
        return responseCode;
    }

    public void acknowledgePurchase(String purchaseToken) {
        Timber.d("acknowledgePurchase");
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        billingClient.acknowledgePurchase(params, billingResult -> {
            int responseCode = billingResult.getResponseCode();
            String debugMessage = billingResult.getDebugMessage();
            Timber.d("acknowledgePurchase: " + responseCode + " " + debugMessage);
        });
    }
}
