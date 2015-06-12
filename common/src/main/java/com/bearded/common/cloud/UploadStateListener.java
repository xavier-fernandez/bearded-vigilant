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

public interface UploadStateListener {

    /**
     * Notify a listener a successful cloud upload.
     *
     * @param code with the download state.
     */
    void onUploadCompleted(final int code);

    /**
     * Notify a listener of a failed cloud upload.
     *
     * @param errorMessage describing the error when trying to upload data to the cloud.
     */
    void onUploadFailure(@NonNull final String errorMessage);
}