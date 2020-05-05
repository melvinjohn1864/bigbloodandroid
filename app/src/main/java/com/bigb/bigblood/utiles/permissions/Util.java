package com.bigb.bigblood.utiles.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import com.bigb.bigblood.BuildConfig;
import com.bigb.bigblood.R;
import com.bigb.bigblood.splash.SplashActivity;
import com.bigb.bigblood.utiles.PreferenceController;

/**
 * Created by Melvin John
 */

public class Util {

    public static ProgressDialog sProgressDialog;

    public static void showPermissionRationaleAlert(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.permission_rationale);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionManager.requestPermissions(activity);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDenialAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.permission_denied_explanation);
        builder.setPositiveButton(R.string.permission_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Util.showAppSettings(context);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showAppSettings(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void setIntentToMaps(Activity activity, String latitude, String longitude, String address) {

        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:" + latitude
                            + "," + longitude
                            + "?q=" + latitude
                            + "," + longitude
                            + "(" + address + ")"));
            intent.setComponent(new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            try {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
            }

            e.printStackTrace();
        }
    }

    public static void makeCall(Context ctx, String phoneNumber) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (PermissionManager.checkCallPhonePermission(ctx)) {
            ctx.startActivity(phoneIntent);
            return;
        } else {
            Util.showAppSettings(ctx);
        }

    }

    public static String getDeviceId(Context ctx) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static void showProgressDialog(Context context) {
        dismissProgressDialog();
        sProgressDialog = new ProgressDialog(context, R.style.MyTheme);
        sProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        sProgressDialog.setCancelable(false);
        try {
            sProgressDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void dismissProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
        }
    }

    /**
     * Display toast from classes
     *
     * @param ctx
     * @param msg
     */
    public static void displayToast(Context ctx, String msg) {
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String getRealPathFromURI(Context mContext, Uri contentURI) {
        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void callLogout(final Context context, String message){
        final Activity activity = (Activity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.todoDialogLight);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceController.clearWholeData(context);
                Intent intent = new Intent(activity,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


}
