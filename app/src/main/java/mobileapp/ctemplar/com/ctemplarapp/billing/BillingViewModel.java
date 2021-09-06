package mobileapp.ctemplar.com.ctemplarapp.billing;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.CurrentPlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
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
            Timber.e("Multiple purchases???");
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
