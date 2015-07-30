package com.bearded.modules.ble.discovery.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bearded.common.annotation.TestOnly;
import com.bearded.modules.ble.discovery.persistence.dao.DaoMaster;
import com.bearded.modules.ble.discovery.persistence.dao.DaoSession;

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

    public DatabaseConnector(@NonNull Context context,
                             @NonNull String databaseName) {
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

    /**
     * Removes all database content. Only used in application testing.
     */
    @TestOnly
    public void cleanDatabase() {
        DaoMaster.dropAllTables(getSession().getDatabase(), false);
        DaoMaster.createAllTables(getSession().getDatabase(), false);
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