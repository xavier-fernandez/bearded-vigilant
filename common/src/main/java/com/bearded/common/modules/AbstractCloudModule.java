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

package com.bearded.common.modules;

import android.support.annotation.Nullable;

import com.bearded.common.cloud.UploadStateListener;

import org.joda.time.DateTime;

/**
 * Base class for all the modules that wants to push automatically the data for cloud upload.
 */
public abstract class AbstractCloudModule implements CloudModule, UploadStateListener {

    private static final long DEFAULT_WAITING_TIME_BETWEEN_CLOUD_UPLOADS = 15 * 1000; // 15 seconds

    private final long mWaitingTimeBetweenCloudUploadsMs;
    private DateTime mLastCloudUploadTime;

    protected AbstractCloudModule() {
        mWaitingTimeBetweenCloudUploadsMs = DEFAULT_WAITING_TIME_BETWEEN_CLOUD_UPLOADS;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public DateTime getLastCloudUploadTime() {
        return mLastCloudUploadTime;
    }
}