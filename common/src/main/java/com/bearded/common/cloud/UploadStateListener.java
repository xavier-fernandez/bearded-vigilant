package com.bearded.common.cloud;

import android.support.annotation.Nullable;

/**
 * Interface that notifies to its implementations the upload state callback.
 */
public interface UploadStateListener {

    /**
     * Notify a listener a successful cloud upload.
     *
     * @param code with the download state.
     */
    void onUploadCompleted(int code);

    /**
     * Notify a listener of a failed cloud upload.
     *
     * @param errorMessage describing the error when trying to upload data to the cloud.
     */
    void onUploadFailure(@Nullable String errorMessage);
}