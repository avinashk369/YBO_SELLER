package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.CategoryAdapter;
import com.techcamino.mlm.yboseller.adapter.FilterAdapter;
import com.techcamino.mlm.yboseller.details.CategoryDetails;
import com.techcamino.mlm.yboseller.details.FilterDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.DividerItemDecorator;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity implements FilterAdapter.CustomDialogListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private ArrayList<CategoryDetails> categoryDetails;
    private ArrayList<FilterDetails> filterDetails;
    private RecyclerView categoryList;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager linearLayoutManager;
    private CardView buttonFilter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView filterGrid;
    private FilterAdapter filterAdapter;

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

    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.FILTER);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        filterGrid = findViewById(R.id.filter_list);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        categoryList = findViewById(R.id.category_list);
        categoryDetails = new ArrayList<>();

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CategoryDetails> categoryDetails = new ArrayList<>();
                categoryDetails = categoryAdapter.getCategorySelected();
                for(CategoryDetails categoryDetails1 : categoryDetails) {
                    Log.d("Category", categoryDetails1.getCategoryName() + " "+categoryDetails1.getCategoryId());
                }
            }
        });

        String[] dashboardGridItem = context.getResources().getStringArray(R.array.filter_items);
        renderGrid(dashboardGridItem);
        getCategoryList();

    }

    private void renderGrid(String[] gridLitems){
        filterDetails = new ArrayList<>();
        for(int i=0; i<gridLitems.length;i++){
            FilterDetails dashboardGridDetails1 = new FilterDetails();
            dashboardGridDetails1.setGridName(gridLitems[i]);
            filterDetails.add(dashboardGridDetails1);
        }


        gridLayoutManager =  new GridLayoutManager(getApplicationContext(),4, GridLayoutManager.VERTICAL,false);
        filterGrid.setLayoutManager(gridLayoutManager);

        filterGrid.setItemAnimator(new DefaultItemAnimator());

        filterAdapter = new FilterAdapter(context,filterDetails);
        filterGrid.setAdapter(filterAdapter);
    }

    public void getCategoryList(){
        dialog.show();
        Call<ArrayList<CategoryDetails>> call = apiService.getCategory();
        call.enqueue(new Callback<ArrayList<CategoryDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryDetails>> call, Response<ArrayList<CategoryDetails>> response) {
                if (response.isSuccessful()) {

                    categoryDetails = response.body();
                    categoryAdapter = new CategoryAdapter(categoryDetails,context);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    categoryList.setLayoutManager(mLayoutManager);
                    categoryList.addItemDecoration(new DividerItemDecorator(context.getResources().getDrawable(R.drawable.divider)));
                    categoryList.setAdapter(categoryAdapter);

                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemClicked(FilterDetails filterDetails) {
        Toast.makeText(context,filterDetails.getGridName(),Toast.LENGTH_LONG).show();
    }
}
