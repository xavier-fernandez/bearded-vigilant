package com.sensirion.libble.devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sensirion.libble.listeners.NotificationListener;
import com.sensirion.libble.services.AbstractBleService;
import com.sensirion.libble.services.AbstractHistoryService;
import com.sensirion.libble.services.BleService;
import com.sensirion.libble.services.BleServiceFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a remote piece of Hardware that can have 1-N {@link com.sensirion.libble.services.AbstractBleService}
 */

public class Peripheral implements BleDevice, Comparable<Peripheral> {

    // Class TAG for debugging.
    private static final String TAG = Peripheral.class.getSimpleName();

    // Force reading attributes.
    private static final byte INTERVAL_BETWEEN_CHECKS_MILLISECONDS = 51; // Every connection interval.
    private static final short DEFAULT_TIMEOUT_BETWEEN_REQUEST_MILLISECONDS = 205; // Every 4 connection intervals.
    private static final short DEFAULT_NUMBER_FORCE_REQUEST = 5;
    @NonNull
    private final Queue<Object> mLastActionUsedQueue = new LinkedBlockingQueue<>();

    //Peripheral attributes
    @Nullable
    private final String mAdvertisedName;
    @NonNull
    private final BlePeripheralService mPeripheralService;
    @NonNull
    private final BluetoothDevice mBluetoothDevice;
    @NonNull
    private final String mAddress;

    //Listener list
    @NonNull
    private final Set<NotificationListener> mNotificationListeners = Collections.synchronizedSet(new HashSet<NotificationListener>());

    //Services List
    @NonNull
    private final Set<BleService> mServices = Collections.synchronizedSet(new HashSet<BleService>());
    private volatile boolean mForceOperationRunning = false;
    private boolean mIsConnected = false;
    private int mRSSI;

    //Gathering controller.
    @Nullable
    private BluetoothGatt mBluetoothGatt;

    private final BleStackProtector mBleStackProtector = new BleStackProtector() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onConnectionStateChange(@NonNull BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            final String address = gatt.getDevice().getAddress();

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.d(TAG, "onConnectionStateChange() -> BluetoothProfile.STATE_CONNECTING: " + address);
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i(TAG, "onConnectionStateChange() -> connected to GATT server: " + address);
                    mIsConnected = true;
                    mBluetoothGatt = gatt;
                    Log.d(TAG, "onConnectionStateChange() -> mBluetoothGatt.discoverServices(): " + mBluetoothGatt.discoverServices());
                    mPeripheralService.onPeripheralConnectionChanged(Peripheral.this);
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.d(TAG, "onConnectionStateChange() -> BluetoothProfile.STATE_DISCONNECTING: " + address);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i(TAG, "onConnectionStateChange() -> disconnected from GATT server: " + address);
                    mIsConnected = false;
                    mPeripheralService.onPeripheralConnectionChanged(Peripheral.this);
                    break;
                default:
                    throw new IllegalStateException("onConnectionStateChange() -> state not implemented: " + newState);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt,
                                         @NonNull BluetoothGattCharacteristic characteristic,
                                         int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (final BleService service : mServices) {
                    service.onCharacteristicUpdate(characteristic);
                }
                if (mForceOperationRunning) {
                    mLastActionUsedQueue.add(characteristic);
                }
            } else {
                Log.w(TAG, String.format("onCharacteristicRead -> Characteristic %s failed with the following status: %d", characteristic.getUuid(), status));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (mBluetoothGatt == null) {
                Log.e(TAG, "onCharacteristicChanged -> Bluetooth gatt is not initialized yet.");
                return;
            }
            for (final BleService service : mServices) {
                service.onCharacteristicUpdate(characteristic);
            }
            mBleStackProtector.execute(mBluetoothGatt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCharacteristicWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, String.format("onCharacteristicWrite -> Received Characteristic %s with status %d", characteristic.getUuid(), status));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (final BleService service : mServices) {
                    service.onCharacteristicWrite(characteristic);
                }
                if (mForceOperationRunning) {
                    mLastActionUsedQueue.add(characteristic);
                }
            } else {
                Log.e(TAG, String.format("onCharacteristicWrite -> The device %s was unable to write in the characteristic %s.", getAddress(), characteristic.getUuid()));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServicesDiscovered(@NonNull BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final List<BluetoothGattService> discoveredServices = gatt.getServices();
                Log.w(TAG, String.format("onServicesDiscovered -> Discovered %d services in the device %s.", discoveredServices.size(), getAddress()));
                for (final BluetoothGattService service : discoveredServices) {
                    final BleService knownService = BleServiceFactory.getInstance().createServiceFor(Peripheral.this, service);
                    if (knownService != null) {
                        mServices.add(knownService);
                        Log.d(TAG, String.format("onServiceDiscovered -> Added service %s to the service list.", knownService));
                    }
                }
                for (final NotificationListener listener : mNotificationListeners) {
                    registerDeviceListener(listener);
                }
                mPeripheralService.onPeripheralServiceDiscovery(Peripheral.this);
            } else {
                Log.w(TAG, String.format("onServicesDiscovered -> Failed with status: %d", status));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mForceOperationRunning) {
                    mLastActionUsedQueue.add(descriptor);
                }
                Log.i(TAG, String.format("onDescriptorRead -> Descriptor %s was read successfully.", descriptor.getUuid()));
                for (final BleService service : mServices) {
                    service.onDescriptorRead(descriptor);
                }
            } else {
                Log.i(TAG, String.format("onDescriptorRead -> Descriptor %s failed with status %d.", descriptor.getUuid(), status));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDescriptorWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, String.format("onDescriptorWrite -> Descriptor %s was written successfully.", descriptor.getUuid()));
                if (mForceOperationRunning) {
                    mLastActionUsedQueue.add(descriptor);
                }
                for (final BleService service : mServices) {
                    service.onDescriptorWrite(descriptor);
                }
            } else {
                Log.i(TAG, String.format("onDescriptorWrite -> Descriptor %s failed with status %d.", descriptor.getUuid(), status));
            }
        }
    };

    public Peripheral(@NonNull BlePeripheralService parent, @NonNull BluetoothDevice bluetoothDevice, int rssi) {
        mPeripheralService = parent;
        mBluetoothDevice = bluetoothDevice;
        mBluetoothGatt = null;
        mAddress = bluetoothDevice.getAddress();
        if (bluetoothDevice.getName() == null) {
            Log.w(TAG, "Constructor -> The incoming device does not have a valid advertise name.");
            mAdvertisedName = null;
        } else {
            mAdvertisedName = bluetoothDevice.getName().trim();
        }
        mRSSI = rssi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(@NonNull Context context) {
        // We want to directly connect to the device, so we are setting the autoConnect parameter to false.
        mBluetoothDevice.connectGatt(context, false, mBleStackProtector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reconnect() {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "reconnect -> Bluetooth gatt it's not connected.");
            return false;
        }
        return mBluetoothGatt.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "disconnect -> Bluetooth gatt was already disconnected.");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getAddress() {
        return mAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRSSI() {
        return mRSSI;
    }

    /**
     * Sets the signal strength of the device towards the external BleDevice.
     *
     * @param RSSI with the signal strength.
     */
    public void setRSSI(final int RSSI) {
        mRSSI = RSSI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdvertisedName() {
        return mAdvertisedName;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    public DeviceBluetoothType getBluetoothType() {
        final int deviceClassId = mBluetoothDevice.getBluetoothClass().getDeviceClass();
        return DeviceBluetoothType.getDeviceBluetoothDeviceTypeFromId(deviceClassId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return mIsConnected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractBleService> T getDeviceService(@NonNull Class<T> type) {
        for (final BleService service : mServices) {
            if (service.getClass().equals(type)) {
                return (T) service;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BleService getDeviceService(@NonNull String serviceName) {
        for (final BleService service : mServices) {
            if (service.isExplicitService(serviceName)) {
                return service;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Iterable<BleService> getDiscoveredServices() {
        final List<BleService> discoveredBleServices = new LinkedList<>();
        for (final BleService service : mServices) {
            discoveredBleServices.add(service);
        }
        return discoveredBleServices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Iterable<String> getDiscoveredServicesNames() {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "getDiscoveredServiceNames() -> Bluetooth gatt is not initialized yet.");
            return new LinkedList<>();
        }
        final Set<String> discoveredServices = new HashSet<>();
        for (final BleService service : mServices) {
            discoveredServices.add(String.format("%s %s", service.getClass().getSimpleName(), service.getUUIDString()));
        }
        return discoveredServices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractHistoryService getHistoryService() {
        for (final BleService service : mServices) {
            if (service instanceof AbstractHistoryService) {
                return (AbstractHistoryService) service;
            }
        }
        return null;
    }

    /**
     * Close the corresponding BluetoothGatt so that resources are cleaned up properly.
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicUpdate(android.bluetooth.BluetoothGatt,
     * android.bluetooth.BluetoothGattCharacteristic, int)} callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(@NonNull BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "readCharacteristic -> Bluetooth gatt is not initialized yet.");
            return;
        }
        mBleStackProtector.addReadCharacteristic(characteristic);
        mBleStackProtector.execute(mBluetoothGatt);
    }

    /**
     * Method that send once a characteristic to the device, informing the user if the characteristic was retrieved.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param characteristic that is going to be readed. Cannot be <code>null</code>
     * @param maxWaitingTime acceptable time without receiving an answer from the peripheral.
     * @return <code>true</code> if the characteristic was read - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean readCharacteristicWithConfirmation(@NonNull BluetoothGattCharacteristic characteristic,
                                                      int maxWaitingTime) {
        return forceReadCharacteristic(characteristic, maxWaitingTime, 1);
    }

    /**
     * Convenience method for forcing a characteristic read, in case we want to be sure to read the characteristic.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param characteristic that is going to be readed. Cannot be <code>null</code>
     * @return <code>true</code> if the characteristic was read - <code>false</code> otherwise.
     */
    public boolean forceReadCharacteristic(@NonNull BluetoothGattCharacteristic characteristic) {
        return forceReadCharacteristic(characteristic, DEFAULT_TIMEOUT_BETWEEN_REQUEST_MILLISECONDS, DEFAULT_NUMBER_FORCE_REQUEST);
    }

    /**
     * Convenience method for forcing a characteristic read, in case we want to be sure to read the characteristic.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param characteristic       that is going to be readed. Cannot be <code>null</code>
     * @param timeoutMs            acceptable time without receiving an answer from the peripheral.
     * @param maxNumberConnections maximumNumberOfReads. It has to be a positive number.
     * @return <code>true</code> if the characteristic was read - <code>false</code> otherwise.
     */
    public boolean forceReadCharacteristic(@NonNull BluetoothGattCharacteristic characteristic,
                                           int timeoutMs,
                                           int maxNumberConnections) {
        return forceActionReadOrWrite(characteristic, timeoutMs, maxNumberConnections, true);
    }

    /**
     * Request a write to a given {@code BluetoothGattCharacteristic}. The write
     * operation is done asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicWrite(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)} callback.
     *
     * @param characteristic The characteristic to overwrite.
     */
    public void writeCharacteristic(@NonNull BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "writeCharacteristic -> Bluetooth gatt is not initialized yet.");
            return;
        }
        mBleStackProtector.addWriteCharacteristic(characteristic);
        mBleStackProtector.execute(mBluetoothGatt);
    }

    /**
     * Method that send once a characteristic to the device, informing the user if the characteristic was retrieved.
     * It blocks the UI thread until it receives a response or a timeout is produced.
     *
     * @param characteristic that is going to be updated in the device. Cannot be <code>null</code>
     * @param maxWaitingTime acceptable time without receiving an answer from the peripheral.
     * @return <code>true</code> if the characteristic was read - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean writeCharacteristicWithConfirmation(@NonNull BluetoothGattCharacteristic characteristic, int maxWaitingTime) {
        return forceWriteCharacteristic(characteristic, maxWaitingTime, 1);
    }

    /**
     * Convenience method for forcing a characteristic to write, in case we want to be sure to write the characteristic.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param characteristic that is going to be wrote. Cannot be <code>null</code>
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean forceWriteCharacteristic(@NonNull BluetoothGattCharacteristic characteristic) {
        return forceWriteCharacteristic(characteristic, DEFAULT_TIMEOUT_BETWEEN_REQUEST_MILLISECONDS, DEFAULT_NUMBER_FORCE_REQUEST);
    }

    /**
     * Convenience method for forcing a characteristic to write, in case we want to be sure to write the characteristic.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param characteristic  that is going to be wrote. Cannot be <code>null</code>
     * @param timeoutMs       acceptable time without receiving an answer from the peripheral.
     * @param maxRequestCount maximumNumberOfReads. It has to be a positive number.
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    public boolean forceWriteCharacteristic(@NonNull BluetoothGattCharacteristic characteristic,
                                            int timeoutMs, int maxRequestCount) {
        return forceActionReadOrWrite(characteristic, timeoutMs, maxRequestCount, false);
    }

    /**
     * Reads a {@link android.bluetooth.BluetoothGattDescriptor} in the bluetooth gatherer.
     *
     * @param descriptor the descriptor we want to read.
     */
    public void readDescriptor(@NonNull final BluetoothGattDescriptor descriptor) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "readCharacteristic -> Bluetooth gatt is not initialized yet.");
            return;
        }
        mBleStackProtector.addReadDescriptor(descriptor);
        mBleStackProtector.execute(mBluetoothGatt);
    }

    /**
     * Method that send a read request of a descriptor to device, informing the user if the characteristic was retrieved.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param descriptor that is going to be readed. Cannot be <code>null</code>
     * @param timeoutMs  acceptable time without receiving an answer from the peripheral.
     * @return <code>true</code> if the characteristic was read - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean readDescriptorWithConfirmation(@NonNull BluetoothGattDescriptor descriptor, int timeoutMs) {
        return forceDescriptorRead(descriptor, timeoutMs, 1);
    }

    /**
     * Convenience method for forcing the read of a descriptor.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param descriptor that is going to be written. Cannot be <code>null</code>
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean forceDescriptorRead(@NonNull BluetoothGattDescriptor descriptor) {
        return forceDescriptorRead(descriptor, DEFAULT_TIMEOUT_BETWEEN_REQUEST_MILLISECONDS, DEFAULT_NUMBER_FORCE_REQUEST);
    }

    /**
     * Convenience method for forcing the read of a descriptor.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param descriptor       that is going to be written. Cannot be <code>null</code>
     * @param timeoutMs        acceptable time without receiving an answer from the peripheral.
     * @param maxNumberRequest It needs to be a positive number.
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    public boolean forceDescriptorRead(@NonNull BluetoothGattDescriptor descriptor,
                                       int timeoutMs,
                                       int maxNumberRequest) {
        return forceActionReadOrWrite(descriptor, timeoutMs, maxNumberRequest, true);
    }

    /**
     * Writes a {@link android.bluetooth.BluetoothGattDescriptor} in the bluetooth gatherer.
     *
     * @param descriptor the descriptor we want to write in the device.
     */
    public void writeDescriptor(@NonNull BluetoothGattDescriptor descriptor) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "writeDescriptor -> Bluetooth gatt is not initialized yet.");
            return;
        }
        mBleStackProtector.addWriteDescriptor(descriptor);
        mBleStackProtector.execute(mBluetoothGatt);
    }

    /**
     * Method that send a read request of a descriptor to device, informing the user if the characteristic was retrieved.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param descriptor that is going to be written. Cannot be <code>null</code>
     * @param timeoutMs  acceptable time without receiving an answer from the peripheral.
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    @SuppressWarnings("unused")
    public boolean writeDescriptorWithConfirmation(@NonNull BluetoothGattDescriptor descriptor,
                                                   int timeoutMs) {
        return forceDescriptorWrite(descriptor, timeoutMs, 1);
    }

    /**
     * Convenience method for forcing the write of a descriptor.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param descriptor       that is going to be written. Cannot be <code>null</code>
     * @param timeoutMs        acceptable time without receiving an answer from the peripheral.
     * @param maxNumberRequest It needs to be a positive number.
     * @return <code>true</code> if the characteristic was written - <code>false</code> otherwise.
     */
    public boolean forceDescriptorWrite(@NonNull BluetoothGattDescriptor descriptor,
                                        int timeoutMs,
                                        int maxNumberRequest) {
        return forceActionReadOrWrite(descriptor, timeoutMs, maxNumberRequest, false);
    }

    /**
     * Convenience method for forcing a characteristic read or write, in case we want to be sure to read the characteristic.
     * It blocks the calling thread until it receives a response or a timeout is produced.
     *
     * @param action          that is going to be processed. Needs to be {@link android.bluetooth.BluetoothGattCharacteristic} or a {@link android.bluetooth.BluetoothGattDescriptor}. Cannot be <code>null</code>
     * @param timeoutMs       acceptable time without receiving an answer from the peripheral.
     * @param maxRequestCount maximumNumberOfReads. It has to be a positive number.
     * @return <code>true</code> if the characteristic was processed - <code>false</code> otherwise.
     */
    private synchronized boolean forceActionReadOrWrite(@NonNull Object action,
                                                        int timeoutMs,
                                                        final int maxRequestCount,
                                                        final boolean isReadAction) {
        try {
            mForceOperationRunning = true;
            final long timeNow = System.currentTimeMillis();
            mBleStackProtector.cleanCharacteristicCache();
            int requestCounter = 0;
            while (isConnected()) {
                if (System.currentTimeMillis() - timeoutMs * requestCounter > timeNow) {
                    executeAction(action, isReadAction);
                    requestCounter++;
                }
                if (mLastActionUsedQueue.contains(action)) {
                    return true;
                }
                try {
                    Thread.sleep(INTERVAL_BETWEEN_CHECKS_MILLISECONDS);
                } catch (final InterruptedException ignored) {
                    Log.w(TAG, String.format("forceActionReadOrWrite -> Action %s produced an interruptedException.", action));
                }
                if (requestCounter >= maxRequestCount) {
                    break;
                }
            }
            return false;
        } finally {
            mLastActionUsedQueue.clear();
            mForceOperationRunning = false;
            mBleStackProtector.cleanCharacteristicCache();
        }
    }

    private void executeAction(@NonNull Object action, boolean isReadAction) {
        if (isReadAction) {
            if (action instanceof BluetoothGattCharacteristic) {
                readCharacteristic((BluetoothGattCharacteristic) action);
            } else if (action instanceof BluetoothGattDescriptor) {
                readDescriptor((BluetoothGattDescriptor) action);
            }
        } else {
            if (action instanceof BluetoothGattCharacteristic) {
                writeCharacteristic((BluetoothGattCharacteristic) action);
            } else if (action instanceof BluetoothGattDescriptor) {
                writeDescriptor((BluetoothGattDescriptor) action);
            }
        }
    }

    /**
     * Enables or disables notification on a given characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        if <code>true</code> enable notification - <code>false</code> disable notification.
     */
    public void setCharacteristicNotification(@NonNull BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "readCharacteristic -> Bluetooth gatt is not initialized yet.");
            return;
        }
        mBleStackProtector.addCharacteristicNotification(characteristic, enabled);
        mBleStackProtector.execute(mBluetoothGatt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllNotificationsEnabled(boolean enabled) {
        for (final BleService service : mServices) {
            service.setNotificationsEnabled(enabled);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean registerDeviceListener(@NonNull NotificationListener listener) {
        mNotificationListeners.add(listener);
        boolean validServiceFound = false;
        for (final BleService service : mServices) {
            if (service.registerNotificationListener(listener)) {
                validServiceFound = true;
                service.setNotificationsEnabled(true);
            }
        }
        return validServiceFound;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterDeviceListener(@NonNull NotificationListener listener) {
        for (final BleService service : mServices) {
            service.unregisterNotificationListener(listener);
        }
        mNotificationListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberServices() {
        return mServices.size();
    }

    /**
     * {@inheritDoc}
     */
    public void cleanCharacteristicCache() {
        mBleStackProtector.cleanCharacteristicCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object otherPeripheral) {
        if (otherPeripheral instanceof Peripheral) {
            return mAddress.equals(((Peripheral) otherPeripheral).getAddress());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areAllServicesReady() {
        for (final BleService service : mServices) {
            if (!service.isServiceReady()) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nullable Peripheral anotherPeripheral) {
        if (isConnected()) {
            return 0;
        }
        if (anotherPeripheral == null) {
            Log.e(TAG, "compareTo -> Received a null peripheral.");
            return 0; //A peripheral cannot be compared to a null peripheral.
        }
        return getRSSI() - anotherPeripheral.getRSSI();
    }
}
