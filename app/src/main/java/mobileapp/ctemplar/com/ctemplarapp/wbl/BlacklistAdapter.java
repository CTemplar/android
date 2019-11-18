package mobileapp.ctemplar.com.ctemplarapp.wbl;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Myself.BlackListContact;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.DataViewHolder> {
    private List<BlackListContact> contactsList;
    private List<BlackListContact> filteredList;

    public BlacklistAdapter(List<BlackListContact> contacts) {
        contactsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        contactsList.addAll(contacts);
        filteredList.addAll(contacts);
    }

    public BlacklistAdapter(BlackListContact[] contacts) {
        this(Arrays.asList(contacts));
    }

    private BlackListContact getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_contact_holder, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i) {
        BlackListContact contact = getItem(i);
        dataViewHolder.emailView.setText(contact.email);
        dataViewHolder.nameView.setText(contact.name);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    public void filter(CharSequence s) {
        filteredList.clear();
        for (BlackListContact contact : contactsList) {
            if (contact.name.contains(s) || contact.email.contains(s)) {
                filteredList.add(contact);
            }
        }
        notifyDataSetChanged();
    }

    public BlackListContact removeAt(int position) {
        BlackListContact removedContact = filteredList.remove(position);
        contactsList.remove(removedContact);
        notifyItemRemoved(position);
        return removedContact;
    }

    public void restoreItem(BlackListContact contact, int position) {
        if (contactsList.contains(contact)) {
            return;
        }
        contactsList.add(position, contact);
        filteredList.add(position, contact);
        notifyItemInserted(position);
    }

    class DataViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView emailView;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.item_contact_holder_name);
            emailView = itemView.findViewById(R.id.item_contact_holder_mail);
        }
    }
}
