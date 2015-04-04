package com.bearded.common;


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

import com.google.gson.JsonObject;

/**
 * The objects implementing this interface must have a method for parsing its data into a JSON
 * object. This is useful for sending the data to the Cloud.
 */
public interface ParseableJson {

    /**
     * Converts the object into a JSON object. If the object have a connection with another
     * {@link com.bearded.common.ParseableJson} object, it will call this method do cascade calls
     * to this method from other objects.
     *
     * @return {@link com.google.gson.JsonObject} with the relevant object information.
     */
    JsonObject toJsonObject();
}