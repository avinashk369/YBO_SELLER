package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class CategoryDetails implements Serializable {

    @SerializedName("Data")
    private @Getter@Setter
    ArrayList<CategoryDetails> categoryDetailsArrayList;

    @SerializedName("name")
    private @Getter@Setter String name;
    @SerializedName("categoryId")
    private @Getter@Setter String categoryId;
    @SerializedName("categoryName")
    private @Getter@Setter String categoryName;
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
    private @Getter@Setter boolean checked;


}
