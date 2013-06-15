package com.tooyoung.common.db;
/*
 * Copyright 2002-2007 the original author or authors.
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

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * Base class for {@link org.springframework.jdbc.core.JdbcTemplate} and
 * other JDBC-accessing DAO helpers, defining common properties such as
 * DataSource and exception translator.
 *
 * <p>Not intended to be used directly.
 * See {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Juergen Hoeller
 * @since 28.11.2003
 * @see org.springframework.jdbc.core.JdbcTemplate
 */
public abstract class JdbcAccessor implements InitializingBean {

	/** Logger available to subclasses */
	//protected final Log logger = LogFactory.getLog(getClass());

	private boolean lazyInit = true;

	/**
	 * Set whether to lazily initialize the SQLExceptionTranslator for this accessor,
	 * on first encounter of a SQLException. Default is "true"; can be switched to
	 * "false" for initialization on startup.
	 * <p>Early initialization just applies if <code>afterPropertiesSet()</code> is called.
	 * @see #getExceptionTranslator()
	 * @see #afterPropertiesSet()
	 */
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return whether to lazily initialize the SQLExceptionTranslator for this accessor.
	 * @see #getExceptionTranslator()
	 */
	public boolean isLazyInit() {
		return this.lazyInit;
	}

	/**
	 * Eagerly initialize the exception translator, if demanded,
	 * creating a default one for the specified DataSource if none set.
	 */
	public void afterPropertiesSet(DataSource dataSource) {
		if (dataSource == null) {
			throw new IllegalArgumentException("Property 'dataSource' is required");
		}
		if (!isLazyInit()) {
			getExceptionTranslator(dataSource);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//throw new UnsupportedOperationException("Error: not support afterPropertiesSet()");
	}
	
	public abstract SQLExceptionTranslator getExceptionTranslator(DataSource dataSource);

}
