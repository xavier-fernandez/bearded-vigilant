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

import android.util.Log;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public abstract class RestApiClass {

    private static String ROOT = "http://bearded.com";
    private final String TAG = this.getClass().getSimpleName();

    protected RestApiClass() {
        Log.i(TAG, String.format("RestClient -> Initializing class %s for root -> %s", TAG, ROOT));
        setupRestClientApi();
    }

    private void setupRestClientApi() {
        Log.i(TAG, "setupRestClientApi -> Setting the rest client API");
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient())
                .build();
        adapter.setLogLevel(RestAdapter.LogLevel.FULL);
        adapter.create(this.getClass());
    }
}