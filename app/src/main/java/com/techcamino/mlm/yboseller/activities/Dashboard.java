package com.techcamino.mlm.yboseller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.OfferAdapter;
import com.techcamino.mlm.yboseller.details.DashboardDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.OfferDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MaterialSearchBar.OnSearchActionListener, OnChartValueSelectedListener {

    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private TextView logOut;
    private MaterialSearchBar searchBar;
    private DrawerLayout drawer;
    private ArrayList<OfferDetails> offerDetailsArrayList;
    private RecyclerView offerGrid;
    private OfferAdapter offerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ShimmerFrameLayout mShimmerViewContainer;
    private UserDetails userDetails;
    private DashboardDetails dashboardDetails;

    private PieChart chart;
    private int[] MY_COLORS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dialog = Security.getProgressDialog(context, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        MY_COLORS = new int[]{
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.blue_sky),
                getResources().getColor(R.color.grad_dusk_end),
                getResources().getColor(R.color.yellow_dark)};

    }

    private void renderOffer(){
        offerAdapter = new OfferAdapter(offerDetailsArrayList,context);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        offerGrid.setLayoutManager(mLayoutManager);
        //offerGrid.addItemDecoration(new DividerItemDecorator(context.getResources().getDrawable(R.drawable.divider)));
        offerGrid.setAdapter(offerAdapter);
        // Stopping Shimmer Effect's animation after data is loaded to ListView
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    private void generateOfferList(){
        offerDetailsArrayList = new ArrayList<>();
        for(int i=0;i<10;i++){
            OfferDetails offerDetails = new OfferDetails();
            offerDetails.setName("Grand"+i);
            offerDetails.setDescription("Des"+i);
            offerDetails.setOfferValue(new Random().nextInt((10 - 5) + 1)+5+"%");
            offerDetailsArrayList.add(offerDetails);
        }
    }

    private void initUI(){

        offerGrid = findViewById(R.id.offer_list);
        chart = findViewById(R.id.dashboard_chart);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        String uri = "@drawable/ic_filter_list_black_24dp";
        searchBar.inflateMenu(R.menu.main,getResources().getIdentifier(uri, null, getPackageName()));
        //searchBar.setText("Hello World!");
        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
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
                Intent filter = new Intent(Dashboard.this, FilterActivity
                        .class);
                filter.putExtra(Constants.PARENTCLASS,Constants.DASHBOARD);
                startActivity(filter);
            }
        });

        logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Security.deletePrefrences(context);
                //Security.saveBooleanPrefrences(Constants.LOGIN_ID, false, context);
                /*Intent logout = new Intent(MainActivity.this, LoginActivity
                        .class);
                startActivity(logout);*/
                finish();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView  userName = headerView.findViewById(R.id.user_name);
        TextView  loginId = headerView.findViewById(R.id.login_id);
        TextView  doj = headerView.findViewById(R.id.doj);
        userName.setText("YBO");
        loginId.setText(userDetails.getMobile());
        doj.setText(new Date().toString().substring(0,10));
    }

    public void dashboardDetail(String sellerId){
        dialog.show();
        Call<DashboardDetails> call = apiService.getSellerDashboard(sellerId);
        call.enqueue(new Callback<DashboardDetails>() {
            @Override
            public void onResponse(Call<DashboardDetails> call, Response<DashboardDetails> response) {
                if (response.isSuccessful()) {

                    dashboardDetails = new DashboardDetails();
                    dashboardDetails = response.body();
                    setData(5, 2,dashboardDetails);

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
            public void onFailure(Call<DashboardDetails> call, Throwable t) {
                Log.d("Failure", t.getMessage().toString());
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails = (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);

        dashboardDetail(String.valueOf(userDetails.getSellerId()));
        initUI();
        initChart();
        /*generateOfferList();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                renderOffer();

            }
        }, 3000);*/



    }

    public void initChart(){
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterTextTypeface(ResourcesCompat.getFont(this, R.font.montserrat_regular));
        chart.setCenterText(generateCenterSpannableText());


        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(48f);
        chart.setTransparentCircleRadius(51f);

        chart.setDrawCenterText(false);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);

        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setDrawEntryLabels(true);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.montserrat_regular));
        chart.setEntryLabelTextSize(12f);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }


    private void setData(int count, float range, DashboardDetails dashboardDetails) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        boolean found = false;
        entries.add(new PieEntry(dashboardDetails.getActiveOffer(),
                "5",
                getResources().getDrawable(R.drawable.ic_person_black_24dp)));
        entries.add(new PieEntry(dashboardDetails.getActiveProduct(),
                ""+dashboardDetails.getActiveProduct(),
                getResources().getDrawable(R.drawable.ic_person_black_24dp)));
        entries.add(new PieEntry(dashboardDetails.getDeactiveProduct(),
                "2",
                getResources().getDrawable(R.drawable.ic_person_black_24dp)));
        entries.add(new PieEntry(dashboardDetails.getProductInOffer(),
                "3",
                getResources().getDrawable(R.drawable.ic_person_black_24dp)));
        entries.add(new PieEntry(dashboardDetails.getProductWithoutOffer(),
                "4",
                getResources().getDrawable(R.drawable.ic_person_black_24dp)));


        PieDataSet dataSet = new PieDataSet(entries, null);

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        //if(!found)
        if(!found)
        {
            entries.add(new PieEntry(1,
                    "No value",
                    getResources().getDrawable(R.drawable.ic_person_black_24dp)));
            chart.getLegend().setEnabled(false);
            dataSet.setDrawValues(false);
        }

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c: MY_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new IntValueFormatter());
        //data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_regular));
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    public class IntValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        //searchBar.setMenuIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            /*case R.id.action_search:

                return true;*/

        }
        return true;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Handler mHandler = new Handler(); // use handler only when required
        int elapse = 220;

        if(id == R.id.nav_contact) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent activation = new Intent(Dashboard.this, ForgotPassword
                            .class);
                    startActivity(activation);
                }
            },elapse);


        }else if (id == R.id.nav_profile) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent profile = new Intent(Dashboard.this, ProfileActivity
                            .class);
                    profile.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(profile);
                }
            },elapse);

        }
        else if (id == R.id.nav_bank) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent profile = new Intent(Dashboard.this, BankDetailActivity
                            .class);
                    profile.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(profile);
                }
            },elapse);

        }
        else if (id == R.id.nav_address) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent profile = new Intent(Dashboard.this, ManageShopActivity
                            .class);
                    profile.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(profile);
                }
            },elapse);

        }
        else if (id == R.id.nav_product) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent product = new Intent(Dashboard.this, ProductsActivity
                            .class);
                    product.putExtra(Constants.USER_DETAIL,userDetails);
                    startActivity(product);
                }
            },elapse);

        }else if (id == R.id.nav_news) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    /*Intent profile = new Intent(Dashboard.this, ProfileActivity
                            .class);
                    startActivity(profile);*/
                }
            },elapse);

        }





        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        Toast.makeText(Dashboard.this, "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Toast.makeText(Dashboard.this, "Search " + text, Toast.LENGTH_SHORT).show();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>(offerDetailsArrayList);
        offerDetailsArrayList.clear();
        for(OfferDetails offerDetails : offerDetailsList){
            if(offerDetails.getName().toLowerCase().contains(text.toString().toLowerCase())){
                offerDetailsArrayList.add(offerDetails);
            }
        }
        Dashboard.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                offerAdapter.notifyDataSetChanged();
            }
        });

    }



    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
                break;
            default:
                break;
        }
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

}
