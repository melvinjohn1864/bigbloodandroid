package com.bigb.bigblood.donor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.authentication.RegistrationActivity;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.components.CustomTextView;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
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

public class DonorActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_toolbar_title)
    CustomTextView tv_toolbar_title;
    @BindView(R.id.donateButton)
    CustomButton donateButton;
    @BindView(R.id.et_name)
    CustomEditText et_name;
    @BindView(R.id.et_phone)
    CustomEditText et_phone;
    @BindView(R.id.et_address)
    CustomEditText et_address;
    @BindView(R.id.bloodGrpSpinner)
    Spinner bloodGrpSpinner;
    @BindView(R.id.et_date)
    CustomEditText et_date;
    @BindView(R.id.required_date)
    CustomTextView required_date;
    
    private DonorViewModel mDonorViewModel;
    private String bloodGroup;

    public static final String TAG = "DonorActivity";
    private static final int LOC_REQ_CODE = 1;
    private static final int PLACE_PICKER_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        ButterKnife.bind(this);

        et_date.setVisibility(View.GONE);
        required_date.setVisibility(View.GONE);
        
        donateButton.setOnClickListener(this);

        tv_toolbar_title.setText(R.string.donator_title);

        et_address.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            bloodGroup = bundle.getString("blood_group");
        }

        setSpinnerItem(bloodGroup);

        mDonorViewModel = ViewModelProviders.of(DonorActivity.this).get(DonorViewModel.class);
    }

    private void setSpinnerItem(String bloodGroup) {
        String bloodGrpArray[] = getResources().getStringArray(R.array.blood_groups);

        for (int i = 0;i < bloodGrpArray.length; i++){
            if (bloodGrpArray[i].toUpperCase().equalsIgnoreCase(bloodGroup.toUpperCase())){
                bloodGrpSpinner.setSelection(i);
            }
        }
        bloodGrpSpinner.setEnabled(false);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == LOC_REQ_CODE){
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

    private boolean validateDonorTextFields(){
        if (TextUtils.isEmpty(et_name.getText().toString())){
            String error_message = getResources().getString(R.string.empty_username);
            et_name.requestFocus();
            et_name.setError(error_message);
            return true;
        }else if(TextUtils.isEmpty(et_phone.getText().toString())){
            String error_message = getResources().getString(R.string.empty_phone);
            et_phone.requestFocus();
            et_phone.setError(error_message);
            return true;
        }else if(TextUtils.isEmpty(et_address.getText().toString())){
            String error_message = getResources().getString(R.string.empty_address);
            et_address.requestFocus();
            et_address.setError(error_message);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.donateButton:
                if (!validateDonorTextFields()){
                    updateDonorDetails();
                }
            case R.id.et_address:
                getCurrentPlaceItems();
                break;
        }
    }

    private void updateDonorDetails() {
        UserDetails details = new UserDetails();
        details.setName(et_name.getText().toString());
        details.setBloodGroup(bloodGrpSpinner.getSelectedItem().toString().trim());
        details.setPhone(et_phone.getText().toString());
        details.setLocation(et_address.getText().toString());
        
        mDonorViewModel.callUpdateDonorDetailsApi(details);
        mDonorViewModel.getUpdateResponse().observe(DonorActivity.this,responseObserver);
    }
    
    private ApiResponseLiveData.BigBApiObserver<String> responseObserver = new ApiResponseLiveData.BigBApiObserver<String>() {
        @Override
        public void showSuccessResponse(String response) {
            if (response.equalsIgnoreCase("Success")){
                showAlertDialog();
            }else {
                Toast.makeText(DonorActivity.this,R.string.network_connection_problem,Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DonorActivity.this);
        builder1.setTitle("Big 'B'");
        builder1.setMessage(R.string.donor_message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
