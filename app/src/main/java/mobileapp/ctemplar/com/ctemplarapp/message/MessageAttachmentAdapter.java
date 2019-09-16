package mobileapp.ctemplar.com.ctemplarapp.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.AttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class MessageAttachmentAdapter extends RecyclerView.Adapter<MessageAttachmentHolder> {

    final private static String PDF = "PDF";
    final private static String DOC = "DOC";
    final private static String PNG = "PNG";
    final private static String JPG = "JPG";
    final private static String JPEG = "JPEG";

    private List<AttachmentProvider> attachmentList;
    private final PublishSubject<Integer> onClickAttachmentLink = PublishSubject.create();

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

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAttachmentLink.onNext(attachmentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }
}
