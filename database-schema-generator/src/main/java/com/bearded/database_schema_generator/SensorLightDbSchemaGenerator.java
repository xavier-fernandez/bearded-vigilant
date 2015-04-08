package com.bearded.database_schema_generator;

import android.support.annotation.NonNull;

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
public class SensorLightDbSchemaGenerator extends AbstractDbSchemaGenerator {

    private static final String MODULE_PACKAGE = "com.bearded.modules.sensor.light";

    private static final String ENTITY_PACKAGE = String.format("%s.domain", MODULE_PACKAGE);
    private static final String DAO_PACKAGE = String.format("%s.persistence.dao", MODULE_PACKAGE);

    private static final int SCHEMA_VERSION = 1;

    private static final String OUT_DIR = "./mod-sensor-light/src/main/java";
    private static final String TEST_DIR = "./mod-sensor-light/src/androidTest/java";

    static void generateLightSensorDatabaseSchema() throws Exception {
        System.out.println(String.format("Creating database schema with name: %s", MODULE_PACKAGE));
        final Schema dbSchema = new Schema(SCHEMA_VERSION, ENTITY_PACKAGE);
        dbSchema.setDefaultJavaPackageDao(DAO_PACKAGE);
        dbSchema.setDefaultJavaPackageTest(DAO_PACKAGE);
        // Initializes the database schema.
        // The database schema will have 'keep' sections that will not be overridden when executing this class.
        dbSchema.enableKeepSectionsByDefault();
        // Creates the measurement series entity.
        final Entity measurementSeriesEntity = createLightMeasurementSeriesEntity(dbSchema);
        // Creates the measurement entity.
        createLightMeasurementEntity(dbSchema, measurementSeriesEntity);
        // Creates the DAO classes in the specified folder.
        final DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(dbSchema, OUT_DIR, TEST_DIR);
    }

    /**
     * CREATE TABLE measurement_series (
     * _id              INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * start_timestamp  TEXT       NOTNULL,
     * end_timestamp    TEXT
     * );
     */
    @NonNull
    private static Entity createLightMeasurementSeriesEntity(@NonNull final Schema dbSchema) {
        final Entity seriesEntity = createEntity(dbSchema, "LightMeasurementSeries");
        seriesEntity.addStringProperty("startTimestamp").notNull();
        seriesEntity.addStringProperty("endTimestamp");
        return seriesEntity;
    }

    /**
     * CREATE TABLE ble_event_series (
     * _id                      INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * measurement_series_id    INTEGER    FOREIGN KEY   REFERENCES  measurement_series (_id)  NOT NULL,
     * mid_light_value          FLOAT      NOTNULL,
     * start_timestamp          TEXT       NOTNULL,
     * end_timestamp            TEXT       NOTNULL,
     * bin_size                 INTEGER    NOTNULL
     * );
     */
    private static void createLightMeasurementEntity(@NonNull final Schema dbSchema, @NonNull final Entity createMeasurementSeriesEntity) {
        final Entity seriesEntity = createEntity(dbSchema, "LightMeasurement");
        seriesEntity.addToOne(createMeasurementSeriesEntity, seriesEntity.addLongProperty("measurementSeriesEntity").getProperty());
        seriesEntity.addFloatProperty("midLightValue").notNull();
        seriesEntity.addStringProperty("startTimestamp").notNull();
        seriesEntity.addStringProperty("endTimestamp").notNull();
        seriesEntity.addShortProperty("binSize").notNull();
    }
}
