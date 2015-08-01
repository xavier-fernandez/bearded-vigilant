package com.bearded.modules.sensor.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.domain.SensorEntity;
import com.sensirion.libble.BleManager;
import com.sensirion.libble.devices.BleDevice;
import com.sensirion.libble.devices.KnownDevices;
import com.sensirion.libble.listeners.services.HumidityListener;
import com.sensirion.libble.utils.HumidityUnit;

import org.joda.time.DateTime;

/**
 * Class instantiation is done with refraction in {@see com.bearded.vigilant.ModuleManager}
 */
@SuppressWarnings("unused")
public class BleRelativeHumidityModule extends AbstractBleSensorModule implements HumidityListener {

    @NonNull
    private static final String TAG = BleRelativeHumidityModule.class.getSimpleName();

    private static final int DATA_BIN_TIME_MS = 4 * 1000; // 4 SECONDS

    private static final int BLE_HUMIDITY_MODULE_VERSION_NUMBER = 1;
    private static final long ONE_MINUTE_MS = 60l * 1000l;
    private static long mLastDeviceConnectionPetitionTimestamp = 0;
    @NonNull
    private final Context mApplicationContext;

    @Nullable
    private DateTime mLastSensorValueReceivedTime;

    public BleRelativeHumidityModule(@NonNull Context context) {
        super(context, SensorType.RELATIVE_HUMIDITY, DATA_BIN_TIME_MS);
        mApplicationContext = context.getApplicationContext();
    }

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
        return BLE_HUMIDITY_MODULE_VERSION_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return mLastSensorValueReceivedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewHumidity(@NonNull BleDevice device,
                              float humidity,
                              @NonNull String sensorName,
                              @NonNull HumidityUnit unit) {
        if (getDatabaseFacade() != null) {
            Log.d(TAG, String.format("onNewHumidity -> Received humidity %f from sensor %s from the device %s.", humidity, sensorName, device.getAddress()));
            final SensorEntity bleSensor = new SensorEntity();
            bleSensor.setSensorAddress(device.getAddress());
            final String sensorAddress = device.getAddress();
            final String sensorUnit = unit.name();
            getDatabaseFacade().insertSensorReading(humidity, sensorAddress, SensorType.RELATIVE_HUMIDITY, sensorUnit, sensorName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewHistoricalHumidity(@NonNull BleDevice device,
                                        float relativeHumidity,
                                        long timestampMilliseconds,
                                        @NonNull String sensorName,
                                        @NonNull HumidityUnit unit) {
        // TODO: Implement
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceConnected(@NonNull BleDevice device) {
        device.registerDeviceListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDisconnected(@NonNull BleDevice device) {
        // TODO: Implement
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDiscovered(@NonNull BleDevice device) {
        if (KnownDevices.TEMPERATURE_GADGETS.getAdvertisedNames().contains(device.getAdvertisedName())) {
            if (!device.isConnected()) {
                synchronized (this) {
                    if (System.currentTimeMillis() - ONE_MINUTE_MS > mLastDeviceConnectionPetitionTimestamp) {
                        mLastDeviceConnectionPetitionTimestamp = System.currentTimeMillis();
                        Log.d(TAG, String.format("onDeviceDiscovered -> Connecting to device %s with address %s.", device.getAdvertisedName(), device.getAddress()));
                        device.connect(mApplicationContext);
                        device.setAllNotificationsEnabled(true);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceAllServicesDiscovered(@NonNull BleDevice device) {
        Log.d(TAG, String.format("onDeviceAllServicesDiscovered -> Device %s has all its services available.  ", device.getAddress()));
        device.registerDeviceListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanStateChanged(boolean isScanEnabled) {
        if (!isScanEnabled) {
            BleManager.getInstance().startScanning();
        }
    }
}
