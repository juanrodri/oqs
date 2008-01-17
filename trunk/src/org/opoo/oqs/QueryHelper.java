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
package org.opoo.oqs;

import org.apache.commons.lang.StringUtils;
import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.criterion.Order;
import org.opoo.oqs.type.Type;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public abstract class QueryHelper {
    public static final String WHERE = " where ";

    public static Query createQuery(QueryFactory qf, String baseSql,
                                    Criterion c, Order order) {
        String qs = buildQueryString(baseSql, c, order);
        Query q = qf.createQuery(qs);
        Object[] values = c.getValues();
        Type[] types = c.getTypes();
        if (values != null && values.length > 0) {
            q.setParameters(values, types);
        }
        return q;
    }

    public static Query createQuery(QueryFactory qf, String baseSql,
                                    Criterion c) {
        return createQuery(qf, baseSql, c, null);
    }

    public static String buildQueryString(String baseSql, Criterion c,
                                          Order order) {
        if (c == null) {
            return baseSql;
        }
        String cs = c.toString();
        if (StringUtils.isBlank(cs)) {
            return baseSql;
        }

        int i = baseSql.toLowerCase().indexOf(WHERE);
        if (i == -1) {
            baseSql += WHERE;
        } else {
            baseSql += " and ";
        }
        baseSql += cs;
        if (order != null) {
            baseSql += " order by " + order.toString();
        }
        return baseSql;
    }

    public static String buildQueryString(String baseSql, Criterion c) {
        return buildQueryString(baseSql, c, null);
    }

}
