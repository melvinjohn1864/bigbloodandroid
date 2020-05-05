package com.bigb.bigblood.utiles;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Melvin John
 */

public class PreferenceController {
    private static final String PREFERENCE_NAME = "pref_bigb";

    public interface PreferenceKeys {
        String PREFERENCE_ID = "user_id";
        String FIREBASE_TOKEN = "firebase_token";
        String PREFERENCE_LAT= "lat";
        String PREFERENCE_LNG= "lng";
        String PREFERENCE_PLACE_NAME = "place";
    }

    public static void setPreference(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setPreference(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setPreference(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void setPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setPreference(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static int getIntPreference(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    public static float getFloatPreference(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getFloat(key, -1f);
    }

    public static boolean getBooleanPreference(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static long getLongPreference(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, -1);
    }

    public static void clearWholeData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
