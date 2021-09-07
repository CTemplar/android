package mobileapp.ctemplar.com.ctemplarapp.billing;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.sentry.Sentry;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.CurrentPlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubscriptionMobileUpgradeRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SubscriptionMobileUpgradeResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.MyselfResult;
import mobileapp.ctemplar.com.ctemplarapp.net.response.myself.PaymentTransactionResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PaymentTransactionDTO;
import timber.log.Timber;

public class BillingViewModel extends AndroidViewModel {
    private final BillingController billingController;
    private final UserRepository userRepository;
    private final MutableLiveData<CurrentPlanData> currentPlanDataLiveData;
    private final List<PurchasesUpdateListener> nextPurchasesUpdateListeners = new ArrayList();
//    private MediatorLiveData<List<SubscriptionStatus>> subscriptions;

    public BillingViewModel(Application application) {
        super(application);
        currentPlanDataLiveData = new MutableLiveData<>();
        userRepository = CTemplarApp.getUserRepository();
        billingController = BillingController.getInstance(application);
        billingController.init();
        billingController.getSubscriptionPurchases().observeForever(this::handlePurchases);
        loadUserSubscription();
    }

    @Override
    public void onCleared() {
        billingController.dispose();
        billingController.getSubscriptionPurchases().removeObserver(this::handlePurchases);
    }

    public LiveData<List<SkuDetails>> getSkuDetailListLiveData() {
        return Transformations.map(billingController.getSkuDetails(), input -> new ArrayList<>(input.values()));
    }

    public LiveData<CurrentPlanData> getCurrentPlanDataLiveData() {
        return currentPlanDataLiveData;
    }

    public void updateUserSubscription() {
        loadUserSubscription();
    }

    private void loadUserSubscription() {
        userRepository.getMyselfInfo().subscribe(new Observer<MyselfResponse>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull MyselfResponse myselfResponse) {
                MyselfResult result = myselfResponse.getResult()[0];
                PlanType planType = result.getSettings().getPlanType();
                if (planType == PlanType.FREE) {
                    currentPlanDataLiveData.setValue(new CurrentPlanData(PlanType.FREE, null));
                    return;
                }
                PaymentTransactionResponse paymentTransactionResponse = result.getPaymentTransaction();
                if (paymentTransactionResponse == null) {
                    Timber.e("Paid plan type does not contains payment transaction");
                    return;
                }
                currentPlanDataLiveData.setValue(new CurrentPlanData(planType,
                        PaymentTransactionDTO.fromResponse(paymentTransactionResponse)));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Timber.e(e, "Failed to load user subscription");
            }

            @Override
            public void onComplete() {

            }
        });
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
        List<Purchase> notAckedPurchases = new ArrayList<>();
        for (Purchase purchase : purchases) {
            if (purchase.isAcknowledged()) {
                continue;
            }
            String subscription = BillingUtilities.getPurchaseSubscription(purchase);
            if (subscription == null) {
                continue;
            }
            notAckedPurchases.add(purchase);
            Timber.e("Purchase needs to be requested to the server: %s", purchase.getPurchaseToken());
        }
        if (notAckedPurchases.isEmpty()) {
            return;
        }
        if (nextPurchasesUpdateListeners.isEmpty()) {
            Timber.e("Not found update listener for %s purchases", notAckedPurchases.size());
            return;
        }
        List<PurchasesUpdateListener> listeners = new ArrayList<>(nextPurchasesUpdateListeners);
//        nextPurchasesUpdateListeners.clear();
        for (PurchasesUpdateListener listener : listeners) {
            listener.onPurchasesUpdate(notAckedPurchases.toArray(new Purchase[0]));
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

    public Disposable listenForNextPurchasesUpdate(PurchasesUpdateListener listener) {
        nextPurchasesUpdateListeners.add(listener);
        return new Disposable() {
            @Override
            public void dispose() {
                nextPurchasesUpdateListeners.remove(listener);
            }

            @Override
            public boolean isDisposed() {
                return !nextPurchasesUpdateListeners.contains(listener);
            }
        };
    }

    private void subscribeInternal(Activity activity, String planSku, String oldPlanSku) {
        Timber.i("subscribeInternal " + planSku + (oldPlanSku == null ? "" : (" => " + oldPlanSku)));

        SkuDetails skuDetails = null;
        if (billingController.getSkuDetails().getValue() != null) {
            skuDetails = billingController.getSkuDetails().getValue().get(planSku);
        }
        if (skuDetails == null) {
            Timber.e("Could not find SkuDetails to make purchase.");
            throw new RuntimeException("Could not find SkuDetails to make purhase");
        }
        BillingFlowParams.Builder billingBuilder =
                BillingFlowParams.newBuilder().setSkuDetails(skuDetails);
        if (oldPlanSku != null && !planSku.equals(oldPlanSku)) {
            Purchase oldPurchase = BillingUtilities
                    .getPurchaseForSku(billingController.getSubscriptionPurchases().getValue(), oldPlanSku);
            if (oldPurchase != null) {
                billingBuilder.setSubscriptionUpdateParams(
                        BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                .setOldSkuPurchaseToken(oldPurchase.getPurchaseToken())
                                .build());
            } else {
                Sentry.captureMessage("oldPurchase is null");
                Timber.wtf("oldPurchase is null!");
                throw new RuntimeException("Failed to get current purchases. Please, try later");
            }
        }
        BillingFlowParams billingParams = billingBuilder.build();
        int responseCode = billingController.launchBillingFlow(activity, billingParams);
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            return;
        }
        switch (responseCode) {
            case BillingClient.BillingResponseCode.USER_CANCELED:
                throw new RuntimeException("Cancelled");
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                throw new RuntimeException("Item is already owned");
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                throw new RuntimeException("Bad configuration. Please, upgrade the app");
        }
        throw new RuntimeException("Failed to purchase. Unknown error (" + responseCode + ")");

    }

    public Single<SubscriptionMobileUpgradeResponse> subscriptionUpgrade(SubscriptionMobileUpgradeRequest request) {
        return userRepository.subscriptionUpgrade(request);
    }
}
