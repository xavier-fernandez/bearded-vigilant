package com.bearded.modules.sensor.persistence.cloud;

import android.support.annotation.NonNull;

import com.bearded.common.cloud.RestApiClass;
import com.bearded.common.cloud.UploadStateListener;

import retrofit.Callback;
import retrofit.http.Field;

public class InternalSensorCloudUploader extends RestApiClass implements SensorCloudApi {

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadSensorData(@Field("internalSensorData") @NonNull final String jsonString,
                                 @NonNull final Callback<UploadStateListener> callback) {
        // TODO: Implement
    }
}
