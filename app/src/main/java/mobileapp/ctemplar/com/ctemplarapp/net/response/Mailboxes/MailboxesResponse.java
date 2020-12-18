package mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MailboxesResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("page_count")
    private int pageCount;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<MailboxesResult> mailboxesList;


    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<MailboxesResult> getMailboxesList() {
        return mailboxesList;
    }
}
