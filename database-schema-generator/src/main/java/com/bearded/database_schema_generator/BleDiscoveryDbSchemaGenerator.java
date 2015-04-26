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

    private static final int SCHEMA_VERSION = 1;

    private static final String OUT_DIR = "./mod-ble-discovery/src/main/java";
    private static final String TEST_DIR = "./mod-ble-discovery/src/androidTest/java";

    static void generateBleDatabaseSchema() throws Exception {
        System.out.println(String.format("Creating database schema with name: %s", MODULE_PACKAGE));
        // Initializes the database schema.
        final Schema dbSchema = new Schema(SCHEMA_VERSION, ENTITY_PACKAGE);
        dbSchema.setDefaultJavaPackageDao(DAO_PACKAGE);
        dbSchema.setDefaultJavaPackageTest(DAO_PACKAGE);
        dbSchema.enableKeepSectionsByDefault();
        // Creates the database table.
        final Entity deviceEntity = createBleDeviceEntity(dbSchema);
        final Entity eventSeriesEntity = createBleEventSeriesEntity(dbSchema, deviceEntity);
        createBleEventEntity(dbSchema, eventSeriesEntity);
        // Creates the DAO classes in the specified folder.
        final DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(dbSchema, OUT_DIR, TEST_DIR);
    }

    /**
     * CREATE TABLE ble_device (
     * _id             INTEGER  PRIMARY KEY AUTOINCREMENT,
     * device_address  TEXT     NOT NULL,
     * advertise_name  TEXT
     * );
     */
    @NonNull
    private static Entity createBleDeviceEntity(@NonNull final Schema dbSchema) {
        final Entity deviceEntity = createEntity(dbSchema, "BleDevice");
        deviceEntity.addStringProperty("deviceAddress").notNull();
        deviceEntity.addStringProperty("advertiseName");
        return deviceEntity;
    }

    /**
     * CREATE TABLE ble_event_series (
     * _id              INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * ble_device_id    INTEGER    FOREIGN KEY   REFERENCES  ble_device (_id)  NOT NULL,
     * start_timestamp  TEXT       NonNull,
     * end_timestamp    TEXT,
     * );
     */
    @NonNull
    private static Entity createBleEventSeriesEntity(@NonNull final Schema dbSchema,
                                                     @NonNull final Entity deviceEntity) {
        final Entity seriesEntity = createEntity(dbSchema, "BleEventSeries");
        final Property bleDeviceIdFK = seriesEntity.addLongProperty("bleDeviceId").getProperty();
        seriesEntity.addToOne(deviceEntity, bleDeviceIdFK);
        seriesEntity.addStringProperty("startTimestamp").notNull();
        seriesEntity.addStringProperty("endTimestamp");
        return seriesEntity;
    }

    /**
     * CREATE TABLE ble_event (
     * _id                       INTEGER   PRIMARY KEY  AUTOINCREMENT,
     * event_series_id           INTEGER   FOREIGN KEY  REFERENCES  ble_event_series(_id) NOT NULL,
     * start_timestamp           TEXT      NOT NULL,
     * end_timestamp             TEXT,
     * received_signal_strength  INTEGER   NOT NULL
     * );
     */
    private static void createBleEventEntity(@NonNull final Schema dbSchema,
                                             @NonNull final Entity eventSeriesEntity) {
        final Entity eventEntity = createEntity(dbSchema, "BleEvent");
        final Property eventSeriesIdFK = eventEntity.addLongProperty("eventSeriesId").getProperty();
        eventEntity.addToOne(eventSeriesEntity, eventSeriesIdFK);
        eventEntity.addStringProperty("startTimestamp").notNull();
        eventEntity.addStringProperty("endTimestamp");
        eventEntity.addByteProperty("receivedSignalStrength").notNull();
    }
}