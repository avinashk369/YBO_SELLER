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

public class ManageShopActivity extends AppCompatActivity {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private UserDetails userDetails;
    private TextView shopName,shopAddress,landmark,state,city,pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.SHOP);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        shopName = findViewById(R.id.shop_name);
        shopAddress = findViewById(R.id.shop_address);
        landmark = findViewById(R.id.landmark);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        pincode = findViewById(R.id.pincode);

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
        shopName.setText(userDetails.getShopName());
        shopAddress.setText(userDetails.getAddress());
        landmark.setText(userDetails.getLandmark());
        state.setText(userDetails.getStateName());
        city.setText(userDetails.getCityName());
        pincode.setText(userDetails.getPinCode());

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
                Intent profile = new Intent(ManageShopActivity.this,EditShopDetail.class);
                profile.putExtra(Constants.USER_DETAIL,userDetails);
                startActivity(profile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
