package com.ctemplar.app.fdroid.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.repository.entity.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Contact> contactList = new ArrayList<>();
    private final List<Contact> filteredList = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;

    public ContactsAdapter() {
    }

    public void setItems(List<Contact> contactList, String filterText) {
        this.contactList = contactList;
        filter(filterText);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_contact_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public Contact removeAt(int position) {
        Contact removedContact = filteredList.remove(position);
        contactList.remove(removedContact);
        notifyItemRemoved(position);
        return removedContact;
    }

    public void restoreItem(Contact contact, int position) {
        if (contactList.contains(contact)) {
            return;
        }
        contactList.add(position, contact);
        filteredList.add(position, contact);
        notifyItemInserted(position);
    }

    public void filter(String filter) {
        if (filter == null) {
            return;
        }
        filteredList.clear();
        for (Contact contact : contactList) {
            if (containsStr(contact, filter)) {
                filteredList.add(contact);
            }
        }
        notifyDataSetChanged();
    }

    private boolean containsStr(Contact contact, String filter) {
        filter = filter.toLowerCase();
        return wrap(contact.getAddress()).contains(filter) ||
                wrap(contact.getEmail()).contains(filter) ||
                wrap(contact.getName()).contains(filter) ||
                wrap(contact.getNote()).contains(filter) ||
                wrap(contact.getProvider()).contains(filter) ||
                wrap(contact.getPhone()).contains(filter) ||
                wrap(contact.getPhone2()).contains(filter);
    }

    private String wrap(String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView name;
        private final TextView email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            name = itemView.findViewById(R.id.item_contact_holder_name);
            email = itemView.findViewById(R.id.item_contact_holder_mail);
        }

        public void update(Contact contact) {
            name.setText(contact.getName());
            email.setText(contact.getEmail());
            root.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditContactActivity.class);
                intent.putExtra(EditContactActivity.ARG_ID, contact.getId());
                context.startActivity(intent);
            });
        }
    }
}
