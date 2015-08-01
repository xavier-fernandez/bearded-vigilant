package com.bearded.common.modules;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.cloud.UploadStateListener;
import com.bearded.common.device.DeviceIdentifierManager;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

/**
 * Base class for all the modules that wants to push automatically the data for cloud upload.
 */
public abstract class AbstractCloudModule implements CloudModule, UploadStateListener {

    private final String TAG = this.getClass().getSimpleName();

    @NonNull
    private final String mDeviceId;
    @Nullable
    private DateTime mLastCloudUpload;

    protected AbstractCloudModule(@NonNull Context context) {
        mDeviceId = DeviceIdentifierManager.getDeviceId(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadCompleted(int code) {
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
    public void onUploadFailure(@Nullable String errorMessage) {
        Log.d(TAG, String.format("onUploadCompleted -> The following error was" +
                " thrown when uploading the data to the cloud: %s", errorMessage));
    }

    /**
     * Obtains the device metadata.
     *
     * @return {@link com.google.gson.JsonObject} with the device metadata
     */
    @NonNull
    protected JsonObject getDeviceMetadataJson() {
        final JsonObject databaseJsonObject = new JsonObject();
        databaseJsonObject.addProperty("DeviceIdentifier", mDeviceId);
        databaseJsonObject.addProperty("DeviceManufacturer", Build.MANUFACTURER);
        databaseJsonObject.addProperty("DeviceModel", Build.MODEL);
        final String androidRelease = String.format("Android %s", Build.VERSION.RELEASE);
        databaseJsonObject.addProperty("OperatingSystem", androidRelease);
        return databaseJsonObject;
    }
}
