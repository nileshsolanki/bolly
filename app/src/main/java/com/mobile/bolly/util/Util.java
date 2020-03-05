package com.mobile.bolly.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mobile.bolly.R;
import com.mobile.bolly.WatchActivity;

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
            Snackbar.make(root, "MX Player required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Install", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MX_AD)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MX_AD)));
                            }
                        }
                    })
                    .setActionTextColor(context.getResources().getColor(R.color.colorAccentYellow)).show();
        }





    }
}
