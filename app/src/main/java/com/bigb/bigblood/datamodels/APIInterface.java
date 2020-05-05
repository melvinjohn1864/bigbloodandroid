package com.bigb.bigblood.datamodels;

import com.bigb.bigblood.notifications.models.NotificationRequest;
import com.bigb.bigblood.notifications.models.NotificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AIzaSyCqYmw0dibbKsOg1bL-VZ83KwYmmu70HiQ"
    })
    @POST("fcm/send")
    Call<NotificationResponse> sendNotification(@Body NotificationRequest notificationRequest);
}

