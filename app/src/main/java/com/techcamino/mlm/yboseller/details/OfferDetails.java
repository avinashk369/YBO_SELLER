package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class OfferDetails implements Serializable {

    @SerializedName("name")
    private @Getter@Setter String name;
    @SerializedName("id")
    private @Getter@Setter int id;
    @SerializedName("description")
    private @Getter@Setter String description;
    @SerializedName("offer_value")
    private @Getter@Setter String offerValue;
    @SerializedName("region_id")
    private @Getter@Setter int regionId;
    @SerializedName("brand_id")
    private @Getter@Setter int brandId;
    @SerializedName("store_id")
    private @Getter@Setter int storeId;
    @SerializedName("created_on")
    private @Getter@Setter String createdOn;
    @SerializedName("flags")
    private @Getter@Setter String flags;

}
