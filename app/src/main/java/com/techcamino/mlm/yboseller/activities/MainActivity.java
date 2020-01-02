package com.techcamino.mlm.yboseller.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.GpsUtils;
import com.techcamino.mlm.yboseller.util.Security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String [] appPermission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int PERMISSSION_REQUEST_CODE = 1240;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isGPS = false;
    private LocationRequest locationRequest;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        /*if (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            //return;
        }*/
        if(checkAndRequestPermission()) {


            getLocation();
        }
        super.onResume();
    }

    private void getLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest,new LocationCallback(),null);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d("Location",location.getLatitude()+" Lattitude" + location.getLongitude());
                            Security.savePrefrences(Constants.USER_LAT,String.valueOf(location.getLatitude()),context);
                            Security.savePrefrences(Constants.USER_LNG,String.valueOf(location.getLongitude()),context);
                            handleSplash();
                        }else{
                            Log.d("Location","No found");
                            //fusedLocationClient.requestLocationUpdates(locationRequest,new LocationCallback(),null);
                            getLocation();
                        }
                    }
                });
    }

    public void handleSplash() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        }, 3000);
    }

    private boolean checkAndRequestPermission() {
        List<String> listPermissionNeeded  = new ArrayList<>();
        for(String perm : appPermission){

            if(ContextCompat.checkSelfPermission(this,perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(perm);
            }
        }

        if(!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    PERMISSSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSSION_REQUEST_CODE){
            HashMap<String,Integer> permissinResult = new HashMap<>();
            int deniedCount =0;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    permissinResult.put(permissions[i],grantResults[i]);
                    deniedCount++;
                }

                if(deniedCount==0){
                    //handleSplash();
                    getLocation();
                }else{
                    for(Map.Entry<String,Integer> entry:permissinResult.entrySet()){
                        String permName = entry.getKey();
                        int permResult = entry.getValue();

                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,permName)){
                            showDialog("", "This app needs storage permission", "Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                            checkAndRequestPermission();
                                        }
                                    }, "No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    },false);

                        }else{

                            showDialog("", "You have denied storage permission", "Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package",getPackageName(),null));
                                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, "No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    },false);

                        }
                    }
                }

            }
        }
    }



    public AlertDialog showDialog(String title, String msg, String positiveLbl,
                                  DialogInterface.OnClickListener positiveOnclick,
                                  String negativeLbl, DialogInterface.OnClickListener negativeOnClick,
                                  boolean isCancelable){

        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLbl,positiveOnclick);
        builder.setNegativeButton(negativeLbl,negativeOnClick);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
