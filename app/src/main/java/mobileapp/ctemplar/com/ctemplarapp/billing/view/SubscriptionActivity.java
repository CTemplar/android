package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.SkuDetails;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingViewModel;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanInfo;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivitySubscriptionBinding;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import mobileapp.ctemplar.com.ctemplarapp.view.menu.BillingPlanCycleMenuItem;
import timber.log.Timber;

public class SubscriptionActivity extends AppCompatActivity implements ViewPagerAdapter.ViewPagerAdapterListener, BillingPlanCycleMenuItem.OnPlanCycleChangeListener {
    private ActivitySubscriptionBinding binding;
    private BillingViewModel billingViewModel;
    private final ViewPagerAdapter adapter = new ViewPagerAdapter(this);
    private String planJsonData;

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
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) ->
                tab.setText(adapter.getItemTitle(position))).attach();
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
        BillingPlanCycleMenuItem view = (BillingPlanCycleMenuItem) item.getActionView();
        view.setIsYearly(true);
        view.setOnChangeListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    private void subscribe(String sku) {
        try {
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
}
