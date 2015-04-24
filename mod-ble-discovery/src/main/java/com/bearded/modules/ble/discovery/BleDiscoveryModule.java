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

package com.bearded.modules.ble.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.common.modules.Module;

import org.joda.time.DateTime;

public class BleDiscoveryModule implements Module {

    private static final String TAG = BleDiscoveryModule.class.getSimpleName();
    private static final int BLE_DISCOVERY_MODULE_VERSION = 1;

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
        return BLE_DISCOVERY_MODULE_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModuleEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastCloudUploadTime() {
        return null;
    }
}
