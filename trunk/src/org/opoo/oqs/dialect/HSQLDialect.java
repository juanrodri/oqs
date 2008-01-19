/*
 * $Id: HSQLDialect.java 1.0 08-1-19 ÏÂÎç5:24 $
 *
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opoo.oqs.dialect;

/**
 * An SQL dialect compatible with HSQLDB (Hypersonic SQL).
 * @version 1.0
 */
public class HSQLDialect extends AbstractDialect {
    public HSQLDialect() {
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public String getIdentitySelectString() {
        return "call identity()";
    }

    public String getIdentityInsertString() {
        return "null";
    }

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuffer(sql.length() + 10)
                .append(sql)
                .insert(sql.toLowerCase().indexOf("select") + 6,
                        hasOffset ? " limit ? ?" : " top ?")
                .toString();
    }

    public boolean bindLimitParametersFirst() {
        return true;
    }
}
