package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.ProductAdapter;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.ProductDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, ProductAdapter.CustomDialogListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView productGrid;
    private MaterialSearchBar searchBar;
    private ArrayList<ProductDetails> productDetailsArrayList;
    private LinearLayout noData;
    private ProductAdapter productAdapter;
    private GridLayoutManager gridLayoutManager;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        userDetails = new UserDetails();
        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);
        noData = findViewById(R.id.no_data_layout);

        initSearchBar();

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        productGrid = findViewById(R.id.product_list);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_add:
                    Intent addProduct = new Intent(ProductsActivity.this, AddProduct
                            .class);
                    addProduct.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(addProduct);
                    return true;
                case R.id.navigation_filter:
                    Intent filter = new Intent(ProductsActivity.this, FilterActivity
                            .class);
                    filter.putExtra(Constants.PARENTCLASS,Constants.SHOP);
                    startActivity(filter);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);

        getProductList(String.valueOf(userDetails.getSellerId()));
    }

    public void getProductList(String sellerId){
        dialog.show();
        Call<ArrayList<ProductDetails>> call = apiService.getProducts(sellerId);
        call.enqueue(new Callback<ArrayList<ProductDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<ProductDetails>> call, Response<ArrayList<ProductDetails>> response) {
                if (response.isSuccessful()) {

                    productDetailsArrayList = response.body();
                    if(productDetailsArrayList.size()>0) {
                        noData.setVisibility(View.GONE);
                        productAdapter = new ProductAdapter(productDetailsArrayList,context);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                        productGrid.setLayoutManager(mLayoutManager);
                        gridLayoutManager =  new GridLayoutManager(getApplicationContext(),2, GridLayoutManager.VERTICAL,false);
                        productGrid.setLayoutManager(gridLayoutManager);
                        productGrid.setAdapter(productAdapter);
                        // Stopping Shimmer Effect's animation after data is loaded to ListView
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }

                } else {
                    try {
                        /*MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(),MessageDetails.class);
                        Log.d("Avinash", messageDetails.getMessage());*/
                        noData.setVisibility(View.VISIBLE);

                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<ProductDetails>> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    private void initSearchBar(){
        searchBar = findViewById(R.id.searchBar);
        ImageView navIcon = searchBar.findViewById(R.id.mt_nav);
        navIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular);
        searchBar.setOnSearchActionListener(this);
        /*String uri = "@drawable/ic_filter_list_black_24dp";
        searchBar.inflateMenu(R.menu.main,getResources().getIdentifier(uri, null, getPackageName()));*/
        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());


        CardView container = searchBar.findViewById(R.id.mt_container);
        container.setMinimumHeight(Integer.MAX_VALUE);
        searchBar.setCardViewElevation(5);
        searchBar.getPlaceHolderView().setTypeface(typeface);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        /*searchBar.findViewById(R.id.mt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter = new Intent(ProductsActivity.this, FilterActivity
                        .class);
                filter.putExtra(Constants.PARENTCLASS,Constants.SHOP);
                startActivity(filter);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
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
            /*case R.id.action_filter:
                Intent filter = new Intent(ShopActivity.this, FilterActivity
                        .class);
                filter.putExtra(Constants.PARENTCLASS,Constants.SHOP);
                startActivity(filter);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                finish();
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
                ImageView navIcon = searchBar.findViewById(R.id.mt_nav);
                navIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                Drawable mDrawable = navIcon.getDrawable();
                if (mDrawable instanceof Animatable) {
                    ((Animatable) mDrawable).stop();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(ProductDetails productDetails) {
        Toast.makeText(context,productDetails.getProductName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRemoveClicked(ProductDetails productDetails) {
        productDetailsArrayList.remove(productDetails);
        productAdapter.notifyDataSetChanged();
    }
}
