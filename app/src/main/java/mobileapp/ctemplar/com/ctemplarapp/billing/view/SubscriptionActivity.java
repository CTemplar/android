package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import static mobileapp.ctemplar.com.ctemplarapp.billing.view.MySubscriptionDialog.CURRENT_PLAN_DATA;
import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;
import mobileapp.ctemplar.com.ctemplarapp.utils.BillingUtils;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingViewModel;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.CurrentPlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanInfo;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivitySubscriptionBinding;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SubscriptionMobileUpgradeRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.SubscriptionMobileUpgradeResponse;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import mobileapp.ctemplar.com.ctemplarapp.view.menu.BillingPlanCycleMenuItem;
import timber.log.Timber;

public class SubscriptionActivity extends AppCompatActivity implements ViewPagerAdapter.ViewPagerAdapterListener,
        BillingPlanCycleMenuItem.OnPlanCycleChangeListener {
    private ActivitySubscriptionBinding binding;

    private BillingViewModel billingViewModel;
    private final ViewPagerAdapter adapter = new ViewPagerAdapter(this);

    private String planJsonData;
    private Disposable nextPurchasesListenerDisposable;
    private BillingPlanCycleMenuItem billingPlanCycleMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        planJsonData = PlanInfo.getJSON(this);
        binding.viewPager.setAdapter(adapter);
        billingViewModel = new ViewModelProvider(this).get(BillingViewModel.class);
        billingViewModel.getSkuDetailListLiveData().observe(this, this::onSkuDetailsListUpdated);
        billingViewModel.getCurrentPlanDataLiveData().observe(this, this::onCurrentPlanDataChanged);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) ->
                tab.setText(adapter.getItemTitle(position))).attach();
        nextPurchasesListenerDisposable = billingViewModel.listenForNextPurchasesUpdate((purchasesToAck) -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            if (purchasesToAck.length == 0) {
                progressDialog.dismiss();
                return;
            }
            if (purchasesToAck.length > 1) {
                Timber.e("Purchases count is more than 1");
            }
            boolean requestedSubscription = false;
            for (Purchase purchase : purchasesToAck) {
                String subscription = BillingUtils.getPurchaseSubscription(purchase);
                if (subscription == null) {
                    Timber.e("Subscription not found for purchase: %s", purchase.toString());
                    continue;
                }
                PlanType planType = getPlanType(subscription);
                if (planType == null) {
                    Timber.e("Plan type not found for subscription %s", subscription);
                    continue;
                }
                requestedSubscription = true;
                billingViewModel.subscriptionUpgrade(new SubscriptionMobileUpgradeRequest(
                        purchase.getPurchaseToken(),
                        subscription,
                        BillingConstants.GOOGLE,
                        isMonthlySku(subscription) ? BillingConstants.MONTHLY : BillingConstants.ANNUALLY,
                        planType.name()

                )).subscribe(new SingleObserver<SubscriptionMobileUpgradeResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull SubscriptionMobileUpgradeResponse subscriptionMobileUpgradeResponse) {
                        if (!subscriptionMobileUpgradeResponse.isStatus()) {
                            ToastUtils.showToast(SubscriptionActivity.this, getString(R.string.subscription_upgrade_fail));
                        } else {
                            ToastUtils.showToast(SubscriptionActivity.this, getString(R.string.subscription_upgrade_success));
                            billingViewModel.updateUserSubscription();
                        }
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                        ToastUtils.showToast(SubscriptionActivity.this,
                                getString(R.string.subscription_upgrade_fail) + e.getMessage());
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
            if (!requestedSubscription) {
                ToastUtils.showToast(this, getString(R.string.error_connection));
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billing_cycle_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.plan_cycle_menu_item);
        billingPlanCycleMenuItem = (BillingPlanCycleMenuItem) item.getActionView();
        billingPlanCycleMenuItem.setIsYearly(true);
        billingPlanCycleMenuItem.setOnChangeListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nextPurchasesListenerDisposable != null) {
            nextPurchasesListenerDisposable.dispose();
            nextPurchasesListenerDisposable = null;
        }
    }

    private void subscribe(String sku) {
        boolean isPurchaseOwnerDevice = billingViewModel.getIsPurchaseOwnerDeviceLiveData().getValue();
        if (!isPurchaseOwnerDevice) {
            ToastUtils.showToast(this, getString(R.string.subscription_upgrade_another_device));
            return;
        }
        try {
            if (sku == null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                CurrentPlanData currentPlanData = billingViewModel.getCurrentPlanDataLiveData().getValue();
                if (currentPlanData != null) {
                    boolean isMonthly = BillingConstants.MONTHLY.equals(currentPlanData.getPaymentTransactionDTO() == null
                            ? null : currentPlanData.getPaymentTransactionDTO().getPaymentType());
                    String currentSku = getPlanSku(currentPlanData.getPlanType(), isMonthly);
                    intent.setData(Uri.parse(String.format(BillingConstants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
                            currentSku, getPackageName())));
                    startActivity(intent);
                } else {
                    intent.setData(Uri.parse(BillingConstants.PLAY_STORE_SUBSCRIPTION_URL));
                    startActivity(intent);
                }
            }
            billingViewModel.subscribe(this, sku);
        } catch (Throwable e) {
            ToastUtils.showToast(this, e.getMessage());
        }
    }

    @Override
    public void onSubscribeClicked(String sku) {
        subscribe(sku);
    }

    @Override
    public void onOpenCurrentPlanClicked(CurrentPlanData currentPlanData) {
        if (currentPlanData.getPlanType() == PlanType.FREE) {
            return;
        }
        MySubscriptionDialog dialog = new MySubscriptionDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CURRENT_PLAN_DATA, GENERAL_GSON.toJson(currentPlanData));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    @Override
    public void onPlanCycleChange(boolean isYearly) {
        adapter.setPlanCycle(isYearly);
    }

    private void onSkuDetailsListUpdated(List<SkuDetails> skuDetails) {
        List<PlanInfo> itemsList = new ArrayList<>();
        PlanInfo freePlanInfo = new PlanInfo(PlanType.FREE, planJsonData);
        PlanInfo primePlanInfo = new PlanInfo(PlanType.PRIME, planJsonData);
        PlanInfo knightPlanInfo = new PlanInfo(PlanType.KNIGHT, planJsonData);
        PlanInfo marshalPlanInfo = new PlanInfo(PlanType.MARSHAL, planJsonData);
        PlanInfo championPlanInfo = new PlanInfo(PlanType.CHAMPION, planJsonData);
        itemsList.add(freePlanInfo);
        itemsList.add(primePlanInfo);
        itemsList.add(knightPlanInfo);
        itemsList.add(marshalPlanInfo);
        itemsList.add(championPlanInfo);
        for (SkuDetails skuDetail : skuDetails) {
            switch (skuDetail.getSku()) {
                case BillingConstants.PRIME_MONTHLY_SKU:
                    primePlanInfo.setMonthlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.PRIME_ANNUAL_SKU:
                    primePlanInfo.setYearlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.KNIGHT_MONTHLY_SKU:
                    knightPlanInfo.setMonthlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.KNIGHT_ANNUAL_SKU:
                    knightPlanInfo.setYearlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.MARSHAL_MONTHLY_SKU:
                    marshalPlanInfo.setMonthlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.MARSHAL_ANNUAL_SKU:
                    marshalPlanInfo.setYearlyPlanSkuDetails(skuDetail);
                    break;
                case BillingConstants.CHAMPION_MONTHLY_SKU:
                    championPlanInfo.setMonthlyPlanSkuDetails(skuDetail);
                    break;
                default:
                    Timber.e("Undefined sku %s", skuDetail.getSku());
            }
        }
        adapter.setItems(itemsList);
    }

    private String getPlanSku(PlanType planType, boolean isMonthly) {
        switch (planType) {
            case FREE:
                return null;
            case PRIME:
                return isMonthly ? BillingConstants.PRIME_MONTHLY_SKU : BillingConstants.PRIME_ANNUAL_SKU;
            case KNIGHT:
                return isMonthly ? BillingConstants.KNIGHT_MONTHLY_SKU : BillingConstants.KNIGHT_ANNUAL_SKU;
            case MARSHAL:
                return isMonthly ? BillingConstants.MARSHAL_MONTHLY_SKU : BillingConstants.MARSHAL_ANNUAL_SKU;
            case CHAMPION:
                return isMonthly ? BillingConstants.CHAMPION_MONTHLY_SKU : null;
        }
        return null;
    }

    private static boolean isMonthlySku(String sku) {
        switch (sku) {
            case BillingConstants.PRIME_MONTHLY_SKU:
            case BillingConstants.MARSHAL_MONTHLY_SKU:
            case BillingConstants.KNIGHT_MONTHLY_SKU:
            case BillingConstants.CHAMPION_MONTHLY_SKU:
                return true;
            case BillingConstants.PRIME_ANNUAL_SKU:
            case BillingConstants.MARSHAL_ANNUAL_SKU:
            case BillingConstants.KNIGHT_ANNUAL_SKU:
            default:
                return false;
        }
    }

    private static PlanType getPlanType(String sku) {
        switch (sku) {
            case BillingConstants.PRIME_MONTHLY_SKU:
            case BillingConstants.PRIME_ANNUAL_SKU:
                return PlanType.PRIME;
            case BillingConstants.MARSHAL_MONTHLY_SKU:
            case BillingConstants.MARSHAL_ANNUAL_SKU:
                return PlanType.MARSHAL;
            case BillingConstants.KNIGHT_MONTHLY_SKU:
            case BillingConstants.KNIGHT_ANNUAL_SKU:
                return PlanType.KNIGHT;
            case BillingConstants.CHAMPION_MONTHLY_SKU:
                return PlanType.CHAMPION;
        }
        return null;
    }

    private void onCurrentPlanDataChanged(CurrentPlanData currentPlanData) {
        adapter.setCurrentPlanData(currentPlanData);
        int index = adapter.getItemIndexByPlanType(currentPlanData.getPlanType());
        if (index >= 0) {
            binding.tabs.selectTab(binding.tabs.getTabAt(index));
            if (currentPlanData.getPaymentTransactionDTO() != null) {
                if (billingPlanCycleMenuItem == null) {
                    return;
                }
                boolean isMonthlyType = BillingConstants.MONTHLY.equals(
                        currentPlanData.getPaymentTransactionDTO().getPaymentType());
                billingPlanCycleMenuItem.setIsYearly(!isMonthlyType);
            }
        }
    }
}
