package com.bearded.modules.ble.discovery.persistence;

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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bearded.modules.ble.discovery.persistence.dao.DaoMaster;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for obtaining database sessions.
 */
class DiscoveryDatabaseHandler {

    private static final String TAG = DiscoveryDatabaseHandler.class.getSimpleName();
    private static final String DATABASE_NAME = DiscoveryDatabaseHandler.class.getCanonicalName();

    private static DiscoveryDatabaseHandler mInstance;
    private final Context mApplicationContext;
    @Nullable
    private SQLiteDatabase mDatabase = null;
    private boolean mIsReadOnly;
    @Nullable
    private DaoSession mSession = null;

    private DiscoveryDatabaseHandler(@NotNull final Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    static synchronized DiscoveryDatabaseHandler getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(String.format("%s: getInstance() -> init() must be called before trying to obtain the class instance.", TAG));
        }
        return mInstance;
    }

    static void init(@NotNull final Context context) {
        new DiscoveryDatabaseHandler(context);
    }

    @NotNull
    public DaoSession getReadableSession(final boolean newSession) {
        return getSession(newSession, true);
    }

    @NotNull
    public DaoSession getWriteableSession(final boolean newSession) {
        return getSession(newSession, false);
    }

    @NotNull
    private DaoSession getSession(final boolean newSession, final boolean readOnly) {
        if (newSession) {
            return getMaster(readOnly).newSession();
        }
        if (mSession == null) {
            mSession = getMaster(readOnly).newSession();
        }
        return mSession;
    }

    @NotNull
    private synchronized DaoMaster getMaster(final boolean readOnly) {
        if (mDatabase == null || mIsReadOnly != readOnly) {
            try {
                final DaoMaster.OpenHelper databaseHelper = new DatabaseOpenHelper();
                if (readOnly) {
                    mDatabase = databaseHelper.getReadableDatabase();
                } else {
                    mDatabase = databaseHelper.getWritableDatabase();
                }
                mIsReadOnly = readOnly;
            } catch (final Exception e) {
                Log.e(TAG, String.format("getWritableDatabase -> The following exception was thrown when opening the database %s -> ", DATABASE_NAME), e);
            } catch (final Error err) {
                Log.e(TAG, String.format("getWritableDatabase -> The following error was thrown when opening the database %s -> ", DATABASE_NAME), err);
            }
        }
        return new DaoMaster(mDatabase);
    }

    private class DatabaseOpenHelper extends DaoMaster.OpenHelper {
        public DatabaseOpenHelper() {
            super(mApplicationContext, DATABASE_NAME, null);
        }

        @Override
        public void onUpgrade(@NotNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.d(TAG, String.format("onUpgrade -> Database %s upgraded from version %d to version %d.", DATABASE_NAME, oldVersion, newVersion));
        }
    }
}