package com.bearded.modules.ble.discovery.persistence.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.bearded.modules.ble.discovery.domain.BleDeviceEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table BleDevice.
 */
public class BleDeviceEntityDao extends AbstractDao<BleDeviceEntity, Long> {

    public static final String TABLENAME = "BleDevice";

    public BleDeviceEntityDao(DaoConfig config) {
        super(config);
    }

    ;


    public BleDeviceEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'BleDevice' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'DEVICE_ADDRESS' TEXT NOT NULL ," + // 1: deviceAddress
                "'ADVERTISE_NAME' TEXT," + // 2: advertiseName
                "'IS_EDR_OR_BR' INTEGER," + // 3: isEdrOrBr
                "'IS_LOW_ENERGY' INTEGER);"); // 4: isLowEnergy
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "ble_device_address_index ON BleDevice" +
                " (DEVICE_ADDRESS);");
        db.execSQL("CREATE INDEX " + constraint + "ble_device_advertise_name_index ON BleDevice" +
                " (ADVERTISE_NAME);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BleDevice'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, BleDeviceEntity entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDeviceAddress());

        String advertiseName = entity.getAdvertiseName();
        if (advertiseName != null) {
            stmt.bindString(3, advertiseName);
        }

        Boolean isEdrOrBr = entity.getIsEdrOrBr();
        if (isEdrOrBr != null) {
            stmt.bindLong(4, isEdrOrBr ? 1l : 0l);
        }

        Boolean isLowEnergy = entity.getIsLowEnergy();
        if (isLowEnergy != null) {
            stmt.bindLong(5, isLowEnergy ? 1l : 0l);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public BleDeviceEntity readEntity(Cursor cursor, int offset) {
        BleDeviceEntity entity = new BleDeviceEntity( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // deviceAddress
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // advertiseName
                cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // isEdrOrBr
                cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0 // isLowEnergy
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, BleDeviceEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceAddress(cursor.getString(offset + 1));
        entity.setAdvertiseName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsEdrOrBr(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setIsLowEnergy(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(BleDeviceEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(BleDeviceEntity entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Properties of entity BleDeviceEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceAddress = new Property(1, String.class, "deviceAddress", false, "DEVICE_ADDRESS");
        public final static Property AdvertiseName = new Property(2, String.class, "advertiseName", false, "ADVERTISE_NAME");
        public final static Property IsEdrOrBr = new Property(3, Boolean.class, "isEdrOrBr", false, "IS_EDR_OR_BR");
        public final static Property IsLowEnergy = new Property(4, Boolean.class, "isLowEnergy", false, "IS_LOW_ENERGY");
    }

}
