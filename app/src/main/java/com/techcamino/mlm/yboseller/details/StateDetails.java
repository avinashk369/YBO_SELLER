package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class StateDetails implements Serializable {

    @SerializedName("StateName")
    private @Getter@Setter String name;
    @SerializedName("StateId")
    private @Getter@Setter String id;

    @SerializedName("Data")
    private @Getter@Setter
    ArrayList<StateDetails> stateDetailsArrayList;

}
