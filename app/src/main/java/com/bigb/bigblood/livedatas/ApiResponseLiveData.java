package com.bigb.bigblood.livedatas;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

/**
 * Melvin John
 */

public class ApiResponseLiveData<T>  extends BigBLiveData<T> {

    public void observe(LifecycleOwner owner, final BigBApiObserver<T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T response) {
                if (response == null) {
                    return;
                }
                observer.showSuccessResponse(response);
            }
        });
    }

    public interface BigBApiObserver<T> {
        void showSuccessResponse(T response);
    }
}
