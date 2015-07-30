package com.bearded.modules.sensor.persistence.cloud;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Interface defining the API for sending the internal sensor data to the Cloud.
 */
interface SensorCloudApi {

    /**
     * Uploads a bunch of internal sensor data to the cloud.
     *
     * @param jsonString with the JSON {@link String} that is going to be sent to the cloud.
     */
    @FormUrlEncoded
    @POST("/api/vigilant/v1/vigilant-sensor-data")
    void uploadSensorData(@Field("data") String jsonString, Callback<Integer> callback);
}
