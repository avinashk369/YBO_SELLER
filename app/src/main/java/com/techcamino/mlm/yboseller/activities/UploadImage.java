package com.techcamino.mlm.yboseller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImage extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 99;
    private ArrayList<Image> images;
    private APIInterFace apiService;
    private ProgressDialog dialog;
    private Context context = this;
    private CardView add,change;
    private ImageView imageView;
    private UserDetails userDetails;
    private String imageType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(Constants.UPLOAD_IMAGE);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        dialog = Security.getProgressDialog(this, Constants.PLEASE_WAIT);
        apiService =
                APIClient.getClient().create(APIInterFace.class);
        userDetails = new UserDetails();

        if(getIntent().hasExtra(Constants.USER_DETAIL))
            userDetails  =(UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAIL);
        if(getIntent().hasExtra(Constants.IMAGE_TYPE))
            imageType = getIntent().getStringExtra(Constants.IMAGE_TYPE);

        add = findViewById(R.id.add_more);
        change = findViewById(R.id.change);
        imageView = findViewById(R.id.imageView);
        openCamera();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMediaFile(String.valueOf(userDetails.getSellerId()),imageType);
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void openCamera() {


        if (ContextCompat.checkSelfPermission(UploadImage.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadImage.this,
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
                                ActivityCompat.requestPermissions(UploadImage.this,
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
                ActivityCompat.requestPermissions(UploadImage.this,
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
            Glide.with(context).load(images.get(0).getPath()).into(imageView);
            change.setVisibility(View.VISIBLE);

        }else{
            change.setVisibility(View.GONE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showFileChooser() {
        images = new ArrayList<>();
        ImagePicker.with(UploadImage.this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(false)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle("Done")               //  Done button title
                .setLimitMessage("You have reached selection limit")    // Selection limit message
                .setMaxSize(1)                     //  Max images can be selected
                .setSavePath("ImagePicker")         //  Image capture folder name
                .setSelectedImages(images)          //  Selected images
                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();                           //  Start ImagePicker
    }

    private void uploadMediaFile(String imageId, String imageType) {
        // Map is used to multipart the file using okhttp3.RequestBody

        // Parsing any Media type file
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart(Constants.IMAGE_TYPE, imageType);
        builder.addFormDataPart(Constants.IMAGE_ID, imageId);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < images.size(); i++) {

            File file = new File(images.get(i).getPath());
            builder.addFormDataPart("ImagePath", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        File file = new File("");
        MultipartBody requestBody = builder.build();

        Call<ResponseBody> call = apiService.uploadImage(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject jObjError = new JSONObject(response.body().string());
                        Log.d("Path",jObjError.get("Data").toString());
                        showSnackbar("Upload complete",jObjError.get("Data").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else  {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(), MessageDetails.class);
                        Log.d("Image",messageDetails.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Image", "Error " + t.getMessage());
            }
        });
    }

    public void showSnackbar(String message,final String imagePath){
        View contextView = findViewById(R.id.context_view);

        Snackbar snackbar = Snackbar
                .make(contextView, message, Snackbar.LENGTH_LONG)
                .setAction(Constants.CLOSE, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, ProfileActivity.class);
                        i.putExtra(Constants.USER_IMAGE, imagePath);
                        startActivity(i);
                        finish();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra(Constants.USER_IMAGE, imagePath);
                startActivity(i);
                finish();
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
}
