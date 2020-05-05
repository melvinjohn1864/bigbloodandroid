package com.bigb.bigblood.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.acceptor.AcceptorActivity;
import com.bigb.bigblood.authentication.models.ImageUploadInfo;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomTextView;
import com.bigb.bigblood.donor.DonorActivity;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.splash.SplashActivity;
import com.bigb.bigblood.utiles.PreferenceController;
import com.bigb.bigblood.utiles.permissions.PermissionManager;
import com.bigb.bigblood.utiles.permissions.Util;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.name)
    CustomTextView name;
    @BindView(R.id.blood_group)
    CustomTextView blood_group;
    @BindView(R.id.txt_email)
    CustomTextView txt_email;
    @BindView(R.id.txt_phone)
    CustomTextView txt_phone;
    @BindView(R.id.txt_address)
    CustomTextView txt_address;
    @BindView(R.id.userImage)
    ImageView userImage;
    @BindView(R.id.btn_donor)
    CustomButton btn_donor;
    @BindView(R.id.btn_acceptor)
    CustomButton btn_acceptor;
    @BindView(R.id.logout)
    ImageView logout;

    private HomeViewModel mhomeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        btn_donor.setOnClickListener(this);
        btn_acceptor.setOnClickListener(this);
        logout.setOnClickListener(this);

        mhomeViewModel = ViewModelProviders.of(HomeActivity.this).get(HomeViewModel.class);

        mhomeViewModel.getCurrentUserDetails(HomeActivity.this);
        Util.showProgressDialog(HomeActivity.this);
        mhomeViewModel.getmUserDetailsResponse().observe(HomeActivity.this,responseLiveData);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionForApp();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionForApp() {
        if (PermissionManager.checkCameraPermission(HomeActivity.this) &&
                PermissionManager.checkCallPhonePermission(HomeActivity.this)) {
        } else {
            if (PermissionManager.isShouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.CAMERA)
                    && PermissionManager.isShouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.CALL_PHONE)) {
                Util.showPermissionRationaleAlert(HomeActivity.this);
            } else {
                PermissionManager.requestPermissions(HomeActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private ApiResponseLiveData.BigBApiObserver<UserDetails> responseLiveData = new ApiResponseLiveData.BigBApiObserver<UserDetails>() {
        @Override
        public void showSuccessResponse(UserDetails response) {
            Util.dismissProgressDialog();
            if (response != null){
                mhomeViewModel.getUserProfileImage(HomeActivity.this);
                mhomeViewModel.imageUploadInfoLiveData().observe(HomeActivity.this,imageLiveData);
                name.setText(response.getName());
                blood_group.setText(response.getBloodGroup().toUpperCase());
                txt_email.setText(response.getEmailId());
                txt_phone.setText(response.getPhone());
                txt_address.setText(response.getLocation());
            }else {
                Toast.makeText(HomeActivity.this,"Network Connection Problem",Toast.LENGTH_SHORT).show();
            }

        }
    };

    private ApiResponseLiveData.BigBApiObserver<ImageUploadInfo> imageLiveData = new ApiResponseLiveData.BigBApiObserver<ImageUploadInfo>() {
        @Override
        public void showSuccessResponse(ImageUploadInfo response) {
            Glide.with(HomeActivity.this).load(response.getImageUrl()).placeholder(R.drawable.default_profile_image).into(userImage);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_donor:
                Intent intent =new Intent(HomeActivity.this,DonorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("blood_group",blood_group.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_acceptor:
                Intent intent1 = new Intent(HomeActivity.this,AcceptorActivity.class);
                startActivity(intent1);
                break;
            case R.id.logout:
                showLogoutAlert();
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mhomeViewModel.getCurrentUserDetails(HomeActivity.this);
        Util.showProgressDialog(HomeActivity.this);
        mhomeViewModel.getmUserDetailsResponse().observe(HomeActivity.this,responseLiveData);
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.todoDialogLight);
        builder.setMessage(R.string.logout_alert_message);

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PreferenceController.clearWholeData(HomeActivity.this);
                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
