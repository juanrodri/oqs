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
package org.opoo.oqs.core.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.core.AsteriskProperty;
import org.opoo.util.ArrayUtils;
import org.opoo.util.Assert;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public final class AsteriskPropertyMapper extends SinglePropertyMapper implements
        PropertyMapper {
    private static final Log log = LogFactory.getLog(AsteriskPropertyMapper.class);
    private final AsteriskProperty ap;
    private PropertyMapper[] singlePropertyMappers;
    public AsteriskPropertyMapper(AsteriskProperty ap) {
        super(ap.getName(), ap.getString(), ap.getStartIndex());
        this.ap = ap;
    }

    public void initialize(ResultSetMetaData rsmd) throws SQLException {
        log.debug(getClass().getName() + ".initialize() is called.");
        super.initialize(rsmd);
        singlePropertyMappers = createSinglePropertyMappers();
    }

    public Object map(ResultSet rs, int rowNum) throws SQLException {
        int startIndex = getIndex();
        int columnCount = ap.getColumnCount();

        if (ap.getColumnCount() <= 0) {
            if (rsmd == null) {
                rsmd = rs.getMetaData();
            }
            Assert.notNull(rsmd,
                           "ResultSetMeta is required in uncounted asterisk query.");
            columnCount = rsmd.getColumnCount() -
                          ap.getCountAfterCurrentAsterisk()
                          - startIndex + 1;
        }

        Object[] objects = new Object[columnCount];
        for (int index = startIndex, i = 0; i < columnCount; i++, index++) {
            objects[i] = getColumnValue(rs, index);
        }
        return objects;
    }

    public PropertyMapper[] getSinglePropertyMappers() throws SQLException {
        return singlePropertyMappers;
    }

    private PropertyMapper[] createSinglePropertyMappers() throws SQLException {
        int startIndex = getIndex();
        int columnCount = ap.getColumnCount();

        if (ap.getColumnCount() <= 0) {
            Assert.notNull(rsmd,
                           "ResultSetMeta is required in uncounted asterisk query.");
            columnCount = rsmd.getColumnCount() -
                          ap.getCountAfterCurrentAsterisk()
                          - startIndex + 1;
        }
        //int startIndex = getIndex();
        //int endIndex = rsmd.getColumnCount() - ap.getAfterAsterisk();
        //PropertyMapper[] mappers = new SinglePropertyMapper[endIndex - startIndex + 1];
        //for(int i = startIndex, n = 0 ; i <= endIndex ; i++, n++)
        log.debug("解析" + getMapperString() + ",解析后字段数：" + columnCount);
        PropertyMapper[] mappers = new SinglePropertyMapper[columnCount];
        for (int index = startIndex, i = 0; i < columnCount; i++, index++) {
            String name = rsmd.getColumnName(index);
            SinglePropertyMapper mapper = new SinglePropertyMapper(name, name,
                    index);
            mapper.initialize(rsmd);
            mappers[i] = mapper;
        }

        return mappers;
    }


    public String getMapperString() {
        return "[AsteriskPropertyMapper{string=" + ap.getString() +
                ", startIndex="
                + getIndex() + ", count=" + ap.getColumnCount() + ", after="
                + ap.getCountAfterCurrentAsterisk() + "}]";
    }

    public Class getReturnType() {
        return ArrayUtils.EMPTY_OBJECT_ARRAY.getClass();
    }

    public String toString() {
        //System.out.println(ap.getStartIndex() + ", " + ap.getColumnCount());
        return ap.getString();
    }
}
