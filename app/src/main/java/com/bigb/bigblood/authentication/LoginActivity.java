package com.bigb.bigblood.authentication;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.authentication.models.AuthenticationRequest;
import com.bigb.bigblood.authentication.models.AuthenticationResponse;
import com.bigb.bigblood.authentication.viewmodels.LoginViewModel;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.components.CustomTextView;
import com.bigb.bigblood.home.HomeActivity;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.IntentCallerLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;
import com.bigb.bigblood.notifications.NotificatonViewPageActivity;
import com.bigb.bigblood.utiles.permissions.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.et_username)
    CustomEditText et_username;
    @BindView(R.id.et_password)
    CustomEditText et_password;
    @BindView(R.id.loginButton)
    CustomButton loginButton;
    @BindView(R.id.tv_register)
    CustomTextView tv_register;

    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginViewModel = ViewModelProviders.of(LoginActivity.this).get(LoginViewModel.class);
        mLoginViewModel.getMessageShower().observe(LoginActivity.this, messageShowerObserver);
        mLoginViewModel.getIntentCaller().observe(LoginActivity.this, intentObserver);

        loginButton.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    private boolean validateLoginTextFields(){
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
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                if (!validateLoginTextFields()){
                    callLoginApi();
                }
                break;
            case R.id.tv_register:
                Intent registerIntent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void callLoginApi() {
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmailId(et_username.getText().toString());
        loginRequest.setPassword(et_password.getText().toString());

        mLoginViewModel.setLoginDetails(LoginActivity.this,loginRequest);
        Util.showProgressDialog(LoginActivity.this);
        mLoginViewModel.getLoginResponseLiveData().observe(LoginActivity.this,responseLiveData);
    }

    private MessageShowerLiveData.MessageObserver messageShowerObserver = new MessageShowerLiveData.MessageObserver() {
        @Override
        public void showMessage(String message) {
            Util.dismissProgressDialog();
            Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    };

    private IntentCallerLiveData.BundlePassObserver intentObserver = new IntentCallerLiveData.BundlePassObserver() {
        @Override
        public void passedDetails(Bundle bundle) {
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private ApiResponseLiveData.BigBApiObserver<AuthenticationResponse> responseLiveData = new ApiResponseLiveData.BigBApiObserver<AuthenticationResponse>() {
        @Override
        public void showSuccessResponse(AuthenticationResponse response) {
            mLoginViewModel.exploreResponse(response);
        }
    };
}
