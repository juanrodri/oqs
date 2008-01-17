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
package org.opoo.oqs.spring.transaction;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.QueryFactory;
import org.opoo.oqs.core.AbstractQueryFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


/**
 * 用于在Spring ApplicationContext中配置声明性的事务的类。
 *
 * <p>
 * 此类继承了{@link DataSourceTransactionManager}，创建时系统自动探测
 * <tt>QueryFactory</tt>采用了何种实现（{@link org.opoo.oqs.core.QueryFactoryImpl}还是
 * {@link org.opoo.oqs.spring.SpringQueryFactoryImpl}），
 * 然后决定是否采用{@link DataSourceTransactionManager}
 * 的事务处理实现。因此
 * 此类可用于两种QueryFactory实现的方式，用户不必确切的知道您的<tt>QueryFactory</tt>
 * 采用了那种实现。
 *
 * <p>在声明性事务配置中，如果您确切地知道你的<tt>QueryFactory</tt>采用了
 * {@link org.opoo.oqs.spring.SpringQueryFactoryImpl}，那也可以
 * 直接使用{@link DataSourceTransactionManager}。 *
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SpringTransactionManager extends DataSourceTransactionManager {
    private static final Log log = LogFactory.getLog(SpringTransactionManager.class);

    public SpringTransactionManager() {
        super();
    }

    public SpringTransactionManager(QueryFactory qf) {
        this();
        setQueryFactory(qf);
    }

    public void setQueryFactory(QueryFactory qf) {
        DataSource ds = ((AbstractQueryFactory) qf).getDataSource();
        setDataSource(ds);
    }
}
