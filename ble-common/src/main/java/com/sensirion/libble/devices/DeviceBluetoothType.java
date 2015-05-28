package com.sensirion.libble.devices;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

/**
 * This class represents a Bluetooth device type, distinguished
 */
public enum DeviceBluetoothType {
    // Classic - BR/EDR devices
    DEVICE_TYPE_CLASSIC(BluetoothDevice.DEVICE_TYPE_CLASSIC),
    // Low Energy - LE-only
    DEVICE_TYPE_LE(BluetoothDevice.DEVICE_TYPE_LE),
    // Dual Mode - BR/EDR/LE
    DEVICE_TYPE_DUAL(BluetoothDevice.DEVICE_TYPE_DUAL),
    // Unknown
    DEVICE_TYPE_UNKNOWN(BluetoothDevice.DEVICE_TYPE_UNKNOWN);

    private final int mId;

    DeviceBluetoothType(final int id) {
        mId = id;
    }

    /**
     * Obtains a Device Bluetooth type from a given ID.
     *
     * @param id - must be {@see BluetoothDevice.DEVICE_TYPE_CLASSIC} or {@see BluetoothDevice.DEVICE_TYPE_LE}
     *           or {@see BluetoothDevice.DEVICE_TYPE_DUAL} or {@see BluetoothDevice.DEVICE_TYPE_UNKNOWN}
     * @return the found {@link DeviceBluetoothType} - {@see DEVICE_TYPE_UNKNOWN} if the id is unknown.
     */
    @NonNull
    static DeviceBluetoothType getDeviceBluetoothDeviceTypeFromId(final int id) {
        for (final DeviceBluetoothType deviceBluetoothType : values()) {
            if (id == deviceBluetoothType.mId) {
                return deviceBluetoothType;
            }
        }
        return DEVICE_TYPE_UNKNOWN;
    }

    /**
     * Returns the {@link DeviceBluetoothType} name.
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String toString() {
        return this.name();
    }
}