package com.ctemplar.app.fdroid.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import com.ctemplar.app.fdroid.utils.EditTextUtils;

public class CustomAppCompatMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    public CustomAppCompatMultiAutoCompleteTextView(@NonNull Context context) {
        super(context);
    }

    public CustomAppCompatMultiAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAppCompatMultiAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        if (id == android.R.id.paste) {
            onTextPaste();
        }
        return consumed;
    }

    public void onTextPaste(){
        CharSequence parsed = EditTextUtils.parseInputEmails(getText());
        setText(parsed);
        setSelection(parsed.length());
    }
}
