package com.bearded.common.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

public abstract class DeviceIdentifierManager {

    @NonNull
    private static final String TAG = DeviceIdentifierManager.class.getSimpleName();
    @NonNull
    private static final String PREFS_FILE = "device_id.xml";
    @NonNull
    private static final String PREFS_DEVICE_ID = "device_id";
    @Nullable
    private static String mDeviceId;

    /**
     * Obtains a unique device identifier.
     * Tries to obtain the ethernet adapter Mac Address, if it is not possible it fallbacks to the
     * Wifi interface mac address, otherwise it will use the Android ID. If none of this is
     * available it will generate a random UUID.
     * @param context needed to obtain and/or store the obtained id to a persisted preference file.
     * @return {@link String} with the preference file.
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    public static synchronized String getDeviceId(@NonNull Context context) {
        if (mDeviceId == null) {
            mDeviceId = obtainDeviceIdentifier(context);
            storeIdIntoPreferences(context, mDeviceId);
        }
        return mDeviceId;
    }

    /**
     * Use Ethernet Mac Address if available, if it is not possible use the wifi mac address.
     * Otherwise use the Android ID unless it's broken, in which case callback on deviceId, unless
     * it's not available, then fallback on a random UUID number.
     *
     * @return {@link String} with the device unique identifier.
     */
    @NonNull
    private static String obtainDeviceIdentifier(@NonNull Context context) {
        final String storedId = getIdFromPreferences(context);
        if (storedId != null) {
            return storedId;
        }
        final String ethernetMacAddress = getEthernetMacAddress();
        if (ethernetMacAddress != null) {
            return ethernetMacAddress;
        }
        final String wifiMacAddress = getWifiMacAddress(context);
        if (wifiMacAddress != null) {
            return wifiMacAddress;
        }
        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (isValidAndroidId(androidId)) {
            return androidId;
        } else {
            final String deviceId =
                    ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            if (deviceId == null) {
                return UUID.randomUUID().toString();
            } else {
                return deviceId;
            }
        }
    }

    /**
     * Obtains an ID from an stored preference file, if available.
     * @param context needed for reading the preference file.
     * @return {@link String} with the device ID, if it is available.
     */
    @Nullable
    private static String getIdFromPreferences(@NonNull Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        return prefs.getString(PREFS_DEVICE_ID, null);
    }

    /**
     * Stores a device ID into a preference file.
     * @param context needed to store the file into the preference file.
     * @param deviceId that will be stored into preferences.
     */
    private static void storeIdIntoPreferences(@NonNull Context context, @NonNull String deviceId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        prefs.edit().putString(PREFS_DEVICE_ID, deviceId).apply();
    }

    /**
     * In addition, if a user upgrades their phone from
     * certain buggy implementations of Android 2.2 to a newer, non-buggy version of Android,
     * the device ID may change. Or, if a user uninstalls your app on a device that has neither
     * a proper Android ID nor a Device ID, this ID may change when the user deletes the
     * application from the system.
     * <p/>
     * Note that if the code falls back on using TelephonyManager.getDeviceId(), the resulting ID
     * will NOT change after a factory reset. Something to be aware of.
     * <p/>
     * Works around a bug in Android 2.2 for many devices when using ANDROID_ID directly.
     *
     * @return <code>true</code> if it is a valid Android id - <code>false</code> otherwise.
     * @see <a href="URL#http://code.google.com/p/android/issues/detail?id=10603">Google Issue</a>
     */
    private static boolean isValidAndroidId(@NonNull String androidId) {
        return !"9774d56d682e549c".equals(androidId);
    }

    /**
     * Tries to obtain a valid Mac Address from an Ethernet connection, if available.
     * @return {@link String} with the mac address. <code>null</code> if it is not available.
     */
    @Nullable
    private static String getEthernetMacAddress(){
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            final StringBuilder fileData = new StringBuilder(1000);
            fileReader = new FileReader("/sys/class/net/eth0/address");
            reader = new BufferedReader(fileReader);
            char[] buf = new char[1024];
            int numRead;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            final String macAddress = fileData.toString().toUpperCase().substring(0, 17);
            Log.i(TAG, String.format("getEthernetMacAddress -> Obtained address: %s.", macAddress));
            return macAddress;
        } catch (IOException e) {
            Log.i(TAG, "getEthernetMacAddress -> The device does not have an ethernet connection.");
            return null;
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ignored){}
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored){}
            }
        }
    }

    /**
     * Tries to obtain a valid Mac Address from a the Wifi peripheral.
     * @param context used for obtaining the system service.
     * @return {@link String} with the mac address. <code>null</code> if it is not available.
     */
    @Nullable
    private static String getWifiMacAddress(@NonNull Context context) {
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            Log.i(TAG, "getWifiMacAddress -> The device does not have a connected WIFI device.");
            return null;
        }
        for (int numTries = 0; !manager.isWifiEnabled() || numTries < 10; numTries++) {
            manager.setWifiEnabled(true);
            try {
                Thread.sleep(150);
            } catch (final InterruptedException ignored) {
            }
        }
        if (manager.isWifiEnabled()) {
            final WifiInfo info = manager.getConnectionInfo();
            final String macAddress = info.getMacAddress().toUpperCase();
            Log.i(TAG, String.format("getWifiMacAddress -> Obtained address %s", macAddress));
            return macAddress;
        }
        Log.d(TAG, "getWifiMacAddress -> It was impossible to obtain a valid Wifi mac address.");
        return null;
    }
}