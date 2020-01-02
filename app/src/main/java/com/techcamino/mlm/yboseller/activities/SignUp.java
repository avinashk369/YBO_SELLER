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
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.ErrorConstants;
import com.techcamino.mlm.yboseller.util.Security;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private TextView login,accessMsg;
    private boolean checked = false;
    private EditText phoneNumber;
    private Context context = this;

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private UserDetails userDetails;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    String emailPattern_two = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean other = false;
    private CardView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        accessMsg = findViewById(R.id.verify);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        userDetails = new UserDetails();
    }

    @Override
    protected void onStart() {
        initUI();
        super.onStart();
    }

    private void initUI(){

        phoneNumber = findViewById(R.id.user_phone);
        login = findViewById(R.id.login);
        loginBtn = findViewById(R.id.buttonContinue);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateField()) {
                    return;
                }
                userDetails.setEmail(phoneNumber.getText().toString().trim());
                saveUser(phoneNumber.getText().toString().trim(),
                        Security.getPreference(Constants.FCM_TOKEN,context),
                        Security.getPreference(Constants.USER_LAT,context),
                        Security.getPreference(Constants.USER_LNG,context));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,LoginActivity.class));
            }
        });

    }

    public void saveUser(String mobile,String fcmToken,String lat, String lng){
        dialog.show();
        Call<UserDetails> call = apiService.userRegistration(mobile, fcmToken, lat, lng);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    accessMsg.setVisibility(View.GONE);
                    Intent intent = new Intent(SignUp.this, AccountVerification.class);
                    intent.putExtra(Constants.USER_DETAIL,userDetails);
                    intent.putExtra(Constants.MOBILE,phoneNumber.getText().toString().trim());
                    startActivity(intent);

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

    /*public void userDetail(String userId){
        dialog.show();
        Call<UserDetails> call = apiService.getUserDetail(userId);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    UserDetails userDetails1 = response.body();
                    switch (userDetails1.getUserAccess()){
                        case 1:
                        case 3:
                            Intent vmDashBoard = new Intent(SignUp.this,VMDashboard.class);
                            vmDashBoard.putExtra(Constants.USER,userDetails1);
                            startActivity(vmDashBoard);
                            finish();
                            break;
                        case 2:
                        case 4:
                            Intent managerDashboard = new Intent(SignUp.this,ManagerDashboard.class);
                            managerDashboard.putExtra(Constants.USER,userDetails1);
                            startActivity(managerDashboard);
                            finish();
                            break;
                        default:
                            break;

                    }



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
    }*/



    private boolean validateField(){

        if(phoneNumber.getText().toString().isEmpty() || phoneNumber.getText().toString().length() < 10){
            phoneNumber.setError(ErrorConstants.LOGIN_ID_ERROR);
            phoneNumber.requestFocus();
            return false;
        }

        return true;
    }

}
