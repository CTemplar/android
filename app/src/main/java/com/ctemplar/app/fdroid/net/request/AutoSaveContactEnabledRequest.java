package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

public class AutoSaveContactEnabledRequest {
    @SerializedName("save_contacts")
    private boolean saveContacts;

    public AutoSaveContactEnabledRequest(boolean saveContacts) {
        this.saveContacts = saveContacts;
    }
}
