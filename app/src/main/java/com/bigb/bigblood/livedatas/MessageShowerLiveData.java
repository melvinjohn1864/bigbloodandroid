package com.bigb.bigblood.livedatas;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Melvin John
 */

public class MessageShowerLiveData extends SimpleLiveData<String> {

    public void observe(@NonNull LifecycleOwner owner, @NonNull final MessageObserver observer) {
        super.observe(owner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s == null) {
                    return;
                }
                observer.showMessage(s);
            }
        });
    }

    public interface MessageObserver {
        void showMessage(String message);
    }
}
