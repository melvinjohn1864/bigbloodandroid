package com.bigb.bigblood.datamodels;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bigb.bigblood.authentication.models.AuthenticationRequest;
import com.bigb.bigblood.authentication.models.AuthenticationResponse;
import com.bigb.bigblood.authentication.models.DetailsSubmitResponse;
import com.bigb.bigblood.authentication.models.ForgetPasswordResponse;
import com.bigb.bigblood.authentication.models.ForgotPasswordRequest;
import com.bigb.bigblood.authentication.models.ImageUploadInfo;
import com.bigb.bigblood.authentication.models.UserDetails;
import com.bigb.bigblood.livedatas.ApiResponseLiveData;
import com.bigb.bigblood.notifications.models.NotificationRequest;
import com.bigb.bigblood.notifications.models.NotificationResponse;
import com.bigb.bigblood.utiles.PreferenceController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkController {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference bloodGrpReference;
    private DatabaseReference userReference;
    private DatabaseReference userRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String bloodGrpText = "Blood Group";

    private static NetworkController controller;

    public static NetworkController getNetworkController() {
        if (controller == null) {
            controller = new NetworkController();
        }
        return controller;
    }

    public ApiResponseLiveData<AuthenticationResponse> createNewUser(Activity activity, AuthenticationRequest registrationRequest){
        auth =FirebaseAuth.getInstance();
        final ApiResponseLiveData<AuthenticationResponse> apiResponseLiveData = new ApiResponseLiveData<>();

        auth.createUserWithEmailAndPassword(registrationRequest.getEmailId(),registrationRequest.getPassword())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        AuthenticationResponse registrationResponse = new AuthenticationResponse();
                        if (task.isSuccessful()){
                            registrationResponse.setStatus(true);
                            registrationResponse.setMessage("User created successfully");
                            registrationResponse.setUserId(auth.getCurrentUser().getUid());
                            apiResponseLiveData.setValue(registrationResponse);
                        }else {
                            registrationResponse.setStatus(false);
                            registrationResponse.setMessage(task.getException() +"");
                            registrationResponse.setUserId("");
                            apiResponseLiveData.setValue(registrationResponse);
                        }
                    }
                });
        return apiResponseLiveData;
    }


    public ApiResponseLiveData<AuthenticationResponse> loginUser(Activity activity, AuthenticationRequest loginRequestParams){
        auth =FirebaseAuth.getInstance();
        final ApiResponseLiveData<AuthenticationResponse> loginResponseLiveData = new ApiResponseLiveData<>();

        auth.signInWithEmailAndPassword(loginRequestParams.getEmailId(),loginRequestParams.getPassword())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        AuthenticationResponse loginResponse = new AuthenticationResponse();
                        if (task.isSuccessful()){
                            loginResponse.setStatus(true);
                            loginResponse.setMessage("User signed in successfully");
                            loginResponse.setUserId(auth.getCurrentUser().getUid());
                            loginResponseLiveData.setValue(loginResponse);
                        }else {
                            loginResponse.setStatus(false);
                            loginResponse.setMessage(task.getException() + "");
                            loginResponse.setUserId("");
                            loginResponseLiveData.setValue(loginResponse);
                        }
                    }
                });
        return loginResponseLiveData;
    }

    public ApiResponseLiveData<ForgetPasswordResponse> resetPassword(Activity activity,ForgotPasswordRequest forgotPasswordRequest){
        auth = FirebaseAuth.getInstance();
        final ApiResponseLiveData<ForgetPasswordResponse> forgetPasswordLiveData = new ApiResponseLiveData<>();

        auth.sendPasswordResetEmail(forgotPasswordRequest.getEmail())
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ForgetPasswordResponse forgetPasswordResponse = new ForgetPasswordResponse();
                        if (task.isSuccessful()){
                            forgetPasswordResponse.setMessage("We have sent you instructions to reset your password");
                            forgetPasswordLiveData.setValue(forgetPasswordResponse);
                        }else {
                            forgetPasswordResponse.setMessage(task.getException() +"");
                            forgetPasswordLiveData.setValue(forgetPasswordResponse);
                        }
                    }
                });
        return forgetPasswordLiveData;
    }

    public ApiResponseLiveData<DetailsSubmitResponse> storeDetails(UserDetails userDetails){
        final ApiResponseLiveData<DetailsSubmitResponse> detailsApiResponseLiveData = new ApiResponseLiveData<>();
        auth = FirebaseAuth.getInstance();
        String key = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("details");
        bloodGrpReference = databaseReference.child(bloodGrpText.toLowerCase());

        saveCurrentUserDataToDatabase(userDetails);

        DetailsSubmitResponse detailsSubmitResponse = new DetailsSubmitResponse();

        if(bloodGrpText != userDetails.getBloodGroup()){
            bloodGrpText = userDetails.getBloodGroup();
            bloodGrpReference = databaseReference.child(userDetails.getBloodGroup().toLowerCase());
            bloodGrpReference.child(key).setValue(userDetails);

            detailsSubmitResponse.setStatus(true);
            detailsApiResponseLiveData.setValue(detailsSubmitResponse);
        }else {
            bloodGrpReference.child(key).setValue(userDetails);

            detailsSubmitResponse.setStatus(true);
            detailsApiResponseLiveData.setValue(detailsSubmitResponse);
        }
        return detailsApiResponseLiveData;
    }


    private void saveCurrentUserDataToDatabase(UserDetails userDetails) {
        auth = FirebaseAuth.getInstance();
        String key = auth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference.child(key).setValue(userDetails);
    }

    public ApiResponseLiveData<String> uploadProfileImage(final Activity activity, Uri imagePath){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final String key = PreferenceController.getStringPreference(activity, PreferenceController.PreferenceKeys.PREFERENCE_ID);
        final ApiResponseLiveData<String> imageUploadResponse = new ApiResponseLiveData<>();

        if (imagePath != null){
            final StorageReference ref = storageReference.child("images/" + key);
            ref.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageUploadResponse.setValue("Success");
                            getImageDownloadUrl(activity);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageUploadResponse.setValue("Failed");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        return imageUploadResponse;
    }

    private void getImageDownloadUrl(Activity activity) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userReference = FirebaseDatabase.getInstance().getReference("users");
        final String key = PreferenceController.getStringPreference(activity, PreferenceController.PreferenceKeys.PREFERENCE_ID);
        final ImageUploadInfo imageUploadInfo = new ImageUploadInfo();
        final StorageReference ref = storageReference.child("images/" + key);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageUploadInfo.setImageUrl(uri.toString());
                // Adding image upload id s child element into databaseReference.
                userReference.child(key + "image/").setValue(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Url error",exception.getMessage());
            }
        });
    }


    public ApiResponseLiveData<UserDetails> getCurrentUserDetails(Activity activity){
        final ApiResponseLiveData<UserDetails> userDetailsApiResponseLiveData = new ApiResponseLiveData<>();
        String key = PreferenceController.getStringPreference(activity, PreferenceController.PreferenceKeys.PREFERENCE_ID);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(key);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails details = dataSnapshot.getValue(UserDetails.class);
                userDetailsApiResponseLiveData.setValue(details);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        });
        return userDetailsApiResponseLiveData;
    }

    public ApiResponseLiveData<ImageUploadInfo> getImageDetail(Activity activity){
        final ApiResponseLiveData<ImageUploadInfo> imageUploadInfoLiveData = new ApiResponseLiveData<>();
        String key = PreferenceController.getStringPreference(activity, PreferenceController.PreferenceKeys.PREFERENCE_ID);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(key + "image/");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = (String) dataSnapshot.getValue();
                ImageUploadInfo imageUploadInfo = new ImageUploadInfo();
                imageUploadInfo.setImageUrl(imageUrl);
                imageUploadInfoLiveData.setValue(imageUploadInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        });
        return imageUploadInfoLiveData;
    }

    public ApiResponseLiveData<String> updateDonorDetails(UserDetails details){
        final ApiResponseLiveData<String> updateDetailsResponse = new ApiResponseLiveData<>();

        auth = FirebaseAuth.getInstance();
        String key = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("details");
        bloodGrpReference = databaseReference.child(details.getBloodGroup().toLowerCase());
        bloodGrpReference.child(key).child("name").setValue(details.getName());
        bloodGrpReference.child(key).child("phone").setValue(details.getPhone());
        bloodGrpReference.child(key).child("location").setValue(details.getLocation());

        userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference.child(key).child("name").setValue(details.getName());
        userReference.child(key).child("phone").setValue(details.getPhone());
        userReference.child(key).child("location").setValue(details.getLocation());

        updateDetailsResponse.setValue("Success");

        return updateDetailsResponse;
    }

    public ApiResponseLiveData<NotificationResponse> sendNotification(NotificationRequest notificationRequest){
        final ApiResponseLiveData<NotificationResponse> notificationApiResponseLiveData = new ApiResponseLiveData<>();

        APIInterface service = RetrofitInstance.getRetrofitInstance().create(APIInterface.class);

        Call<NotificationResponse> call = service.sendNotification(notificationRequest);

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setStatus(response.code());
                notificationApiResponseLiveData.setValue(notificationResponse);
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setStatus(0);
                notificationApiResponseLiveData.setValue(notificationResponse);
            }
        });
        return notificationApiResponseLiveData;
    }
}
