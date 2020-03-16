package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class AntiPhishingPhraseRequest {

    @SerializedName("is_anti_phishing_enabled")
    private boolean antiPhishingEnabled;

    @SerializedName("anti_phishing_phrase")
    private String antiPhishingPhrase;

    public AntiPhishingPhraseRequest(boolean antiPhishingEnabled, String antiPhishingPhrase) {
        this.antiPhishingEnabled = antiPhishingEnabled;
        this.antiPhishingPhrase = antiPhishingPhrase;
    }

    public void setAntiPhishingEnabled(boolean antiPhishingEnabled) {
        this.antiPhishingEnabled = antiPhishingEnabled;
    }

    public void setAntiPhishingPhrase(String antiPhishingPhrase) {
        this.antiPhishingPhrase = antiPhishingPhrase;
    }
}
