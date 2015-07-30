package com.bearded.modules.sensor.internal;

import android.content.Context;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.sensor.SensorType;

import org.joda.time.DateTime;

/**
 * Class instantiation is done with refraction in {@see com.bearded.vigilant.ModuleManager}
 */
@SuppressWarnings("unused")
public class AmbientTemperatureInternalSensorModule extends AbstractInternalSensorManager {

    private static final String TAG = AmbientTemperatureInternalSensorModule.class.getSimpleName();

    private static final int DATA_BIN_TIME_MS = 1000; // 1 SECOND

    private static final int AMBIENT_TEMPERATURE_INTERNAL_SENSOR_MODULE_VERSION = 1;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public AmbientTemperatureInternalSensorModule(@NonNull final Context context) {
        super(context, SensorType.AMBIENT_TEMPERATURE, DATA_BIN_TIME_MS);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getModuleName() {
        return TAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModuleVersion() {
        return AMBIENT_TEMPERATURE_INTERNAL_SENSOR_MODULE_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return mLastSensorValueReceivedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        if (getSensor() == null) {
            Log.e(TAG, "onSensorChanged -> Sensor %s is not initialized yet.");
            return;
        }
        final float tempInCelsius = event.values[0];
        mLastSensorValueReceivedTime = DateTime.now();
        Log.d(TAG, String.format("onSensorChanged -> Sensor with name %s retrieved: %f  of relative humidity.", getSensor().getName(), tempInCelsius));
    }
}
