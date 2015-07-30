package com.bearded.modules.sensor.persistence.cloud;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.cloud.RestApiClass;
import com.bearded.common.cloud.UploadStateListener;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SensorCloudUploader extends RestApiClass {

    private final String TAG = getClass().getSimpleName();

    /**
     * @see SensorCloudApi#uploadSensorData
     */
    public void uploadSensorData(@NonNull final String jsonString,
                                 @NonNull final UploadStateListener callback) {

        final SensorCloudApi apiService = getRestAdapter().create(SensorCloudApi.class);
        apiService.uploadSensorData(jsonString, new Callback<Integer>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(@NonNull final Integer code, @NonNull final Response response) {
                Log.d(TAG, "uploadSensorData -> The data have been sent successfully with the code: " + response.getStatus());
                callback.onUploadCompleted(response.getStatus());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(@NonNull final RetrofitError error) {
                Log.e(TAG, "uploadSensorData -> The data sending produced the following error " + error);
                callback.onUploadFailure(error.getMessage());
            }
        });
    }
}
