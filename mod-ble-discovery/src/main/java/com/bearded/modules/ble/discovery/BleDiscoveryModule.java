package com.bearded.modules.ble.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.modules.AbstractCloudModule;
import com.bearded.modules.ble.discovery.persistence.BleDiscoveryDatabaseFacade;
import com.bearded.modules.ble.discovery.persistence.cloud.BluetoothDiscoveryCloudUploader;
import com.google.gson.JsonObject;
import com.sensirion.libble.BleManager;
import com.sensirion.libble.devices.BleDevice;
import com.sensirion.libble.listeners.devices.DeviceStateListener;
import com.sensirion.libble.listeners.devices.ScanListener;

import org.joda.time.DateTime;

@SuppressWarnings("unused")
public class BleDiscoveryModule extends AbstractCloudModule implements ScanListener, DeviceStateListener {

    @NonNull
    private static final String TAG = BleDiscoveryModule.class.getSimpleName();
    private static final int BLE_DISCOVERY_MODULE_VERSION = 1;
    private static final int DATA_BIN_TIME_MS = 1000; // 1 SECOND
    @NonNull
    private final BleDiscoveryDatabaseFacade mDatabaseFacade;
    @NonNull
    private final BluetoothDiscoveryCloudUploader mCloudUploader;
    private byte mConsecutiveTimeouts = 0;
    @Nullable
    private DateTime mLastDataReceived;
    @Nullable
    private DateTime mLastDataSentToCloud;

    public BleDiscoveryModule(@NonNull Context context) {
        super(context);
        mDatabaseFacade = new BleDiscoveryDatabaseFacade(context, DATA_BIN_TIME_MS);
        mCloudUploader = new BluetoothDiscoveryCloudUploader();
        BleManager.getInstance().registerNotificationListener(this);
        BleManager.getInstance().startScanning();
        Log.i(TAG, "BleDiscoveryModule -> Discovery Module have been initialized.");
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
        return BLE_DISCOVERY_MODULE_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModuleEnabled() {
        return BleManager.getInstance().isBluetoothEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastSensorDataReceived() {
        return mLastDataReceived;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastCloudUploadTime() {
        return mLastDataSentToCloud;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushCloudDataToTheCloud() {
        final JsonObject jsonObject = mDatabaseFacade.getBleDiscoveryDataJson(getDeviceMetadataJson());
        if (jsonObject == null) {
            Log.w(TAG, "pushCloudDataToTheCloud -> No data to upload.");
        } else if (getLastSensorDataReceived() == null) {
            Log.w(TAG, "pushCloudDataToCloud -> No data have been received yet.");
            BleManager.getInstance().registerNotificationListener(this);
        } else {
            Log.d(TAG, "pushCloudDataToCloud -> Uploading cloud data.");
            mCloudUploader.uploadBluetoothDiscoveryData(jsonObject.toString(), this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanStateChanged(boolean isScanEnabled) {
        if (!isScanEnabled) {
            BleManager.getInstance().startScanning();
            Log.d(TAG, "onScanStateChanged -> Scan disabled, ");
            for (final BleDevice device : BleManager.getInstance().getDiscoveredBleDevices()) {
                this.onDeviceDiscovered(device);
            }
            for (final BleDevice device : BleManager.getInstance().getConnectedBleDevices()) {
                this.onDeviceDiscovered(device);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceConnected(@NonNull BleDevice device) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDisconnected(@NonNull BleDevice device) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceDiscovered(@NonNull BleDevice device) {
        Log.i(TAG, String.format("onDeviceDiscovered -> Discovered device %s with RSSI %d.", device.getAddress(), device.getRSSI()));
        mDatabaseFacade.insertBleEvent(device);
        mLastDataReceived = DateTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeviceAllServicesDiscovered(@NonNull BleDevice device) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadCompleted(int code) {
        Log.d(TAG, String.format("onUploadCompleted -> Upload completed with code: %d", code));
        mDatabaseFacade.purgeAllUploadedBleEvents();
        mLastDataSentToCloud = DateTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUploadFailure(@Nullable String message) {
        Log.d(TAG, String.format("onUploadFailure with message: %s", message));
        if (message != null && message.startsWith("timeout")) {
            synchronized (this) {
                if (mConsecutiveTimeouts++ > 3) {
                    mConsecutiveTimeouts = 0;
                    Log.w(TAG, "onUploadFailure -> Purging database, file is to big for sending it completely to the cloud.");
                    mDatabaseFacade.purgeAllUploadedBleEvents();
                }
            }
        }
    }
}