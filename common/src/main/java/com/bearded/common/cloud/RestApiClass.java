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