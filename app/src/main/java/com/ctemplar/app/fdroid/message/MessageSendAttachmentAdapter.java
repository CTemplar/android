package com.ctemplar.app.fdroid.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.net.response.Messages.MessageAttachment;
import com.ctemplar.app.fdroid.utils.AppUtils;

public class MessageSendAttachmentAdapter extends RecyclerView.Adapter<MessageSendAttachmentHolder> {

    private List<MessageAttachment> attachmentList = new ArrayList<>();
    private SendMessageActivityViewModel sendMessageActivityViewModel;

    public MessageSendAttachmentAdapter(FragmentActivity fragmentActivity) {
        sendMessageActivityViewModel = new ViewModelProvider(fragmentActivity).get(SendMessageActivityViewModel.class);
    }

    public List<MessageAttachment> getAttachmentList() {
        return attachmentList;
    }

    @NonNull
    @Override
    public MessageSendAttachmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_attachment_send, viewGroup, false);

        return new MessageSendAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageSendAttachmentHolder holder, int position) {
        final MessageAttachment messageAttachment = attachmentList.get(position);
        final String documentLink = messageAttachment.getDocumentLink();
        final String fileName = AppUtils.getFileNameFromURL(documentLink);

        holder.txtName.setText(fileName);
        holder.imgDelete.setOnClickListener(v -> deleteAttachment(messageAttachment));
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    public void addAttachment(MessageAttachment messageAttachment) {
        attachmentList.add(messageAttachment);
        notifyItemInserted(attachmentList.size() - 1);
    }

    public void deleteAttachment(MessageAttachment messageAttachment) {
        sendMessageActivityViewModel.deleteAttachment(messageAttachment.getId());
        int attachmentPosition = attachmentList.indexOf(messageAttachment);
        if (attachmentPosition != -1) {
            attachmentList.remove(attachmentPosition);
            notifyItemRemoved(attachmentPosition);
        }
    }
}
