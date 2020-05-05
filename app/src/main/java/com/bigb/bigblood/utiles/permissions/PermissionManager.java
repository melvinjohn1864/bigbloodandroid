package com.bigb.bigblood.utiles.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by yadhukrishnan.e@oneteam.us
 */

public class PermissionManager {

    public static final int REQUEST_FOR_PERMISSION = 1001;

    /*
     * Add the required permission in this array and manifest
     */
    private static String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean checkPermissions(Context context) {
        for (final String permission : permissions) {
            final boolean status = checkPermission(context, permission);
            if (!status) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissionRationale(Activity context) {
        for (final String permission : permissions) {
            final boolean status = isShouldShowRequestPermissionRationale(context, permission);
            if (!status) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissionGranted(int[] results) {
        for (final int result : results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Activity context) {
        ActivityCompat.requestPermissions(context, permissions, REQUEST_FOR_PERMISSION);
    }


    private static boolean checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkCameraPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkCallPhonePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isShouldShowRequestPermissionRationale(Activity context, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
    }
}
