package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {

    private Context context = this;
    private APIInterFace apiService;
    private ProgressDialog dialog;
    private RelativeLayout top;
    private String [] gradStart = {
            "#141E30",
            "#536976",
            "#16222a",
            "#485563",
            "#304352",
            "#283048"

    };
    private String [] gradEnd = {
            "#243B55",
            "#292e49",
            "#3a6073",
            "#29323C",
            "#d7d2cc",
            "#859398"
    };
    private UserDetails userDetails;
    private TextView maritalStatus,email,gender,name,mobile;
    private CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#00000000"));
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setVisibility(View.GONE);
        title.setText(Constants.PROFILE);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        userDetails = new UserDetails();

        top = findViewById(R.id.top);
        maritalStatus = findViewById(R.id.seller_marital_status);
        email = findViewById(R.id.seller_email);
        gender = findViewById(R.id.seller_gender);
        name = findViewById(R.id.seller_name);
        mobile = findViewById(R.id.seller_mobile);
        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent userImage = new Intent(ProfileActivity.this,UploadImage.class);
                userImage.putExtra(Constants.IMAGE_TYPE,Constants.SHOP_LOGO_IMAGE);
                userImage.putExtra(Constants.USER_DETAIL,userDetails);
                startActivity(userImage);
                finish();
            }
        });

        try{
            int[] colors = {Color.parseColor("#ffe259"),
                    Color.parseColor("#ffa751")};
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,colors);
            gradientDrawable.setCornerRadius(0f);
            top.setBackground(gradientDrawable);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails)getIntent().getSerializableExtra(Constants.USER_DETAIL);
        if(getIntent().hasExtra(Constants.USER_IMAGE))
            Glide.with(context).load(getIntent().getStringExtra(Constants.USER_IMAGE)).into(profileImage);
        else
            Glide.with(context).load(userDetails.getShopLogo()).into(profileImage);

        userDetail(String.valueOf(userDetails.getSellerId()));
    }

    private void renderUI(UserDetails userDetails){
        //maritalStatus,email,gender
        maritalStatus.setText((userDetails.getMartialStatus().equals("1") ? "Married" : "Unmarried"));
        email.setText(userDetails.getEmail());
        name.setText(userDetails.getSellerName());
        mobile.setText(userDetails.getMobile());
        gender.setText((userDetails.getGender().equals("1") ? "Male" : "Female"));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
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
                Intent profile = new Intent(ProfileActivity.this,EditProfile.class);
                profile.putExtra(Constants.USER_DETAIL,userDetails);
                startActivity(profile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
