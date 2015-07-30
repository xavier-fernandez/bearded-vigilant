package com.bearded.common.sensor;

import android.hardware.Sensor;
import android.support.annotation.NonNull;

public enum SensorType {

    LIGHT(Sensor.TYPE_LIGHT, "Light", "Lux"),
    PROXIMITY(Sensor.TYPE_PROXIMITY, "Proximity", "cm"),
    AMBIENT_TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", "Celsius"),
    RELATIVE_HUMIDITY(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity", "Relative Humidity Percentage");

    private static final String TAG = SensorType.class.getSimpleName();
    private final int mSensorId;
    @NonNull
    private final String mSensorTypeName;
    @NonNull
    private final String mSensorUnit;

    SensorType(final int sensorId, @NonNull String sensorTypeName, @NonNull String unit) {
        mSensorId = sensorId;
        mSensorTypeName = sensorTypeName;
        mSensorUnit = unit;
    }

    /**
     * Obtain a {@link SensorType} from a sensor ID.
     *
     * @param sensorId of an {@link android.hardware.Sensor}.
     * @return {@link SensorType} with the sensor Type with the id - <code>null</code> if not available.
     * @throws IllegalArgumentException when the given sensor type is not implemented.
     */
    @NonNull
    public static SensorType getSensorTypeFromId(int sensorId) {
        for (final SensorType sensorType : values()) {
            if (sensorType.mSensorId == sensorId) {
                return sensorType;
            }
        }
        throw new IllegalArgumentException(String.format("%s: Sensor type %d is not implemented yet.", TAG, sensorId));
    }

    /**
     * Returns the sensor ID.
     *
     * @return {@link java.lang.Integer} with the sensor ID specified in {@link android.hardware.Sensor}
     */
    public int getSensorId() {
        return mSensorId;
    }

    /**
     * Returns the sensor type name in english.
     *
     * @return {@link java.lang.String} with the sensor type name. Cannot be null.
     */
    @NonNull
    public String getSensorTypeName() {
        return mSensorTypeName;
    }

    /**
     * Returns the sensor unit.
     *
     * @return {@link java.lang.String} with the sensor unit.
     */
    @NonNull
    public String getSensorUnit() {
        return mSensorUnit;
    }
}