package mobileapp.ctemplar.com.ctemplarapp.contact;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.ResponseStatus;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;

public class EditContactActivity extends BaseActivity {

    public static final String ARG_ID = "id";
    private EditContactViewModel viewModel;
    private long contactId;

    @BindView(R.id.activity_edit_contact_name_input)
    EditText editTextContactName;
    @BindView(R.id.activity_edit_contact_email_input)
    EditText editTextContactEmail;
    @BindView(R.id.activity_edit_contact_phone_input)
    EditText editTextContactPhoneNumber;
    @BindView(R.id.activity_edit_contact_address_input)
    EditText editTextContactAddress;
    @BindView(R.id.activity_edit_contact_note_input)
    EditText editTextContactNote;
    @BindView(R.id.activity_edit_contact_progress_bar)
    View progressBar;

    @BindView(R.id.edit_contact_toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(EditContactViewModel.class);
        viewModel.getResponseStatus().observe(this, new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus responseStatus) {
                handleResponse(responseStatus);
            }
        });

        viewModel.getContactResponse().observe(this, new Observer<Contact>() {
            @Override
            public void onChanged(@Nullable Contact contact) {
                if (contact != null) {
                    fillFields(contact);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        long id = getIntent().getLongExtra(ARG_ID, -1);
        if (id == -1) {
            return; //ToDo
        }

        viewModel.getContact(id);
    }

    private void fillFields(Contact contact) {
        contactId = contact.getId();
        editTextContactName.setText(contact.getName());
        editTextContactEmail.setText(contact.getEmail());
        editTextContactPhoneNumber.setText(contact.getPhone());
        editTextContactAddress.setText(contact.getAddress());
        editTextContactNote.setText(contact.getNote());
    }

    private void handleResponse(ResponseStatus responseStatus) {
        if (responseStatus == null || responseStatus == ResponseStatus.RESPONSE_ERROR) {
            onBackPressed();
            String loadingErrorTxt = getResources().getString(R.string.txt_contact_loading_error);
            Toast.makeText(this, loadingErrorTxt, Toast.LENGTH_SHORT).show();
        } else if (responseStatus == ResponseStatus.RESPONSE_COMPLETE) {
            onBackPressed();
        }
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
                updateContact();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void updateContact() {
        String contactName = editTextContactName.getText().toString();
        String contactEmail = editTextContactEmail.getText().toString();
        String contactPhone = editTextContactPhoneNumber.getText().toString();
        String contactAddress = editTextContactAddress.getText().toString();
        String contactNote = editTextContactNote.getText().toString();

        if (contactName.isEmpty()) {
            editTextContactName.setError(getResources().getString(R.string.txt_enter_name));
        } else {
            editTextContactName.setError(null);
        }
        if (Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
            editTextContactEmail.setError(null);
        } else {
            editTextContactEmail.setError(getResources().getString(R.string.txt_enter_valid_email));
        }
        if (editTextContactName.getError() != null || editTextContactEmail.getError() != null) {
            return;
        }

        ContactData contactData = new ContactData();
        contactData.setId(contactId);
        contactData.setName(contactName);
        contactData.setEmail(contactEmail);
        contactData.setPhone(contactPhone);
        contactData.setAddress(contactAddress);
        contactData.setNote(contactNote);

        viewModel.updateContact(contactData);
    }

}