package mobileapp.ctemplar.com.ctemplarapp.wbl;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;

public class AddWhitelistContactActivity extends BaseActivity {
    AddWhitelistContactViewModel model;
    @BindView(R.id.activity_add_whitelist_contact_name)
    EditText nameEditView;

    @BindView(R.id.activity_add_whitelist_contact_email)
    EditText emailEditView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_whitelist_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.activity_add_whitelist_contact_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        model = new ViewModelProvider(this).get(AddWhitelistContactViewModel.class);
        model.getResponseStatus()
                .observe(this, responseStatus -> {
                    if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
                        onBackPressed();
                    } else {
                        Toast.makeText(AddWhitelistContactActivity.this, getString(R.string.adding_whitelist_contact_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.activity_add_whitelist_contact_button_add)
    void clickAdd() {
        model.addWhitelistContact(nameEditView.getText().toString(), emailEditView.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
