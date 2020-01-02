package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.techcamino.mlm.yboseller.adapter.CityAdpter;
import com.techcamino.mlm.yboseller.adapter.StateAdpter;
import com.techcamino.mlm.yboseller.details.CityDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.StateDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditShopDetail extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private UserDetails userDetails;
    private ScrollView scrollView;
    private TextView accessMsg;
    private CardView buttonSubmit,citySpinner;
    private EditText shopName,landmark,address,pincode;
    private Spinner stateList,cityList;
    private String stateId,cityId;
    private ArrayList<StateDetails> stateDetailsArrayList;
    private ArrayList<CityDetails> cityDetailsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_detail);

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

        stateDetailsArrayList = new ArrayList<>();
        cityDetailsArrayList = new ArrayList<>();

        accessMsg = findViewById(R.id.verify);
        stateList = findViewById(R.id.seller_state);
        cityList = findViewById(R.id.seller_city);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        scrollView = findViewById(R.id.shop_detail_scroll);
        citySpinner = findViewById(R.id.city_card);
        shopName = findViewById(R.id.shop_name);
        landmark = findViewById(R.id.landmark);
        address = findViewById(R.id.shop_address);
        pincode = findViewById(R.id.pincode);

        stateList.setSelection(0,false);
        cityList.setSelection(0,false);
        buttonSubmit.setOnClickListener(this);
        stateList.setOnItemSelectedListener(this);
        cityList.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);

        getStateList(Constants.COUNTRY_ID);
        renderUI(userDetails);
    }

    private void renderUI(UserDetails userDetails){

        shopName.setText(userDetails.getShopName());
        landmark.setText(userDetails.getLandmark());
        address.setText(userDetails.getAddress());
        pincode.setText(userDetails.getPinCode());

        for(StateDetails stateDetails : stateDetailsArrayList){
            if(stateDetails.getId().equalsIgnoreCase(userDetails.getStateId()))
                stateList.setSelection(stateDetailsArrayList.indexOf(stateDetails));
        }

        for(CityDetails cityDetails : cityDetailsArrayList){
            if(cityDetails.getId().equalsIgnoreCase(userDetails.getCityId()))
                cityList.setSelection(cityDetailsArrayList.indexOf(cityDetails));
        }

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

                saveShopDetail(String.valueOf(userDetails.getSellerId()),
                        userDetails.getLoginID(),
                        shopName.getText().toString().trim(),
                        Security.getPreference(Constants.USER_LAT,context),
                        Security.getPreference(Constants.USER_LNG,context),
                        address.getText().toString().trim(),
                        landmark.getText().toString().trim(),
                        userDetails.getCityId(),
                        userDetails.getCityName(),
                        userDetails.getStateId(),
                        userDetails.getStateName(),
                        pincode.getText().toString().trim(),
                        userDetails.getMobile(),
                        new SimpleDateFormat("yyyyMMddHH").format(new Date()),
                        userDetails.getSellerName(),
                        new SimpleDateFormat("yyyyMMddHH").format(new Date()));
                break;
            default:
                break;
        }
    }

    public void saveShopDetail(String sellerId,String loginId,String shopname, String lat, String lng,String address,
                               String landmark,String cityid,String cityname,String stateid, String statename,String pincode,
                               String mobile,String updatedBy,String sellerName,String updatedOn){
        dialog.show();

        Call<UserDetails> call = apiService.editUserBusinessProfile(sellerId,loginId,shopname,lat,lng,address,landmark,cityid,cityname,
                stateid,statename,pincode,mobile,updatedBy,sellerName,updatedOn);
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


        switch (parent.getId()){
            case R.id.seller_state:
                StateDetails spinnerDetails = (StateDetails) parent.getItemAtPosition(position);
                if(position>0) {
                    getCityList(spinnerDetails.getId());
                    stateId = spinnerDetails.getId();
                    userDetails.setStateId(spinnerDetails.getId());
                    userDetails.setStateName(spinnerDetails.getName());
                }
                break;
            case R.id.seller_city:
                CityDetails cityDetails = (CityDetails) parent.getItemAtPosition(position);
                if(position>0) {
                    cityId = cityDetails.getId();
                    userDetails.setCityId(cityDetails.getId());
                    userDetails.setCityName(cityDetails.getName());
                }
                break;
            default:
                break;

        }

    }

    public void getStateList(String countryId){
        dialog.show();

        Call<StateDetails> call = apiService.getState(countryId);
        call.enqueue(new Callback<StateDetails>() {
            @Override
            public void onResponse(Call<StateDetails> call, Response<StateDetails> response) {
                if (response.isSuccessful()) {

                    stateDetailsArrayList = response.body().getStateDetailsArrayList();

                    StateDetails categoryDetails = new StateDetails();
                    categoryDetails.setName(Constants.SELECT_STATE);
                    stateDetailsArrayList.add(0,categoryDetails);
                    StateAdpter customAdapter=new StateAdpter(context,stateDetailsArrayList);
                    stateList.setAdapter(customAdapter);

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
            public void onFailure(Call<StateDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    public void getCityList(String stateId){
        dialog.show();

        Call<CityDetails> call = apiService.getCity(stateId);
        call.enqueue(new Callback<CityDetails>() {
            @Override
            public void onResponse(Call<CityDetails> call, Response<CityDetails> response) {
                if (response.isSuccessful()) {

                    cityDetailsArrayList = response.body().getCityDetailsArrayList();

                    citySpinner.setVisibility(View.VISIBLE);
                    CityDetails categoryDetails = new CityDetails();
                    categoryDetails.setName(Constants.SELECT_CITY);
                    cityDetailsArrayList.add(0,categoryDetails);
                    CityAdpter customAdapter=new CityAdpter(context,cityDetailsArrayList);
                    cityList.setAdapter(customAdapter);

                } else {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());
                        citySpinner.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CityDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
