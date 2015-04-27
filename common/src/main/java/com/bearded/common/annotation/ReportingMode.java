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