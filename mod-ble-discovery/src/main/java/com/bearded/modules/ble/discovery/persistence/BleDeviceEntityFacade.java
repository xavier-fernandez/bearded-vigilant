package com.bearded.modules.ble.discovery.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;
import com.bearded.modules.ble.discovery.persistence.dao.BleDeviceEntityDao;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

class BleDeviceEntityFacade {

    private final Map<String, BleDeviceEntity> mKnownBleDevices =
            Collections.synchronizedMap(new HashMap<String, BleDeviceEntity>());

    /**
     * Returns a known BleDevice from the database, or from a known {@link BleDeviceEntity} proxy.
     *
     * @param session                    needed to create and/or retrieve the {@link BleDeviceEntity} from the database.
     * @param deviceAddress              of the {@link BleDeviceEntity}
     * @param advertiseName              of the {@link BleDeviceEntity}
     * @param isEdrOrBrDevice            if the device accepts classic bluetooth connections
     * @param isLowEnergyBluetoothDevice if the device accepts low energy Bluetooth connections.
     * @return {@link BleDeviceEntity} in case the device is already on the database.
     */
    @NonNull
    BleDeviceEntity getBleDeviceEntity(@NonNull final DaoSession session,
                                       @NonNull final String deviceAddress,
                                       @Nullable final String advertiseName,
                                       @Nullable final Boolean isEdrOrBrDevice,
                                       @Nullable final Boolean isLowEnergyBluetoothDevice) {
        if (mKnownBleDevices.containsKey(deviceAddress.trim())) {
            return mKnownBleDevices.get(deviceAddress);
        }
        final BleDeviceEntityDao dao = session.getBleDeviceEntityDao();
        final QueryBuilder<BleDeviceEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(BleDeviceEntityDao.Properties.DeviceAddress.eq(deviceAddress.trim()));
        final List<BleDeviceEntity> deviceList = queryBuilder.list();
        BleDeviceEntity device = (deviceList.isEmpty()) ? null : deviceList.get(0);
        if (device == null) {
            device = new BleDeviceEntity();
            device.setDeviceAddress(deviceAddress);
            device.setAdvertiseName(advertiseName);
            device.setIsEdrOrBr(isEdrOrBrDevice);
            device.setIsLowEnergy(isLowEnergyBluetoothDevice);
            session.insert(device);
        }
        mKnownBleDevices.put(device.getDeviceAddress(), device);
        return device;
    }

    /**
     * Returns a list with all the {@link BleDeviceEntity} stored in the database.
     *
     * @param session needed to create and/or retrieve all {@link BleDeviceEntity} from the database.
     * @return {@link java.util.List} with all the {@link BleDeviceEntity}.
     */
    @NonNull
    List<BleDeviceEntity> getAllBleDevices(@NonNull final DaoSession session) {
        final BleDeviceEntityDao dao = session.getBleDeviceEntityDao();
        final QueryBuilder<BleDeviceEntity> queryBuilder = dao.queryBuilder();
        return queryBuilder.listLazy();
    }
}