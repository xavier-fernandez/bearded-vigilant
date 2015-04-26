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

class InternalSensorDbSchemaGenerator extends AbstractDbSchemaGenerator {

    private static final String MODULE_PACKAGE = "com.bearded.modules.sensor.internal";

    private static final String ENTITY_PACKAGE = String.format("%s.domain", MODULE_PACKAGE);
    private static final String DAO_PACKAGE = String.format("%s.persistence.dao", MODULE_PACKAGE);

    private static final int SCHEMA_VERSION = 1;

    private static final String OUT_DIR = "./mod-internal-sensors/src/main/java";
    private static final String TEST_DIR = "./mod-internal-sensors/src/androidTest/java";

    static void generateInternalSensorDatabaseSchema() throws Exception {
        System.out.println(String.format("Creating database schema with name: %s", MODULE_PACKAGE));
        final Schema dbSchema = new Schema(SCHEMA_VERSION, ENTITY_PACKAGE);
        dbSchema.setDefaultJavaPackageDao(DAO_PACKAGE);
        dbSchema.setDefaultJavaPackageTest(DAO_PACKAGE);
        // Initializes the database schema.
        // The database schema will have 'keep' sections that will not be overridden when executing this class.
        dbSchema.enableKeepSectionsByDefault();
        // Creates the sensor entity.
        final Entity sensorEntity = createInternalSensorEntity(dbSchema);
        // Creates the measurement series entity.
        final Entity measurementSeriesEntity = createSensorMeasurementSeriesEntity(dbSchema, sensorEntity);
        // Creates the measurement entity.
        createSensorMeasurementEntity(dbSchema, measurementSeriesEntity);
        // Creates the DAO classes in the specified folder.
        final DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(dbSchema, OUT_DIR, TEST_DIR);
    }

    /**
     * The following method obtains all the sensor data obtainable with the Android sensor API.
     * Comments obtained in: http://developer.android.com/reference/android/hardware/Sensor.html
     * <p/>
     * CREATE TABLE sensor (
     * _id                           INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * sensor_name                   TEXT       NOT NULL      UNIQUE     INDEX,
     * sensor_type                   TEXT       NOT NULL,
     * sensor_unit                   TEXT       NOT NULL,
     * minimum_delay_microseconds    INTEGER,
     * maximum_delay_microseconds    INTEGER,
     * fifo_max_event_count          INTEGER,
     * fifo_reserved_event_count     INTEGER,
     * maximum_range                 FLOAT,
     * reporting_mode                TEXT,
     * power_in_milli_amperes        FLOAT,
     * sensor_resolution             FLOAT,
     * sensor_vendor                 TEXT,
     * sensor_version                INTEGER
     * );
     */
    @NonNull
    private static Entity createInternalSensorEntity(@NonNull final Schema dbSchema) {
        final Entity sensorEntity = createEntity(dbSchema, "InternalSensor");
        /**
         * maximum range of the sensor in the sensor's unit.
         */
        sensorEntity.addStringProperty("sensorName").notNull().index();
        /**
         * The type of this sensor as a string.
         */
        sensorEntity.addStringProperty("sensorType").notNull().index();
        /**
         * Sensor unit string.
         */
        sensorEntity.addStringProperty("sensorUnit").notNull();
        /** the minimum delay allowed between two events in microsecond or zero if this sensor only
         * returns a value when the data it's measuring changes.
         */
        sensorEntity.addIntProperty("minimumDelayMicroseconds");
        /**
         * This value is defined only for continuous and on-change sensors. It is the delay between
         * two sensor events corresponding to the lowest frequency that this sensor supports. When
         * lower frequencies are requested through registerListener() the events will be generated
         * at this frequency instead. It can be used to estimate when the batch FIFO may be full.
         * Older devices may set this value to zero. Ignore this value in case it is negative or
         * zero.
         */
        sensorEntity.addIntProperty("maximumDelayMicroseconds");
        /**
         * Maximum number of events of this sensor that could be batched. If this value is zero it
         * indicates that batch mode is not supported for this sensor. If other applications
         * registered to batched sensors, the actual number of events that can be batched might be
         * smaller because the hardware FiFo will be partially used to batch the other sensors.
         */
        sensorEntity.addIntProperty("fifoMaxEventCount");
        /**
         * Number of events reserved for this sensor in the batch mode FIFO. This gives a guarantee
         * on the minimum number of events that can be batched.
         */
        sensorEntity.addIntProperty("fifoReservedEventCount");
        /**
         * Maximum range of the sensor in the sensor's unit.
         */
        sensorEntity.addFloatProperty("maximumRange");
        /**
         * Each sensor has exactly one reporting mode associated with it. This method returns
         * the reporting mode constant for this sensor type.
         * must be {@link REPORTING_MODE_CONTINUOUS} or {@link REPORTING_MODE_ON_CHANGE}
         * or {@link REPORTING_MODE_ONE_SHOT} or {@link REPORTING_MODE_SPECIAL_TRIGGER}
         */
        sensorEntity.addStringProperty("reportingMode").getProperty();
        /**
         * The power in mA used by this sensor while in use
         */
        sensorEntity.addFloatProperty("powerInMilliAmperes");
        /**
         * Resolution of the sensor in the sensor's unit.
         */
        sensorEntity.addFloatProperty("sensorResolution");
        /**
         * Version of the sensor's module.
         */
        sensorEntity.addStringProperty("sensorVendor");
        /**
         * Sensor Version.
         */
        sensorEntity.addIntProperty("sensorVersion");
        return sensorEntity;
    }

    /**
     * CREATE TABLE internal_sen6sor_measurement_series (
     * _id              INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * sensor_id        INTEGER    FOREIGN KEY   REFERENCES  sensor (_id)  NonNull,
     * start_timestamp  TEXT       NOT NULL,
     * end_timestamp    TEXT
     * );
     */
    @NonNull
    private static Entity createSensorMeasurementSeriesEntity(@NonNull final Schema dbSchema,
                                                              @NonNull final Entity sensorEntity) {
        final Entity seriesEntity = createEntity(dbSchema, "InternalSensorMeasurementSeries");
        final Property sensorFK = seriesEntity.addLongProperty("sensor_id").notNull().getProperty();
        seriesEntity.addToOne(sensorEntity, sensorFK);
        seriesEntity.addStringProperty("startTimestamp").notNull();
        seriesEntity.addStringProperty("endTimestamp");
        return seriesEntity;
    }

    /**
     * CREATE TABLE internal_sensor_measurement (
     * _id                    INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * measurement_series_id  INTEGER    FOREIGN KEY   REFERENCES  measurement_series  NOT NULL,
     * sensor_value           FLOAT      NOT NULL,
     * start_timestamp        TEXT       NOT NULL,
     * end_timestamp          TEXT       NOT NULL,
     * bin_size               INTEGER    NOT NULL
     * );
     */
    private static void createSensorMeasurementEntity(@NonNull final Schema dbSchema,
                                                      @NonNull final Entity seriesEntity) {
        final Entity entity = createEntity(dbSchema, "InternalSensorMeasurement");
        final Property seriesFK = entity.addLongProperty("measurement_series_id").getProperty();
        entity.addToOne(seriesEntity, seriesFK);
        entity.addFloatProperty("sensorValue").notNull();
        entity.addStringProperty("startTimestamp").notNull();
        entity.addStringProperty("endTimestamp").notNull();
        entity.addShortProperty("binSize").notNull();
    }
}
