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
public class LightInternalSensorModule extends AbstractInternalSensorManager {

    private static final String TAG = LightInternalSensorModule.class.getSimpleName();

    private static final short DATA_BIN_TIME_MS = 1000; // 1 SECOND

    private static final byte LIGHT_SENSOR_MODULE_VERSION = 1;

    private static final SensorType SENSOR_TYPE = SensorType.LIGHT;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public LightInternalSensorModule(@NonNull Context context) {
        super(context, SENSOR_TYPE, DATA_BIN_TIME_MS);
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
        return LIGHT_SENSOR_MODULE_VERSION;
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
        final float lux = event.values[0];
        mLastSensorValueReceivedTime = DateTime.now();
        assert (getDatabaseFacade() != null);
        getDatabaseFacade().insertSensorReading(getSensor(), lux);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return mLastSensorValueReceivedTime;
    }
}
