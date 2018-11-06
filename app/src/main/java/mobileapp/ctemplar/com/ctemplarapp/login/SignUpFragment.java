package mobileapp.ctemplar.com.ctemplarapp.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.login.step.SignUpFragmentsAdapter;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepEmailFragment;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepPasswordFragment;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepRegistrationActions;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepRegistrationViewModel;
import mobileapp.ctemplar.com.ctemplarapp.login.step.StepUsernameFragment;
import mobileapp.ctemplar.com.ctemplarapp.login.step.ViewPagerNoScroll;

public class SignUpFragment extends BaseFragment{

    @BindView(R.id.fragment_sign_up_view_pager)
    ViewPagerNoScroll viewPager;

    StepRegistrationViewModel stepModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new StepUsernameFragment());
        list.add(new StepPasswordFragment());
        list.add(new StepEmailFragment());

        viewPager.setAdapter(new SignUpFragmentsAdapter(getFragmentManager(), list));
        viewPager.setOnTouchListener(null);

        stepModel = ViewModelProviders.of(getActivity()).get(StepRegistrationViewModel.class);
        stepModel.getAction().observe(this, new Observer<StepRegistrationActions>() {
            @Override
            public void onChanged(@Nullable StepRegistrationActions stepRegistrationActions) {
                handleRegistrationActions(stepRegistrationActions);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick(R.id.fragment_sign_up_back)
    public void onClickBack() {
        stepModel.changeAction(StepRegistrationActions.ACTION_BACK);
        // onBackPressed();
    }

    public void onBackPressed() {
        if(viewPager.getCurrentItem() == 0) {
            getActivity().onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void onNextPressed() {
        if(viewPager.getCurrentItem() != viewPager.getAdapter().getCount() -1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void handleRegistrationActions(StepRegistrationActions actions) {
        switch (actions) {
            case ACTION_BACK:
                onBackPressed();
                stepModel.changeAction(StepRegistrationActions.ACTION_DEFAULT);
                break;
            case ACTION_NEXT:
                onNextPressed();
                stepModel.changeAction(StepRegistrationActions.ACTION_DEFAULT);
                break;
        }
    }
}
