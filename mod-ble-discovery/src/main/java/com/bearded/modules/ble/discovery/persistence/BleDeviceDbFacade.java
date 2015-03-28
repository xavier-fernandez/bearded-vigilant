package com.bearded.modules.ble.discovery.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.modules.ble.discovery.domain.BleDevice;
import com.bearded.modules.ble.discovery.persistence.dao.BleDeviceDao;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

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
@EBean(scope = EBean.Scope.Singleton)
public class BleDeviceDbFacade {

    private final Map<String, BleDevice> mKnownBleDevices = Collections.synchronizedMap(new HashMap<String, BleDevice>());
    @Bean
    DiscoveryDatabaseFacade mDatabaseFacade;

    /**
     * Returns a known BleDevice from the database. It will store the known BleDevice in a proxy.
     *
     * @param deviceAddress of the BleDevice
     * @param advertiseName of the BleDevice
     * @return {@link com.bearded.modules.ble.discovery.domain.BleDevice} in case the device is already on the database.
     */
    @Nullable
    public BleDevice readBleDevice(@NonNull final String deviceAddress, @Nullable final String advertiseName) {
        if (mKnownBleDevices.containsKey(deviceAddress.trim())) {
            return mKnownBleDevices.get(deviceAddress);
        }
        final DaoSession session = mDatabaseFacade.getReadableSession(false);
        final BleDeviceDao dao = session.getBleDeviceDao();
        final QueryBuilder<BleDevice> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleDeviceDao.Properties.DeviceAddress.eq(deviceAddress.trim()));
        if (advertiseName != null) {
            queryBuilder.where(BleDeviceDao.Properties.AdvertiseName.eq(deviceAddress.trim()));
        }
        final BleDevice device = queryBuilder.uniqueOrThrow();
        mKnownBleDevices.put(device.getDeviceAddress(), device);
        return device;
    }
}