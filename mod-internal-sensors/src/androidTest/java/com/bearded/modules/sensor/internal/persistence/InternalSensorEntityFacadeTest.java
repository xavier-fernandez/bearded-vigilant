package com.bearded.modules.sensor.internal.persistence;

import android.hardware.Sensor;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.bearded.common.sensor.SensorType;
import com.bearded.modules.sensor.internal.persistence.dao.DaoMaster;
import com.bearded.modules.sensor.internal.persistence.dao.DaoSession;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.bearded.common.sensor.SensorType.LIGHT;

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
public class InternalSensorEntityFacadeTest extends AbstractInternalSensorTestCase {

    @MediumTest
    public void testSensorInsertion(){
        assertNotNull(super.mDatabaseConnector);
        final DaoSession session = super.mDatabaseConnector.getSession();
        // TODO: Sensor needs to be mocked. Test sensor insertion.
    }
}