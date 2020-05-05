package com.bigb.bigblood.livedatas;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Melvin John
 */

public class SimpleLiveData<T> extends MutableLiveData<T> {

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    public void setValue(T t) {
        super.setValue(t);
    }
}
