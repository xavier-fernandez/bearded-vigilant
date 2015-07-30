package com.bearded.common.persistance;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

/**
 * The objects implementing this interface must have a method for parsing its data into a JSON
 * object. This is useful for sending the data to the Cloud.
 */
public interface ParseableJson {

    /**
     * Converts the object into a JSON object. If the object have a connection with another
     * {@link ParseableJson} object, it will do cascade calls on the object relationships.
     *
     * @return {@link com.google.gson.JsonObject} with the relevant object information.
     */
    @NonNull
    JsonObject toJsonObject();
}