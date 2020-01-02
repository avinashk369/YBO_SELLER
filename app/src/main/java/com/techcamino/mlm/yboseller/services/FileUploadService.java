package com.techcamino.mlm.yboseller.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.MessageDetails;
import com.techcamino.mlm.yboseller.rest.APIClient;
import com.techcamino.mlm.yboseller.rest.APIInterFace;
import com.techcamino.mlm.yboseller.util.Constants;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FileUploadService extends JobIntentService {
    private static final String TAG = "FileUploadService";
    /**
     * Unique job ID for this service.
     */
    private APIInterFace apiService;
    private ArrayList<String> imagePathList;
    private NotificationManager notificationManager;
    private String NOTIFICATION_CHANNEL_ID = "101";
    private final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)

            .setAutoCancel(true)
            .setContentText("Uploading")
            .setWhen(System.currentTimeMillis())
            .setProgress(0,0,true)
            .setPriority(Notification.PRIORITY_MAX);

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        apiService =
                APIClient.getClient().create(APIInterFace.class);
        imagePathList = new ArrayList<>();
        /**
         *  mIntent.putExtra(Constants.UPLOAD_IMAGE_ID,"0");
         *                 mIntent.putExtra(Constants.UPLOAD_SELLER_ID,"1");
         *                 mIntent.putExtra(Constants.PARENT_ID,"1");
         *                 mIntent.putExtra(Constants.UPLOAD_IMAGE_TYPE,"OfferImage");
         *                 mIntent.putExtra(Constants.UPLOAD_CREATED_BY,"1");
         *                 mIntent.putExtra(Constants.FILE_NAME,images);
         */

        /**
         * ImageId:0
         * sellerId:1
         * ParentId:1
         * ImageType:OfferImage
         *
         * image:imagepath
         * createdBy:1
         */

        ArrayList<Image> fileName = (ArrayList<Image>)intent.getSerializableExtra(Constants.FILE_NAME);
        String parentId = intent.getStringExtra(Constants.PARENT_ID);
        String imageType = intent.getStringExtra(Constants.UPLOAD_IMAGE_TYPE);
        String createdBy = intent.getStringExtra(Constants.UPLOAD_CREATED_BY);
        String sellerId = intent.getStringExtra(Constants.UPLOAD_SELLER_ID);
        //imagePathList.add(fileName);

        uploadMultipleFiles("0",sellerId,parentId,imageType,fileName,createdBy);

        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadMultipleFiles(String imageId, String sellerId, String parentId,String imageType,ArrayList<Image> fileList,String createdBy) {
        // Map is used to multipart the file using okhttp3.RequestBody

        // Parsing any Media type file
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart(Constants.PARENT_ID, parentId);
        builder.addFormDataPart(Constants.UPLOAD_IMAGE_TYPE, imageType);
        builder.addFormDataPart(Constants.UPLOAD_CREATED_BY, "1");
        builder.addFormDataPart(Constants.UPLOAD_SELLER_ID, sellerId);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < fileList.size(); i++) {

            File file = new File(fileList.get(i).getPath());
            builder.addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
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
                    /*sendNotification();
                    Toast.makeText(FileObserverJob.this, "Success ", Toast.LENGTH_LONG).show();*/
                    Toast.makeText(FileUploadService.this, "Success ", Toast.LENGTH_LONG).show();


                } else  {
                    try {
                        MessageDetails messageDetails = new MessageDetails();
                        Gson gson = new Gson();
                        messageDetails=gson.fromJson(response.errorBody().charStream(), MessageDetails.class);
                        Log.d(TAG,messageDetails.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error " + t.getMessage());
            }
        });
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {


    }

}
