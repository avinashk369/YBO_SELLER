package com.techcamino.mlm.yboseller.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.ShopAdapter;
import com.techcamino.mlm.yboseller.details.ShopDetails;
import com.techcamino.mlm.yboseller.util.Constants;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private Context context = this;
    private ArrayList<ShopDetails> shopDetailsArrayList;
    private RecyclerView shopList;
    private ShopAdapter shopAdapter;
    private GridLayoutManager gridLayoutManager;
    private CardView buttonFilter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView shopGrid;
    private MaterialSearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        initSearchBar();

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        shopGrid = findViewById(R.id.shop_list);
        generateShopList();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                renderShop();

            }
        }, 3000);

    }

    private void initSearchBar(){
        searchBar = findViewById(R.id.searchBar);
        ImageView navIcon = searchBar.findViewById(R.id.mt_nav);
        navIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular);
        searchBar.setOnSearchActionListener(this);
        String uri = "@drawable/ic_filter_list_black_24dp";
        searchBar.inflateMenu(R.menu.main,getResources().getIdentifier(uri, null, getPackageName()));
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

        searchBar.findViewById(R.id.mt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter = new Intent(ShopActivity.this, FilterActivity
                        .class);
                filter.putExtra(Constants.PARENTCLASS,Constants.SHOP);
                startActivity(filter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void generateShopList(){
        shopDetailsArrayList = new ArrayList<>();
        for(int i=0;i<10;i++){
            ShopDetails shopDetails = new ShopDetails();
            shopDetails.setName("Grand"+i);
            shopDetails.setDescription("Des"+i);
            shopDetails.setOfferValue("25%"+i);
            shopDetailsArrayList.add(shopDetails);
        }
    }

    private void renderShop(){
        shopAdapter = new ShopAdapter(shopDetailsArrayList,context);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        shopGrid.setLayoutManager(mLayoutManager);
        gridLayoutManager =  new GridLayoutManager(getApplicationContext(),2, GridLayoutManager.VERTICAL,false);
        shopGrid.setLayoutManager(gridLayoutManager);
        shopGrid.setAdapter(shopAdapter);
        // Stopping Shimmer Effect's animation after data is loaded to ListView
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
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
}
