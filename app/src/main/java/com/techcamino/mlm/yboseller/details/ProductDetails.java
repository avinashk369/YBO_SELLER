package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class ProductDetails implements Serializable {

    @SerializedName("Data")
    private @Getter@Setter
    ArrayList<ProductDetails> productDetails;
    @SerializedName("categoryId")
    private @Getter@Setter String categoryId;
    @SerializedName("categoryName")
    private @Getter@Setter String categoryName;
    @SerializedName("sellerId")
    private @Getter@Setter String sellerId;
    @SerializedName("productName")
    private @Getter@Setter String productName;
    @SerializedName("price")
    private @Getter@Setter String price;
    @SerializedName("productImage")
    private @Getter@Setter String productImage;
    @SerializedName("quantity")
    private @Getter@Setter String quantity;
    @SerializedName("active")
    private @Getter@Setter String active;
    @SerializedName("createdBy")
    private @Getter@Setter String createdBy;
    @SerializedName("createdOn")
    private @Getter@Setter String createdOn;
    @SerializedName("updatedBy")
    private @Getter@Setter String updatedBy;
    @SerializedName("updatedOn")
    private @Getter@Setter String updatedOn;
    @SerializedName("deletedBy")
    private @Getter@Setter String deletedBy;
    @SerializedName("deletedOn")
    private @Getter@Setter String deletedOn;

}
