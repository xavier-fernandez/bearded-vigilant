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
