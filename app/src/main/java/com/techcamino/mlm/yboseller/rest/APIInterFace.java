package com.techcamino.mlm.yboseller.rest;



import com.techcamino.mlm.yboseller.details.CategoryDetails;
import com.techcamino.mlm.yboseller.details.CityDetails;
import com.techcamino.mlm.yboseller.details.DashboardDetails;
import com.techcamino.mlm.yboseller.details.ProductDetails;
import com.techcamino.mlm.yboseller.details.StateDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Thinkpad on 12-10-2016.
 */

public interface APIInterFace {


    @FormUrlEncoded
    @POST("Authentication/SellerRegistration")
    Call<UserDetails> userRegistration(@Field("Mobile") String mobile,
                                       @Field("FCMToken") String fcmToken,
                                       @Field("Latitude") String latiTude,
                                       @Field("Longitude") String longiTude);


    @POST("Authentication/verifyMobileSeller")
    Call<UserDetails> verifyUser(@Query("Mobile") String mobile,
                                 @Query("OtpCode") String otpCode);

    @POST("Authentication/forgotPasswordSeller")
    Call<UserDetails> forgotPassword(@Query("Mobile") String mobile);

    @POST("Authentication/verifyForgetPasswordOTPSeller")
    Call<UserDetails> verifyForgotOTP(@Query("Mobile") String mobile,
                                      @Query("OtpCode") String otpCode);

    @POST("Authentication/ResendOTPSeller")
    Call<UserDetails> resendOtp(@Query("Mobile") String mobile);

    @POST("Authentication/ResendForgetOTPSeller")
    Call<UserDetails> resendForgotOtp(@Query("Mobile") String mobile);

    @GET("Authentication/CheckLoginSeller/{sUserName}/{sPassword}")
    Call<UserDetails> checkLogin(@Path("sUserName") String userName,
                                 @Path("sPassword") String password);

    @GET("Authentication/GetProductBySeller")
    Call<ArrayList<ProductDetails>> getProducts(@Query("sellerId") String sellerId);

    @GET("Authentication/GetCategory")
    Call<ArrayList<CategoryDetails>> getCategory();

    @GET("Authentication/GetSellerProfile")
    Call<UserDetails> getUserDetail(@Query("sellerId") String sellerId);

    @GET("Authentication/GetSellerDashboard")
    Call<DashboardDetails> getSellerDashboard(@Query("sellerId") String sellerId);

    @FormUrlEncoded
    @POST("Authentication/InsertProductDetail")
    Call<ProductDetails> saveProductDetail(@Field("categoryId") String categoryId,
                                           @Field("categoryName") String categoryName,
                                           @Field("sellerId") String sellerId,
                                           @Field("productName") String productName,
                                           @Field("price") String price,
                                           @Field("productImage") String productImage,
                                           @Field("quantity") String quantity,
                                           @Field("active") String active,
                                           @Field("createdBy") String createdBy,
                                           @Field("createdOn") String createdOn,
                                           @Field("updatedBy") String updatedBy,
                                           @Field("updatedOn") String updatedOn,
                                           @Field("deletedBy") String deletedBy,
                                           @Field("deletedOn") String deletedOn);

    @FormUrlEncoded
    @POST("Authentication/UpdateSellerProfile")
    Call<UserDetails> editUserProfile(@Field("sellerId") String sellerId,
                                      @Field("loginID") String loginID,
                                      @Field("sellerName") String sellerName,
                                      @Field("dateOfBirth") String dateOfBirth,
                                      @Field("gender") String gender,
                                      @Field("martialStatus") String martialStatus,
                                      @Field("aadhar") String aadhar,
                                      @Field("phone") String phone,
                                      @Field("mobile") String mobile,
                                      @Field("fax") String fax,
                                      @Field("emailID") String emailID,
                                      @Field("updatedBy") String updatedBy,
                                      @Field("updatedOn") String updatedOn);

    @FormUrlEncoded
    @POST("Authentication/UpdateSellerProfile")
    Call<UserDetails> editUserBankProfile(@Field("sellerId") String sellerId,
                                          @Field("loginID") String loginID,
                                          @Field("mobile") String mobile,
                                          @Field("coreBank") String coreBank,
                                          @Field("pan") String pan,
                                          @Field("accountNumber") String accountNumber,
                                          @Field("nameOnAccount") String nameOnAccount,
                                          @Field("IFSCCode") String IFSCCode,
                                          @Field("updatedBy") String updatedBy,
                                          @Field("updatedOn") String updatedOn);

    @FormUrlEncoded
    @POST("Authentication/UpdateSellerProfile")
    Call<UserDetails> editUserBusinessProfile(@Field("sellerId") String sellerId,
                                                 @Field("loginID") String loginID,
                                                 @Field("shopName") String shopName,
                                                 @Field("latitude") String latitude,
                                                 @Field("longitude") String longitude,
                                                 @Field("address") String address,
                                                 @Field("landmark") String landmark,
                                                 @Field("cityId") String cityId,
                                                 @Field("cityName") String cityName,
                                                 @Field("stateId") String stateId,
                                                 @Field("stateName") String stateName,
                                                 @Field("pinCode") String pinCode,
                                                 @Field("mobile") String mobile,
                                                 @Field("registrationDate") String registrationDate,
                                                 @Field("updatedBy") String updatedBy,
                                                 @Field("updatedOn") String updatedOn);

    @POST("Authentication/MediaUpload")
    Call<ResponseBody> uploadImage(@Body RequestBody file);

    @GET("Authentication/GetState/{cId}")
    Call<StateDetails> getState(@Path("cId") String cId);

    @GET("Authentication/GetCity/{sid}")
    Call<CityDetails> getCity(@Path("sid") String sid);

}
