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
package org.opoo.oqs.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.LogFactory;
import org.opoo.util.StringUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class NullableType implements Type {
    private static final boolean IS_TRACE_ENABLED;
    static {
        //cache this, because it was a significant performance cost
        IS_TRACE_ENABLED = LogFactory.getLog(StringUtils.qualifier(Type.class.
                getName())).isTraceEnabled();
    }

    public abstract Object get(ResultSet rs, String name) throws SQLException;

    public abstract Object get(ResultSet rs, int index) throws SQLException;

    public abstract void set(PreparedStatement st, Object value, int index) throws
            SQLException;

    public abstract String toString(Object value);


    public Object safeGet(ResultSet rs, String name) throws SQLException {
        Object value = get(rs, name);
        if (value == null || rs.wasNull()) {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace(
                        "returning null as column: " + name);
            }
            return null;
        } else {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace("returning '" +
                        toString(value) +
                        "' as column: " + name);
            }
            return value;
        }
    }

    public Object safeGet(ResultSet rs, int index) throws SQLException {
        Object value = get(rs, index);
        if (value == null || rs.wasNull()) {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace(
                        "returning null as column index: " + index);
            }
            return null;
        } else {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace("returning '" +
                        toString(value) +
                        "' as column index: " + index);
            }
            return value;
        }
    }


    public void safeSet(PreparedStatement st, Object value, int index) throws
            SQLException {
        if (value == null) {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace(
                        "binding null to parameter: " +
                        index);
            }
            st.setNull(index, sqlType());
        } else {
            if (IS_TRACE_ENABLED) {
                LogFactory.getLog(getClass()).trace("binding '" +
                        toString(value) +
                        "' to parameter: " + index);
            }
            set(st, value, index);
        }
    }
}
