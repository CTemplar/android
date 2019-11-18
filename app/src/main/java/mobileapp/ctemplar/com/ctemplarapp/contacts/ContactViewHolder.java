package mobileapp.ctemplar.com.ctemplarapp.contacts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    public View root;
    public TextView txtName;
    public TextView txtMail;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView;
        txtName = itemView.findViewById(R.id.item_contact_holder_name);
        txtMail = itemView.findViewById(R.id.item_contact_holder_mail);
    }
}
