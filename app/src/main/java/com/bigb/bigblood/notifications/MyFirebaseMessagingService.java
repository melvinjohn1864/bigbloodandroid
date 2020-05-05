package com.bigb.bigblood.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigb.bigblood.R;
import com.bigb.bigblood.notifications.models.Data;
import com.bigb.bigblood.utiles.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    private DatabaseHelper databaseHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Data data = new Data();
            data.setName(remoteMessage.getData().get("name"));
            data.setMessage(remoteMessage.getData().get("message"));
            data.setPlace(remoteMessage.getData().get("place"));
            data.setPhone(remoteMessage.getData().get("phone"));
            data.setLat(remoteMessage.getData().get("lat"));
            data.setLon(remoteMessage.getData().get("lon"));
            data.setBloodGrp(remoteMessage.getData().get("bloodGrp"));
            data.setDate(remoteMessage.getData().get("date"));
            showNotification(data);
        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void showNotification(Data data) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, NotificatonViewPageActivity.class);
        intent.putExtra(Constants.MESSAGE_NOTIFICATION, true);
        intent.putExtra("name",data.getName());
        intent.putExtra("place",data.getPlace());
        intent.putExtra("lat",data.getLat());
        intent.putExtra("lon",data.getLon());
        intent.putExtra("bloodGrp",data.getBloodGrp());
        intent.putExtra("phone",data.getPhone());
        intent.putExtra("date",data.getDate());
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Constants.BigB)
                .setContentText(data.getMessage())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(requestID, builder.build());
    }
}