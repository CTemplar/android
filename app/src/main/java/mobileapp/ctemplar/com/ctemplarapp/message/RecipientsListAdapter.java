package mobileapp.ctemplar.com.ctemplarapp.message;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;

public class RecipientsListAdapter extends ArrayAdapter<Contact> implements Filterable {

    private Context context;

    private List<Contact> contactList;
    private List<Contact> tempContactList;
    private List<Contact> suggestionList;

    RecipientsListAdapter(Context context, int textViewResourceId, List<Contact> contactList) {
        super(context, textViewResourceId, contactList);
        this.context = context;
        this.contactList = contactList;

        tempContactList = new ArrayList<>(contactList);
        suggestionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.recipients_list_view_item, parent, false);
        } else {
            view = convertView;
        }

        Contact contact = contactList.get(position);
        if (contact != null) {
            TextView firstLastName = view.findViewById(R.id.recipients_list_view_item_first_last_name);
            TextView email = view.findViewById(R.id.recipients_list_view_item_mail);

            firstLastName.setText(contact.getName());
            email.setText(contact.getEmail());
        }

        return view;
    }

    private Filter mFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Contact) resultValue).getEmail();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestionList.clear();
                for (Contact contact : tempContactList) {
                    String contactName = contact.getName().toLowerCase();
                    String contactEmail = contact.getEmail().toLowerCase();

                    if (contactName.contains(constraint.toString().toLowerCase()) ||
                            contactEmail.contains(constraint.toString().toLowerCase())
                    ) {
                        suggestionList.add(contact);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestionList;
                filterResults.count = suggestionList.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Object object = results.values;
            if (!(object instanceof List)) {
                return;
            }
            List<?> objects = (List<?>) object;
            if (objects.size() > 0) {
                clear();
                for (Object contactObject : objects) {
                    if (!(contactObject instanceof Contact)) {
                        continue;
                    }
                    add((Contact) contactObject);
                    notifyDataSetChanged();
                }
            }
        }
    };

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
