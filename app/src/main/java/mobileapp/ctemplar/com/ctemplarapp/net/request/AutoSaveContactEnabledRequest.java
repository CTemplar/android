package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class AutoSaveContactEnabledRequest {
    @SerializedName("save_contacts")
    private boolean saveContacts;

    public AutoSaveContactEnabledRequest(boolean saveContacts) {
        this.saveContacts = saveContacts;
    }
}
