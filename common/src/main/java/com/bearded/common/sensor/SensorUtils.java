package com.bearded.common.sensor;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.bearded.common.annotation.ReportingMode;

public abstract class SensorUtils {

    @NonNull
    private static final String TAG = SensorUtils.class.getSimpleName();

    /**
     * This method obtains a reporting time string out of a reporting type.
     * This method should only be called from SDK version 21, since it uses the new sensor APIs
     * introduced in Android 5.
     *
     * @param type {@link int} with the type.
     * @return {@link String} that correspond to the given {@param type}
     */
    @NonNull
    @TargetApi(21)
    public static String getReportingTimeString(@ReportingMode int type) {
        switch (type) {
            case Sensor.REPORTING_MODE_ONE_SHOT:
                return "REPORTING_MODE_ONE_SHOT";
            case Sensor.REPORTING_MODE_SPECIAL_TRIGGER:
                return "REPORTING_MODE_SPECIAL_TRIGGER";
            case Sensor.REPORTING_MODE_ON_CHANGE:
                return "REPORTING_MODE_ON_CHANGE";
            case Sensor.REPORTING_MODE_CONTINUOUS:
                return "REPORTING_MODE_CONTINUOUS";
            default:
                throw new IllegalArgumentException(TAG + ": getReportingTimeString -> Type does" +
                        " not correspond to a valid reporting type");
        }
    }
}