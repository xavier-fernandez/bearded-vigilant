package com.bearded.modules.ble.discovery.persistence.cloud;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Interface defining the API for sending the bluetooth discovery data to the Cloud.
 */
interface BluetoothDiscoveryAPI {

    /**
     * Uploads a bunch of discovery bluetooth data to the cloud.
     *
     * @param jsonString with the JSON {@link String} that is going to be sent to the cloud.
     */
    @FormUrlEncoded
    @POST("/api/vigilant/v1/ble/ble-discovery-event")
    void uploadBluetoothDiscoveryData(@Field("data") String jsonString, Callback<Integer> callback);
}
