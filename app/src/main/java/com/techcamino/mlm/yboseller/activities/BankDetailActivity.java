package com.techcamino.mlm.yboseller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class BankDetailActivity extends AppCompatActivity {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private UserDetails userDetails;
    private TextView bankName,accountNumber,accountName,ifscCode,pan,bankName1,accountNumber1,accountName1,ifscCode1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.BANK_DETAIL);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        bankName = findViewById(R.id.bank_name);
        bankName1 = findViewById(R.id.bank_name_1);
        accountNumber = findViewById(R.id.account_number);
        accountNumber1 = findViewById(R.id.account_number_1);
        accountName= findViewById(R.id.account_name);
        accountName1= findViewById(R.id.account_name_1);
        ifscCode =findViewById(R.id.ifsc_code);
        ifscCode1 =findViewById(R.id.ifsc_code_1);
        pan = findViewById(R.id.pan_number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);

        userDetail(String.valueOf(userDetails.getSellerId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    private void renderUI(UserDetails userDetails){
        //bankName,accountNumber,accountName,ifscCode,pan;
        bankName.setText(userDetails.getCoreBank());
        bankName1.setText(userDetails.getCoreBank());
        accountNumber.setText(userDetails.getAccountNumber());
        accountNumber1.setText(userDetails.getAccountNumber());
        accountName.setText(userDetails.getNameOnAccount());
        accountName1.setText(userDetails.getNameOnAccount());
        ifscCode.setText(userDetails.getIFSCCode());
        ifscCode1.setText(userDetails.getIFSCCode());
        pan.setText(userDetails.getPan());

    }
    public void userDetail(String userId){
        dialog.show();
        Call<UserDetails> call = apiService.getUserDetail(userId);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    userDetails = response.body();
                    renderUI(userDetails);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                Intent profile = new Intent(BankDetailActivity.this,EditBankDetails.class);
                profile.putExtra(Constants.USER_DETAIL,userDetails);
                startActivity(profile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
