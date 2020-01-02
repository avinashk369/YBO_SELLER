package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class DashboardDetails implements Serializable {

    @SerializedName("activeProduct")
    private @Getter@Setter
    int activeProduct;
    @SerializedName("deactiveProduct")
    private @Getter@Setter int deactiveProduct;
    @SerializedName("productInOffer")
    private @Getter@Setter int productInOffer;
    @SerializedName("productWithoutOffer")
    private @Getter@Setter int productWithoutOffer;
    @SerializedName("activeOffer")
    private @Getter@Setter int activeOffer;
}
