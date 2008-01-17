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
package org.opoo.oqs.core;


/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class AsteriskProperty implements Property {
    private final String string;
    private final int startIndex;
    private int countAfterCurrentAsterisk = 0;
    private int columnCount = 0;
    public AsteriskProperty(String sql, int start) {
        string = sql;
        this.startIndex = start;
    }

    public AsteriskProperty(String sql, int start, int columnCount) {
        this(sql, start);
        this.columnCount = columnCount;
    }

    /**
     * getName
     *
     * @return String
     */
    public String getName() {
        return "*";
    }

    /**
     * getString
     *
     * @return String
     */
    public String getString() {
        return string;
    }

    /**
     *
     * @return int
     */
    public int getStartIndex() {
        return startIndex;
    }

    //public void setColumnCount(int i)
    //{
    //size = i;
    //}
    /**
     *
     * @return int
     */
    public int getColumnCount() {
        return columnCount;
    }


    /**
     *
     * @return int
     */
    public int getCountAfterCurrentAsterisk() {
        return countAfterCurrentAsterisk;
    }

    /**
     *
     * @param i int
     */
    public void pulsCountAfterCurrentAsterisk(int i) {
        countAfterCurrentAsterisk += i;
    }

    /**
     *
     * @return String
     */
    public String toString() {
        return string;
    }
}
