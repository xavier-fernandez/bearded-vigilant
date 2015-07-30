package com.bearded.database_schema_generator;

import android.support.annotation.NonNull;

import com.bearded.common.persistance.ParseableJson;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Abstract class that contains a method that should be used when creating any type of entity.
 */
abstract class AbstractDbSchemaGenerator {

    private static final String ENTITY_SUFFIX = "Entity";

    /**
     * Creates a entity following all the project conventions.
     * - Add an 'Entity suffix' to the entity name. (Not the database table name)
     * - Adds an autoincrement primary key ID property.
     * - Implements {@link ParseableJson} interface.
     *
     * @param dbSchema  where the entity is going to be added.
     * @param tableName of the database table.
     * @return the created {@link de.greenrobot.daogenerator.Entity}.
     */
    @NonNull
    static Entity createEntity(@NonNull Schema dbSchema, @NonNull String tableName) {
        final String entityName = String.format("%s%s", tableName, ENTITY_SUFFIX);
        final Entity entity = dbSchema.addEntity(entityName);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.setTableName(tableName);
        entity.implementsInterface(ParseableJson.class.getCanonicalName());
        entity.implementsInterface(String.format("%s<%s>", Comparable.class.getCanonicalName(), entity.getClassName()));
        return entity;
    }

    /**
     * CREATE TABLE location (
     * _id                    INTEGER    PRIMARY KEY   AUTOINCREMENT,
     * latitude               FLOAT      NOTNULL,
     * longitude              FLOAT      NOTNULL,
     * timestamp              TEXT       NOTNULL,
     * accuracyInMeters       FLOAT,
     * speedInMetersSecond    FLOAT
     * );
     */
    @NonNull
    static Entity createLocationEntity(@NonNull Schema dbSchema) {
        final Entity metadataEntity = createEntity(dbSchema, "Location");
        metadataEntity.addDoubleProperty("latitude").notNull();
        metadataEntity.addDoubleProperty("longitude").notNull();
        metadataEntity.addStringProperty("timestamp").notNull();
        metadataEntity.addFloatProperty("accuracyInMeters");
        metadataEntity.addFloatProperty("speedInMetersSecond");
        return metadataEntity;
    }
}