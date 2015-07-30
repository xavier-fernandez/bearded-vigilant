package com.bearded.common.annotation;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@TargetApi(21)
@IntDef({Sensor.REPORTING_MODE_ONE_SHOT, Sensor.REPORTING_MODE_SPECIAL_TRIGGER,
        Sensor.REPORTING_MODE_ON_CHANGE, Sensor.REPORTING_MODE_CONTINUOUS})
@Retention(RetentionPolicy.SOURCE)
public @interface ReportingMode {
}