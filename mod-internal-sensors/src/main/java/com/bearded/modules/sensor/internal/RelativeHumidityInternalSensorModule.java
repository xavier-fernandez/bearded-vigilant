package com.bearded.modules.sensor.internal;

import android.content.Context;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.sensor.SensorType;

import org.joda.time.DateTime;

/**
 * Class instantiation is done using refraction in {@see com.bearded.vigilant.ModuleManager}
 */
@SuppressWarnings("unused")
public class RelativeHumidityInternalSensorModule extends AbstractInternalSensorManager {

    private static final String TAG = RelativeHumidityInternalSensorModule.class.getSimpleName();

    private static final int RELATIVE_HUMIDITY_INTERNAL_SENSOR_MODULE_VERSION = 1;

    private static final int DATA_BIN_TIME_MS = 1000; // 1 SECOND

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    /**
     * Constructor called in {@see com.bearded.vigilant.ModuleManager}
     *
     * @param context needed to initialize the {@link android.hardware.SensorManager}
     */
    public RelativeHumidityInternalSensorModule(@NonNull Context context) {
        super(context, SensorType.RELATIVE_HUMIDITY, DATA_BIN_TIME_MS);
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
        return RELATIVE_HUMIDITY_INTERNAL_SENSOR_MODULE_VERSION;
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
        final float rh = event.values[0];
        mLastSensorValueReceivedTime = DateTime.now();
        Log.d(TAG, String.format("onSensorChanged -> Sensor with name %s retrieved: %f%% of relative humidity.", getSensor().getName(), rh));
    }
}
