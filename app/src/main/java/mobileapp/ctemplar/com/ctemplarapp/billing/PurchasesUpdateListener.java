package mobileapp.ctemplar.com.ctemplarapp.billing;

import com.android.billingclient.api.Purchase;

public interface PurchasesUpdateListener {
    void onPurchasesUpdate(Purchase[] purchases);
}
