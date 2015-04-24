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
package com.bearded.modules.sensor.internal.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.modules.sensor.internal.persistence.dao.DaoMaster;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

/**
 * Class used for obtaining database sessions.
 */
class DatabaseConnector {

    private static final String TAG = DatabaseConnector.class.getSimpleName();

    @NonNull
    private final Context mApplicationContext;
    @NonNull
    private final String mDatabaseName;
    @Nullable
    private SQLiteDatabase mDatabase = null;
    @Nullable
    private DaoSession mSession = null;

    public DatabaseConnector(@NonNull final Context context,
                             @NonNull final String databaseName) {
        mApplicationContext = context.getApplicationContext();
        mDatabaseName = databaseName;
    }

    @NonNull
    public DaoSession getSession() {
        if (mSession == null) {
            mSession = getMaster().newSession();
        }
        return mSession;
    }

    @NonNull
    private synchronized DaoMaster getMaster() {
        if (mDatabase == null) {
            try {
                final DaoMaster.OpenHelper databaseHelper = new DatabaseOpenHelper();
                mDatabase = databaseHelper.getWritableDatabase();
            } catch (final Exception e) {
                Log.e(TAG, String.format("getWritableDatabase -> The following exception was thrown when opening the database %s -> ", mDatabaseName), e);
            } catch (final Error err) {
                Log.e(TAG, String.format("getWritableDatabase -> The following error was thrown when opening the database %s -> ", mDatabaseName), err);
            }
        }
        return new DaoMaster(mDatabase);
    }

    private class DatabaseOpenHelper extends DaoMaster.OpenHelper {
        public DatabaseOpenHelper() {
            super(mApplicationContext, mDatabaseName, null);
        }

        @Override
        public void onUpgrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.d(TAG, String.format("onUpgrade -> Database %s upgraded from version %d to version %d.", mDatabaseName, oldVersion, newVersion));
        }
    }
}