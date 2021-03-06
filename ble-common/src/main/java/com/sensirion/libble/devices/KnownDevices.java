package com.sensirion.libble.devices;

import java.util.ArrayList;
import java.util.List;

public enum KnownDevices {

    /**
     * Devices that returns Humidity and Temperature data.
     * Notification interface: {@link com.sensirion.libble.listeners.services.RHTListener}
     */
    RHT_GADGETS {
        @Override
        public List<String> getAdvertisedNames() {
            final List<String> deviceNames = new ArrayList<>();
            final List<String> temperatureGadgets = TEMPERATURE_GADGETS.getAdvertisedNames();
            for (final String humidityGadget : HUMIDITY_GADGETS.getAdvertisedNames()) {
                if (temperatureGadgets.contains(humidityGadget)) {
                    deviceNames.add(humidityGadget);
                }
            }
            return deviceNames;
        }
    },

    /**
     * Devices that returns Temperature data.
     * Notification interface: {@link com.sensirion.libble.listeners.services.TemperatureListener}
     */
    TEMPERATURE_GADGETS {
        @Override
        public List<String> getAdvertisedNames() {
            final List<String> deviceNames = new ArrayList<>(4);
            deviceNames.add("SHTC1 smart gadget"); // Official device name is not capitalized like the following one.
            deviceNames.add("SHT31 Smart Gadget");
            deviceNames.add("Smart Humigadget");
            deviceNames.add("SensorTag");
            return deviceNames;
        }
    },

    /**
     * Devices that returns Humidity data.
     * Notification interface: {@link com.sensirion.libble.listeners.services.HumidityListener}
     */
    HUMIDITY_GADGETS {
        @Override
        public List<String> getAdvertisedNames() {
            final List<String> deviceNames = new ArrayList<>(4);
            deviceNames.add("SHTC1 smart gadget"); // Official device name is not capitalized like the following one.
            deviceNames.add("SHT31 Smart Gadget");
            deviceNames.add("Smart Humigadget");
            deviceNames.add("SensorTag");
            return deviceNames;
        }
    };

    /**
     * Returns a {@link java.util.List} of {@link java.lang.String} of the valid advertised names of the gadget.
     *
     * @return {@link java.util.List} with the advertised names of compatible devices.
     */
    @SuppressWarnings("unused")
    public abstract List<String> getAdvertisedNames();
}