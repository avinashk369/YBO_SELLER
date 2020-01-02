package com.techcamino.mlm.yboseller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.adapter.ImageAdapter;
import com.techcamino.mlm.yboseller.details.ImageDetails;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.services.FileUploadService;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductImage extends AppCompatActivity {

    private Context context = this;
    private boolean propertyMode;
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private ArrayList<ImageDetails> imageDetails;
    private ImageDetails img;

    ImageAdapter RecyclerViewHorizontalAdapter;

    private String pictureFilePath;
    private ArrayList<Uri> imageList;
    private ArrayList<String> imagePathList;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 99;
    private int PICK_IMAGE_REQUEST = 7;
    private boolean pictures = false;
    private CardView add,change;
    private ArrayList<Image> images;
    private APIInterFace apiService;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.PRODUCT_IMAGES);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);

        img = new ImageDetails();
        imageDetails = new ArrayList<>();
        imageList = new ArrayList<>();
        imagePathList = new ArrayList<>();
        add = findViewById(R.id.add_more);
        change = findViewById(R.id.change);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadMultipleFiles(String.valueOf(1),"demo",String.valueOf(3),"4");
                /**
                 * ImageId:0
                 * sellerId:1
                 * ParentId:1
                 * ImageType:OfferImage
                 *
                 * image:imagepath
                 * createdBy:1
                 */
                Intent mIntent = new Intent(context, FileUploadService.class);
                mIntent.putExtra(Constants.UPLOAD_IMAGE_ID,"0");
                mIntent.putExtra(Constants.UPLOAD_SELLER_ID,"1");
                mIntent.putExtra(Constants.PARENT_ID,"1");
                mIntent.putExtra(Constants.UPLOAD_IMAGE_TYPE,"OfferImage");
                mIntent.putExtra(Constants.UPLOAD_CREATED_BY,"1");
                mIntent.putExtra(Constants.FILE_NAME,images);

                context.startService(mIntent);
            }
        });
    }

    private void openCamera() {


        if (ContextCompat.checkSelfPermission(ProductImage.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProductImage.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Camera Permission")
                        .setMessage("You have to give this permission to access this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ProductImage.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ProductImage.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            showFileChooser();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CAMERA){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
                showFileChooser();
            }else{

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            change.setVisibility(View.VISIBLE);

            initPager();
        }else{
            change.setVisibility(View.GONE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void initPager(){


        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        ImageAdapter viewPagerAdapter = new ImageAdapter(this,images);

        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];
        sliderDotspanel.removeAllViews();
        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showFileChooser() {
        images = new ArrayList<>();
        ImagePicker.with(ProductImage.this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(true)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle("Done")               //  Done button title
                .setLimitMessage("You have reached selection limit")    // Selection limit message
                .setMaxSize(10)                     //  Max images can be selected
                .setSavePath("ImagePicker")         //  Image capture folder name
                .setSelectedImages(images)          //  Selected images
                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();                           //  Start ImagePicker
    }


    public void showSnackbar(String message){
        View contextView = findViewById(R.id.context_view);

        Snackbar snackbar = Snackbar
                .make(contextView, message, Snackbar.LENGTH_LONG)
                .setAction(Constants.CLOSE, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent i = new Intent(co<View
        android:id="@+id/context_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>ntext, ViewComplain.class);
                        i.putExtra(Constants.USER_ID, userId);
                        startActivity(i);
                        finish();*/
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                /*Intent i = new Intent(context, ViewComplain.class);
                i.putExtra(Constants.USER_ID, userId);
                startActivity(i);
                finish();*/
            }
        });

        // Changing action button text color
        View sbView = snackbar.getView();

                /*FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)sbView.getLayoutParams();
                params.gravity = Gravity.TOP;
                sbView.setLayoutParams(params);*/

        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
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

    private void uploadMultipleFiles(String complainId, String folderName, String userAccess,String storeId) {
        // Map is used to multipart the file using okhttp3.RequestBody

        dialog.show();
        // Parsing any Media type file
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart(Constants.OWNER_ID, complainId);
        builder.addFormDataPart(Constants.FOLDER_NAME, folderName);
        builder.addFormDataPart(Constants.USER_ACCESS, userAccess);
        builder.addFormDataPart(Constants.STORE_ID, storeId);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < images.size(); i++) {

            File file = new File(images.get(i).getPath());
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        File file = new File("");
        MultipartBody requestBody = builder.build();

        Call<ResponseBody> call = apiService.uploadMultiFile(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    /*Toast.makeText(context, "Success " + response.message(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();*/
                    Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();

                } else  {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(), MessageDetails.class);
                        Log.d("Error",messageDetails.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.d("TESTING", "Error " + t.getMessage());
                showSnackbar(t.getMessage());
            }
        });
    }
}
