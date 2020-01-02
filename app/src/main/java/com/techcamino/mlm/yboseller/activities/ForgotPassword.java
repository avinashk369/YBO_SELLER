package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.techcamino.mlm.yboseller.util.Security;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private Context context = this;
    private TextView accessMsg;
    private APIInterFace apiService;
    private ProgressDialog dialog;
    private UserDetails userDetails;
    private CardView forgotBtn;
    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        accessMsg = findViewById(R.id.verify);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        userDetails = new UserDetails();

        phoneNumber = findViewById(R.id.user_phone);
        forgotBtn = findViewById(R.id.buttonContinue);
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword(phoneNumber.getText().toString());
            }
        });


    }


    public void forgotPassword(final String mobile){
        dialog.show();
        Call<UserDetails> call = apiService.forgotPassword(mobile);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    accessMsg.setVisibility(View.GONE);
                    Intent intent = new Intent(ForgotPassword.this, AccountVerification.class);
                    intent.putExtra(Constants.MOBILE,mobile);
                    intent.putExtra(Constants.FORGOT,true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
