package com.bigb.bigblood.notifications;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigb.bigblood.R;
import com.bigb.bigblood.components.CustomEditText;
import com.bigb.bigblood.components.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificatonViewPageActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.lt_call_click)
    LinearLayout lt_call_click;
    @BindView(R.id.lt_chat_click)
    LinearLayout lt_chat_click;
    @BindView(R.id.lt_location_click)
    LinearLayout lt_location_click;
    @BindView(R.id.tv_name)
    CustomEditText tv_name;
    @BindView(R.id.tv_blood_group)
    CustomEditText tv_blood_group;
    @BindView(R.id.tv_date)
    CustomEditText tv_date;
    private int CALL_PERMISSION_CODE = 23;
    private String phoneNumber="";

    @BindView(R.id.tv_toolbar_title)
    CustomTextView tv_toolbar_title;

    private String acceptorName;
    private String bloodGrp;
    private String place;
    private String lat;
    private String lon;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        ButterKnife.bind(this);

        tv_toolbar_title.setText("ACCEPTOR INFO");

        acceptorName = getIntent().getStringExtra("name");
        bloodGrp = getIntent().getStringExtra("bloodGrp");
        place = getIntent().getStringExtra("place");
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        date = getIntent().getStringExtra("date");
        phoneNumber=getIntent().getStringExtra("phone");
        tv_name.setText(acceptorName);
        tv_blood_group.setText(bloodGrp);
        tv_date.setText(date);


        lt_call_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCallAllowed()) {
                    CallNumber(phoneNumber);
                } else {
                    requestCallPermission();
                }
            }
        });
        lt_chat_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
ChatNumber();
            }
        });
        lt_location_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLocation();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    void CallNumber(String number){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(intent);
    }
    void ChatNumber(){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body","");
        startActivity(smsIntent);
    }
    void ShowLocation(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+lat+","+lon+"?q="+lat+","+lon+"("+place+")"));
        startActivity(intent);
    }

    private boolean isCallAllowed() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestCallPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},CALL_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == CALL_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

               CallNumber(phoneNumber);
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

}
