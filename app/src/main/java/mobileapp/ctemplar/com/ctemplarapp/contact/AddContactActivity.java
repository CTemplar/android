package mobileapp.ctemplar.com.ctemplarapp.contact;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;

public class AddContactActivity extends BaseActivity {

    private AddContactViewModel viewModel;

    @BindView(R.id.activity_add_contact_name_input)
    EditText editTextContactName;
    @BindView(R.id.activity_add_contact_email_input)
    EditText editTextContactEmail;
    @BindView(R.id.activity_add_contact_phone_input)
    EditText editTextContactPhoneNumber;
    @BindView(R.id.activity_add_contact_address_input)
    EditText editTextContactAddress;
    @BindView(R.id.activity_add_contact_note_input)
    EditText editTextContactNote;

    @BindView(R.id.add_contact_toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(AddContactViewModel.class);
        viewModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                handleResponse(responseStatus);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_contact:
                saveContact();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void handleResponse(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            Toast.makeText(this, "Not saved", Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            onBackPressed();
        } else {
            Toast.makeText(this, "Undefined error", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveContact() {
        String contactName = editTextContactName.getText().toString();
        String contactEmail = editTextContactEmail.getText().toString();
        String contactPhone = editTextContactPhoneNumber.getText().toString();
        String contactAddress = editTextContactAddress.getText().toString();
        String contactNote = editTextContactNote.getText().toString();

        if (contactName.isEmpty()) {
            editTextContactName.setError("Enter Name");
        } else {
            editTextContactName.setError(null);
        }
        if (Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
            editTextContactEmail.setError(null);
        } else {
            editTextContactEmail.setError("Enter valid email address");
        }
        if (editTextContactName.getError() != null || editTextContactEmail.getError() != null) {
            return;
        }

        ContactData contactData = new ContactData();
        contactData.setName(contactName);
        contactData.setEmail(contactEmail);
        contactData.setPhone(contactPhone);
        contactData.setAddress(contactAddress);
        contactData.setNote(contactNote);

        viewModel.saveContact(contactData);
    }

}
