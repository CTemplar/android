package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.billing.BillingViewModel;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanInfo;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ActivitySubscriptionBinding;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;

public class SubscriptionActivity extends AppCompatActivity implements ViewPagerAdapter.ViewPagerAdapterListener {
    private ActivitySubscriptionBinding binding;
    private BillingViewModel billingViewModel;

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
//        binding.subscribeButton.setOnClickListener(v -> subscribe());
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        billingViewModel = new ViewModelProvider(this).get(BillingViewModel.class);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) ->
                tab.setText(adapter.getItemTitle(position))).attach();
        String planJsonData = PlanInfo.getJSON(this);
        List<PlanInfo> itemsList = new ArrayList<>();
        itemsList.add(new PlanInfo(PlanType.PRIME, planJsonData));
        itemsList.add(new PlanInfo(PlanType.KNIGHT, planJsonData));
        itemsList.add(new PlanInfo(PlanType.MARSHAL, planJsonData));
        itemsList.add(new PlanInfo(PlanType.CHAMPION, planJsonData));
        adapter.setItems(itemsList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
