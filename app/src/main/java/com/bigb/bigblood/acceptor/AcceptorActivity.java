package com.bigb.bigblood.acceptor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.components.CustomTextView;
import com.bigb.bigblood.donor.DonorActivity;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.notifications.models.Data;
import com.bigb.bigblood.notifications.models.NotificationResponse;
import com.bigb.bigblood.utiles.PreferenceController;
import com.bigb.bigblood.utiles.permissions.Util;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcceptorActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_toolbar_title)
    CustomTextView tv_toolbar_title;
    @BindView(R.id.donateButton)
    CustomButton donateButton;
    @BindView(R.id.bloodGrpSpinner)
    Spinner bloodGrpSpinner;
    @BindView(R.id.et_name)
    CustomEditText et_name;
    @BindView(R.id.et_phone)
    CustomEditText et_phone;
    @BindView(R.id.et_address)
    CustomEditText et_address;
    @BindView(R.id.et_date)
    CustomEditText et_date;
    @BindView(R.id.required_date)
    CustomTextView required_date;

    private AcceptorViewModel mAcceptorViewModel;

    public static final String TAG = "AcceptorActivity";
    private static final int LOC_REQ_CODE = 1;
    private static final int PLACE_PICKER_REQ_CODE = 2;

    private double lat;
    private double lng;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        ButterKnife.bind(this);

        donateButton.setOnClickListener(this);
        et_date.setOnClickListener(this);

        tv_toolbar_title.setText(R.string.acceptor_title);
        donateButton.setText(R.string.send);

        et_address.setOnClickListener(this);

        myCalendar = Calendar.getInstance();
        
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        mAcceptorViewModel = ViewModelProviders.of(AcceptorActivity.this).get(AcceptorViewModel.class);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_date.setText(sdf.format(myCalendar.getTime()));
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
                LatLng value = place.getLatLng();
                lat = value.latitude;
                lng = value.longitude;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.donateButton:
                if (!validateAcceptorTextFields()){
                    Data data = new Data();

                    data.setTitle("Need" + bloodGrpSpinner.getSelectedItem().toString().trim() + "urgently");
                    data.setMessage(et_name.getText().toString() + "is urgently need of blood. Please help as soon as poosible so that a life could be saved.");
                    data.setDate(et_date.getText().toString());
                    data.setName(et_name.getText().toString());
                    data.setLon(String.valueOf(lng));
                    data.setLat(String.valueOf(lat));
                    data.setPhone(et_phone.getText().toString());
                    data.setPlace(et_address.getText().toString());
                    data.setBloodGrp(bloodGrpSpinner.getSelectedItem().toString().trim());


                    mAcceptorViewModel.sendAcceptNotificationrequest(data);
                    mAcceptorViewModel.getResponseApiResponseLiveData().observe(AcceptorActivity.this,responseObserver);
                    Util.showProgressDialog(AcceptorActivity.this);
                }
                break;
            case R.id.et_address:
                getCurrentPlaceItems();
                break;
            case R.id.et_date:
                new DatePickerDialog(AcceptorActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private boolean validateAcceptorTextFields(){
        if (TextUtils.isEmpty(et_name.getText().toString())){
            String error_message = getResources().getString(R.string.empty_username);
            et_name.requestFocus();
            et_name.setError(error_message);
            return true;
        }else if(bloodGrpSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(getResources().getString(R.string.blood_group_prompt))){
            String error_message = getResources().getString(R.string.empty_bloodgrp);
            Toast.makeText(AcceptorActivity.this,error_message,Toast.LENGTH_SHORT).show();
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

    private ApiResponseLiveData.BigBApiObserver<NotificationResponse> responseObserver = new ApiResponseLiveData.BigBApiObserver<NotificationResponse>() {
        @Override
        public void showSuccessResponse(NotificationResponse response) {
            if (response.getStatus() == 200){
                Util.dismissProgressDialog();
                showAlertDialog();
            }
        }
    };

    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AcceptorActivity.this);
        builder1.setTitle("Big 'B'");
        builder1.setMessage(R.string.acceptor_message);
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
