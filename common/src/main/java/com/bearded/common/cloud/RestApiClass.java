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

package com.bearded.common.cloud;

import android.support.annotation.NonNull;
import android.util.Log;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public abstract class RestApiClass {

    private static final String TAG = RestApiClass.class.getSimpleName();

    @NonNull
    private static final String BASE_URL = "http://46.101.187.73";
    private static final short PORT = 8080;
    @NonNull
    private static final String URL = BASE_URL + ":" + PORT;

    @NonNull
    private final RestAdapter mRestAdapter;

    protected RestApiClass() {
        Log.i(TAG, String.format("RestClient -> Initializing class %s for root -> %s", TAG, URL));
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    /**
     * Gets the project REST adapter.
     *
     * @return {@link RestAdapter}.
     */
    @NonNull
    protected RestAdapter getRestAdapter() {
        return mRestAdapter;
    }
}