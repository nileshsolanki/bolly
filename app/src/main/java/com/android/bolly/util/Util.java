package com.android.bolly.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.android.bolly.BuildConfig;
import com.android.bolly.R;
import com.android.bolly.models.UpdateLog;
import com.android.bolly.networking.RetrofitSingleton;
import com.android.bolly.phone.update.UpdateFragment;
import com.android.bolly.television.update.UpdateActivityTv;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A collection of utility methods, all static.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

    /*
     * Making sure public utility methods remain static
     */
    private Util() {
    }

    /**
     * Returns the screen/display size
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Shows a (long) toast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }



    /*
    start mx player and play file at given uri
     */
    public static void startMxPlayer(Context context, String url, View root) {

        Log.d(TAG, "start mx player");
        final String MX_AD = "com.mxtech.videoplayer.ad";
        final String MX_PRO = "com.mxtech.videoplayer.pro";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setData(Uri.parse(url));


        final PackageManager packageManager = context.getPackageManager();
        Intent pro = packageManager.getLaunchIntentForPackage(MX_PRO);
        Intent ad = packageManager.getLaunchIntentForPackage(MX_AD);

        if(pro != null){
            intent.setPackage(MX_PRO);
            context.startActivity(intent);
        }else if(ad != null){
            intent.setPackage(MX_AD);
            context.startActivity(intent);
        }else{
            if(root == null){
                AlertDialog dialog = new AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog)
                        .setTitle("MX Player Required. Install Now?")
                        .setItems(
                                new String[]{"Yes", "No"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i){
                                            case 0:
                                                visitMxPlayerMarket(context);
                                                break;

                                            case 1:
                                                dialogInterface.dismiss();
                                        }
                                    }
                                }
                        ).create();

                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }else{
                Snackbar.make(root, "MX Player required", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Install", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                visitMxPlayerMarket(context);
                            }
                        })
                        .setActionTextColor(context.getResources().getColor(R.color.colorAccentYellow)).show();
            }

        }



    }


    private static void visitMxPlayerMarket(Context context){
        final String MX_AD = "com.mxtech.videoplayer.ad";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MX_AD)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MX_AD)));
        }
    }



    public static Uri getUriForFile(Context context, File file){

        Uri fileUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            fileUri = FileProvider.getUriForFile(context, context.getOpPackageName() + ".provider", file);
        else
            fileUri = Uri.fromFile(file);

        return fileUri;

    }


    public static void deleteFile(File file){
        try {
            file.getCanonicalFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void fullScreen(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void checkUpdate(FragmentManager fragmentManager) {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Bundle args = new Bundle();
                        args.putString("downloadUrl", response.body().getUrl());
                        args.putString("version", response.body().getLatestVersion());

                        UpdateFragment updateFragment = new UpdateFragment();
                        updateFragment.setArguments(args);

                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(android.R.id.content, updateFragment, "update")
                                .addToBackStack("stack")
                                .commitAllowingStateLoss();

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }


    public static void checkUpdateTv(Activity activity) {

        RetrofitSingleton.getUpdateService().checkUpdates().enqueue(new Callback<UpdateLog>() {
            @Override
            public void onResponse(Call<UpdateLog> call, Response<UpdateLog> response) {
                if(response.body().getLatestVersion() != null){

                    if(BuildConfig.VERSION_CODE < response.body().getLatestVersionCode() ){

                        Intent intent = new Intent(activity, UpdateActivityTv.class);
                        intent.putExtra("downloadUrl", response.body().getUrl());
                        activity.startActivity(intent);

                    }


                }
            }

            @Override
            public void onFailure(Call<UpdateLog> call, Throwable t) {

            }
        });


    }

}
