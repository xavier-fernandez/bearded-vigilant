package com.bearded.database_schema_generator;

import android.support.annotation.NonNull;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

abstract class BleDiscoveryDbSchemaGenerator extends AbstractDbSchemaGenerator {

    private static final String MODULE_PACKAGE = "com.bearded.modules.ble.discovery";

    private static final String ENTITY_PACKAGE = String.format("%s.domain", MODULE_PACKAGE);
    private static final String DAO_PACKAGE = String.format("%s.persistence.dao", MODULE_PACKAGE);
    private static final String ENTITY_TEST_PACKAGE = ENTITY_PACKAGE;

    private static final int SCHEMA_VERSION = 1;

    private static final String OUT_DIR = "./mod-ble-discovery/src/main/java";
    private static final String TEST_DIR = "./mod-ble-discovery/src/androidTest/java";

    static void generateBleDatabaseSchema() throws Exception {
        System.out.println(String.format("Creating database schema with name: %s", MODULE_PACKAGE));
        // Initializes the database schema.
        final Schema dbSchema = new Schema(SCHEMA_VERSION, ENTITY_PACKAGE);
        dbSchema.setDefaultJavaPackageDao(DAO_PACKAGE);
        dbSchema.setDefaultJavaPackageTest(ENTITY_TEST_PACKAGE);
        dbSchema.enableKeepSectionsByDefault();
        // Creates the database table.
        final Entity deviceEntity = createBleDeviceEntity(dbSchema);
        // Creates the event series entity.
        final Entity eventSeriesEntity = createBleEventSeriesEntity(dbSchema, deviceEntity);
        // Creates the location entity.
        final Entity locationEntity = createLocationEntity(dbSchema);
        createBleEventEntity(dbSchema, eventSeriesEntity, locationEntity);
        // Creates the DAO classes in the specified folder.
        final DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(dbSchema, OUT_DIR, TEST_DIR);
    }

    /**
     * CREATE TABLE ble_device (
     * _id             INTEGER  PRIMARY KEY AUTOINCREMENT,
     * device_address  TEXT     NOT NULL,
     * advertise_name  TEXT,
     * edr_or_br       BOOLEAN,
     * isLowEnergy     BOOLEAN
     * );
     */
    @NonNull
    private static Entity createBleDeviceEntity(@NonNull Schema dbSchema) {
        final Entity deviceEntity = createEntity(dbSchema, "BleDevice");
        deviceEntity.addStringProperty("deviceAddress").notNull().indexAsc("ble_device_address_index", true);
        deviceEntity.addStringProperty("advertiseName").indexAsc("ble_device_advertise_name_index", false);
        // Indicates if the device supports data transmission using old Bluetooth protocols.
        deviceEntity.addBooleanProperty("isEdrOrBr");
        // Indicates if the device supports data transmission using low-energy Bluetooth protocols.
        deviceEntity.addBooleanProperty("isLowEnergy");
        return deviceEntity;
    }

    /**
     * CREATE TABLE ble_event_series (
     * _id              INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * ble_device_id    INTEGER    FOREIGN KEY   REFERENCES  ble_device (_id)  NOT NULL,
     * start_timestamp  TEXT       NOT NULL,
     * end_timestamp    TEXT,
     * );
     */
    @NonNull
    private static Entity createBleEventSeriesEntity(@NonNull Schema dbSchema,
                                                     @NonNull Entity deviceEntity) {
        final Entity seriesEntity = createEntity(dbSchema, "BleEventSeries");
        final Property bleDeviceIdFK = seriesEntity.addLongProperty("bleDeviceId").notNull().getProperty();
        seriesEntity.addToOne(deviceEntity, bleDeviceIdFK);
        seriesEntity.addStringProperty("startTimestamp").notNull();
        seriesEntity.addStringProperty("endTimestamp");
        return seriesEntity;
    }

    /**
     * CREATE TABLE ble_event (
     * _id                               INTEGER   PRIMARY KEY  AUTOINCREMENT,
     * event_series_id                   INTEGER   FOREIGN KEY  REFERENCES  ble_event_series(_id)  NOT NULL,
     * location_id                       INTEGER   FOREIGN KEY  REFERENCES  location(_id);
     * start_timestamp                   TEXT      NOT NULL,
     * end_timestamp                     TEXT      NOT NULL,
     * median_received_signal_strength   INTEGER   NOT NULL,
     * bin_size                          INTEGER   NOT NULL
     * );
     */
    private static void createBleEventEntity(@NonNull Schema dbSchema,
                                             @NonNull Entity eventSeriesEntity,
                                             @NonNull Entity locationEntity) {
        final Entity eventEntity = createEntity(dbSchema, "BleEvent");
        final Property eventSeriesIdFK = eventEntity.addLongProperty("eventSeriesId").notNull().getProperty();
        eventEntity.addToOne(eventSeriesEntity, eventSeriesIdFK);
        final Property locationFK = eventEntity.addLongProperty("location_id").getProperty();
        eventEntity.addToOne(locationEntity, locationFK);
        eventEntity.addStringProperty("startTimestamp").notNull();
        eventEntity.addStringProperty("endTimestamp").notNull();
        eventEntity.addByteProperty("medianReceivedSignalStrength").notNull();
        eventEntity.addShortProperty("binSize").notNull();
    }
}