/*
 * Copyright (C) 2013-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.series.db_custom.dao;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.I18nProcedureEntity;
import org.n52.series.db_custom.beans.ProcedureTEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProcedureDao extends AbstractInsertDao<ProcedureTEntity> {

    private static final String SERIES_PROPERTY = "procedure";

    private static final String COLUMN_REFERENCE = "reference";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SERVICE_PKID = "service.pkid";

    public ProcedureDao(Session session) {
        super(session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProcedureTEntity> find(DbQuery query) {
        Criteria criteria = translate(I18nProcedureEntity.class, getDefaultCriteria(), query)
                .add(Restrictions.ilike("name", "%" + query.getSearchTerm() + "%"));
        return addFilters(criteria, query).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProcedureTEntity> getAllInstances(DbQuery query) throws DataAccessException {
        Criteria criteria = translate(I18nProcedureEntity.class, getDefaultCriteria(), query);
        return (List<ProcedureTEntity>) addFilters(criteria, query).list();
    }

    @Override
    protected Criteria getDefaultCriteria() {
        return super.getDefaultCriteria()
                .add(eq(COLUMN_REFERENCE, Boolean.FALSE));
    }

    @Override
    protected String getSeriesProperty() {
        return SERIES_PROPERTY;
    }

    @Override
    protected Class<ProcedureTEntity> getEntityClass() {
        return ProcedureTEntity.class;
    }

    @Override
    public ProcedureTEntity getOrInsertInstance(ProcedureTEntity procedure) {
        ProcedureTEntity instance = getInstance(procedure);
        if (instance == null) {
            this.session.save(procedure);
            instance = procedure;
        }
        return instance;
    }

    private ProcedureTEntity getInstance(ProcedureTEntity procedure) {
        Criteria criteria = session.createCriteria(getEntityClass())
                .add(Restrictions.eq(COLUMN_NAME, procedure.getName()))
                .add(Restrictions.eq(COLUMN_SERVICE_PKID, procedure.getService().getPkid()));
        return (ProcedureTEntity) criteria.uniqueResult();
    }

}