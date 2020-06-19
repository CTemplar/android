package mobileapp.ctemplar.com.ctemplarapp.view.pinlock;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.ThemeUtils;

public class KeypadView extends RecyclerView {
    private PasscodeView passcodeView;
    private KeypadAdapter adapter;
    private KeypadAdapter.KeypadListener keypadListener;

    private int pinLength = 4;
    private String pinCode = "";

    private KeypadAdapter.OnNumClickListener onNumClickListener
            = new KeypadAdapter.OnNumClickListener() {
        @Override
        public void onNumClicked(int keyValue) {
            if (pinCode.length() < pinLength) {
                pinCode = pinCode.concat(String.valueOf(keyValue));
                if (ispasscodeViewAttached()) {
                    passcodeView.updatepasscode(pinCode.length());
                }
                if (pinCode.length() == 1) {
                    adapter.setPinLength(pinCode.length());
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
                if (keypadListener != null) {
                    if (pinCode.length() == pinLength) {
                        keypadListener.onComplete(pinCode);
                    } else {
                        keypadListener.onPINChanged(pinCode.length(), pinCode);
                    }
                }
            } else {
                if (keypadListener != null) {
                    keypadListener.onComplete(pinCode);
                }
            }
        }
    };

    private KeypadAdapter.OnDeleteClickListener onDeleteClickListener
            = new KeypadAdapter.OnDeleteClickListener() {
        @Override
        public void onDeleteClicked() {
            if (pinCode.length() > 0) {
                pinCode = pinCode.substring(0, pinCode.length() - 1);
                if (ispasscodeViewAttached()) {
                    passcodeView.updatepasscode(pinCode.length());
                }
                if (pinCode.length() == 0) {
                    adapter.setPinLength(pinCode.length());
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
                if (keypadListener != null) {
                    if (pinCode.length() == 0) {
                        keypadListener.onEmpty();
                        resetPinCode();
                    } else {
                        keypadListener.onPINChanged(pinCode.length(), pinCode);
                    }
                }
            } else {
                if (keypadListener != null) {
                    keypadListener.onEmpty();
                }
            }
        }

        @Override
        public void onDeleteLongClicked() {
            resetKeypadView();
            if (keypadListener != null) {
                keypadListener.onEmpty();
            }
        }
    };

    public KeypadView(Context context) {
        super(context);
        initView();
    }

    public KeypadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public KeypadView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new KeypadAdapter();
        adapter.setOnItemClickListener(onNumClickListener);
        adapter.setOnDeleteClickListener(onDeleteClickListener);
        setAdapter(adapter);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setKeypadListener(KeypadAdapter.KeypadListener keypadListener) {
        this.keypadListener = keypadListener;
    }

    public void setPinLength(int pinLength) {
        this.pinLength = pinLength;
    }

    private void resetPinCode() {
        pinCode = "";
    }

    public void resetKeypadView() {
        resetPinCode();
        adapter.setPinLength(pinCode.length());
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
        if (passcodeView != null) {
            passcodeView.updatepasscode(pinCode.length());
        }
    }

    public boolean ispasscodeViewAttached() {
        return passcodeView != null;
    }

    public void attachpasscodeView(PasscodeView passcodeView) {
        this.passcodeView = passcodeView;
    }
}
