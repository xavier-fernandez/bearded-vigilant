package com.bearded.modules.ble.discovery.domain;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.bearded.modules.ble.discovery.domain.BleEventEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BleEvent.
*/
public class BleEventEntityDao extends AbstractDao<BleEventEntity, Long> {

    public static final String TABLENAME = "BleEvent";

    /**
     * Properties of entity BleEventEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property BleDevice = new Property(1, Long.class, "bleDevice", false, "BLE_DEVICE");
        public final static Property EventSeries = new Property(2, Long.class, "eventSeries", false, "EVENT_SERIES");
        public final static Property StartTimestamp = new Property(3, java.util.Date.class, "startTimestamp", false, "START_TIMESTAMP");
        public final static Property EndTimestamp = new Property(4, java.util.Date.class, "endTimestamp", false, "END_TIMESTAMP");
    };

    private DaoSession daoSession;


    public BleEventEntityDao(DaoConfig config) {
        super(config);
    }
    
    public BleEventEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BleEvent' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'BLE_DEVICE' INTEGER," + // 1: bleDevice
                "'EVENT_SERIES' INTEGER," + // 2: eventSeries
                "'START_TIMESTAMP' INTEGER NOT NULL ," + // 3: startTimestamp
                "'END_TIMESTAMP' INTEGER);"); // 4: endTimestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BleEvent'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BleEventEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bleDevice = entity.getBleDevice();
        if (bleDevice != null) {
            stmt.bindLong(2, bleDevice);
        }
 
        Long eventSeries = entity.getEventSeries();
        if (eventSeries != null) {
            stmt.bindLong(3, eventSeries);
        }
        stmt.bindLong(4, entity.getStartTimestamp().getTime());
 
        java.util.Date endTimestamp = entity.getEndTimestamp();
        if (endTimestamp != null) {
            stmt.bindLong(5, endTimestamp.getTime());
        }
    }

    @Override
    protected void attachEntity(BleEventEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BleEventEntity readEntity(Cursor cursor, int offset) {
        BleEventEntity entity = new BleEventEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // bleDevice
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // eventSeries
            new java.util.Date(cursor.getLong(offset + 3)), // startTimestamp
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)) // endTimestamp
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BleEventEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBleDevice(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setEventSeries(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setStartTimestamp(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setEndTimestamp(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BleEventEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BleEventEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getBleDeviceEntityDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getBleEventSeriesEntityDao().getAllColumns());
            builder.append(" FROM BleEvent T");
            builder.append(" LEFT JOIN BleDevice T0 ON T.'BLE_DEVICE'=T0.'_id'");
            builder.append(" LEFT JOIN BleEventSeries T1 ON T.'EVENT_SERIES'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected BleEventEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        BleEventEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        BleDeviceEntity bleDeviceEntity = loadCurrentOther(daoSession.getBleDeviceEntityDao(), cursor, offset);
        entity.setBleDeviceEntity(bleDeviceEntity);
        offset += daoSession.getBleDeviceEntityDao().getAllColumns().length;

        BleEventSeriesEntity bleEventSeriesEntity = loadCurrentOther(daoSession.getBleEventSeriesEntityDao(), cursor, offset);
        entity.setBleEventSeriesEntity(bleEventSeriesEntity);

        return entity;    
    }

    public BleEventEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<BleEventEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<BleEventEntity> list = new ArrayList<BleEventEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<BleEventEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<BleEventEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
