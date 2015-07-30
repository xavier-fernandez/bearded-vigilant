package com.bearded.common.modules;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

/**
 * This interface needs to be implemented on all the base classes from all modules.
 */
public interface Module {

    /**
     * Default timeout for receiving sensor data.
     */
    long DEFAULT_SENSOR_TIMEOUT_MILLISECONDS = 50 * 1000l;

    /**
     * Returns the module name.
     *
     * @return {@link java.lang.String} with the module name.
     */
    @NonNull
    String getModuleName();

    /**
     * Returns the version number of the module.
     *
     * @return {@link java.lang.Integer} with the version number of the module.
     */
    int getModuleVersion();

    /**
     * Checks if the module is enabled.
     *
     * @return <code>true</code> if the module is enabled - <code>false</code> otherwise.
     */
    boolean isModuleEnabled();

    /**
     * Obtains the time of the last received sensor data.
     *
     * @return {@link org.joda.time.DateTime} with the last received sensor data time.
     */
    @Nullable
    DateTime getLastSensorDataReceived();

}