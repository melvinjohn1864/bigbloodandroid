package com.bigb.bigblood.authentication.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.bigb.bigblood.authentication.models.AuthenticationRequest;
import com.bigb.bigblood.authentication.models.AuthenticationResponse;
import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.IntentCallerLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;
import com.bigb.bigblood.utiles.PreferenceController;

public class LoginViewModel extends AndroidViewModel {
    private ApiResponseLiveData<AuthenticationResponse> loginResponseLiveData;
    private MessageShowerLiveData messageShower;
    private IntentCallerLiveData intentCaller;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        intentCaller = new IntentCallerLiveData();
        messageShower = new MessageShowerLiveData();
    }

    public MessageShowerLiveData getMessageShower() {
        return messageShower;
    }

    public IntentCallerLiveData getIntentCaller() {
        return intentCaller;
    }

    public ApiResponseLiveData<AuthenticationResponse> getLoginResponseLiveData() {
        return loginResponseLiveData;
    }

    public void setLoginDetails(Activity activity, AuthenticationRequest loginRequestParams) {
        NetworkController controller = NetworkController.getNetworkController();
        loginResponseLiveData = controller.loginUser(activity,loginRequestParams);
    }

    public void exploreResponse(AuthenticationResponse loginResponse) {
        if (loginResponse == null){
            return;
        }

        if (loginResponse.isStatus()){
            PreferenceController.setPreference(this.getApplication(), PreferenceController.PreferenceKeys.PREFERENCE_ID, loginResponse.getUserId());
            messageShower.setValue(loginResponse.getMessage());
            intentCaller.setValue(null);
        }else {
            messageShower.setValue(loginResponse.getMessage());
        }
    }
}
