package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class SpinnerDetails implements Serializable {

    @SerializedName("name")
    private @Getter@Setter String name;
    @SerializedName("id")
    private @Getter@Setter String id;

    public SpinnerDetails(String name,String id) {
        this.name = name;
        this.id = id;
    }
}
