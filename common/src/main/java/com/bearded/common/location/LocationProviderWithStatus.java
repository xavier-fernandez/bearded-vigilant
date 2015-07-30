package com.bearded.common.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.common.annotation.LocationProviderStatus;

import static android.location.LocationProvider.OUT_OF_SERVICE;

public class LocationProviderWithStatus {

    @NonNull
    private final String mProvider;
    @LocationProviderStatus
    private int mStatus = OUT_OF_SERVICE;

    public LocationProviderWithStatus(@NonNull String provider) {
        mProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(@Nullable Object otherProvider) {
        if (otherProvider == null) {
            return false;
        }
        if (otherProvider instanceof String) {
            return otherProvider.equals((mProvider));
        }
        if (otherProvider instanceof LocationProviderWithStatus) {
            return ((LocationProviderWithStatus) otherProvider).mProvider.equals(mProvider);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mProvider + " with status " + mStatus;
    }

    /**
     * Returns the provider name.
     *
     * @return {@link String} with the provider name.
     */
    @NonNull
    public String getProvider() {
        return mProvider;
    }

    /**
     * Returns the provider status.
     *
     * @return {@link LocationProviderStatus} with the provider status.
     */
    @LocationProviderStatus
    public int getStatus() {
        return mStatus;
    }

    /**
     * Modifies the provider status.
     *
     * @param status {@link LocationProviderStatus} with the provider status.
     */
    public void setStatus(@LocationProviderStatus int status) {
        mStatus = status;
    }
}