package com.bigb.bigblood.donor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.datamodels.NetworkController;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;

public class DonorViewModel extends AndroidViewModel {
    private ApiResponseLiveData<String> updateResponse;
    private MessageShowerLiveData messageShowerLiveData;

    public DonorViewModel(@NonNull Application application) {
        super(application);
    }

    ApiResponseLiveData<String> getUpdateResponse(){
        return updateResponse;
    }

    void callUpdateDonorDetailsApi(UserDetails details){
        NetworkController controller = new NetworkController();
        updateResponse = controller.updateDonorDetails(details);
    }
}
