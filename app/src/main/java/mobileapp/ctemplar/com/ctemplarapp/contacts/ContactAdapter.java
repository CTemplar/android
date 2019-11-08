package mobileapp.ctemplar.com.ctemplarapp.contacts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<Contact> contactsList;
    private List<Contact> filteredList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();

    ContactAdapter(List<Contact> contactsList) {
        this.contactsList = contactsList;
        filteredList = new ArrayList<>();
        filteredList.addAll(contactsList);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact_holder, viewGroup, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        final Contact contacts = filteredList.get(position);
        holder.txtName.setText(contacts.getName());
        holder.txtMail.setText(contacts.getEmail());
        holder.root.setOnClickListener(v -> onClickSubject.onNext(contacts.getId()));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    Contact removeAt(int position) {
        Contact removedContact = filteredList.remove(position);
        contactsList.remove(removedContact);
        notifyItemRemoved(position);
        return removedContact;
    }

    void restoreItem(Contact contact, int position) {
        if (contactsList.contains(contact)) {
            return;
        }
        contactsList.add(position, contact);
        filteredList.add(position, contact);
        notifyItemInserted(position);
    }

    PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }

    public void filter(String filter) {
        filteredList.clear();
        for (Contact contact : contactsList) {
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
}