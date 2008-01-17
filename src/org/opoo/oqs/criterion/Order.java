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
package org.opoo.oqs.criterion;

import java.util.Iterator;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class Order {
    private boolean ascending;
    private boolean ignoreCase;
    private String propertyName;
    private Order root;
    private Order next;

    /**
     * Constructor for Order.
     */
    protected Order(String propertyName, boolean ascending) {
        this(propertyName, ascending, true);
    }

    protected Order(String propertyName, boolean ascending, boolean ignoreCase) {
        this.propertyName = propertyName;
        this.ascending = ascending;
        this.ignoreCase = ignoreCase;
        this.root = this;
        this.next = null;

    }

    /**
     * Return the name of the bean property to compare.
     * Can also be a nested bean property path.
     */
    public String getProperty() {
        return this.propertyName;
    }

    /**
     * Return whether upper and lower case in String values should be ignored.
     */
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    /**
     * Return whether to sort ascending (true) or descending (false).
     */
    public boolean isAscending() {
        return this.ascending;
    }


    public Order add(Order order) {
        Order current = this;
        while (current.next != null) {
            current = current.next;
        }
        current.next = order;
        order.root = this.root;
        return order;
    }

    public String rootToString() {
        //return propertyName + ' ' + (ascending ? "asc" : "desc");
        Order order = root;
        StringBuffer result = new StringBuffer(order.toString());
        while (order.next != null) {
            order = order.next;
            result.append(", " + order.toString());
        }
        return result.toString();
    }


    public String rootToString(boolean encoded) {
        if (encoded) {
            return OrderEncoder.encode(rootToString());
        }
        return rootToString();
    }

    public String toString() {
        return propertyName + ' ' + (ascending ? "asc" : "desc");
    }

    public Iterator iterate() {
        return new OrderIterator(root);
    }

    private static class OrderIterator implements Iterator {
        private Order order;
        private Order current;
        public OrderIterator(Order root) {
            this.order = root;
        }

        public boolean hasNext() {
            if (order != null) {
                current = order;
                order = order.next;
                return true;
            }
            return false;
        }

        public Object next() {
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Ascending order
     *
     * @param propertyName
     * @return Order
     */
    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    /**
     * Descending order
     *
     * @param propertyName
     * @return Order
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }


    public static Order valueOf(String string) {
        if (string == null || (string = string.trim()).length() < 2
                                        || "null".equalsIgnoreCase(string)) {
            return null;
        }

        String[] aa = string.split(",");
        Order order = null;
        for (int i = 0; i < aa.length; i++) {
            Order current = Order.valueOfString(aa[i]);
            if (order == null) {
                order = current;
            } else {
                order.add(current);
                order = order.next;
            }
        }
        return order;
    }

    public static Order valueOf(String string, boolean encoded) {
        if (encoded) {
            string = OrderEncoder.decode(string);
        }
        return valueOf(string);
    }

    private static Order valueOfString(String string) {
        if (string == null || (string = string.trim()).length() < 2 ||
                                        "null".equalsIgnoreCase(string)) {
            return null;
        }
        String f = string.substring(1);
        return new Order(f, string.charAt(0) == '1');
    }


    public static class OrderEncoder {
        private static final char[] SSRC =
                "0123456789abcdefghijklmnopqrstuvwxyz,._-ABCDEFGHIJKLMNOPQRSTUVWXYZ".
                toCharArray();
        private static final char[] SDES =
                "30m7Def2l.4q58YB9dJKiLIvMkosthz,QSEbcGwg_ja6AC1NHpVxTPFUO-WnXuRryZ".
                toCharArray();
        private static final int[] KEY = {3, 17, 31, 5, 43, 7, 23, 19, 24, 3, 9,
                                         18, 24, 30, 53, 47, 40, 12};

        private static final int SL = SSRC.length;
        private static final int KL = KEY.length;

        private static int indexOf(char[] ca, char c) {
            for (int i = 0; i < ca.length; i++) {
                if (ca[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        public static String encode(String string) {
            char[] src = string.toCharArray();
            StringBuffer result = new StringBuffer(src.length);
            int kindex = 0;
            int slen = src.length;
            for (int i = 0; i < slen; i++) {
                //char c = src[i];
                int pos = indexOf(SSRC, src[i]);
                if (pos == -1) {
                    throw new IllegalArgumentException(string +
                            " cannot be encoded");
                }
                pos += KEY[kindex];

                if (pos >= SL) {
                    pos -= SL;
                }
                result.append(SDES[pos]);
                if (++kindex >= KL) {
                    kindex -= KL;
                }
            }
            return result.toString();
        }

        public static String decode(String string) {
            char[] src = string.toCharArray();
            StringBuffer result = new StringBuffer();
            int kindex = 0;
            int slen = src.length;
            for (int i = 0; i < slen; i++) {
                //char c = string.charAt(i);
                int pos = indexOf(SDES, src[i]); //S2.indexOf(c);
                if (pos == -1) {
                    throw new IllegalArgumentException(string +
                            " cannot be encoded");
                }
                pos -= KEY[kindex];

                if (pos < 0) {
                    pos += SL;
                }
                result.append(SSRC[pos]);

                if (++kindex >= KL) {
                    kindex -= KL;
                }
            }
            return result.toString();
        }
    }
}
