package mobileapp.ctemplar.com.ctemplarapp.message;

import android.content.Context;
import android.support.annotation.NonNull;
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
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;

public class RecipientsListAdapter extends ArrayAdapter<ContactData> implements Filterable {

    Context context;
    int textViewResourceId;
    List<ContactData> contacts, tempContacts, suggestions;

    public RecipientsListAdapter(Context context, int textViewResourceId, List<ContactData> contacts) {
        super(context, textViewResourceId, contacts);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.contacts = contacts;
        tempContacts = new ArrayList<>(contacts);
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.recipients_list_view_item, parent, false);
        }

        ContactData contactData = contacts.get(position);
        if (contactData != null) {
            TextView firstLastName = view.findViewById(R.id.recipients_list_view_item_first_last_name);
            TextView mail = view.findViewById(R.id.recipients_list_view_item_mail);
            if (firstLastName != null) {
                firstLastName.setText(contactData.getName());
            }
            if (mail != null) {
                mail.setText(contactData.getEmail());
            }
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    Filter mFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((ContactData) resultValue).getEmail();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ContactData contacts : tempContacts) {
                    if (contacts.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || contacts.getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {

                        suggestions.add(contacts);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<ContactData> filterList = (ArrayList<ContactData>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ContactData contacts : filterList) {
                    add(contacts);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
