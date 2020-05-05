package com.bigb.bigblood.authentication;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.authentication.models.ForgetPasswordResponse;
import com.bigb.bigblood.authentication.models.ForgotPasswordRequest;
import com.bigb.bigblood.authentication.viewmodels.ForgotPasswordViewModel;
import com.bigb.bigblood.components.CustomButton;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.livedatas.MessageShowerLiveData;
import com.bigb.bigblood.utiles.permissions.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.et_email)
    CustomEditText et_email;
    @BindView(R.id.resetPassword)
    CustomButton resetPassword;

    private ForgotPasswordViewModel mForgotpasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        mForgotpasswordViewModel = ViewModelProviders.of(ForgotPasswordActivity.this).get(ForgotPasswordViewModel.class);
        mForgotpasswordViewModel.getMessageShowerLiveData().observe(ForgotPasswordActivity.this, messageShowerObserver);

        resetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetPassword:
                if (TextUtils.isEmpty(et_email.getText().toString())){
                    String error_message = getResources().getString(R.string.empty_email);
                    et_email.requestFocus();
                    et_email.setError(error_message);
                }else{
                    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
                    forgotPasswordRequest.setEmail(et_email.getText().toString());

                    mForgotpasswordViewModel.sendEmailToResetPassword(ForgotPasswordActivity.this,forgotPasswordRequest);
                    Util.showProgressDialog(ForgotPasswordActivity.this);
                    mForgotpasswordViewModel.getForgetPasswordResponse().observe(ForgotPasswordActivity.this,forgetPasswordObserver);
                }
                break;
        }
    }

    private MessageShowerLiveData.MessageObserver messageShowerObserver = new MessageShowerLiveData.MessageObserver() {
        @Override
        public void showMessage(String message) {
            Util.dismissProgressDialog();
            Toast.makeText(ForgotPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private ApiResponseLiveData.BigBApiObserver<ForgetPasswordResponse> forgetPasswordObserver = new ApiResponseLiveData.BigBApiObserver<ForgetPasswordResponse>() {
        @Override
        public void showSuccessResponse(ForgetPasswordResponse response) {
            mForgotpasswordViewModel.exploreResponse(response);
        }
    };
}
