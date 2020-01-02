package com.techcamino.mlm.yboseller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.CategorySpinnerAdpter;
import com.techcamino.mlm.yboseller.adapter.StateAdpter;
import com.techcamino.mlm.yboseller.details.CategoryDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.SpinnerDetails;
import com.techcamino.mlm.yboseller.details.StateDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.ErrorConstants;
import com.techcamino.mlm.yboseller.util.Security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBankDetails extends AppCompatActivity implements View.OnClickListener {

    private EditText bankName,accountNumber,accountName,ifscCode,pan;
    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private ScrollView scrollView;
    private UserDetails userDetails;
    private TextView accessMsg;
    private CardView buttonSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.BANK_DETAIL);
        getSupportActionBar().setTitle(Constants.PROFILE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        userDetails = new UserDetails();
        //bankName,accountNumber,accountName,ifscCode,pan

        accessMsg = findViewById(R.id.verify);
        bankName = findViewById(R.id.bank_name);
        accountNumber = findViewById(R.id.account_number);
        accountName = findViewById(R.id.account_name);
        ifscCode = findViewById(R.id.ifsc_code);
        pan = findViewById(R.id.pan_number);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        scrollView = findViewById(R.id.bank_detail_scroll);
        buttonSubmit.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);


        renderUI(userDetails);
    }

    private void renderUI(UserDetails userDetails){
        bankName.setText(userDetails.getCoreBank());
        accountName.setText(userDetails.getNameOnAccount());
        accountNumber.setText(userDetails.getAccountNumber());
        ifscCode.setText(userDetails.getIFSCCode());
        pan.setText(userDetails.getPan());
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.buttonSubmit:
//String sellerId, String loginId,String bankName,String accountName,String accountNumber,
// String ifscCode, String pan,String updatedBy,String updatedOn
                saveBankDetail(
                        String.valueOf(userDetails.getSellerId()),
                        userDetails.getLoginID(),
                        userDetails.getMobile(),
                        bankName.getText().toString().trim(),
                        accountName.getText().toString(),
                        accountNumber.getText().toString().trim(),
                        ifscCode.getText().toString().trim(),
                        pan.getText().toString().trim(),
                        userDetails.getSellerName(),
                        new SimpleDateFormat("yyyyMMddHH").format(new Date())
                );
                break;
            default:
                break;
        }
    }

    public void saveBankDetail(String sellerId, String loginId,String mobile,String bankName,String accountName,String accountNumber, String ifscCode, String pan,String updatedBy,String updatedOn){
        dialog.show();

        Call<UserDetails> call = apiService.editUserBankProfile(sellerId,loginId,mobile,bankName,pan,accountNumber,accountName,ifscCode,updatedBy,updatedOn);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {

                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body();
                    accessMsg.setText(Constants.SUCCESS);
                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                        accessMsg.setText(messageDetails.getMessage());
                        accessMsg.setTextColor(context.getResources().getColor(R.color.red));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(dialog.isShowing())
                    dialog.dismiss();
                accessMsg.setVisibility(View.VISIBLE);
                Security.scrollToView(scrollView, accessMsg);

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
