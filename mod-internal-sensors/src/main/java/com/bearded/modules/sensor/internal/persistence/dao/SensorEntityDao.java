package com.bearded.modules.sensor.internal.persistence.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.bearded.modules.sensor.internal.domain.SensorEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table Sensor.
 */
public class SensorEntityDao extends AbstractDao<SensorEntity, Long> {

    public static final String TABLENAME = "Sensor";

    public SensorEntityDao(DaoConfig config) {
        super(config);
    }

    ;


    public SensorEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'Sensor' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'SENSOR_NAME' TEXT NOT NULL ," + // 1: sensorName
                "'SENSOR_TYPE' TEXT NOT NULL ," + // 2: sensorType
                "'SENSOR_UNIT' TEXT NOT NULL ," + // 3: sensorUnit
                "'MINIMUM_DELAY_MICROSECONDS' INTEGER," + // 4: minimumDelayMicroseconds
                "'MAXIMUM_DELAY_MICROSECONDS' INTEGER," + // 5: maximumDelayMicroseconds
                "'FIFO_MAX_EVENT_COUNT' INTEGER," + // 6: fifoMaxEventCount
                "'FIFO_RESERVED_EVENT_COUNT' INTEGER," + // 7: fifoReservedEventCount
                "'MAXIMUM_RANGE' REAL," + // 8: maximumRange
                "'REPORTING_MODE' TEXT," + // 9: reportingMode
                "'POWER_IN_MILLI_AMPERES' REAL," + // 10: powerInMilliAmperes
                "'SENSOR_RESOLUTION' REAL," + // 11: sensorResolution
                "'SENSOR_VENDOR' TEXT," + // 12: sensorVendor
                "'SENSOR_VERSION' INTEGER);"); // 13: sensorVersion
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_Sensor_SENSOR_NAME ON Sensor" +
                " (SENSOR_NAME);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_Sensor_SENSOR_TYPE ON Sensor" +
                " (SENSOR_TYPE);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Sensor'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, SensorEntity entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getSensorName());
        stmt.bindString(3, entity.getSensorType());
        stmt.bindString(4, entity.getSensorUnit());

        Integer minimumDelayMicroseconds = entity.getMinimumDelayMicroseconds();
        if (minimumDelayMicroseconds != null) {
            stmt.bindLong(5, minimumDelayMicroseconds);
        }

        Integer maximumDelayMicroseconds = entity.getMaximumDelayMicroseconds();
        if (maximumDelayMicroseconds != null) {
            stmt.bindLong(6, maximumDelayMicroseconds);
        }

        Integer fifoMaxEventCount = entity.getFifoMaxEventCount();
        if (fifoMaxEventCount != null) {
            stmt.bindLong(7, fifoMaxEventCount);
        }

        Integer fifoReservedEventCount = entity.getFifoReservedEventCount();
        if (fifoReservedEventCount != null) {
            stmt.bindLong(8, fifoReservedEventCount);
        }

        Float maximumRange = entity.getMaximumRange();
        if (maximumRange != null) {
            stmt.bindDouble(9, maximumRange);
        }

        String reportingMode = entity.getReportingMode();
        if (reportingMode != null) {
            stmt.bindString(10, reportingMode);
        }

        Float powerInMilliAmperes = entity.getPowerInMilliAmperes();
        if (powerInMilliAmperes != null) {
            stmt.bindDouble(11, powerInMilliAmperes);
        }

        Float sensorResolution = entity.getSensorResolution();
        if (sensorResolution != null) {
            stmt.bindDouble(12, sensorResolution);
        }

        String sensorVendor = entity.getSensorVendor();
        if (sensorVendor != null) {
            stmt.bindString(13, sensorVendor);
        }

        Integer sensorVersion = entity.getSensorVersion();
        if (sensorVersion != null) {
            stmt.bindLong(14, sensorVersion);
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
    public SensorEntity readEntity(Cursor cursor, int offset) {
        SensorEntity entity = new SensorEntity( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // sensorName
                cursor.getString(offset + 2), // sensorType
                cursor.getString(offset + 3), // sensorUnit
                cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // minimumDelayMicroseconds
                cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // maximumDelayMicroseconds
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // fifoMaxEventCount
                cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // fifoReservedEventCount
                cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8), // maximumRange
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // reportingMode
                cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10), // powerInMilliAmperes
                cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11), // sensorResolution
                cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // sensorVendor
                cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13) // sensorVersion
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, SensorEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSensorName(cursor.getString(offset + 1));
        entity.setSensorType(cursor.getString(offset + 2));
        entity.setSensorUnit(cursor.getString(offset + 3));
        entity.setMinimumDelayMicroseconds(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setMaximumDelayMicroseconds(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setFifoMaxEventCount(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setFifoReservedEventCount(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setMaximumRange(cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8));
        entity.setReportingMode(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPowerInMilliAmperes(cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10));
        entity.setSensorResolution(cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11));
        entity.setSensorVendor(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSensorVersion(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(SensorEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(SensorEntity entity) {
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
     * Properties of entity SensorEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SensorName = new Property(1, String.class, "sensorName", false, "SENSOR_NAME");
        public final static Property SensorType = new Property(2, String.class, "sensorType", false, "SENSOR_TYPE");
        public final static Property SensorUnit = new Property(3, String.class, "sensorUnit", false, "SENSOR_UNIT");
        public final static Property MinimumDelayMicroseconds = new Property(4, Integer.class, "minimumDelayMicroseconds", false, "MINIMUM_DELAY_MICROSECONDS");
        public final static Property MaximumDelayMicroseconds = new Property(5, Integer.class, "maximumDelayMicroseconds", false, "MAXIMUM_DELAY_MICROSECONDS");
        public final static Property FifoMaxEventCount = new Property(6, Integer.class, "fifoMaxEventCount", false, "FIFO_MAX_EVENT_COUNT");
        public final static Property FifoReservedEventCount = new Property(7, Integer.class, "fifoReservedEventCount", false, "FIFO_RESERVED_EVENT_COUNT");
        public final static Property MaximumRange = new Property(8, Float.class, "maximumRange", false, "MAXIMUM_RANGE");
        public final static Property ReportingMode = new Property(9, String.class, "reportingMode", false, "REPORTING_MODE");
        public final static Property PowerInMilliAmperes = new Property(10, Float.class, "powerInMilliAmperes", false, "POWER_IN_MILLI_AMPERES");
        public final static Property SensorResolution = new Property(11, Float.class, "sensorResolution", false, "SENSOR_RESOLUTION");
        public final static Property SensorVendor = new Property(12, String.class, "sensorVendor", false, "SENSOR_VENDOR");
        public final static Property SensorVersion = new Property(13, Integer.class, "sensorVersion", false, "SENSOR_VERSION");
    }

}