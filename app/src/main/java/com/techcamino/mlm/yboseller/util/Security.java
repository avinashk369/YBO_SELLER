package com.techcamino.mlm.yboseller.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;


import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.activities.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class Security {

    private static final String DIPSON_PREF = "DIPSON";

    /**
     * Sharedpreferences start
     */
    public static boolean savePrefrences(String key, String values, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, values);
        editor.apply();
        return true;
    }

    public static boolean saveBooleanPrefrences(String key, boolean values, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, values);
        editor.apply();
        return true;
    }

    public static boolean getBooleanPref(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        return sharedPreferences.getBoolean(key,false);
    }
    /*public static boolean saveLong(String key,Long values , Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SODPREFRENCES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key , values );
        editor.apply();
        return true;
    }
    public static long getLong(String key , Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SODPREFRENCES, 0);
        return sharedPreferences.getLong(key , 0);

    }*/

    public static boolean savePrefrences(String key, int values, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, values);
        editor.apply();
        return true;
    }

    public static String getPreference(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);

        return sharedPreferences.getString(key, Constants.NA);
    }

    public static String getTimeStamp(Date date)
    {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return s.format(date);
    }


    public static int getIntPreference(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        return sharedPreferences.getInt(key, Integer.MIN_VALUE);
    }

    public static boolean deletePrefrences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                DIPSON_PREF, 0);
        return sharedPreferences.edit().clear().commit();
    }

    /**
     * Sharedprefrences End
     */

	/* Progress Bar */
    public static ProgressDialog getProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setIndeterminate(false);
        //dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.logo));
        dialog.setCancelable(false);
        return dialog;
    }
    /* End of progress Bar*/

    public static void seesionOut(Context context){

        Security.deletePrefrences(context);
        Security.saveBooleanPrefrences(Constants.LOGIN_NAME, false, context);
        Intent logout = new Intent(context, LoginActivity
                .class);
        context.startActivity(logout);
        ((Activity) context).finish();
    }

    /*public static void showError(String titleMsg,String errorMsg,Context context)
    {
        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(titleMsg);
        dialog.setCancelable(false);

        // set values for custom dialog components - text, image and button
        //String fontPath = context.getString(R.string.tf_normal);
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        //text.setTypeface(tf);

        text.setText(errorMsg);
        *//*ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        image.setImageResource(R.drawable.logo);*//*

        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

    }*/

    public static String generateRandom(int length) {
        Random rand=new Random();
        StringBuilder res=new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randIndex=rand.nextInt(Constants.ATOZ.length());
            res.append(Constants.ATOZ.charAt(randIndex));
        }
        return res.toString();
    }

    public static Dialog getCustomDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_custom_dialog);

        return dialog;

    }

    public static void scrollToView(final View scrollView, final View view) {
        view.requestFocus();
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (!view.getLocalVisibleRect(scrollBounds)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    int toScroll = getRelativeTop(view) - getRelativeTop(scrollView);
                    ((ScrollView) scrollView).smoothScrollTo(0, toScroll-120);
                }
            });
        }
    }
    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) return myView.getTop();
        else return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
