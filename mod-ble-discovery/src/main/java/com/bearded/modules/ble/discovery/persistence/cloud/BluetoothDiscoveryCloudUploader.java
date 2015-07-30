package com.bearded.modules.ble.discovery.persistence.cloud;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bearded.common.cloud.RestApiClass;
import com.bearded.common.cloud.UploadStateListener;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BluetoothDiscoveryCloudUploader extends RestApiClass {

    private final String TAG = getClass().getSimpleName();

    /**
     * @see BluetoothDiscoveryCloudUploader#uploadBluetoothDiscoveryData
     */
    public void uploadBluetoothDiscoveryData(@NonNull String jsonString,
                                             @NonNull final UploadStateListener callback) {

        final BluetoothDiscoveryAPI apiService = getRestAdapter().create(BluetoothDiscoveryAPI.class);
        apiService.uploadBluetoothDiscoveryData(jsonString, new Callback<Integer>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void success(@NonNull Integer code, @NonNull Response response) {
                Log.d(TAG, "uploadBluetoothDiscoveryData -> The data have been sent successfully with the code: " + response.getStatus());
                callback.onUploadCompleted(response.getStatus());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(@NonNull RetrofitError error) {
                Log.e(TAG, "uploadBluetoothDiscoveryData -> The data sending produced the following error " + error);
                callback.onUploadFailure(error.getMessage());
            }
        });
    }
}
