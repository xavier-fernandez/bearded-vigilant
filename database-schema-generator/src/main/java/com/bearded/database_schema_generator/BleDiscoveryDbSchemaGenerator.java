package com.bearded.database_schema_generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

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
abstract class BleDiscoveryDbSchemaGenerator extends AbstractDbSchemaGenerator {

    private static final String MODULE_PACKAGE = "com.bearded.modules.ble.discovery";

    private static final String TARGET_PACKAGE = MODULE_PACKAGE + ".domain";

    private static final int SCHEMA_VERSION = 1;

    private static final String OUTPUT_DIR = "./mod-ble-discovery/src/main/java";

    static void generateBleDatabaseSchema() throws Exception {
        System.out.println(String.format("Creating database schema with name: %s", MODULE_PACKAGE));
        final Schema mDbSchema = new Schema(SCHEMA_VERSION, TARGET_PACKAGE);
        // Initializes the database schema.
        // The database schema will have 'keep' sections that will not be overridden when executing this class.
        mDbSchema.enableKeepSectionsByDefault();
        // Creates the database table.
        final Entity deviceEntity = createBleDeviceEntity(mDbSchema);
        final Entity eventSeriesEntity = createBleEventSeriesEntity(mDbSchema);
        createBleEventEntity(mDbSchema, deviceEntity, eventSeriesEntity);
        // Creates the DAO classes in the specified folder.
        final DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(mDbSchema, OUTPUT_DIR);
    }

    /**
     * CREATE TABLE ble_device (
     * _id             INTEGER  PRIMARY KEY AUTOINCREMENT,
     * device_address  TEXT     NOT NULL,
     * advertise_name  TEXT
     * );
     */
    private static Entity createBleDeviceEntity(final Schema dbSchema) {
        final String tableName = "BleDevice";
        final Entity deviceEntity = createEntity(dbSchema, tableName);
        deviceEntity.addIdProperty().primaryKey();
        deviceEntity.addStringProperty("deviceAddress").notNull();
        deviceEntity.addStringProperty("advertiseName");
        return deviceEntity;
    }

    /**
     * CREATE TABLE ble_event_series (
     * _id              INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * start_timestamp  DATE       NOTNULL,
     * end_timestamp    DATE
     * );
     */
    private static Entity createBleEventSeriesEntity(final Schema dbSchema) {
        final String tableName = "BleEventSeries";
        final Entity seriesEntity = createEntity(dbSchema, tableName);
        seriesEntity.addIdProperty().primaryKey();
        seriesEntity.addDateProperty("startTimestamp").notNull();
        seriesEntity.addDateProperty("endTimestamp");
        return seriesEntity;
    }

    /**
     * CREATE TABLE ble_event (
     * _id               INTEGER   PRIMARY KEY   AUTOINCREMENT,
     * ble_device        INTEGER   FOREIGN KEY   REFERENCES  ble_device (_id)        NOT NULL,
     * event_series      INTEGER   FOREIGN KEY   REFERENCES  ble_event_series (_id)  NOT NULL,
     * start_timestamp   DATE      NOT NULL,
     * end_timestamp     DATE
     * );
     */
    private static void createBleEventEntity(final Schema dbSchema, final Entity deviceEntity, final Entity eventSeriesEntity) {
        final String tableName = "BleEvent";
        final Entity eventEntity = createEntity(dbSchema, tableName);
        eventEntity.addIdProperty().primaryKey();
        eventEntity.addToOne(deviceEntity, eventEntity.addLongProperty("bleDevice").getProperty());
        eventEntity.addToOne(eventSeriesEntity, eventEntity.addLongProperty("eventSeries").getProperty());
        eventEntity.addDateProperty("startTimestamp").notNull();
        eventEntity.addDateProperty("endTimestamp");
    }
}