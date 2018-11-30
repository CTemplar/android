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
import java.util.LinkedList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;

public class RecipientsListAdapter extends ArrayAdapter<ContactData> implements Filterable {

    List<ContactData> data = new LinkedList<>();
    List<ContactData> filteredData = new LinkedList<>();
    LayoutInflater layoutInflater;

    public RecipientsListAdapter(Context context, int textViewResourceId, List<ContactData> data) {
        super(context, textViewResourceId, data);
        this.data.addAll(data);
        filteredData.addAll(data);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = layoutInflater.inflate(R.layout.recipients_list_view_item, parent, false);
        }

        ContactData contactData = data.get(position);

        TextView firstLastName = vi.findViewById(R.id.recipients_list_view_item_first_last_name);
        TextView mail = vi.findViewById(R.id.recipients_list_view_item_mail);

        firstLastName.setText(contactData.getName());
        mail.setText(contactData.getEmail());

        return vi;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<ContactData> suggestions = new ArrayList<>();
                for (ContactData contactData : data) {
                    // Note: change the "contains" toEmail "startsWith" if you only want starting matches
                    if (contactData.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || contactData.getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(contactData);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<ContactData>) results.values);
            } else {
                // no filter, add entire original list back in
//                addAll(data);
            }
            notifyDataSetChanged();
        }
    };
}
