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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.core.Property;
import org.opoo.oqs.core.SimpleProperty;
import org.opoo.util.Assert;
import org.opoo.util.ClassUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class AbstractMultiPropertyMapper extends SimpleProperty implements
        PropertyMapper {
    private static final Log log = LogFactory.getLog(AbstractMultiPropertyMapper.class);
    protected PropertyMapper[] mappers;
    private Offset offset = new Offset();
    public AbstractMultiPropertyMapper(String name, String string,
                                       PropertyMapper[] mappers) {
        super(name, string);
        this.mappers = mappers;
    }

    public AbstractMultiPropertyMapper(Property sp, PropertyMapper[] mappers) {
        super(sp);
        this.mappers = mappers;
    }
    public void setOffset(Offset offset){
	this.offset = offset;
    }

    /**
     *
     * @param rsmd ResultSetMetaData
     * @throws SQLException
     */
    public final void initialize(ResultSetMetaData rsmd) throws SQLException {
        log.debug(getClass().getName() + ".initialize() is called.");
        Assert.notEmpty(mappers, "mappers is required.");
        List list = new ArrayList();
        boolean hasAsterisk = false;
	//int extraCount = 0;
        for (int i = 0; i < mappers.length; i++) {
            PropertyMapper mapper = mappers[i];
            //setParametersFor(mapper);
            //初始化每一个MAPPER
	    log.debug("setting offset " + offset + " for " + mapper);
	    mapper.setOffset(offset);
            mapper.initialize(rsmd);

            if (mapper instanceof AsteriskPropertyMapper) {
                AsteriskPropertyMapper apm = (AsteriskPropertyMapper) mapper;
		list.addAll(Arrays.asList(apm.getSinglePropertyMappers()));
		processExtraCount(apm.getExtraCount());
                hasAsterisk = true;
            } else {
                list.add(mapper);
            }
        }
        if (hasAsterisk) {
            mappers = (PropertyMapper[]) list.toArray(new PropertyMapper[
                    mappers.length]);
        }
        //forced gc
        list = null;
        //return map0(rs, rowNum);
    }

    void processExtraCount(int cnt) {
        if (cnt > 0) {
            offset.setOffset(cnt);
        }
    }


    /**
     * 注意：方法在查询前查询后调用结果不一致
     * @return String
     */
    public String toString() {
        if (mappers.length == 1) {
            return mappers[0].toString();
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = mappers.length, x = n - 1; i < n; i++) {
            sb.append(mappers[i].toString());
            if (i < x) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * 注意：方法在查询前查询后调用结果不一致
     * @return String
     */
    public String getMapperString() {
        log.debug(getClass().getName() + ".getMapperString() is called.");
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = mappers.length; i < n; i++) {
            sb.append(mappers[i].getMapperString());
            if (i < n - 1) {
                sb.append(", ");
            }
        }

        /**
         * JDK1.5以前的版本不支持Class.getSimpleName();
         */
        //return "[" + getClass().getSimpleName() + "{name=" + getName() +
        return "[" + ClassUtils.getSimpleName(getClass()) + "{name=" + getName() +
                ", string="
                + getString() + ", mappers="
                + sb.toString() + "}]";
    }
}
