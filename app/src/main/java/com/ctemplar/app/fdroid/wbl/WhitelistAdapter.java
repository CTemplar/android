package com.ctemplar.app.fdroid.wbl;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.response.myself.WhiteListContact;

public class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.DataViewHolder> {
    private List<WhiteListContact> contactsList;
    private List<WhiteListContact> filteredList;

    public WhitelistAdapter(List<WhiteListContact> contacts) {
        contactsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        contactsList.addAll(contacts);
        filteredList.addAll(contacts);
    }

    public WhitelistAdapter(WhiteListContact[] contacts) {
        this(Arrays.asList(contacts));
    }

    private WhiteListContact getItem(int position) {
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
        WhiteListContact contact = getItem(i);
        dataViewHolder.emailView.setText(contact.getEmail());
        dataViewHolder.nameView.setText(contact.getName());
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(CharSequence s) {
        filteredList.clear();
        for (WhiteListContact contact : contactsList) {
            if (contact.getName().contains(s) || contact.getEmail().contains(s)) {
                filteredList.add(contact);
            }
        }
        notifyDataSetChanged();
    }

    public WhiteListContact removeAt(int position) {
        WhiteListContact removedContact = filteredList.remove(position);
        contactsList.remove(removedContact);
        notifyItemRemoved(position);
        return removedContact;
    }

    public void restoreItem(WhiteListContact contact, int position) {
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
