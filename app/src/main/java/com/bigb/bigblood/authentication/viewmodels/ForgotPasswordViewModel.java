package com.bigb.bigblood.authentication.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.bigb.bigblood.authentication.models.ForgetPasswordResponse;
import com.bigb.bigblood.authentication.models.ForgotPasswordRequest;
import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private ApiResponseLiveData<ForgetPasswordResponse> forgetPasswordResponse;
    private MessageShowerLiveData messageShowerLiveData;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        messageShowerLiveData = new MessageShowerLiveData();
    }

    public MessageShowerLiveData getMessageShowerLiveData(){
        return messageShowerLiveData;
    }

    public ApiResponseLiveData<ForgetPasswordResponse> getForgetPasswordResponse(){
        return forgetPasswordResponse;
    }

    public void sendEmailToResetPassword(Activity activity, ForgotPasswordRequest forgotPasswordRequest) {
        NetworkController controller = NetworkController.getNetworkController();
        forgetPasswordResponse = controller.resetPassword(activity,forgotPasswordRequest);
    }

    public void exploreResponse(ForgetPasswordResponse forgetPasswordResponse) {
        if (forgetPasswordResponse == null){
            return;
        }
        messageShowerLiveData.setValue(forgetPasswordResponse.getMessage());
    }
}
