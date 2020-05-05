package com.bigb.bigblood.notifications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationRequest {

    @Expose
    private String to;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}