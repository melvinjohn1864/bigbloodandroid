package com.bigb.bigblood.home;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.bigb.bigblood.authentication.models.ImageUploadInfo;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;

public class HomeViewModel extends AndroidViewModel {
    private ApiResponseLiveData<UserDetails> mUserDetailsResponse;
    private ApiResponseLiveData<ImageUploadInfo> infoApiResponseLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    ApiResponseLiveData<UserDetails> getmUserDetailsResponse(){
        return mUserDetailsResponse;
    }

    ApiResponseLiveData<ImageUploadInfo> imageUploadInfoLiveData(){
        return infoApiResponseLiveData;
    }

    void getUserProfileImage(Activity activity){
        NetworkController controller = NetworkController.getNetworkController();
        infoApiResponseLiveData = controller.getImageDetail(activity);
    }

    void getCurrentUserDetails(Activity activity){
        NetworkController controller = NetworkController.getNetworkController();
        mUserDetailsResponse = controller.getCurrentUserDetails(activity);
    }
}
