package com.techcamino.mlm.yboseller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.CategoryAdapter;
import com.techcamino.mlm.yboseller.adapter.CategorySpinnerAdpter;
import com.techcamino.mlm.yboseller.adapter.ProductAdapter;
import com.techcamino.mlm.yboseller.details.CategoryDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.ProductDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.DividerItemDecorator;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProduct extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private ArrayList<CategoryDetails> categoryDetailsArrayList;
    private Spinner categorySpinner;
    private CardView buttonSubmit;
    private HashMap<String,String> dataMap;
    private HashMap<String,String> messageMap;
    private EditText productName,productPrice,productQty;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.ADD_PRODUCT);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        categoryDetailsArrayList = new ArrayList<>();
        categorySpinner = findViewById(R.id.product_category);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productQty = findViewById(R.id.product_qty);
        dataMap = new HashMap<>();
        messageMap = new HashMap<>();
        userDetails = new UserDetails();
        categorySpinner.setOnItemSelectedListener(this);
        buttonSubmit.setOnClickListener(this);

        getCategoryList();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);
    }


    private void showBottomSheet(HashMap<String, String> messageMap){
        View dialogView = getLayoutInflater().inflate(R.layout.product_image_bottom, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(dialogView);
        dialog.show();
        TextView productMessage = dialog.findViewById(R.id.product_message);
        productMessage.setText(messageMap.get(Constants.DIALOG_MESSAGE));
        RelativeLayout productImage = dialog.findViewById(R.id.bottom_section);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProduct.this,ProductImage.class));
                dialog.dismiss();
            }
        });
    }
    public void getCategoryList(){
        dialog.show();
        Call<ArrayList<CategoryDetails>> call = apiService.getCategory();
        call.enqueue(new Callback<ArrayList<CategoryDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryDetails>> call, Response<ArrayList<CategoryDetails>> response) {
                if (response.isSuccessful()) {

                    categoryDetailsArrayList = response.body();
                    CategoryDetails categoryDetails = new CategoryDetails();
                    categoryDetails.setCategoryName(Constants.SELECT_CATEGORY);
                    categoryDetailsArrayList.add(0,categoryDetails);
                    CategorySpinnerAdpter customAdapter=new CategorySpinnerAdpter(context,categoryDetailsArrayList);
                    categorySpinner.setAdapter(customAdapter);

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
            public void onFailure(Call<ArrayList<CategoryDetails>> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CategoryDetails categoryDetails = (CategoryDetails) parent.getItemAtPosition(position);
        dataMap.put(Constants.CATEGORY_ID,categoryDetails.getCategoryId());
        dataMap.put(Constants.CATEGORY_NAME,categoryDetails.getCategoryName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void saveProduct(HashMap<String,String> dataMap){
        dialog.show();
        /**
         * @Field("categoryId") String categoryId,
*                                         @Field("categoryName") String categoryName,
*                                         @Field("sellerId") String sellerId,
*                                         @Field("productName") String productName,
*                                         @Field("price") String price,
*                                         @Field("productImage") String productImage,
*                                         @Field("quantity") String quantity,
*                                         @Field("active") String active,
*                                         @Field("createdBy") String createdBy,
*                                         @Field("createdOn") String createdOn,
*                                         @Field("updatedBy") String updatedBy,
*                                         @Field("updatedOn") String updatedOn,
*                                         @Field("deletedBy") String deletedBy,
*                                         @Field("deletedOn") String deletedOn
         */
        Call<ProductDetails> call = apiService.saveProductDetail(
                dataMap.get(Constants.CATEGORY_ID),
                dataMap.get(Constants.CATEGORY_NAME),
                dataMap.get(Constants.SELLER_ID),
                dataMap.get(Constants.PRODUCT_NAME),
                dataMap.get(Constants.PRODUCT_PRICE),
                dataMap.get(Constants.PRODUCT_IMG),
                dataMap.get(Constants.PRODUCT_QTY),
                dataMap.get(Constants.ACTIVE),
                dataMap.get(Constants.CREATED_BY),
                dataMap.get(Constants.CREATED_ON),
                dataMap.get(Constants.UPDATED_BY),
                dataMap.get(Constants.UPDATED_ON),
                dataMap.get(Constants.DELETED_BY),
                dataMap.get(Constants.CREATED_ON) );
        call.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, Response<ProductDetails> response) {
                if (response.isSuccessful()) {

                    ProductDetails productDetails = new ProductDetails();
                    productDetails = response.body();
                    Log.d("Product",productDetails.getProductName());
                    messageMap.put(Constants.DIALOG_MESSAGE,Constants.PRODUCT_ADDED);
                    showBottomSheet(messageMap);

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
            public void onFailure(Call<ProductDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                dataMap.put(Constants.PRODUCT_NAME,productName.getText().toString());
                dataMap.put(Constants.PRODUCT_PRICE,productPrice.getText().toString());
                dataMap.put(Constants.PRODUCT_QTY,productQty.getText().toString());
                dataMap.put(Constants.PRODUCT_IMG,"IMAGE");
                dataMap.put(Constants.ACTIVE,"1");
                dataMap.put(Constants.SELLER_ID,String.valueOf(userDetails.getSellerId()));
                dataMap.put(Constants.CREATED_ON,new Date().toString());
                dataMap.put(Constants.CREATED_BY,userDetails.getSellerName());
                dataMap.put(Constants.UPDATED_BY,userDetails.getSellerName());
                dataMap.put(Constants.UPDATED_ON,new Date().toString());
                dataMap.put(Constants.DELETED_BY,userDetails.getSellerName());
                dataMap.put(Constants.DELETED_ON,new Date().toString());
                saveProduct(dataMap);
                break;
            default:
                break;
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
}
