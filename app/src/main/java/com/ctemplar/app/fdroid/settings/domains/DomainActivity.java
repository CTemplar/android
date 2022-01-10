package com.ctemplar.app.fdroid.settings.domains;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.ActivityCustomDomainBinding;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.domains.CustomDomainDTO;
import com.ctemplar.app.fdroid.settings.domains.step.DKIMStepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.DMARCStepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.DomainNameStepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.MXStepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.SPFStepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.StepFragment;
import com.ctemplar.app.fdroid.settings.domains.step.VerifyStepFragment;
import com.ctemplar.app.fdroid.utils.ThemeUtils;
import com.ctemplar.app.fdroid.utils.ToastUtils;
import timber.log.Timber;

public class DomainActivity extends BaseActivity implements StepFragment.StepActionListener {
    public static final String EDIT_DOMAIN_KEY = "domain.id";
    private static final List<StepFragment> stepFragments = Arrays.asList(
            new DomainNameStepFragment(),
            new VerifyStepFragment(),
            new MXStepFragment(),
            new SPFStepFragment(),
            new DKIMStepFragment(),
            new DMARCStepFragment()
    );

    private ActivityCustomDomainBinding binding;
    private DomainsViewModel domainsViewModel;

    private CustomDomainDTO domainDTO;
    private boolean initialized;
    private int editDomainId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        binding = ActivityCustomDomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        for (StepFragment stepFragment : stepFragments) {
            stepFragment.setListener(this);
        }
        domainsViewModel = new ViewModelProvider(this).get(DomainsViewModel.class);
        Intent intent = getIntent();
        if (intent != null) {
            editDomainId = intent.getIntExtra(EDIT_DOMAIN_KEY, -1);
        }
        domainsViewModel.getCustomDomain().observe(this, this::handleCustomDomain);
        if (editDomainId == -1) {
            init();
        } else {
            if (actionBar != null) {
                actionBar.setTitle(R.string.edit_domain);
            }
            binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
                @NonNull
                @Override
                public Fragment createFragment(int position) {
                    return new Fragment();
                }

                @Override
                public int getItemCount() {
                    return 0;
                }
            });
            domainsViewModel.customDomainRequest(editDomainId);
        }
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (editDomainId != -1 && position < 1) {
                    binding.viewPager.setCurrentItem(1, false);
                    return;
                }
                if (domainDTO == null) {
                    if (positionOffset > 0) {
                        binding.viewPager.setCurrentItem(0, false);
                    }
                } else {
                    int domainVerifyIndex = 1;
                    boolean isVerified = isStepVerified(domainDTO, domainVerifyIndex);
                    if (isVerified) {
                        return;
                    }
                    if (position > domainVerifyIndex) {
                        binding.viewPager.setCurrentItem(domainVerifyIndex, false);
                    } else if (position == domainVerifyIndex && positionOffset > 0) {
                        binding.viewPager.setCurrentItem(domainVerifyIndex, false);
                    }
                }
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

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return stepFragments.get(position);
            }

            @Override
            public int getItemCount() {
                return stepFragments.size();
            }
        });
        binding.viewPager.setOffscreenPageLimit(stepFragments.size());
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, this::setTabTextView).attach();
    }

    private void handleCustomDomain(DTOResource<CustomDomainDTO> domainDTOResource) {
        if (!domainDTOResource.isSuccess()) {
            ToastUtils.showToast(this, domainDTOResource.getError());
            return;
        }
        this.domainDTO = domainDTOResource.getDto();
        init();
        for (StepFragment stepFragment : stepFragments) {
            stepFragment.setDomain(domainDTO);
        }
        binding.viewPager.setCurrentItem(1, false);
        int tabCount = binding.tabLayout.getTabCount();
        for (int i = 0; i < tabCount; ++i) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            if (tab == null) {
                continue;
            }
            setTabTextView(tab, i, true);
        }
        for (int i = 0; i < tabCount; ++i) {
            boolean verified = isStepVerified(domainDTO, i);
            if (!verified) {
                if (i != 0) {
                    binding.viewPager.setCurrentItem(i);
                }
                break;
            }
        }
    }

    private void setTabTextView(TabLayout.Tab tab, int position) {
        setTabTextView(tab, position, false);
    }

    private void setTabTextView(TabLayout.Tab tab, int position, boolean verifyColors) {
        int blueColor = ContextCompat.getColor(this, R.color.colorDarkBlue2);
        int greenColor = ContextCompat.getColor(this, R.color.colorGreen3);
        int redColor = ContextCompat.getColor(this, R.color.colorOrangeLight);
        String tabName;
        String tabLabel;
        switch (position) {
            case 0:
                tabName = domainDTO == null
                        ? getString(R.string.domain_name)
                        : domainDTO.getDomain().toUpperCase();
                tabLabel = "";
                break;
            case 1:
                tabName = getString(R.string.verify);
                tabLabel = getString(R.string.required);
                break;
            case 2:
                tabName = getString(R.string.mx);
                tabLabel = getString(R.string.required);
                break;
            case 3:
                tabName = getString(R.string.spf);
                tabLabel = getString(R.string.recommended);
                break;
            case 4:
                tabName = getString(R.string.dkim);
                tabLabel = getString(R.string.recommended);
                break;
            case 5:
                tabName = getString(R.string.dmarc);
                tabLabel = getString(R.string.optional_advanced);
                break;
            default:
                tabName = "";
                tabLabel = "";
        }
        boolean verified = isStepVerified(domainDTO, position);
        int textColor = verifyColors ? verified ? greenColor : redColor : blueColor;
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(tabName);
        spannableBuilder.setSpan(
                new ForegroundColorSpan(textColor),
                0,
                spannableBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableBuilder.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                spannableBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableBuilder.append(" ").append(tabLabel);
        TextView textView = new TextView(this);
        textView.setText(spannableBuilder, TextView.BufferType.SPANNABLE);
        if (tab.getCustomView() != null) {
            tab.setCustomView(null);
        }
        tab.setCustomView(textView);
    }

    private boolean isStepVerified(CustomDomainDTO domainDTO, int number) {
        if (domainDTO == null) {
            return false;
        }
        switch (number) {
            case 0:
                return true;
            case 1:
                return domainDTO.isDomainVerified();
            case 2:
                return domainDTO.isMxVerified();
            case 3:
                return domainDTO.isSpfVerified();
            case 4:
                return domainDTO.isDkimVerified();
            case 5:
                return domainDTO.isDmarcVerified();
            default:
                return false;
        }
    }

    @Override
    public void onVerifyStepClick() {
        if (editDomainId == -1) {
            Timber.e("editDomainId is -1");
            return;
        }
        domainsViewModel.verifyCustomDomainRequest(editDomainId);
    }

    @Override
    public void onNextStepClick() {
        binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
    }

    @Override
    public void onDomainCreated(CustomDomainDTO domainDTO) {
        editDomainId = domainDTO.getId();
        domainsViewModel.verifyCustomDomainRequest(editDomainId);
    }
}
