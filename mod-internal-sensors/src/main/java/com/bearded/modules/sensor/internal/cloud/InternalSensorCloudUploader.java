package com.bearded.modules.sensor.internal.cloud;

import android.support.annotation.NonNull;

import com.bearded.common.cloud.RestApiClass;
import com.bearded.common.cloud.UploadStateListener;

import retrofit.Callback;
import retrofit.http.Field;

public class InternalSensorCloudUploader extends RestApiClass implements InternalSensorCloudApi {

    @Override
    public void uploadInternalSensorData(@Field("internalSensorData") @NonNull String jsonString,
                                         @NonNull Callback<UploadStateListener> callback) {

    }
}
