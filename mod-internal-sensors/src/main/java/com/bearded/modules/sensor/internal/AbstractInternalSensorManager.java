package com.bearded.modules.sensor.internal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.AbstractCloudModule;
import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.persistence.SensorDatabaseFacade;
import com.bearded.modules.sensor.persistence.cloud.SensorCloudUploader;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static com.bearded.common.time.TimeUtils.millisecondsFromNow;

/**
 * This abstract class should be inherited by all the internal sensor modules which wants
 * to listen for notifications from the default sensor for the given {@link SensorType}
 */
abstract class AbstractInternalSensorManager extends AbstractCloudModule implements SensorEventListener {

    private static final String TAG = AbstractInternalSensorManager.class.getSimpleName();

    @NonNull
    private final SensorManager mSensorManager;
    @NonNull
    private final SensorType mSensorType;
    @Nullable
    private final Sensor mInternalSensor;
    @Nullable
    private final SensorDatabaseFacade mDatabaseFacade;
    @NonNull
    private final SensorCloudUploader mInternalSensorCloudUploader;
    private byte mConsecutiveTimeouts = 0;

    protected AbstractInternalSensorManager(@NonNull Context context,
                                            @NonNull SensorType sensorType,
                                            int binSizeMillis) {
        super(context);
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mSensorType = sensorType;
        mInternalSensor = getSensorManager().getDefaultSensor(sensorType.getSensorId());
        if (getSensor() == null) {
            final String typeName = sensorType.getSensorTypeName();
            Log.w(TAG, String.format("%s -> The device do not have a %s sensor.", TAG, typeName));
            mDatabaseFacade = null;
        } else {
            getSensorManager().registerListener(this, getSensor(), SENSOR_DELAY_NORMAL);
            mDatabaseFacade = new SensorDatabaseFacade(context, sensorType, binSizeMillis);
        }
        mInternalSensorCloudUploader = new SensorCloudUploader();
    }

    /**
     * Returns the sensor type of the module.
     *
     * @return {@link com.bearded.common.sensor.SensorType}
     */
    @NonNull
    public SensorType getSensorType() {
        return mSensorType;
    }

    /**
     * Returns the default sensor of the module {@link SensorType}
     *
     * @return {@link Sensor} used in the module - <code>null</code> if is not available.
     */
    @Nullable
    public Sensor getSensor() {
        return mInternalSensor;
    }

    /**
     * Obtains the {@link SensorDatabaseFacade} of the module {@link Sensor}
     *
     * @return the module {@link SensorDatabaseFacade}
     */
    @Nullable
    protected SensorDatabaseFacade getDatabaseFacade() {
        return mDatabaseFacade;
    }

    /**
     * Obtains the {@link SensorManager} of the module {@link Sensor}
     *
     * @return the module {@link SensorManager}
     */
    @NonNull
    protected SensorManager getSensorManager() {
        return mSensorManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isModuleEnabled() {
        if (getSensor() == null) {
            return false;
        }
        final DateTime lastReceivedDataTime = getLastSensorDataReceived();
        if (lastReceivedDataTime == null) {
            return false;
        }
        return millisecondsFromNow(lastReceivedDataTime) > DEFAULT_SENSOR_TIMEOUT_MILLISECONDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(@NonNull Sensor sensor, int accuracy) {
        Log.i(TAG, String.format("onAccuracyChanged -> Accuracy changed to %d.", accuracy));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushCloudDataToTheCloud() {
        if (mDatabaseFacade == null) {
            Log.w(TAG, "pushCloudDataToTheCloud -> Database facade is null.");
        } else {
            final JsonObject sensorDataJson = mDatabaseFacade.getSensorDataJson(getDeviceMetadataJson());
            if (sensorDataJson == null) {
                Log.w(TAG, "pushCloudDataToTheCloud -> No data to upload.");
            } else {
                mInternalSensorCloudUploader.uploadSensorData(sensorDataJson.toString(), this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadCompleted(int code) {
        super.onUploadCompleted(code);
        Log.d(TAG, String.format("onUploadComplete -> With code: %d", code));
        assert mDatabaseFacade != null;
        mDatabaseFacade.removeAllUploadedSensorMeasurements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadFailure(@Nullable String message) {
        Log.d(TAG, String.format("onUploadFailure with message: %s", message));
        if (message != null && message.startsWith("timeout")) {
            assert mDatabaseFacade != null;
            synchronized (this) {
                if (mConsecutiveTimeouts++ > 3) {
                    mConsecutiveTimeouts = 0;
                    Log.w(TAG, "onUploadFailure -> Purging database, file is to big for sending it completely to the cloud.");
                    mDatabaseFacade.removeAllUploadedSensorMeasurements();
                }
            }
        }
    }
}