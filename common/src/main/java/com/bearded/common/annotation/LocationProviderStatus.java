package com.bearded.common.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

@IntDef({AVAILABLE, OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface LocationProviderStatus {
}