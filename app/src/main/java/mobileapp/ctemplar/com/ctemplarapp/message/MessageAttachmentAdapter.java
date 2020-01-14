package mobileapp.ctemplar.com.ctemplarapp.message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageAttachmentAdapter extends RecyclerView.Adapter<MessageAttachmentHolder> {

    private static final String PDF = "PDF";
    private static final String DOC = "DOC";
    private static final String PNG = "PNG";
    private static final String JPG = "JPG";
    private static final String JPEG = "JPEG";

    private List<AttachmentProvider> attachmentList;
    private final PublishSubject<Integer> onClickAttachmentLink = PublishSubject.create();

    public MessageAttachmentAdapter() {

    }

    public MessageAttachmentAdapter(List<AttachmentProvider> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public void setAttachmentList(List<AttachmentProvider> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public AttachmentProvider getAttachment(int position) {
        return attachmentList.get(position);
    }

    public PublishSubject<Integer> getOnClickAttachmentLink() {
        return onClickAttachmentLink;
    }

    @NonNull
    @Override
    public MessageAttachmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_attachment, viewGroup, false);
        return new MessageAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAttachmentHolder holder, int position) {
        final AttachmentProvider messageAttachment = attachmentList.get(position);
        final String documentLink = messageAttachment.getDocumentLink();
        final String fileName = AppUtils.getFileNameFromURL(documentLink);
        final int attachmentPosition = position;

        holder.txtName.setText(fileName);
        String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();

        switch (fileExt) {
            case PDF:
                holder.imgExt.setImageResource(R.drawable.ic_pdf);
                break;
            case DOC:
                holder.imgExt.setImageResource(R.drawable.ic_doc);
                break;
            case PNG:
                holder.imgExt.setImageResource(R.drawable.ic_png);
                break;
            case JPG:
            case JPEG:
                holder.imgExt.setImageResource(R.drawable.ic_jpg);
                break;
            default:
                holder.imgExt.setImageResource(R.drawable.ic_other);
                break;
        }

        holder.root.setOnClickListener(v -> onClickAttachmentLink.onNext(attachmentPosition));
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }
}
