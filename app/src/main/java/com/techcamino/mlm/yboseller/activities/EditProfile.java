package com.techcamino.mlm.yboseller.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.CategorySpinnerAdpter;
import com.techcamino.mlm.yboseller.adapter.SpinnerAdapter;
import com.techcamino.mlm.yboseller.details.CategoryDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.ProductDetails;
import com.techcamino.mlm.yboseller.details.SpinnerDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.ErrorConstants;
import com.techcamino.mlm.yboseller.util.Security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private ScrollView scrollView;
    private DatePickerDialog picker;
    private TextView dateOfBirth,accessMsg;
    private Spinner genderSpinner,maritialStatus;
    private ArrayList<SpinnerDetails> genderList;
    private ArrayList<SpinnerDetails> marititialList;
    private UserDetails userDetails;
    private String gender,maritalStatus;
    private CardView buttonSubmit;
    private EditText name,phone,email,adhar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    String emailPattern_two = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.PROFILE);
        getSupportActionBar().setTitle(Constants.PROFILE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        userDetails = new UserDetails();
        name = findViewById(R.id.seller_name);
        phone = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        adhar = findViewById(R.id.adahar_number);
        accessMsg = findViewById(R.id.verify);
        genderSpinner = findViewById(R.id.seller_gender);
        maritialStatus = findViewById(R.id.maritial_status);
        dateOfBirth = findViewById(R.id.seller_dob);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        scrollView = findViewById(R.id.profile_scroll);
        buttonSubmit.setOnClickListener(this);
        dateOfBirth.setOnClickListener(this);
        genderSpinner.setSelection(0,false);
        maritialStatus.setSelection(0,false);
        genderSpinner.setOnItemSelectedListener(this);
        maritialStatus.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);

        generateGender();
        generateMarititialStatus();

        renderUI(userDetails);
    }

    private void renderUI(UserDetails userDetails){
        name.setText(userDetails.getSellerName());
        phone.setText(userDetails.getPhone());
        email.setText(userDetails.getEmail());
        adhar.setText(userDetails.getAadhar());
        dateOfBirth.setText(userDetails.getDateOfBirth());
        for(SpinnerDetails gender : genderList){
            if(gender.getId().equalsIgnoreCase(userDetails.getGender()))
                genderSpinner.setSelection(genderList.indexOf(gender));
        }
        for(SpinnerDetails maritalStatus : marititialList){
            if(maritalStatus.getId().equalsIgnoreCase(userDetails.getMartialStatus()))
                maritialStatus.setSelection(marititialList.indexOf(maritalStatus));
        }

    }

    private void generateGender(){
        genderList = new ArrayList<SpinnerDetails>(){
            {
                add(new SpinnerDetails("Select gender","0"));
                add(new SpinnerDetails("Male","1"));
                add(new SpinnerDetails("Female","2"));
            }
        };
        SpinnerAdapter customAdapter=new SpinnerAdapter(context,genderList);
        genderSpinner.setAdapter(customAdapter);
    }

    private void generateMarititialStatus(){
        marititialList = new ArrayList<SpinnerDetails>(){
            {
                add(new SpinnerDetails("Select marital status ","0"));
                add(new SpinnerDetails("Married","1"));
                add(new SpinnerDetails("Unmarried","2"));
            }
        };
        SpinnerAdapter customAdapter=new SpinnerAdapter(context,marititialList);
        maritialStatus.setAdapter(customAdapter);
    }

    private void openCalendar(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        picker = new DatePickerDialog(EditProfile.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirth.setText((month + 1) + "-" + dayOfMonth + "-" + year);
            }
        },year,month,day);
        picker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        picker.show();
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
            case R.id.seller_dob:
                openCalendar();
                break;
            case R.id.buttonSubmit:
                if (!email.getText().toString().trim().matches(emailPattern) && !email.getText().toString().trim().matches(emailPattern_two)) {
                    email.setError(ErrorConstants.EMAIL_ERROR);
                    email.requestFocus();
                    return ;
                }
                saveProfile(
                        String.valueOf(userDetails.getSellerId()),
                        userDetails.getLoginID(),
                        name.getText().toString().trim(),
                        dateOfBirth.getText().toString(),
                        gender,
                        maritalStatus,
                        adhar.getText().toString().trim(),
                        phone.getText().toString().trim(),
                        userDetails.getMobile(),
                        "FAX",
                        email.getText().toString(),
                        userDetails.getSellerName(),
                        new SimpleDateFormat("yyyyMMddHH").format(new Date())
                        );
                break;
            default:
                break;
        }
    }

    public void saveProfile(String sellerId,String loginId,String name, String dob, String gender,String maritalStatus,String adhar,String phone,String mobile,String fax, String email,String updatedBy,String updatedOn){
        dialog.show();

        Call<UserDetails> call = apiService.editUserProfile(sellerId,loginId,name,dob,gender,maritalStatus,adhar,phone,mobile,fax,email,updatedBy,updatedOn);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        SpinnerDetails spinnerDetails = (SpinnerDetails) parent.getItemAtPosition(position);
        switch (parent.getId()){
            case R.id.seller_gender:
                if(position>0)
                    gender = spinnerDetails.getId();
                break;
            case R.id.maritial_status:
                if(position>0)
                    maritalStatus = spinnerDetails.getId();
                break;
            default:
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
