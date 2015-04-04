package com.bearded.database_schema_generator;

import android.support.annotation.NonNull;

import com.bearded.common.ParseableJson;

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

/**
 * Abstract class that contains a method that should be used when creating any type of entity.
 */
abstract class AbstractDbSchemaGenerator {

    private static final String ENTITY_SUFFIX = "Entity";

    /**
     * Creates a entity following all the project conventions.
     * - Add an 'Entity suffix' to the entity name. (Not the database table name)
     * - Adds an autoincrement primary key ID property.
     * - Implements {@link com.bearded.common.ParseableJson} interface.
     *
     * @param dbSchema  where the entity is going to be added.
     * @param tableName of the database table.
     * @return the created {@link de.greenrobot.daogenerator.Entity}.
     */
    @NonNull
    static Entity createEntity(@NonNull final Schema dbSchema, @NonNull final String tableName) {
        final String entityName = String.format("%s%s", tableName, ENTITY_SUFFIX);
        final Entity entity = dbSchema.addEntity(entityName);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.setTableName(tableName);
        entity.implementsInterface(ParseableJson.class.getCanonicalName());
        return entity;
    }
}