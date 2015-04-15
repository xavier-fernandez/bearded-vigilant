package com.bearded.modules.sensor.light;

import com.bearded.common.modules.Module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

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

public class LightSensorModule implements Module {

    private static final String TAG = LightSensorModule.class.getSimpleName();
    private static final int LIGHT_SENSOR_MODULE_VERSION = 1;

    private LightSensorManager mLightSensorManager = LightSensorManager.getInstance();

    /**
     * {@inheritDoc}
     */
    @NotNull
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
    public boolean isModuleEnabled() {
        return mLightSensorManager.hasLightSensor();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime lastCloudUploadTime() {
        return null;
    }
}
