package mobileapp.ctemplar.com.ctemplarapp.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageSendAttachmentAdapter extends RecyclerView.Adapter<MessageSendAttachmentHolder> {
    private final SendMessageActivityViewModel sendMessageActivityViewModel;
    private final List<AttachmentProvider> attachmentList = new ArrayList<>();

    public MessageSendAttachmentAdapter(FragmentActivity fragmentActivity) {
        sendMessageActivityViewModel = new ViewModelProvider(fragmentActivity)
                .get(SendMessageActivityViewModel.class);
    }

    public List<AttachmentProvider> getAttachmentList() {
        return attachmentList;
    }

    @NonNull
    @Override
    public MessageSendAttachmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message_attachment_send, viewGroup, false);

        return new MessageSendAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageSendAttachmentHolder holder, int position) {
        final AttachmentProvider messageAttachment = attachmentList.get(position);
        final String documentUrl = messageAttachment.getDocumentUrl();
        final String fileName = messageAttachment.getName() == null
                ? AppUtils.getFileNameFromURL(documentUrl) : messageAttachment.getName();

        holder.txtName.setText(fileName);
        holder.imgDelete.setOnClickListener(v -> deleteAttachment(messageAttachment));
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    public void setAttachments(List<AttachmentProvider> attachments) {
        attachmentList.addAll(attachments);
        notifyDataSetChanged();
    }

    public void addAttachment(AttachmentProvider attachment) {
        attachmentList.add(attachment);
        notifyItemInserted(attachmentList.size() - 1);
    }

    public void deleteAttachment(AttachmentProvider attachment) {
        sendMessageActivityViewModel.deleteAttachment(attachment.getId());
        int attachmentPosition = attachmentList.indexOf(attachment);
        if (attachmentPosition != -1) {
            attachmentList.remove(attachmentPosition);
            notifyItemRemoved(attachmentPosition);
        }
    }
}
