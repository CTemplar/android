package mobileapp.ctemplar.com.ctemplarapp.billing;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.SingleLiveEvent;
import timber.log.Timber;

public class BillingViewModel extends AndroidViewModel {
    private final BillingController billingController;
//    private MediatorLiveData<List<SubscriptionStatus>> subscriptions;

    public BillingViewModel(Application application) {
        super(application);
        billingController = BillingController.getInstance(application);
        billingController.init();
        billingController.getSubscriptionPurchases().observeForever(this::handlePurchases);
    }

    @Override
    public void onCleared() {
        billingController.dispose();
        billingController.getSubscriptionPurchases().removeObserver(this::handlePurchases);
    }

    private void handlePurchases(List<Purchase> purchases) {
        if (purchases == null) {
            return;
        }
        if (purchases.isEmpty()) {
            return;
        }
        if (purchases.size() > 1) {
            Timber.e("Multiple purchases?");
        }
        for (Purchase purchase : purchases) {
            if (purchase.isAcknowledged()) {
                continue;
            }
            String subscription = BillingUtilities.getPurchaseSubscription(purchase);
            if (subscription == null) {
                continue;
            }
            purchase.getPurchaseToken();
        }
    }

    public void subscribe(Activity activity, String planSku) {
        if (planSku == null) {
            throw new NullPointerException("'planSku' must not be null");
        }
        BillingUtilities.checkSubscriptionSku(planSku);
        BillingUtilities.checkLoadedSubscriptionSku(billingController.getSkuDetails().getValue(), planSku);
        Purchase currentSubscriptionPurchase = BillingUtilities.getCurrentSubscriptionPurchase(billingController.getSubscriptionPurchases().getValue());
        String currentSubscriptionSku = currentSubscriptionPurchase == null
                ? null
                : BillingUtilities.getPurchaseSubscription(currentSubscriptionPurchase);
        if (planSku.equals(currentSubscriptionSku)) {
            throw new RuntimeException("Already owned subscription '" + planSku + "'");
        }
        subscribeInternal(activity, planSku, currentSubscriptionSku);
    }

    private void subscribeInternal(Activity activity, String planSku, String oldPlanSku) {
        Timber.i("subscribeInternal " + planSku + (oldPlanSku == null ? "" : (" => " + oldPlanSku)));

        SkuDetails skuDetails = null;
        if (billingController.getSkuDetails().getValue() != null) {
            skuDetails = billingController.getSkuDetails().getValue().get(planSku);
        }
        if (skuDetails == null) {
            Timber.e("Could not find SkuDetails to make purchase.");
            return;
        }
        BillingFlowParams.Builder billingBuilder =
                BillingFlowParams.newBuilder().setSkuDetails(skuDetails);
        if (oldPlanSku != null && !planSku.equals(oldPlanSku)) {
            Purchase oldPurchase = BillingUtilities
                    .getPurchaseForSku(billingController.getSubscriptionPurchases().getValue(), oldPlanSku);
            billingBuilder.setSubscriptionUpdateParams(
                    BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                            .setOldSkuPurchaseToken(oldPurchase.getPurchaseToken())
                            .build());
        }
        BillingFlowParams billingParams = billingBuilder.build();
        int responseCode = billingController.launchBillingFlow(activity, billingParams);
    }
}
