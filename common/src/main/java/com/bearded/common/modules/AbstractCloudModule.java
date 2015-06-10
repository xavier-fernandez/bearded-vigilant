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

package com.bearded.common.modules;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.cloud.UploadStateListener;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

/**
 * Base class for all the modules that wants to push automatically the data for cloud upload.
 */
public abstract class AbstractCloudModule implements CloudModule, UploadStateListener {

    private final String TAG = this.getClass().getSimpleName();

    @NonNull
    private final String mDeviceMacAddress;
    @Nullable
    private DateTime mLastCloudUpload;

    protected AbstractCloudModule(@NonNull final Context context) {
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = manager.getConnectionInfo();
        mDeviceMacAddress = info.getMacAddress().toUpperCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadCompleted(final int code) {
        Log.d(TAG, String.format("onUploadCompleted -> Upload completed with code: %d", code));
        mLastCloudUpload = DateTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastCloudUploadTime() {
        return mLastCloudUpload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadFailure(final int errorCode) {
        Log.d(TAG, String.format("onUploadCompleted -> The error code %d was thrown", errorCode));
    }

    /**
     * Obtains the device metadata.
     *
     * @return {@link com.google.gson.JsonObject} with the device metadata
     */
    @NonNull
    protected JsonObject getDeviceMetadataJson() {
        final JsonObject databaseJsonObject = new JsonObject();
        databaseJsonObject.addProperty("MacAddress", mDeviceMacAddress);
        databaseJsonObject.addProperty("DeviceManufacturer", Build.MANUFACTURER);
        databaseJsonObject.addProperty("DeviceModel", Build.MODEL);
        databaseJsonObject.addProperty("OperatingSystem", String.format("Android %s", Build.VERSION.RELEASE));
        return databaseJsonObject;
    }
}