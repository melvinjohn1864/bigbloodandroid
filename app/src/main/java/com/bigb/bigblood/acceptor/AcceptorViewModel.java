package com.bigb.bigblood.acceptor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.notifications.models.Data;
import com.bigb.bigblood.notifications.models.NotificationRequest;
import com.bigb.bigblood.notifications.models.NotificationResponse;

public class AcceptorViewModel extends AndroidViewModel {
    private ApiResponseLiveData<NotificationResponse> responseApiResponseLiveData;

    public AcceptorViewModel(@NonNull Application application) {
        super(application);
    }

    public ApiResponseLiveData<NotificationResponse> getResponseApiResponseLiveData(){
        return responseApiResponseLiveData;
    }

    void sendAcceptNotificationrequest(Data data){
        NotificationRequest notificationRequest = new NotificationRequest();

        notificationRequest.setTo("/topics/" + getSelectedTopic(data.getBloodGrp().toLowerCase()));
        notificationRequest.setData(data);

        NetworkController networkController = NetworkController.getNetworkController();
        responseApiResponseLiveData = networkController.sendNotification(notificationRequest);
    }

    private String getSelectedTopic(String bloodGroup) {
        if (bloodGroup.toLowerCase().equalsIgnoreCase("o+")){
            return "OPositive";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("o-")){
            return "ONegative";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("a+")){
            return "APositive";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("a-")){
            return "ANegative";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("b+")){
            return "BPositive";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("b-")){
            return "BNegative";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("ab+")){
            return "ABPositive";
        }else if (bloodGroup.toLowerCase().equalsIgnoreCase("ab-")){
            return "ABNegative";
        }
        return "";
    }
}
