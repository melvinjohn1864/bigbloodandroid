package com.bigb.bigblood.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bigb.bigblood.R;
import com.bigb.bigblood.authentication.LoginActivity;
import com.bigb.bigblood.home.HomeActivity;
import com.bigb.bigblood.utiles.PreferenceController;

public class SplashActivity extends AppCompatActivity {

    private final static int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);

        goToLoginPage();
    }

    private void goToLoginPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userId = PreferenceController.getStringPreference(SplashActivity.this, PreferenceController.PreferenceKeys.PREFERENCE_ID);
                if (userId.equals("")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }

            }
        }, DELAY_TIME);
    }
}
