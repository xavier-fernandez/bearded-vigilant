package com.bearded.modules.sensor.internal.persistence.dao;

import android.database.sqlite.SQLiteDatabase;

import com.bearded.modules.sensor.internal.domain.InternalSensorEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementEntity;
import com.bearded.modules.sensor.internal.domain.InternalSensorMeasurementSeriesEntity;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig internalSensorEntityDaoConfig;
    private final DaoConfig internalSensorMeasurementSeriesEntityDaoConfig;
    private final DaoConfig internalSensorMeasurementEntityDaoConfig;

    private final InternalSensorEntityDao internalSensorEntityDao;
    private final InternalSensorMeasurementSeriesEntityDao internalSensorMeasurementSeriesEntityDao;
    private final InternalSensorMeasurementEntityDao internalSensorMeasurementEntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        internalSensorEntityDaoConfig = daoConfigMap.get(InternalSensorEntityDao.class).clone();
        internalSensorEntityDaoConfig.initIdentityScope(type);

        internalSensorMeasurementSeriesEntityDaoConfig = daoConfigMap.get(InternalSensorMeasurementSeriesEntityDao.class).clone();
        internalSensorMeasurementSeriesEntityDaoConfig.initIdentityScope(type);

        internalSensorMeasurementEntityDaoConfig = daoConfigMap.get(InternalSensorMeasurementEntityDao.class).clone();
        internalSensorMeasurementEntityDaoConfig.initIdentityScope(type);

        internalSensorEntityDao = new InternalSensorEntityDao(internalSensorEntityDaoConfig, this);
        internalSensorMeasurementSeriesEntityDao = new InternalSensorMeasurementSeriesEntityDao(internalSensorMeasurementSeriesEntityDaoConfig, this);
        internalSensorMeasurementEntityDao = new InternalSensorMeasurementEntityDao(internalSensorMeasurementEntityDaoConfig, this);

        registerDao(InternalSensorEntity.class, internalSensorEntityDao);
        registerDao(InternalSensorMeasurementSeriesEntity.class, internalSensorMeasurementSeriesEntityDao);
        registerDao(InternalSensorMeasurementEntity.class, internalSensorMeasurementEntityDao);
    }

    public void clear() {
        internalSensorEntityDaoConfig.getIdentityScope().clear();
        internalSensorMeasurementSeriesEntityDaoConfig.getIdentityScope().clear();
        internalSensorMeasurementEntityDaoConfig.getIdentityScope().clear();
    }

    public InternalSensorEntityDao getInternalSensorEntityDao() {
        return internalSensorEntityDao;
    }

    public InternalSensorMeasurementSeriesEntityDao getInternalSensorMeasurementSeriesEntityDao() {
        return internalSensorMeasurementSeriesEntityDao;
    }

    public InternalSensorMeasurementEntityDao getInternalSensorMeasurementEntityDao() {
        return internalSensorMeasurementEntityDao;
    }

}