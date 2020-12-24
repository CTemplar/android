package mobileapp.ctemplar.com.ctemplarapp.net.response;

import com.google.gson.annotations.SerializedName;

public class CaptchaResponse {
    @SerializedName("captcha_key")
    private String captchaKey;

    @SerializedName("captcha_image")
    private String captchaImageUrl;


    public String getCaptchaKey() {
        return captchaKey;
    }

    public String getCaptchaImageUrl() {
        return captchaImageUrl;
    }
}
