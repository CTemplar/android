package mobileapp.ctemplar.com.ctemplarapp.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageAttachmentAdapter extends RecyclerView.Adapter<MessageAttachmentHolder> {

    final private static String PDF = "PDF";
    final private static String DOC = "DOC";
    final private static String PNG = "PNG";
    final private static String JPG = "JPG";

    private List<MessageAttachment> attachmentsList;
    private final PublishSubject<String> onClickAttachmentLink = PublishSubject.create();

    public void setAttachmentsList(List<MessageAttachment> attachmentsList) {
        this.attachmentsList = attachmentsList;
    }

    @NonNull
    @Override
    public MessageAttachmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_attachment, viewGroup, false);

        return new MessageAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAttachmentHolder holder, int position) {
        final MessageAttachment messageAttachment = attachmentsList.get(position);
        final String documentLink = messageAttachment.getDocumentLink();
        final String fileName = AppUtils.getFileNameFromURL(documentLink);

        holder.txtName.setText(fileName);
        String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();

        if (fileExt.equals(PDF)) {
            holder.imgExt.setImageResource(R.drawable.ic_pdf);
        } else if (fileExt.equals(DOC)) {
            holder.imgExt.setImageResource(R.drawable.ic_doc);
        } else if (fileExt.equals(PNG)) {
            holder.imgExt.setImageResource(R.drawable.ic_png);
        } else if (fileExt.equals(JPG)) {
            holder.imgExt.setImageResource(R.drawable.ic_jpg);
        } else {
            holder.imgExt.setImageResource(R.drawable.ic_other);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAttachmentLink.onNext(documentLink);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentsList.size();
    }

    public PublishSubject<String> getOnClickAttachmentLink() {
        return onClickAttachmentLink;
    }
}