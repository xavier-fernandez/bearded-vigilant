package com.bearded.database_schema_generator;

/*
 * (C) Copyright 2015 Xavier Fernández Salas (xavier.fernandez.salas@gmail.com).
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

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

abstract class AbstractDbSchemaGenerator {

    private static final String ENTITY_SUFFIX = "Entity";

    protected static Entity createEntity(final Schema dbSchema, final String tableName) {
        final String entityName = String.format("%s%s", tableName, ENTITY_SUFFIX);
        final Entity entity = dbSchema.addEntity(entityName);
        entity.setTableName(tableName);
        return entity;
    }
}