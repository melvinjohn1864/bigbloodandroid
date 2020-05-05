package com.bigb.bigblood.livedatas;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Melvin John
 */

public class BigBLiveData<T> extends MutableLiveData<T> {

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, final Observer<T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T response) {
                observer.onChanged(response);
            }
        });
    }

    @MainThread
    public void setValue(@Nullable T t) {
        super.setValue(t);
    }


    public void postValue(@NonNull T t) {
        super.postValue(t);
    }

}
