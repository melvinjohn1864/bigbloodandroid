package com.bigb.bigblood.livedatas;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Melvin John
 */

public class IntentCallerLiveData extends SimpleLiveData<Bundle> {

    public void observe(@NonNull LifecycleOwner owner, @NonNull final BundlePassObserver observer) {
        super.observe(owner, new Observer<Bundle>() {
            @Override
            public void onChanged(@Nullable Bundle bundle) {
                observer.passedDetails(bundle);
            }
        });
    }

    public interface BundlePassObserver {
        void passedDetails(Bundle bundle);
    }
}
