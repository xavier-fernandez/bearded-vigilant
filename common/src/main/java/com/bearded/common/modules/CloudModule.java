package com.bearded.common.modules;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

public interface CloudModule extends Module {

    /**
     * Obtains the time of the last successful cloud upload.
     *
     * @return {@link org.joda.time.DateTime} of the last cloud upload, if available.
     */
    @Nullable
    DateTime getLastCloudUploadTime();

    /**
     * Push the stored data the the cloud.
     */
    void pushCloudDataToTheCloud();
}
