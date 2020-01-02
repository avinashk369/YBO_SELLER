package com.techcamino.mlm.yboseller.details;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class ImageDetails implements Serializable {

    @SerializedName("id")
    private @Getter@Setter String id;
    @SerializedName("url")
    private @Getter@Setter int url;

    public ImageDetails(int url) {
        this.url = url;
    }
    public ImageDetails() {
    }

    private @Getter@Setter
    Uri imageUri;
}
