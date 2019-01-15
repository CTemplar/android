package mobileapp.ctemplar.com.ctemplarapp.contact;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<Contact> contactsList;
    private final PublishSubject<Long> onClickSubject = PublishSubject.create();

    public ContactAdapter(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact_holder, viewGroup, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        final Contact contacts = contactsList.get(position);
        holder.txtName.setText(contacts.getName());
        holder.txtMail.setText(contacts.getEmail());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(contacts.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public Contact removeAt(int position) {
        Contact removedContact = contactsList.remove(position);
        notifyItemRemoved(position);
        return removedContact;
    }

    public void restoreItem(Contact contact, int position) {
        contactsList.add(position, contact);
        notifyItemInserted(position);
    }

    public PublishSubject<Long> getOnClickSubject() {
        return onClickSubject;
    }
}