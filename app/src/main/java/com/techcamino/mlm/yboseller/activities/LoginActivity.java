package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity {

    private CardView btnContinue;
    private String user;
    private String userPassword;
    private EditText userName,password;
    private TextView accessMsg,signUp,forgot;
    private ImageView passwordIcon;
    private boolean checked = false;
    private APIInterFace apiService;
    private ProgressDialog dialog;
    private UserDetails userDetail;
    private Context context = this;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    String emailPattern_two = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }
    private void initUI(){
        userName = findViewById(R.id.edit_user_id);
        password = findViewById(R.id.edittextpassword);
        accessMsg = findViewById(R.id.verify);
        signUp = findViewById(R.id.sign_up);
        passwordIcon = findViewById(R.id.password_img);
        forgot = findViewById(R.id.forgot);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
            }
        });
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checked) {
                    checked = true;
                    passwordIcon.setImageDrawable(getDrawable(R.drawable.ic_visibility_black_24dp));
                }
                else {
                    checked = false;
                    passwordIcon.setImageDrawable(getDrawable(R.drawable.ic_visibility_off_black_24dp));
                }

                showPasswordCheckBoxListener(passwordIcon,checked);
            }
        });



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUp.class));
                finish();
            }
        });

        userDetail = new UserDetails();
        btnContinue = findViewById(R.id.buttonContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!validateField()) {
                    return;
                }

                userDetail.setMobile(userName.getText().toString().trim());
                userDetail.setPassword(password.getText().toString().trim());

                userLogin(userDetail.getMobile(),userDetail.getPassword());
            }
        });
    }

    public void showPasswordCheckBoxListener(View view,boolean checked) {
        switch (view.getId()) {
            case R.id.password_img:
                if (!checked)
                    password.setTransformationMethod(new PasswordTransformationMethod());  //hide the password from the edit text
                else
                    password.setTransformationMethod(null); //show the password from the edit text
                break;

        }
    }

    @Override
    protected void onStart() {
        accessMsg.setVisibility(View.GONE);
        super.onStart();
    }

    private boolean validateField(){

        if(userName.getText().toString().isEmpty() || userName.getText().toString().length() < 5){
            userName.setError(ErrorConstants.MOBILE_ERROR);
            userName.requestFocus();
            return false;
        }

        if(password.getText().toString().trim().isEmpty() || password.getText().toString().trim().length() < 1 ){
            password.setError(ErrorConstants.PASSWORD_ERROR);
            password.requestFocus();
            return false;
        }

        /*if(password.getText().toString().isEmpty() || password.getText().toString().length() < Constants.PASSWORD_LENGTH){
            password.setError(ErrorConstants.PASSWORD_ERROR);
            password.requestFocus();
            return false;
        }*/


        return true;
    }

    public void userLogin(String userName, String password){
        dialog.show();
        Call<UserDetails> call = apiService.checkLogin(userName, password);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetail = response.body();
                    accessMsg.setVisibility(View.GONE);
                    Log.d("Avinash",userDetail.getSellerId()+"");
                    userDetail(String.valueOf(userDetail.getSellerId()));


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

    public void userDetail(String userId){
        dialog.show();
        Call<UserDetails> call = apiService.getUserDetail(userId);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetail = response.body();
                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    intent.putExtra(Constants.USER_DETAIL,userDetail);
                    startActivity(intent);
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
}
