package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageSendAttachmentAdapter extends RecyclerView.Adapter<MessageSendAttachmentHolder> {

    private List<MessageAttachment> attachmentList = new ArrayList<>();
    private SendMessageActivityViewModel sendMessageActivityViewModel;

    public MessageSendAttachmentAdapter(FragmentActivity fragmentActivity) {
        sendMessageActivityViewModel = ViewModelProviders.of(fragmentActivity).get(SendMessageActivityViewModel.class);
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
