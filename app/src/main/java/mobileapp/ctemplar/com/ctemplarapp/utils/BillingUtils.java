package mobileapp.ctemplar.com.ctemplarapp.utils;

import androidx.annotation.Nullable;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.List;
import java.util.Map;

import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;

public class BillingUtils {
    public static boolean isSubscriptionSku(String sku) {
        for (String subscription : BillingConstants.ALL_SUBSCRIPTIONS) {
            if (subscription.equals(sku)) {
                return true;
            }
        }
        return false;
    }

    public static void checkSubscriptionSku(String sku) {
        for (String subscription : BillingConstants.ALL_SUBSCRIPTIONS) {
            if (subscription.equals(sku)) {
                return;
            }
        }
        throw new RuntimeException("Not found subscription '" + sku + "'");
    }

    public static void checkLoadedSubscriptionSku(Map<String, SkuDetails> skuDetailsMap, String sku) {
        if (skuDetailsMap == null) {
            throw new RuntimeException("Sku details is not loaded");
        }
        SkuDetails skuDetails = skuDetailsMap.get(sku);
        if (skuDetails == null) {
            throw new RuntimeException("Sku not found");
        }
    }

    public static String getPurchaseSubscription(Purchase purchase) {
        List<String> skus = purchase.getSkus();
        for (String sku : skus) {
            if (isSubscriptionSku(sku)) {
                return sku;
            }
        }
        return null;
    }

    public static Purchase getCurrentSubscriptionPurchase(List<Purchase> purchases) {
        if (purchases == null) {
            throw new RuntimeException("Purchases not instantiated");
        }
        if (purchases.isEmpty()) {
            return null;
        }
        for (Purchase purchase : purchases) {
            String subscriptionSku = getPurchaseSubscription(purchase);
            if (subscriptionSku != null) {
                return purchase;
            }
        }
        return null;
    }

    public static Purchase getPurchaseForSku(@Nullable List<Purchase> purchases, String sku) {
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                if (sku.equals(purchase.getSkus().get(0))) {
                    return purchase;
                }
            }
        }
        return null;
    }

    public static boolean deviceHasGooglePlaySubscription(List<Purchase> purchases, String sku) {
        return getPurchaseForSku(purchases, sku) != null;
    }
}
