package com.bigb.bigblood.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notification.db";
    public String TABLE="notifications";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notifications(ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, message TEXT,date TEXT,name TEXT,phone TEXT,latitude TEXT,longitude TEXT,bloodGroup TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notifications");
    }
//Insert notification
    public boolean Insert(String title,String message,String date,String name,String phone,String latitude,String longitude){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("message", message);
        contentValues.put("date", date);
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        long result = sqLiteDatabase.insert("notifications", null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }



    // Get Notifications
    public ArrayList<HashMap<String, String>> GetNotifications(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> notificationList = new ArrayList<>();
        String query = "SELECT titile,message,date,name,phone,latitude,longitude FROM notifications WHERE date(datetime(DateColumn / 1000 , 'unixepoch')) >= date('now')";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> notification = new HashMap<>();
            notification.put("title",cursor.getString(cursor.getColumnIndex("title")));
            notification.put("message",cursor.getString(cursor.getColumnIndex("message")));
            notification.put("date",cursor.getString(cursor.getColumnIndex("date")));
            notification.put("name",cursor.getString(cursor.getColumnIndex("name")));
            notification.put("phone",cursor.getString(cursor.getColumnIndex("phone")));
            notification.put("latitude",cursor.getString(cursor.getColumnIndex("latitude")));
            notification.put("longitude",cursor.getString(cursor.getColumnIndex("longitude")));
    
           
            notificationList.add(notification);
        }
        return  notificationList;
    }

  
}
