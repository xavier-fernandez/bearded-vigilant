/*
 * (C) Copyright 2015 Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *      Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
 */

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

    public LocationProviderWithStatus(@NonNull final String provider) {
        mProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(@Nullable final Object otherProvider) {
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
    public void setStatus(@LocationProviderStatus final int status) {
        mStatus = status;
    }
}