package com.bearded.database_schema_generator;

import android.support.annotation.Nullable;

abstract class DatabaseGeneratorMain {

    public static void main(@Nullable String... args) throws Exception {
        BleDiscoveryDbSchemaGenerator.generateBleDatabaseSchema();
        SensorDbSchemaGenerator.generateInternalSensorDatabaseSchema();
    }
}