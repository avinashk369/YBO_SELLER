package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class FilterDetails implements Serializable {

    @SerializedName("Data")
    private @Getter@Setter
    ArrayList<FilterDetails> filterDetails;
    @SerializedName("Name")
    private @Getter@Setter String gridName;
    @SerializedName("Value")
    private @Getter@Setter String gridValue;
    @SerializedName("image")
    private @Getter@Setter int gridImage;


    @SerializedName("url")
    private @Getter@Setter String iconUrl;


    @SerializedName("label")
    private @Getter@Setter boolean label = true;

    @SerializedName("tabColor")
    private @Getter@Setter
    HashMap<String,String> tabColor;


}
