package com.techcamino.mlm.yboseller.activities;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import static android.content.ContentValues.TAG;

public class YboApplication extends Application {

    static Activity mActivity;
    private APIInterFace apiService;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        apiService =
                APIClient.getClient().create(APIInterFace.class);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }
            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;
                Log.d(TAG, "onActivityResumed:"+activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        initFirebase();
    }

    public void initFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Security.savePrefrences(Constants.FCM_TOKEN,token,getApplicationContext());
                        //Toast.makeText(getApplicationContext(),token.trim(),Toast.LENGTH_LONG).show();
                        // Log and toast
                        Log.d(TAG, token.trim());
                        /*UserDetails userDetails = new UserDetails();
                        userDetails.setFcmToken(token);
                        updateUser(userDetails);*/
                    }
                });
    }

    /*private void updateUser(UserDetails userDetails) {

        Call<UserDetails> call = apiService.updateUser(userDetails);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    UserDetails user = new UserDetails();
                    user= response.body();
                    Log.d("Saved",user.getId()+"");

                } else  {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(), MessageDetails.class);
                        Log.d("Error",response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.d("TESTING", "Error " + t.getMessage());
            }
        });
    }*/
}
