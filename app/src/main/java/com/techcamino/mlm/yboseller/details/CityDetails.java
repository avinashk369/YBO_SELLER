package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class CityDetails implements Serializable {

    @SerializedName("CityName")
    private @Getter@Setter String name;
    @SerializedName("CityId")
    private @Getter@Setter String id;

    @SerializedName("Data")
    private @Getter@Setter
    ArrayList<CityDetails> cityDetailsArrayList;

}
