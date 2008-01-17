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


/**
 * 查询异常类。属于RuntimeExceotion。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since OQS1.0
 */
public class QueryException extends RuntimeException {
    private String queryString;

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable e) {
        super(message, e);
    }

    /**
     *
     * @param message String
     * @param queryString String
     */
    public QueryException(String message, String queryString) {
        super(message);
        this.queryString = queryString;
    }

    public QueryException(Exception e) {
        super(e);
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getMessage() {
        String msg = super.getMessage();
        if (queryString != null) {
            msg += " [" + queryString + ']';
        }
        return msg;
    }
}
