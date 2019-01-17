package mobileapp.ctemplar.com.ctemplarapp.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageSendAttachmentAdapter extends RecyclerView.Adapter<MessageSendAttachmentHolder> {
    private List<MessageAttachment> attachmentsList = new ArrayList<>();

    public List<MessageAttachment> getAttachmentsList() {
        return  attachmentsList;
    }

    @NonNull
    @Override
    public MessageSendAttachmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_attachment_send, viewGroup, false);

        return new MessageSendAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageSendAttachmentHolder holder, int position) {
        final MessageAttachment messageAttachment = attachmentsList.get(position);
        final String documentLink = messageAttachment.getDocumentLink();
        final String fileName = AppUtils.getFileNameFromURL(documentLink);

        holder.txtName.setText(fileName);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAttachment(messageAttachment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentsList.size();
    }

    public void addAttachment(MessageAttachment messageAttachment) {
        attachmentsList.add(messageAttachment);
        notifyItemInserted(attachmentsList.size() - 1);
    }

    public void deleteAttachment(MessageAttachment messageAttachment) {
        attachmentsList.remove(messageAttachment);
        int attachmentPosition = attachmentsList.indexOf(messageAttachment);
        if (attachmentPosition != -1) {
            notifyItemRemoved(attachmentPosition);
        }
    }
}
