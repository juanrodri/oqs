/*
 * $Id$
 *
 * Copyright 2006-2008 Alex Lin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opoo.oqs.spring;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.opoo.oqs.TypedValue;
import org.opoo.oqs.core.AbstractBatcher;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SpringBatcherImpl extends AbstractBatcher {
    boolean isPrepared = false;
    private JdbcTemplate jdbcTemplate;
    public SpringBatcherImpl(JdbcTemplate jdbcTemplate) {
        super();
        isPrepared = false;
        this.jdbcTemplate = jdbcTemplate;
    }

    public SpringBatcherImpl(JdbcTemplate jdbcTemplate, String sql) {
        super(sql);
        isPrepared = true;
        this.jdbcTemplate = jdbcTemplate;
    }

    public int[] executeBatch() {
        final List list = getTypedValuesList();
        if (isPrepared) {
            return jdbcTemplate.batchUpdate(getSql(),
                                            new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int ii) throws
                        SQLException {
                    TypedValue[] typedValues = (TypedValue[]) list.get(ii);
                    for (int i = 0; i < typedValues.length; i++) {
                        TypedValue tv = typedValues[i];
                        tv.getType().safeSet(ps, tv.getValue(), i + 1);
                    }
                }

                public int getBatchSize() {
                    return list.size();
                }
            });
        } else {
            return jdbcTemplate.batchUpdate(getSqls());
        }
    }

}
