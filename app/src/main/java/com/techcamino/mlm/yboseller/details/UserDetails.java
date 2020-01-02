package com.techcamino.mlm.yboseller.details;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class UserDetails implements Serializable {

    @SerializedName("Data")
    private @Getter@Setter UserDetails userDetails;
    @SerializedName("mobile")
    private @Getter@Setter String mobile;
    @SerializedName("emailID")
    private @Getter@Setter String email;
    @SerializedName("sellerId")
    private @Getter@Setter int sellerId;
    @SerializedName("password")
    private @Getter@Setter String password;
    @SerializedName("sellerName")
    private @Getter@Setter String sellerName;
    @SerializedName("loginID")
    private @Getter@Setter String loginID;
    @SerializedName("dateOfBirth")
    private @Getter@Setter String dateOfBirth;
    @SerializedName("gender")
    private @Getter@Setter String gender;
    @SerializedName("martialStatus")
    private @Getter@Setter String martialStatus;
    @SerializedName("aadhar")
    private @Getter@Setter String aadhar;
    @SerializedName("pan")
    private @Getter@Setter String pan;
    @SerializedName("phone")
    private @Getter@Setter String phone;
    @SerializedName("fax")
    private @Getter@Setter String fax;
    @SerializedName("coreBank")
    private @Getter@Setter String coreBank;
    @SerializedName("accountNumber")
    private @Getter@Setter String accountNumber;
    @SerializedName("nameOnAccount")
    private @Getter@Setter String nameOnAccount;
    @SerializedName("IFSCCode")
    private @Getter@Setter String IFSCCode;
    @SerializedName("shopName")
    private @Getter@Setter String shopName;
    @SerializedName("shopImage")
    private @Getter@Setter String shopImage;
    @SerializedName("shopLogo")
    private @Getter@Setter String shopLogo;
    @SerializedName("latitude")
    private @Getter@Setter String latitude;
    @SerializedName("longitude")
    private @Getter@Setter String longitude;
    @SerializedName("address")
    private @Getter@Setter String address;
    @SerializedName("landmark")
    private @Getter@Setter String landmark;
    @SerializedName("cityId")
    private @Getter@Setter String cityId;
    @SerializedName("cityName")
    private @Getter@Setter String cityName;
    @SerializedName("stateId")
    private @Getter@Setter String stateId;
    @SerializedName("stateName")
    private @Getter@Setter String stateName;
    @SerializedName("pinCode")
    private @Getter@Setter String pinCode;
    @SerializedName("registrationDate")
    private @Getter@Setter String registrationDate;
    @SerializedName("OTPCreationDate")
    private @Getter@Setter String OTPCreationDate;
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
