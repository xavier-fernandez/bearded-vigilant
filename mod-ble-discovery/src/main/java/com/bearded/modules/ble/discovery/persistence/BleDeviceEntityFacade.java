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

package com.bearded.modules.ble.discovery.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

public class BleDeviceEntityFacade {

    private final Map<String, BleDeviceEntity> mKnownBleDevices = Collections.synchronizedMap(new HashMap<String, BleDeviceEntity>());

    /**
     * Returns a known BleDevice from the database. It will store the known BleDevice in a proxy.
     *
     * @param deviceAddress of the BleDevice
     * @param advertiseName of the BleDevice
     * @return {@link com.bearded.modules.ble.discovery.domain.BleDeviceEntity} in case the device is already on the database.
     */
    @Nullable
    public BleDeviceEntity readBleDevice(@NonNull final String deviceAddress, @Nullable final String advertiseName) {
        if (mKnownBleDevices.containsKey(deviceAddress.trim())) {
            return mKnownBleDevices.get(deviceAddress);
        }
        final DaoSession session = DiscoveryDatabaseHandler.getInstance().getReadableSession(false);
        final BleDeviceEntityDao dao = session.getBleDeviceEntityDao();
        final QueryBuilder<BleDeviceEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleDeviceEntityDao.Properties.DeviceAddress.eq(deviceAddress.trim()));
        if (advertiseName != null) {
            queryBuilder.where(BleDeviceEntityDao.Properties.AdvertiseName.eq(deviceAddress.trim()));
        }
        final BleDeviceEntity device = queryBuilder.uniqueOrThrow();
        mKnownBleDevices.put(device.getDeviceAddress(), device);
        return device;
    }
}