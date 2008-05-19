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
package org.opoo.dao.support;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class Page {
    public static final Page FIRST = new Page(1, 0);
    private int number;
    private int start;

    Page(int number, int start) {
        this.number = number;
        this.start = start;
    }

    public Page() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public static Page getPrevious(Page page, int range) {
        return new Page(page.getNumber() - 1, page.getStart() - range);
    }

    public static Page getNext(Page page, int range) {
        return new Page(page.getNumber() + 1, page.getStart() + range);
    }
}
