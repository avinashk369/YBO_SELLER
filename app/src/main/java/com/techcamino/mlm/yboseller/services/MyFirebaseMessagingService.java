package com.techcamino.mlm.yboseller.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.activities.MainActivity;
import com.techcamino.mlm.yboseller.details.UserDetails;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;
import com.techcamino.mlm.yboseller.util.Security;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private UserDetails userDetails;
    private APIInterFace apiService;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

        String oldToken = Security.getPreference(Constants.FCM_TOKEN,this);
        if(!oldToken.equalsIgnoreCase(s)) {
            /*userDetails = new UserDetails();
            userDetails.setFcmToken(s);
            apiService =
                    APIClient.getClient().create(APIInterFace.class);
            updateUser(userDetails);*/
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //new sendNotification(getApplicationContext()).execute(url);
        new SendNotification(this,"Title",
                remoteMessage.getData().get("message"),
                remoteMessage.getData().get("image")).execute();
    }

    public class SendNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message,imageUrl;

        public SendNotification(Context context, String title, String message,String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.message);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                final NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
                style.bigPicture(result);

                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                /*intent.putExtra(Constants.IMG_URL,message);
                intent.putExtra(Constants.ORGIN_IMAGE,imageUrl);*/
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,PendingIntent.FLAG_ONE_SHOT);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "101";

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

                    //Configure Notification Channel
                    notificationChannel.setDescription("Game Notifications");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);

                    notificationManager.createNotificationChannel(notificationChannel);
                }

                final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Title")
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setStyle(style)
                        .setLargeIcon(result)
                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_MAX);




                notificationManager.notify(1, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /*private void updateUser(UserDetails userDetails) {

        Call<UserDetails> call = apiService.updateUser(userDetails);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    UserDetails user = new UserDetails();
                    user= response.body();
                    Log.d("Saved",user.getId());

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
