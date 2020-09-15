package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

public class DisableLoadingImagesRequest {
    @SerializedName("is_disable_loading_images")
    private boolean isDisableLoadingImages;

    public DisableLoadingImagesRequest(boolean isDisableLoadingImages) {
        this.isDisableLoadingImages = isDisableLoadingImages;
    }

    public void setDisableLoadingImages(boolean disableLoadingImages) {
        isDisableLoadingImages = disableLoadingImages;
    }
}
