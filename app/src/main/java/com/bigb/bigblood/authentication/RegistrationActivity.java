package com.bigb.bigblood.authentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.authentication.models.AuthenticationRequest;
import com.bigb.bigblood.authentication.models.AuthenticationResponse;
import com.bigb.bigblood.authentication.models.DetailsSubmitResponse;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.authentication.viewmodels.RegistrationViewModel;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.components.CustomTextView;
import com.bigb.bigblood.home.HomeActivity;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.IntentCallerLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;
import com.bigb.bigblood.utiles.CropCircleTransformation;
import com.bigb.bigblood.utiles.ImagePicker;
import com.bigb.bigblood.utiles.PreferenceController;
import com.bigb.bigblood.utiles.permissions.Util;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.et_username) CustomEditText et_username;
    @BindView(R.id.et_password) CustomEditText et_password;
    @BindView(R.id.et_email) CustomEditText et_email;
    @BindView(R.id.et_phone) CustomEditText et_phone;
    @BindView(R.id.bloodGrpSpinner)
    Spinner bloodGrpSpinner;
    @BindView(R.id.et_address) CustomEditText et_address;
    @BindView(R.id.registerButton) CustomButton registerButton;
    @BindView(R.id.tv_log_in) CustomTextView tv_log_in;
    @BindView(R.id.ic_user_image)
    ImageView ic_user_image;

    private RegistrationViewModel mRegisterViewModel;
    private String ProfileImageUrl = "";
    private Uri selectedImagePath;

    public static final String TAG = "RegistrationActivity";
    private static final int LOC_REQ_CODE = 1;
    private static final int PLACE_PICKER_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        mRegisterViewModel = ViewModelProviders.of(RegistrationActivity.this).get(RegistrationViewModel.class);
        mRegisterViewModel.getMessageShower().observe(RegistrationActivity.this, messageShowerObserver);
        mRegisterViewModel.getIntentCaller().observe(RegistrationActivity.this, intentObserver);
        
        registerButton.setOnClickListener(this);
        tv_log_in.setOnClickListener(this);
        ic_user_image.setOnClickListener(this);
        et_address.setOnClickListener(this);
    }

    private boolean validateRegistrationTextFields(){
        if (TextUtils.isEmpty(et_username.getText().toString())){
            String error_message = getResources().getString(R.string.empty_username);
            et_username.requestFocus();
            et_username.setError(error_message);
            return true;
        }else if(TextUtils.isEmpty(et_password.getText().toString())){
            String error_message = getResources().getString(R.string.empty_password);
            et_password.requestFocus();
            et_password.setError(error_message);
            return true;
        }else if(et_password.getText().toString().length() < 6){
            String error_message = getResources().getString(R.string.invalid_password);
            et_password.requestFocus();
            et_password.setError(error_message);
            return true;
        }else if(TextUtils.isEmpty(et_email.getText().toString())){
            String error_message = getResources().getString(R.string.empty_email);
            et_email.requestFocus();
            et_email.setError(error_message);
            return true;
        }else if(TextUtils.isEmpty(et_phone.getText().toString())){
            String error_message = getResources().getString(R.string.empty_phone);
            et_phone.requestFocus();
            et_phone.setError(error_message);
            return true;
        }else if(bloodGrpSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(getResources().getString(R.string.blood_group_prompt))){
            String error_message = getResources().getString(R.string.empty_bloodgrp);
            Toast.makeText(RegistrationActivity.this,error_message,Toast.LENGTH_SHORT).show();
            return true;
        }else if(TextUtils.isEmpty(et_address.getText().toString())){
            String error_message = getResources().getString(R.string.empty_address);
            et_address.requestFocus();
            et_address.setError(error_message);
            return true;
        }
        return false;
    }


    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            showPlacePicker();
        } else {
            requestLocationAccessPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void showPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerButton:
                if (!validateRegistrationTextFields()){
                    callRegistrationApi();
                }
                break;
            case R.id.tv_log_in:
                Intent loginIntent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.ic_user_image:
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(RegistrationActivity.this);
                startActivityForResult(chooseImageIntent, 100);
                break;
            case R.id.et_address:
                getCurrentPlaceItems();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 100) {

                selectedImagePath = ImagePicker.getImageFromResult(RegistrationActivity.this, resultCode, data);

                if (selectedImagePath != null) {

                    try {
                        Log.e("fil source", "--" + selectedImagePath.toString());
                        ProfileImageUrl = Util.getRealPathFromURI(RegistrationActivity.this, selectedImagePath);
                        if (ProfileImageUrl.substring(ProfileImageUrl.lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
                            Util.displayToast(RegistrationActivity.this, "Please select valid image");
                        } else {

                            Glide.with(this).load(selectedImagePath).placeholder(R.drawable.ic_circular_image).bitmapTransform(new CropCircleTransformation(RegistrationActivity.this)).into(ic_user_image);

                        }

                    } catch (OutOfMemoryError e) {
                        Toast.makeText(RegistrationActivity.this, "Sorry, This image can't be uploaded", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegistrationActivity.this, "Sorry, This image can't be uploaded", Toast.LENGTH_LONG).show();
                    }
                }
            }else if (requestCode == LOC_REQ_CODE){
                showPlacePicker();
            }else if (requestCode == PLACE_PICKER_REQ_CODE){
                Place place = PlacePicker.getPlace(this, data);
                et_address.setText(place.getName());
                PreferenceController.setPreference(this.getApplication(), PreferenceController.PreferenceKeys.PREFERENCE_PLACE_NAME, (String) place.getName());
                LatLng value = place.getLatLng();
                double lat = value.latitude;
                double lng = value.longitude;
                PreferenceController.setPreference(this.getApplication(), PreferenceController.PreferenceKeys.PREFERENCE_LAT, String.valueOf(lat));
                PreferenceController.setPreference(this.getApplication(), PreferenceController.PreferenceKeys.PREFERENCE_LNG, String.valueOf(lng));
            }
        }
    }

    private void callRegistrationApi() {
        AuthenticationRequest registrationRequest = new AuthenticationRequest();
        registrationRequest.setEmailId(et_email.getText().toString());
        registrationRequest.setPassword(et_password.getText().toString());

        UserDetails userDetails = new UserDetails();
        userDetails.setBloodGroup(bloodGrpSpinner.getSelectedItem().toString().trim());
        userDetails.setEmailId(et_email.getText().toString());
        userDetails.setName(et_username.getText().toString());
        userDetails.setLocation(et_address.getText().toString());
        userDetails.setPhone(et_phone.getText().toString());

        mRegisterViewModel.setUserDetails(RegistrationActivity.this,registrationRequest,userDetails,selectedImagePath);
        Util.showProgressDialog(RegistrationActivity.this);
        mRegisterViewModel.getRegistrationResponse().observe(RegistrationActivity.this,registrationObserver);
    }

    private MessageShowerLiveData.MessageObserver messageShowerObserver = new MessageShowerLiveData.MessageObserver() {
        @Override
        public void showMessage(String message) {
            Util.dismissProgressDialog();
            Toast.makeText(RegistrationActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    };

    private IntentCallerLiveData.BundlePassObserver intentObserver = new IntentCallerLiveData.BundlePassObserver() {
        @Override
        public void passedDetails(Bundle bundle) {
            Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

    private ApiResponseLiveData.BigBApiObserver<AuthenticationResponse> registrationObserver = new ApiResponseLiveData.BigBApiObserver<AuthenticationResponse>() {
        @Override
        public void showSuccessResponse(AuthenticationResponse response) {
            mRegisterViewModel.exploreResponse(response);
            mRegisterViewModel.getImageUploadResponse().observe(RegistrationActivity.this,imageUploadObserver);
        }
    };

    private ApiResponseLiveData.BigBApiObserver<DetailsSubmitResponse> detailsObserver = new ApiResponseLiveData.BigBApiObserver<DetailsSubmitResponse>() {
        @Override
        public void showSuccessResponse(DetailsSubmitResponse response) {
            mRegisterViewModel.exploreDetailsResponse(response);
        }
    };

    private ApiResponseLiveData.BigBApiObserver<String> imageUploadObserver = new ApiResponseLiveData.BigBApiObserver<String>() {
        @Override
        public void showSuccessResponse(String response) {
            mRegisterViewModel.exploreImageResponse(response);
            mRegisterViewModel.getDetailsSubmitResponseApiResponseLiveData().observe(RegistrationActivity.this,detailsObserver);
        }
    };
}
