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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.Mapper;
import org.opoo.oqs.MapperException;
import org.opoo.oqs.QueryException;
import org.opoo.oqs.core.mapper.ArrayPropertyMapper;
import org.opoo.oqs.core.mapper.AsteriskPropertyMapper;
import org.opoo.oqs.core.mapper.BeanPropertyMapper;
import org.opoo.oqs.core.mapper.DynaBeanPropertyMapper;
import org.opoo.oqs.core.mapper.ListPropertyMapper;
import org.opoo.oqs.core.mapper.MapPropertyMapper;
import org.opoo.oqs.core.mapper.PropertyMapper;
import org.opoo.oqs.core.mapper.SinglePropertyMapper;
import org.opoo.util.ClassUtils;
import org.opoo.util.StringUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class DefaultSelectParser implements SelectParser {
    private static final Log log = LogFactory.getLog(DefaultSelectParser.class);
    public DefaultSelectParser() {
    }

    /**
     *
     * @param selectString String
     * @param beanClassLoader ClassLoader
     * @return Mapper
     */
    public Mapper parse(String selectString, ClassLoader beanClassLoader) {
        PropertyMapper pm = SelectParserUtils.parsePropertyMapper(selectString,
                beanClassLoader);
        log.debug("[MPS]: " + pm.getMapperString());
        return pm;
    }


    /**
     * 解析select子句的工具类，跟实现有关，因此封装在内部
     */
    public static class SelectParserUtils {
        public static final String ASTERISK_REGEX = "(\\w+\\.)?\\*\\((\\d+)\\)";
        public static final Pattern ASTERISK_PATTERN = Pattern.compile(
                ASTERISK_REGEX);

        public static class Index {
            private int i = 0;
            private int delta = 1;
            private AsteriskProperty ap;
            public int next() {
                return next(1);
            }

            public int next(int count) {
                if (ap == null) {
                    i += delta;
                    delta = count;
                    return i;
                } else {
                    ap.pulsCountAfterCurrentAsterisk(count);
                    return -1;
                }
            }

            public int current() {
                return i;
            }

            public void setUncountedAsteriskProperty(AsteriskProperty sap) {
                if (this.ap != null) {
                    throw new MapperException("最多只允许一个 '*'。");
                } else {
                    this.ap = sap;
                }
            }
        }


        public static PropertyMapper parsePropertyMapper(String qs,
                ClassLoader beanClassLoader) {
            Index index = new Index();
            PropertyMapper[] prms = parsePropertyMappers(qs, index,
                    beanClassLoader);
            if (prms.length == 1) {
                return prms[0];
            } else if (prms.length > 1) {
                return new ArrayPropertyMapper("root", "root", prms);
            } else {
                throw new IllegalArgumentException(
                        "cannot parse select string: " + qs);
            }
        }

        private static PropertyMapper[] parsePropertyMappers(String queryString,
                Index index, ClassLoader beanClassLoader) {
            List list = new ArrayList();
            StringTokenizer st = new StringTokenizer(queryString, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                //不是new
                if (!s.toLowerCase().startsWith("new ")) {
                    list.add(createSimplePropertyMapper(s, index));
                } else {
                    int c1 = StringUtils.countUnquoted(s, ')');
                    int c2 = StringUtils.countUnquoted(s, '(');
                    while (c1 != c2) {
                        String s2 = st.nextToken();
                        c1 += StringUtils.countUnquoted(s2, ')');
                        c2 += StringUtils.countUnquoted(s2, '(');
                        s += "," + s2;
                    }
                    int pos1 = s.indexOf('(');
                    int pos2 = s.lastIndexOf(')');
                    String type = s.substring(4, pos1).trim();
                    String inner = s.substring(pos1 + 1, pos2);
                    String ps = type + index.current() + " " +
                                s.substring(pos2 + 1);
                    //String alias = getAlias(s.substring(pos2 + 1));
                    //boolean invalid = StringHelper.nullOrBlank(alias);

                    PropertyMapper[] mappers = parsePropertyMappers(inner,
                            index, beanClassLoader);
                    Property sp = parseProperty(ps);
                    //PropertyMapper m = new ListQueryRowMapper(sp, mappers);
                    PropertyMapper m = createComplextPropertyMapper(sp, mappers,
                            type, beanClassLoader);
                    list.add(m);
                }
            }
            return (PropertyMapper[]) list.toArray(new PropertyMapper[list.size()]);
        }

        /**
         * 解析非new的类型
         * @param s String
         * @param index Index
         * @return PropertyMapper
         */
        private static PropertyMapper createSimplePropertyMapper(String s,
                Index index) {
            s = s.trim();
            Matcher matcher = ASTERISK_PATTERN.matcher(s);
            if ("*".equals(s) || s.endsWith(".*")) {
                AsteriskProperty ap = new AsteriskProperty(s, index.next());
                index.setUncountedAsteriskProperty(ap);
                return new AsteriskPropertyMapper(ap);
            } else if (matcher.matches()) {
                String sstr = matcher.group(1);
                String scnt = matcher.group(2);
                int cnt = Integer.parseInt(scnt);
                sstr = (sstr == null) ? "*" : sstr + "*";

                AsteriskProperty ap = new AsteriskProperty(sstr, index.next(cnt),
                        cnt);
                return new AsteriskPropertyMapper(ap);
            } else {
                Property sp = parseProperty(s);
                return new SinglePropertyMapper(sp.getName(), sp.getString(),
                                                index.next());
            }
        }

        private static Property parseProperty(String s) {
            int positionOfAs = s.toLowerCase().indexOf(" as ");
            if (positionOfAs != -1) { //remove as
                s = s.substring(0, positionOfAs)
                    + s.substring(positionOfAs + 3);
            }
            if (s.indexOf("(") != -1) {
                return createProperty1(s);
            } else {
                return createProperty2(s);
            }
        }

        private static Property createProperty1(String s) {
            int p = s.lastIndexOf(")");
            if (p == -1) {
                throw new QueryException("syntax error: " + s);
            }
            String name = null;
            String string = s.substring(0, p + 1);
            if (s.length() > p + 1) {
                StringTokenizer st = new StringTokenizer(s.substring(p + 1),
                        " ");
                while (st.hasMoreTokens()) {
                    name = st.nextToken();
                }
            } else {
                name = null;
            }
             return new SimpleProperty(processName(name), string);
        }

        private static Property createProperty2(String s) {
            StringTokenizer st = new StringTokenizer(s, " ");
            List tokens = new ArrayList();
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
            String string = "", name = null;
            int size = tokens.size();
            if (size < 1) {
                throw new QueryException("syntax error.");
            }
            if (size == 1) {
                string = name = (String) tokens.get(0);
                if (name.indexOf('\'') != -1) {
                    name = null;
                }
            } else {
                name = (String) tokens.get(size - 1);
                for (int i = 0, n = size - 1; i < n; i++) {
                    string += tokens.get(i);
                }
            }
            return new SimpleProperty(processName(name), string);
        }

        private static String processName(String name) {
            if (name != null) {
                return name.replace('\'', ' ').replace('`', ' ').trim();
            }
            return null;
        }

        private static PropertyMapper createComplextPropertyMapper(Property sp,
                PropertyMapper[] mappers,
                String type, ClassLoader beanClassLoader) {
            String t = type.toLowerCase();
            if ("map".equals(t)) {
                return new MapPropertyMapper(sp, mappers);
            } else if ("list".equals(t)) {
                return new ListPropertyMapper(sp, mappers);
            } else if ("dynabean".equals(t)) {
                return new DynaBeanPropertyMapper(sp, mappers);
            } else {
                return createBeanPropertyMapper(sp, mappers, type,
                                                beanClassLoader);
            }
        }

        private static PropertyMapper createBeanPropertyMapper(Property sp,
                PropertyMapper[] mappers,
                String type, ClassLoader beanClassLoader) {
            try {
                Class beanClass = ClassUtils.forName(type, beanClassLoader);
                log.debug("bean class: " + beanClass);
                return new BeanPropertyMapper(sp, mappers, beanClass);
            } catch (ClassNotFoundException ex) {
                throw new MapperException(ex);
            }
        }
    }


    public static void main(String[] args) {
    }
}
