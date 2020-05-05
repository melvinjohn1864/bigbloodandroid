package com.bigb.bigblood.authentication.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bigb.bigblood.authentication.models.AuthenticationRequest;
import com.bigb.bigblood.authentication.models.AuthenticationResponse;
import com.bigb.bigblood.authentication.models.DetailsSubmitResponse;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.IntentCallerLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;
import com.bigb.bigblood.utiles.PreferenceController;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistrationViewModel extends AndroidViewModel {
    private ApiResponseLiveData<AuthenticationResponse> registrationResponse;
    private MessageShowerLiveData messageShower;
    private IntentCallerLiveData intentCaller;
    private UserDetails details;
    private ApiResponseLiveData<DetailsSubmitResponse> detailsSubmitResponseApiResponseLiveData;
    private ApiResponseLiveData<String> imageUploadResponse;
    private NetworkController controller;
    private String registrationMessage;
    private Uri selectedImagePath;
    private Activity activity;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        messageShower = new MessageShowerLiveData();
        intentCaller = new IntentCallerLiveData();
        controller = NetworkController.getNetworkController();
    }

    public MessageShowerLiveData getMessageShower() {
        return messageShower;
    }

    public IntentCallerLiveData getIntentCaller() {
        return intentCaller;
    }

    public ApiResponseLiveData<AuthenticationResponse> getRegistrationResponse() {
        return registrationResponse;
    }

    public ApiResponseLiveData<DetailsSubmitResponse> getDetailsSubmitResponseApiResponseLiveData() {
        return detailsSubmitResponseApiResponseLiveData;
    }

    public ApiResponseLiveData<String> getImageUploadResponse(){
        return imageUploadResponse;
    }

    public void setUserDetails(Activity activity, AuthenticationRequest registrationRequest, UserDetails userDetails, Uri imagePath) {
        selectedImagePath = imagePath;
        details = userDetails;
        this.activity =activity;
        registrationResponse = controller.createNewUser(activity,registrationRequest);
    }

    public void exploreResponse(AuthenticationResponse registrationResponse) {
        if (registrationResponse == null){
            return;
        }

        if (registrationResponse.isStatus()){
            PreferenceController.setPreference(this.getApplication(), PreferenceController.PreferenceKeys.PREFERENCE_ID, registrationResponse.getUserId());
            registrationMessage = registrationResponse.getMessage();
            imageUploadResponse = controller.uploadProfileImage(activity,selectedImagePath);
            setFirebaseTopic();
        }else {
            messageShower.setValue(registrationResponse.getMessage());
        }
    }

    private void setFirebaseTopic() {
        if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("o+")){
            FirebaseMessaging.getInstance().subscribeToTopic("OPositive");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("a+")){
            FirebaseMessaging.getInstance().subscribeToTopic("APositive");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("b+")){
            FirebaseMessaging.getInstance().subscribeToTopic("BPositive");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("ab+")){
            FirebaseMessaging.getInstance().subscribeToTopic("ABPositive");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("o-")){
            FirebaseMessaging.getInstance().subscribeToTopic("ONegative");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("a-")){
            FirebaseMessaging.getInstance().subscribeToTopic("ANegative");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("b-")){
            FirebaseMessaging.getInstance().subscribeToTopic("BNegative");
        }else if (details.getBloodGroup().toLowerCase().equalsIgnoreCase("ab-")){
            FirebaseMessaging.getInstance().subscribeToTopic("ABNegative");
        }
    }

    public void exploreDetailsResponse(DetailsSubmitResponse detailsSubmitResponse){
        if (detailsSubmitResponse == null){
            return;
        }

        if (detailsSubmitResponse.isStatus()){
            messageShower.setValue(registrationMessage);
            intentCaller.setValue(null);
        }
    }

    public void exploreImageResponse(String uploadResponse){
        if (uploadResponse == null){
            return;
        }

        if (uploadResponse.equalsIgnoreCase("Success")){
            detailsSubmitResponseApiResponseLiveData = controller.storeDetails(details);
        }
    }
}
