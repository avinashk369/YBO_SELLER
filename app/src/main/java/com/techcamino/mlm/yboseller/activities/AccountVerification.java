package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountVerification extends AppCompatActivity {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private String otp_code,number;
    private TextView resend;
    private boolean message;
    private TextView accessMsg;

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText otp;
    private int registraion;
    private boolean forgot;

    public static final String TAG = AccountVerification.class.getSimpleName();
    private UserDetails userDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        forgot = getIntent().getBooleanExtra(Constants.FORGOT,false);
        if(getIntent().hasExtra(Constants.USER_DETAIL)) {
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);
            number = userDetails.getMobile();
        }
        if(getIntent().hasExtra(Constants.MOBILE))
            number = getIntent().getStringExtra(Constants.MOBILE);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);

        resend = findViewById(R.id.resend);
        accessMsg = findViewById(R.id.verify);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(forgot){
                    resendOtp(number,true);
                }else {
                    resendOtp(number,false);
                }
            }
        });

        otp = findViewById(R.id.otp);

        userDetails = new UserDetails();

        apiService =
                APIClient.getClient().create(APIInterFace.class);



        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(forgot){
                    verifyForgotPwd(number,otp.getText().toString().trim(),true);
                }else {
                    verifyForgotPwd(number,otp.getText().toString().trim(),false);
                }
            }
        });
    }

    public void resendOtp(String number,boolean forgotPassword){
        dialog.show();
        Call<UserDetails> call;
        if(forgotPassword){
            call = apiService.resendForgotOtp(number);
        }else{
            call = apiService.resendOtp(number);
        }

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    accessMsg.setVisibility(View.GONE);

                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                        accessMsg.setText(messageDetails.getMessage());
                        accessMsg.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    /*public void verifyUser(String number, String otp){
        dialog.show();
        Call<UserDetails> call = apiService.verifyUser(number,otp);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    accessMsg.setVisibility(View.GONE);
                    Log.d("RESPONSE", response.body().toString());
                    Intent newReg = new Intent(AccountVerification.this, Dashboard.class);
                    newReg.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(newReg);
                    finish();

                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                        accessMsg.setText(messageDetails.getMessage());
                        accessMsg.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }*/

    public void verifyForgotPwd(String number, String otp, boolean forgotPassword){
        dialog.show();
        Call<UserDetails> call;
        if(forgotPassword){
            call = apiService.verifyForgotOTP(number,otp);
        }else{
            call = apiService.verifyUser(number,otp);
        }
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    accessMsg.setVisibility(View.GONE);
                    Log.d("RESPONSE", response.body().toString());
                    userDetail(String.valueOf(userDetails.getSellerId()));

                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                        accessMsg.setText(messageDetails.getMessage());
                        accessMsg.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }


    public void userDetail(String userId){
        dialog.show();
        Call<UserDetails> call = apiService.getUserDetail(userId);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    Intent newReg = new Intent(AccountVerification.this, Dashboard.class);
                    newReg.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(newReg);
                    finish();

                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }


    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
}
