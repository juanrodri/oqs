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

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.CannotCreateQueryException;
import org.opoo.oqs.CannotCreateQueryFactoryException;
import org.opoo.oqs.Criteria;
import org.opoo.oqs.Oqs;
import org.opoo.oqs.QueryFactory;
import org.opoo.oqs.dialect.Dialect;
import org.opoo.oqs.dialect.HibernateDialectWrapper;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.DataSourceAware;
import org.opoo.util.ClassUtils;

/**
 * 实现接口<tt>QueryFactory</tt>部分方法的抽象类。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since OQS1.0
 */
public abstract class AbstractQueryFactory implements QueryFactory,
        DataSourceAware {
    private static Log log = LogFactory.getLog(QueryFactory.class);
    private DataSource dataSource;
    private ConnectionManager connectionManager;

    /**
     * 数据库Dialect。
     */
    Dialect dialect;
    int debugLevel = 0;
    ClassLoader beanClassLoader = null;

    static {
	log.info("Initializing " + Oqs.getSpecificationTitle()
	 + ": " + Oqs.getSpecificationVersion());
	System.out.println(Oqs.getOqsInfo());
    }

    public AbstractQueryFactory() {
        if (log.isDebugEnabled()) {
            log.debug("OQS QueryFactory: " + getClass().getName());
        }
    }

    public AbstractQueryFactory(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        //connectionManager = new SimpleConnectionManager(dataSource);
        //setConnectionManager(new SimpleConnectionManager(dataSource));
        //setConnectionManager(new TransactionSupportConnectionManager(dataSource));
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * 设置当前QueryFactory要使用的ConnectionManager。
     * @param connectionManager ConnectionManager
     */
    protected void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        log.debug("Using ConnectionManager: " +
                  connectionManager.getClass().getName());
    }

    /**
     * 设置查询过程中SQL调试信息的显示级别。<br>
     * <ul>
     * <li>1 show sql only
     * <li>2 show sql and middle processing query string
     * <li>0 nothing
     * </ul>
     * 设置当前QueryFactory创建的Query的SQL调试信息显示级别。
     * @param debugLevel int
     */
    public void setShowSql(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    /**
     * 设置所使用的Dialect的类全名。
     * @param classname String
     * @throws CannotCreateQueryFactoryException
     */
    public void setDialectClassName(String classname) throws
            CannotCreateQueryFactoryException {
        try {
            Object object = ClassUtils.newInstance(classname);
            if (object instanceof org.hibernate.dialect.Dialect) {
                setDialect((org.hibernate.dialect.Dialect) object);
            } else {
                dialect = (Dialect) object;
            }
        } catch (Exception ex) {
            throw new CannotCreateQueryFactoryException(
                    "Can not create Dialect.", ex);
        }
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDialect(org.hibernate.dialect.Dialect dialect) {
        setDialect(new HibernateDialectWrapper(dialect));
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /**
     *
     * @param queryString String
     * @return Criteria
     * @throws CannotCreateQueryException
     */
    public Criteria createCriteria(String queryString) throws
            CannotCreateQueryException {
        return new DefaultCriteria(this, queryString);
    }





    private static AbstractQueryFactory createQueryFactory(String
            factoryClassName) throws
            CannotCreateQueryFactoryException {
        try {
            Class clazz = ClassUtils.forName(factoryClassName,
                                             AbstractQuery.class.getClassLoader());
            return (AbstractQueryFactory) clazz.newInstance();
        } catch (Exception ex) {
            log.error("CacheFactory.createFactory", ex);
            throw new CannotCreateQueryFactoryException(
                    "Cannot create QueryFactory, " +
                    factoryClassName, ex);
        }
    }


    /**
     * 用指定的<tt>QueryFactory</tt>实现类全名和给定的参数-值 Map创建
     * <tt>QueryFactory</tt>实例。比createQueryFactory(DataSource)更灵活。
     * <pre>
     * Sample:
     *  DataSource dataSource = ...;
     *  String className = "org.opoo.oqs.spring.SpringQueryFactoryImpl";
     *  Map map = new HashMap();
     *  map.put("dataSource", dataSource);
     *  *** 当使用ConnectionManager的其他实现时，请用map.put("connectionManager",
     *  *** youConnectionManager)代替上面的dataSource项。
     *  map.put("dialectClass", "org.opoo.oqs.dialect.MySQLDialect");
     *  map.put("showSql", new Integer(2));
     *  QueryFactory qf = AbstractQueryFactory.createQueryFactory(className, map);
     *
     * </pre>
     *
     * @param factoryClassName String
     * @param properties Map
     * @return QueryFactory
     * @throws CannotCreateQueryFactoryException
     */
    public static QueryFactory createQueryFactory(String factoryClassName,
                                                  Map properties) throws
            CannotCreateQueryFactoryException {

        try {
            AbstractQueryFactory qf = AbstractQueryFactory.createQueryFactory(factoryClassName);
            if (qf != null) {
                ClassUtils.populate(qf, properties);
                if (log.isDebugEnabled()) {
                    log.debug("QueryFactory " + factoryClassName + " created.");
                }
                return qf;
            }
        } catch (Exception ex) {
            throw new CannotCreateQueryFactoryException("Create QueryFactory "
                    + factoryClassName, ex);
        }
        return null;
    }
}
