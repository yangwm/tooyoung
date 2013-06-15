package cc.tooyoung.common.db;

/*
 * Copyright 2002-2008 the original author or authors.
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.core.CollectionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.TimeStatUtil;


/**
 * <b> To get jdbcTemplate from m-s dbs </b>
 * 
 * <p>We can freely update the get strategy, for we can change read/write datasource if the datasource is crash or cannot connect,
 * we can retry get connection if some connection is stale
 * </p>
 * 
 * @author fishermen
 *
 */
public class JdbcTemplate extends JdbcAccessor{

	private DataSource dataSource;
	private List<DataSource> dataSourceSlaves = new ArrayList<DataSource>();
	private int readTryGetConCount = 10;
	private int writeTryGetConCount = 10;
	private int resource;
	
	private Map<DataSource, SQLExceptionTranslator> exceptionTranslators = new ConcurrentHashMap<DataSource, SQLExceptionTranslator>();
	
	private Random random = new Random();
		
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;		
		
		try {
			String port = ((com.mchange.v2.c3p0.ComboPooledDataSource)dataSource).getJdbcUrl().split(":")[3].split("/")[0];
			resource = TimeStatUtil.DB_TYPE + Integer.parseInt(port);
			TimeStatUtil.register(resource);
		} catch (Exception e) {
			//port null
		}
	}

	public void setDataSourceSlaves(List<DataSource> dataSourceSlaves) {
		this.dataSourceSlaves = dataSourceSlaves;
		
		try {
			String port = ((com.mchange.v2.c3p0.ComboPooledDataSource)dataSourceSlaves.get(0)).getJdbcUrl().split(":")[3].split("/")[0];
			resource = TimeStatUtil.DB_TYPE + Integer.parseInt(port);
			TimeStatUtil.register(resource);
		} catch (Exception e) {
		    //port null
		}
	}
	
	public int getReadTryGetConCount() {
		return this.readTryGetConCount;
	}
	
	public void setReadTryGetConCount(int readTryGetConCount) {
		this.readTryGetConCount = readTryGetConCount;
	}
	
	public int getWriteTryGetConCount() {
		return this.readTryGetConCount;
	}
	
	
	public void setWriteTryGetConCount(int writeTryGetConCount) {
		this.writeTryGetConCount = writeTryGetConCount;
	}
	
	/**
	 * model: master: write & read; slave: read
	 * @param isWrite
	 * @return
	 */
	public DataSource getDataSource(boolean isWrite) {		
		if(isWrite || dataSourceSlaves == null || dataSourceSlaves.size() == 0){			
			return dataSource;
		}else{
			if(dataSourceSlaves.size() == 1){				
				return dataSourceSlaves.get(0);
			}else {				
				int rd = random.nextInt(dataSourceSlaves.size());
				return dataSourceSlaves.get(rd);							
			}			
		}
	}

	//private static final String RETURN_RESULT_SET_PREFIX = "#result-set-";

	//private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";


	/** Custom NativeJdbcExtractor */
	private NativeJdbcExtractor nativeJdbcExtractor;

	/** If this variable is false, we will throw exceptions on SQL warnings */
	private boolean ignoreWarnings = true;

	/**
	 * If this variable is set to a non-zero value, it will be used for setting the
	 * fetchSize property on statements used for query processing.
	 */
	private int fetchSize = 0;

	/**
	 * If this variable is set to a non-zero value, it will be used for setting the
	 * maxRows property on statements used for query processing.
	 */
	private int maxRows = 0;

	/**
	 * If this variable is set to a non-zero value, it will be used for setting the
	 * queryTimeout property on statements used for query processing.
	 */
	private int queryTimeout = 0;

	/**
	 * If this variable is set to true then all results checking will be bypassed for any
	 * callable statement processing.  This can be used to avoid a bug in some older Oracle
	 * JDBC drivers like 10.1.0.2.
	 */
	private boolean skipResultsProcessing = false;

	/**
	 * If this variable is set to true then all results from a stored procedure call
	 * that don't have a corresponding SqlOutParameter declaration will be bypassed.
	 * All other results processng will be take place unless the variable 
	 * <code>skipResultsProcessing</code> is set to <code>true</code> 
	 */
	private boolean skipUndeclaredResults = false;

	/**
	 * If this variable is set to true then execution of a CallableStatement will return
	 * the results in a Map that uses case insensitive names for the parameters if
	 * Commons Collections is available on the classpath.
	 */
	private boolean resultsMapCaseInsensitive = false;


	/**
	 * Construct a new JdbcTemplate for bean usage.
	 * <p>Note: The DataSource has to be set before using the instance.
	 * @see #setDataSource
	 */
	public JdbcTemplate() {
		//checkHealthy();
	}

	/**
	 * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
	 * <p>Note: This will not trigger initialization of the exception translator.
	 * @param dataSource the JDBC DataSource to obtain connections from
	 */
	public JdbcTemplate(DataSource dataSource) {
		//setDataSource(dataSource);
		afterPropertiesSet(dataSource);
		//checkHealthy();
	}

	/**
	 * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
	 * <p>Note: Depending on the "lazyInit" flag, initialization of the exception translator
	 * will be triggered.
	 * @param dataSource the JDBC DataSource to obtain connections from
	 * @param lazyInit whether to lazily initialize the SQLExceptionTranslator
	 */
	public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
		//setDataSource(dataSource);
		setLazyInit(lazyInit);
		afterPropertiesSet(dataSource);
		//checkHealthy();
	}


	/**
	 * Set a NativeJdbcExtractor to extract native JDBC objects from wrapped handles.
	 * Useful if native Statement and/or ResultSet handles are expected for casting
	 * to database-specific implementation classes, but a connection pool that wraps
	 * JDBC objects is used (note: <i>any</i> pool will return wrapped Connections).
	 */
	public void setNativeJdbcExtractor(NativeJdbcExtractor extractor) {
		this.nativeJdbcExtractor = extractor;
	}

	/**
	 * Return the current NativeJdbcExtractor implementation.
	 */
	public NativeJdbcExtractor getNativeJdbcExtractor() {
		return this.nativeJdbcExtractor;
	}

	/**
	 * Set whether or not we want to ignore SQLWarnings.
	 * <p>Default is "true", swallowing and logging all warnings. Switch this flag
	 * to "false" to make the JdbcTemplate throw a SQLWarningException instead.
	 * @see java.sql.SQLWarning
	 * @see org.springframework.jdbc.SQLWarningException
	 * @see #handleWarnings
	 */
	public void setIgnoreWarnings(boolean ignoreWarnings) {
		this.ignoreWarnings = ignoreWarnings;
	}

	/**
	 * Return whether or not we ignore SQLWarnings.
	 */
	public boolean isIgnoreWarnings() {
		return this.ignoreWarnings;
	}

	/**
	 * Set the fetch size for this JdbcTemplate. This is important for processing
	 * large result sets: Setting this higher than the default value will increase
	 * processing speed at the cost of memory consumption; setting this lower can
	 * avoid transferring row data that will never be read by the application.
	 * <p>Default is 0, indicating to use the JDBC driver's default.
	 * @see java.sql.Statement#setFetchSize
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Return the fetch size specified for this JdbcTemplate.
	 */
	public int getFetchSize() {
		return this.fetchSize;
	}

	/**
	 * Set the maximum number of rows for this JdbcTemplate. This is important
	 * for processing subsets of large result sets, avoiding to read and hold
	 * the entire result set in the database or in the JDBC driver if we're
	 * never interested in the entire result in the first place (for example,
	 * when performing searches that might return a large number of matches).
	 * <p>Default is 0, indicating to use the JDBC driver's default.
	 * @see java.sql.Statement#setMaxRows
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	/**
	 * Return the maximum number of rows specified for this JdbcTemplate.
	 */
	public int getMaxRows() {
		return this.maxRows;
	}

	/**
	 * Set the query timeout for statements that this JdbcTemplate executes.
	 * <p>Default is 0, indicating to use the JDBC driver's default.
	 * <p>Note: Any timeout specified here will be overridden by the remaining
	 * transaction timeout when executing within a transaction that has a
	 * timeout specified at the transaction level.
	 * @see java.sql.Statement#setQueryTimeout
	 */
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	/**
	 * Return the query timeout for statements that this JdbcTemplate executes.
	 */
	public int getQueryTimeout() {
		return this.queryTimeout;
	}

	/**
	 * Set whether results processing should be skipped.  Can be used to optimize callable
	 * statement processing when we know that no results are being passed back - the processing
	 * of out parameter will still take place.  This can be used to avoid a bug in some older
	 * Oracle JDBC drivers like 10.1.0.2.
	 */
	public void setSkipResultsProcessing(boolean skipResultsProcessing) {
		this.skipResultsProcessing = skipResultsProcessing;
	}

	/**
	 * Return whether results processing should be skipped.
	 */
	public boolean isSkipResultsProcessing() {
		return this.skipResultsProcessing;
	}

	/**
	 * Set whether undelared results should be skipped.
	 */
	public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
		this.skipUndeclaredResults = skipUndeclaredResults;
	}

	/**
	 * Return whether undeclared results should be skipped.
	 */
	public boolean isSkipUndeclaredResults() {
		return this.skipUndeclaredResults;
	}

	/**
	 * Set whether execution of a CallableStatement will return the results in a Map
	 * that uses case insensitive names for the parameters.
	 */
	public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
		this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
	}

	/**
	 * Return whether execution of a CallableStatement will return the results in a Map
	 * that uses case insensitive names for the parameters.
	 */
	public boolean isResultsMapCaseInsensitive() {
		return this.resultsMapCaseInsensitive;
	}


	//-------------------------------------------------------------------------
	// Methods dealing with a plain java.sql.Connection
	//-------------------------------------------------------------------------

	
	/**
	 * Return the exception translator for this instance.
	 * <p>Creates a default {@link SQLErrorCodeSQLExceptionTranslator}
	 * for the specified DataSource if none set, or a
	 * {@link SQLStateSQLExceptionTranslator} in case of no DataSource.
	 * @see #getDataSource()
	 */
	public SQLExceptionTranslator getExceptionTranslator(DataSource dataSource) {
		SQLExceptionTranslator e = exceptionTranslators.get(dataSource);
		synchronized (exceptionTranslators) {
			if (e == null) {
				if (dataSource != null) {
					e = new SQLErrorCodeSQLExceptionTranslator(dataSource);
				}
				else {
					e = new SQLStateSQLExceptionTranslator();
				}
				exceptionTranslators.put(dataSource, e);
			}
		}		
		
		return e;
	}

	/**
	 * Create a close-suppressing proxy for the given JDBC Connection.
	 * Called by the <code>execute</code> method.
	 * <p>The proxy also prepares returned JDBC Statements, applying
	 * statement settings such as fetch size, max rows, and query timeout.
	 * @param con the JDBC Connection to create a proxy for
	 * @return the Connection proxy
	 * @see java.sql.Connection#close()
	 * @see #execute(ConnectionCallback)
	 * @see #applyStatementSettings
	 */
	protected Connection createConnectionProxy(DataSource dataSource, Connection con) {
		return (Connection) Proxy.newProxyInstance(
				ConnectionProxy.class.getClassLoader(),
				new Class[] {ConnectionProxy.class},
				new CloseSuppressingInvocationHandler(dataSource, con));
	}


	//-------------------------------------------------------------------------
	// Methods dealing with static SQL (java.sql.Statement)
	//-------------------------------------------------------------------------

	public Object execute(StatementCallback action, boolean isWrite) throws DataAccessException {
		Assert.notNull(action, "Callback object must not be null");

		long start =System.currentTimeMillis();
		
		DataSource ds = getDataSource(isWrite);
		Connection con = safeGetConnection(ds, isWrite);
		Statement stmt = null;
		try {
			Connection conToUse = con;
			if (this.nativeJdbcExtractor != null &&
					this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
				conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
			}
			stmt = conToUse.createStatement();
			applyStatementSettings(ds, stmt);
			Statement stmtToUse = stmt;
			if (this.nativeJdbcExtractor != null) {
				stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
			}
			Object result = action.doInStatement(stmtToUse);
			handleWarnings(stmt);
			return result;
		}
		catch (Exception ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			DataSourceUtils.releaseConnection(con, ds);
			con = null;
			if (ex instanceof SQLException) {
				throw getExceptionTranslator(ds).translate("StatementCallback", getSql(action), (SQLException) ex);
			} else {
				throw new RuntimeException("StatementCallback " + getSql(action), ex);
			}
		}
		finally {
			JdbcUtils.closeStatement(stmt);
			DataSourceUtils.releaseConnection(con, ds);
			
			//add slow log
			long useTime =System.currentTimeMillis() -start;
			if(useTime > ApiLogger.DB_FIRE_TIME){
				ApiLogger.fire(new StringBuffer().append("DB ").append(((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl()).append(" too slow :").append(useTime).append(" isWrite:").append(isWrite));
			}
			
			TimeStatUtil.addElapseTimeStat(resource, isWrite, start, useTime);
		}
	}
	
	/**
	 * try 3 times to get connection
	 * @param ds
	 * @return
	 * @throws CannotGetJdbcConnectionException
	 */
	private Connection safeGetConnection(DataSource ds, boolean isWrite) throws CannotGetJdbcConnectionException{		
		Connection con = null;
		int retryCount, count;
		retryCount = count = (isWrite ? writeTryGetConCount : readTryGetConCount);
		while(count-- > 0){
			try {
				con = DataSourceUtils.getConnection(ds);
				return con;
			} catch (CannotGetJdbcConnectionException e) {
				ApiLogger.info(new StringBuilder(64).append("get connection try count:").append((retryCount - count)).append(", ds=").append(((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl()));
				DataSourceUtils.releaseConnection(con, ds);
			}
		}

		ApiLogger.fire(new StringBuffer().append("DB ").append(((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl()).append(" Error:").append("Could not get JDBC Connection: "));
		throw new CannotGetJdbcConnectionException("Could not get JDBC Connection: " + ", ds=" + ((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl(),new SQLException());
	}

	public Object query(final String sql, final ResultSetExtractor rse) throws DataAccessException {
		Assert.notNull(sql, "SQL must not be null");
		Assert.notNull(rse, "ResultSetExtractor must not be null");
		if (ApiLogger.isTraceEnabled()) {
			ApiLogger.trace(new StringBuilder(64).append("Executing SQL query [").append(sql).append("]"));
		}

		class QueryStatementCallback implements StatementCallback, SqlProvider {
			public Object doInStatement(Statement stmt) throws SQLException {
				ResultSet rs = null;
				try {
					rs = stmt.executeQuery(sql);
					ResultSet rsToUse = rs;
					if (nativeJdbcExtractor != null) {
						rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
					}
					return rse.extractData(rsToUse);
				}
				finally {
					JdbcUtils.closeResultSet(rs);
				}
			}
			public String getSql() {
				return sql;
			}
		}
		return execute(new QueryStatementCallback(), false);
	}

	public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
		query(sql, new RowCallbackHandlerResultSetExtractor(rch));
	}

	@SuppressWarnings("unchecked")
	public List query(String sql, RowMapper rowMapper) throws DataAccessException {
		return (List) query(sql, new RowMapperResultSetExtractor(rowMapper));
	}

	@SuppressWarnings("unchecked")
	public Map queryForMap(String sql) throws DataAccessException {
		return (Map) queryForObject(sql, getColumnMapRowMapper());
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, RowMapper rowMapper) throws DataAccessException {
		List results = query(sql, rowMapper);
		return DataAccessUtils.requiredSingleResult(results);
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, Class requiredType) throws DataAccessException {
		return queryForObject(sql, getSingleColumnRowMapper(requiredType));
	}

	public long queryForLong(String sql) throws DataAccessException {
		Number number = (Number) queryForObject(sql, Long.class);
		return (number != null ? number.longValue() : 0);
	}

	public int queryForInt(String sql) throws DataAccessException {
		Number number = (Number) queryForObject(sql, Integer.class);
		return (number != null ? number.intValue() : 0);
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Class elementType) throws DataAccessException {
		return query(sql, getSingleColumnRowMapper(elementType));
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql) throws DataAccessException {
		return query(sql, getColumnMapRowMapper());
	}

	public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
		return (SqlRowSet) query(sql, new SqlRowSetResultSetExtractor());
	}

	public int update(final String sql) throws DataAccessException {
		Assert.notNull(sql, "SQL must not be null");
		if (ApiLogger.isTraceEnabled()) {
			ApiLogger.trace(new StringBuilder(64).append("Executing SQL update [").append(sql).append("]"));
		}

		class UpdateStatementCallback implements StatementCallback, SqlProvider {
			public Object doInStatement(Statement stmt) throws SQLException {
				int rows = stmt.executeUpdate(sql);
				if (ApiLogger.isTraceEnabled()) {
					ApiLogger.trace(new StringBuilder(64).append("SQL update affected ").append(rows).append(" rows"));
				}
				return new Integer(rows);
			}
			public String getSql() {
				return sql;
			}
		}
		return ((Integer) execute(new UpdateStatementCallback(), true)).intValue();
	}

	public int[] batchUpdate(final String[] sql) throws DataAccessException {
		Assert.notEmpty(sql, "SQL array must not be empty");
		if (ApiLogger.isTraceEnabled()) {
			ApiLogger.trace(new StringBuilder(128).append("Executing SQL batch update of ").append(sql.length).append(" statements"));
		}

		class BatchUpdateStatementCallback implements StatementCallback, SqlProvider {
			private String currSql;
			public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
				int[] rowsAffected = new int[sql.length];
				if (JdbcUtils.supportsBatchUpdates(stmt.getConnection())) {
					for (int i = 0; i < sql.length; i++) {
						this.currSql = sql[i];
						stmt.addBatch(sql[i]);
					}
					rowsAffected = stmt.executeBatch();
				}
				else {
					for (int i = 0; i < sql.length; i++) {
						this.currSql = sql[i];
						if (!stmt.execute(sql[i])) {
							rowsAffected[i] = stmt.getUpdateCount();
						}
						else {
							throw new InvalidDataAccessApiUsageException("Invalid batch SQL statement: " + sql[i]);
						}
					}
				}
				return rowsAffected;
			}
			public String getSql() {
				return currSql;
			}
		}
		return (int[]) execute(new BatchUpdateStatementCallback(), true);
	}


	//-------------------------------------------------------------------------
	// Methods dealing with prepared statements
	//-------------------------------------------------------------------------

	public Object execute(PreparedStatementCreator psc, PreparedStatementCallback action, boolean isWrite)
			throws DataAccessException {

		Assert.notNull(psc, "PreparedStatementCreator must not be null");
		Assert.notNull(action, "Callback object must not be null");
		if (ApiLogger.isTraceEnabled()) {
			String sql = getSql(psc);
			ApiLogger.trace(new StringBuilder(128).append("Executing prepared SQL statement").append((sql != null ? " [" + sql + "]" : "")));
		}

		long start = System.currentTimeMillis();
		DataSource ds = getDataSource(isWrite);
		Connection con = safeGetConnection(ds, isWrite);
		PreparedStatement ps = null;
		try {
			Connection conToUse = con;
			if (this.nativeJdbcExtractor != null &&
					this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativePreparedStatements()) {
				conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
			}
			ps = psc.createPreparedStatement(conToUse);
			applyStatementSettings(ds, ps);
			PreparedStatement psToUse = ps;
			if (this.nativeJdbcExtractor != null) {
				psToUse = this.nativeJdbcExtractor.getNativePreparedStatement(ps);
			}
			Object result = action.doInPreparedStatement(psToUse);
			handleWarnings(ps);
			return result;
		}
		catch (Exception ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			if (psc instanceof ParameterDisposer) {
				((ParameterDisposer) psc).cleanupParameters();
			}
			String sql = getSql(psc);
			psc = null;
			JdbcUtils.closeStatement(ps);
			ps = null;
			DataSourceUtils.releaseConnection(con, ds);
			con = null;
			if (ex instanceof SQLException) {
				throw getExceptionTranslator(ds).translate("PreparedStatementCallback", sql, (SQLException)ex);
			} else {
				throw new RuntimeException("PreparedStatementCallback " + getSql(psc), ex);
			}
		} 
		finally {
			
			//add slow log
			long useTime =System.currentTimeMillis() -start;
			if(useTime > ApiLogger.DB_FIRE_TIME){
				ApiLogger.fire(new StringBuffer().append("DB ").append(((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl()).append(" too slow :").append(useTime).append(" isWrite:").append(isWrite));
			}
			
			if (psc instanceof ParameterDisposer) {
				((ParameterDisposer) psc).cleanupParameters();
			}
			JdbcUtils.closeStatement(ps);
			DataSourceUtils.releaseConnection(con, ds);
			
			TimeStatUtil.addElapseTimeStat(resource, isWrite, start, useTime);
		}
	}

	public Object execute(String sql, PreparedStatementCallback action, boolean isWrite) throws DataAccessException {
		return execute(new SimplePreparedStatementCreator(sql), action, isWrite);
	}

	/**
	 * Query using a prepared statement, allowing for a PreparedStatementCreator
	 * and a PreparedStatementSetter. Most other query methods use this method,
	 * but application code will always work with either a creator or a setter.
	 * @param psc Callback handler that can create a PreparedStatement given a
	 * Connection
	 * @param pss object that knows how to set values on the prepared statement.
	 * If this is null, the SQL will be assumed to contain no bind parameters.
	 * @param rse object that will extract results.
	 * @return an arbitrary result object, as returned by the ResultSetExtractor
	 * @throws DataAccessException if there is any problem
	 */
	public Object query(
			PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor rse)
			throws DataAccessException {

		Assert.notNull(rse, "ResultSetExtractor must not be null");
		if(ApiLogger.isTraceEnabled())
			ApiLogger.trace("Executing prepared SQL query");

		return execute(psc, new PreparedStatementCallback() {
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException {
				ResultSet rs = null;
				try {
					if (pss != null) {
						pss.setValues(ps);
					}
					rs = ps.executeQuery();
					ResultSet rsToUse = rs;
					if (nativeJdbcExtractor != null) {
						rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
					}
					return rse.extractData(rsToUse);
				}
				finally {
					JdbcUtils.closeResultSet(rs);
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		}, false);
	}

	public Object query(PreparedStatementCreator psc, ResultSetExtractor rse) throws DataAccessException {
		return query(psc, null, rse);
	}

	public Object query(String sql, PreparedStatementSetter pss, ResultSetExtractor rse) throws DataAccessException {
		return query(new SimplePreparedStatementCreator(sql), pss, rse);
	}

	public Object query(String sql, Object[] args, int[] argTypes, ResultSetExtractor rse) throws DataAccessException {
		return query(sql, new ArgTypePreparedStatementSetter(args, argTypes), rse);
	}

	public Object query(String sql, Object[] args, ResultSetExtractor rse) throws DataAccessException {
		return query(sql, new ArgPreparedStatementSetter(args), rse);
	}

	public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
		query(psc, new RowCallbackHandlerResultSetExtractor(rch));
	}

	public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
		query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
	}

	public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
		query(sql, new ArgTypePreparedStatementSetter(args, argTypes), rch);
	}

	public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
		query(sql, new ArgPreparedStatementSetter(args), rch);
	}

	@SuppressWarnings("unchecked")
	public List query(PreparedStatementCreator psc, RowMapper rowMapper) throws DataAccessException {
		return (List) query(psc, new RowMapperResultSetExtractor(rowMapper));
	}

	@SuppressWarnings("unchecked")
	public List query(String sql, PreparedStatementSetter pss, RowMapper rowMapper) throws DataAccessException {
		return (List) query(sql, pss, new RowMapperResultSetExtractor(rowMapper));
	}

	@SuppressWarnings("unchecked")
	public List query(String sql, Object[] args, int[] argTypes, RowMapper rowMapper) throws DataAccessException {
		return (List) query(sql, args, argTypes, new RowMapperResultSetExtractor(rowMapper));
	}

	@SuppressWarnings("unchecked")
	public List query(String sql, Object[] args, RowMapper rowMapper) throws DataAccessException {
		return (List) query(sql, args, new RowMapperResultSetExtractor(rowMapper));
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, Object[] args, int[] argTypes, RowMapper rowMapper)
			throws DataAccessException {

		List results = (List) query(sql, args, argTypes, new RowMapperResultSetExtractor(rowMapper, 1));
		return DataAccessUtils.requiredSingleResult(results);
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, Object[] args, RowMapper rowMapper) throws DataAccessException {
		List results = (List) query(sql, args, new RowMapperResultSetExtractor(rowMapper, 1));
		return DataAccessUtils.requiredSingleResult(results);
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, Object[] args, int[] argTypes, Class requiredType)
			throws DataAccessException {

		return queryForObject(sql, args, argTypes, getSingleColumnRowMapper(requiredType));
	}

	@SuppressWarnings("unchecked")
	public Object queryForObject(String sql, Object[] args, Class requiredType) throws DataAccessException {
		return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
	}

	@SuppressWarnings("unchecked")
	public Map queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return (Map) queryForObject(sql, args, argTypes, getColumnMapRowMapper());
	}

	@SuppressWarnings("unchecked")
	public Map queryForMap(String sql, Object[] args) throws DataAccessException {
		return (Map) queryForObject(sql, args, getColumnMapRowMapper());
	}

	public long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, argTypes, Long.class);
		return (number != null ? number.longValue() : 0);
	}

	public long queryForLong(String sql, Object[] args) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, Long.class);
		return (number != null ? number.longValue() : 0);
	}

	public int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, argTypes, Integer.class);
		return (number != null ? number.intValue() : 0);
	}

	public int queryForInt(String sql, Object[] args) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, Integer.class);
		return (number != null ? number.intValue() : 0);
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Object[] args, int[] argTypes, Class elementType) throws DataAccessException {
		return query(sql, args, argTypes, getSingleColumnRowMapper(elementType));
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Object[] args, Class elementType) throws DataAccessException {
		return query(sql, args, getSingleColumnRowMapper(elementType));
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return query(sql, args, argTypes, getColumnMapRowMapper());
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Object[] args) throws DataAccessException {
		return query(sql, args, getColumnMapRowMapper());
	}

	public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return (SqlRowSet) query(sql, args, argTypes, new SqlRowSetResultSetExtractor());
	}

	public SqlRowSet queryForRowSet(String sql, Object[] args) throws DataAccessException {
		return (SqlRowSet) query(sql, args, new SqlRowSetResultSetExtractor());
	}

	protected int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss)
			throws DataAccessException {

		if(ApiLogger.isTraceEnabled()){
			ApiLogger.trace("Executing prepared SQL update");
		}		

		Integer result = (Integer) execute(psc, new PreparedStatementCallback() {
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException {
				try {
					if (pss != null) {
						pss.setValues(ps);
					}
					int rows = ps.executeUpdate();
					if (ApiLogger.isTraceEnabled()) {
						ApiLogger.trace("SQL update affected " + rows + " rows");
					}
					return new Integer(rows);
				}
				finally {
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		}, true);
		return result.intValue();
	}

	public int update(PreparedStatementCreator psc) throws DataAccessException {
		return update(psc, (PreparedStatementSetter) null);
	}

	@SuppressWarnings("unchecked")
	public int update(final PreparedStatementCreator psc, final KeyHolder generatedKeyHolder)
			throws DataAccessException {

		Assert.notNull(generatedKeyHolder, "KeyHolder must not be null");
		if(ApiLogger.isTraceEnabled()){
			ApiLogger.trace("Executing SQL update and returning generated keys");
		}		

		Integer result = (Integer) execute(psc, new PreparedStatementCallback() {
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException {
				int rows = ps.executeUpdate();
				List generatedKeys = generatedKeyHolder.getKeyList();
				generatedKeys.clear();
				ResultSet keys = ps.getGeneratedKeys();
				if (keys != null) {
					try {
						RowMapper rowMapper = getColumnMapRowMapper();
						RowMapperResultSetExtractor rse = new RowMapperResultSetExtractor(rowMapper, 1);
						generatedKeys.addAll((List) rse.extractData(keys));
					}
					finally {
						JdbcUtils.closeResultSet(keys);
					}
				}
				if (ApiLogger.isTraceEnabled()) {
					ApiLogger.trace("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
				}
				return new Integer(rows);
			}
		}, true);
		return result.intValue();
	}

	public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
		return update(new SimplePreparedStatementCreator(sql), pss);
	}

	public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return update(sql, new ArgTypePreparedStatementSetter(args, argTypes));
	}

	public int update(String sql, Object[] args) throws DataAccessException {
		return update(sql, new ArgPreparedStatementSetter(args));
	}

	@SuppressWarnings("unchecked")
	public int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss) throws DataAccessException {
		if (ApiLogger.isTraceEnabled()) {
			ApiLogger.trace("Executing SQL batch update [" + sql + "]");
		}

		return (int[]) execute(sql, new PreparedStatementCallback() {
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException {
				try {
					int batchSize = pss.getBatchSize();
					InterruptibleBatchPreparedStatementSetter ipss =
							(pss instanceof InterruptibleBatchPreparedStatementSetter ?
							(InterruptibleBatchPreparedStatementSetter) pss : null);
					if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
						for (int i = 0; i < batchSize; i++) {
							pss.setValues(ps, i);
							if (ipss != null && ipss.isBatchExhausted(i)) {
								break;
							}
							ps.addBatch();
						}
						return ps.executeBatch();
					}
					else {
						List rowsAffected = new ArrayList();
						for (int i = 0; i < batchSize; i++) {
							pss.setValues(ps, i);
							if (ipss != null && ipss.isBatchExhausted(i)) {
								break;
							}
							rowsAffected.add(new Integer(ps.executeUpdate()));
						}
						int[] rowsAffectedArray = new int[rowsAffected.size()];
						for (int i = 0; i < rowsAffectedArray.length; i++) {
							rowsAffectedArray[i] = ((Integer) rowsAffected.get(i)).intValue();
						}
						return rowsAffectedArray;
					}
				}
				finally {
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		}, true);
	}


	//-------------------------------------------------------------------------
	// Methods dealing with callable statements
	//-------------------------------------------------------------------------
	
	public Object execute(CallableStatementCreator csc, CallableStatementCallback action, boolean isWrite)
			throws DataAccessException {

		Assert.notNull(csc, "CallableStatementCreator must not be null");
		Assert.notNull(action, "Callback object must not be null");
		if (ApiLogger.isTraceEnabled()) {
			String sql = getSql(csc);
			ApiLogger.trace("Calling stored procedure" + (sql != null ? " [" + sql  + "]" : ""));
		}

		long start = System.currentTimeMillis();
		DataSource ds = getDataSource(isWrite);
		Connection con = safeGetConnection(ds, isWrite);
		CallableStatement cs = null;
		try {
			Connection conToUse = con;
			if (this.nativeJdbcExtractor != null) {
				conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
			}
			cs = csc.createCallableStatement(conToUse);
			applyStatementSettings(ds, cs);
			CallableStatement csToUse = cs;
			if (this.nativeJdbcExtractor != null) {
				csToUse = this.nativeJdbcExtractor.getNativeCallableStatement(cs);
			}
			Object result = action.doInCallableStatement(csToUse);
			handleWarnings(cs);
			return result;
		}
		catch (Exception ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			if (csc instanceof ParameterDisposer) {
				((ParameterDisposer) csc).cleanupParameters();
			}
			String sql = getSql(csc);
			csc = null;
			JdbcUtils.closeStatement(cs);
			cs = null;
			DataSourceUtils.releaseConnection(con, ds);
			con = null;
			if (ex instanceof SQLException) {
				throw getExceptionTranslator(ds).translate("CallableStatementCallback", sql, (SQLException)ex);
			} else {
				throw new RuntimeException("CallableStatementCallback " + getSql(csc), ex);
			}
		}
		finally {
			//add slow log
			long useTime =System.currentTimeMillis() -start;
			if(useTime > ApiLogger.DB_FIRE_TIME){
				ApiLogger.fire(new StringBuffer().append("DB ").append(((com.mchange.v2.c3p0.ComboPooledDataSource)ds).getJdbcUrl()).append(" too slow :").append(useTime).append(" isWrite:").append(isWrite));
			}
			
			if (csc instanceof ParameterDisposer) {
				((ParameterDisposer) csc).cleanupParameters();
			}
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, ds);
			
			TimeStatUtil.addElapseTimeStat(resource, isWrite, start, useTime);
		}
	}
	
	/**
	 * Extract output parameters from the completed stored procedure.
	 * @param cs JDBC wrapper for the stored procedure
	 * @param parameters parameter list for the stored procedure
	 * @return Map that contains returned results
	 */
	@SuppressWarnings("unchecked")
	protected Map extractOutputParameters(CallableStatement cs, List parameters) throws SQLException {
		Map returnedResults = new HashMap();
		int sqlColIndex = 1;
		for (int i = 0; i < parameters.size(); i++) {
			SqlParameter param = (SqlParameter) parameters.get(i);
			if (param instanceof SqlOutParameter) {
				SqlOutParameter outParam = (SqlOutParameter) param;
				if (outParam.isReturnTypeSupported()) {
					Object out = outParam.getSqlReturnType().getTypeValue(
							cs, sqlColIndex, outParam.getSqlType(), outParam.getTypeName());
					returnedResults.put(outParam.getName(), out);
				}
				else {
					Object out = cs.getObject(sqlColIndex);
					if (out instanceof ResultSet) {
						if (outParam.isResultSetSupported()) {
							returnedResults.putAll(processResultSet((ResultSet) out, outParam));
						}
						else {
							String rsName = outParam.getName();
							SqlReturnResultSet rsParam = new SqlReturnResultSet(rsName, new ColumnMapRowMapper());
							returnedResults.putAll(processResultSet(cs.getResultSet(), rsParam));
							ApiLogger.info("Added default SqlReturnResultSet parameter named " + rsName);
						}
					}
					else {
						returnedResults.put(outParam.getName(), out);
					}
				}
			}
			if (!(param.isResultsParameter())) {
				sqlColIndex++;
			}
		}
		return returnedResults;
	}

	/**
	 * Process the given ResultSet from a stored procedure.
	 * @param rs the ResultSet to process
	 * @param param the corresponding stored procedure parameter
	 * @return Map that contains returned results
	 */
	@SuppressWarnings("unchecked")
	protected Map processResultSet(ResultSet rs, ResultSetSupportingSqlParameter param) throws SQLException {
		if (rs == null) {
			return Collections.EMPTY_MAP;
		}
		Map returnedResults = new HashMap();
		try {
			ResultSet rsToUse = rs;
			if (this.nativeJdbcExtractor != null) {
				rsToUse = this.nativeJdbcExtractor.getNativeResultSet(rs);
			}
			if (param.getRowMapper() != null) {
				RowMapper rowMapper = param.getRowMapper();
				Object result = (new RowMapperResultSetExtractor(rowMapper)).extractData(rsToUse);
				returnedResults.put(param.getName(), result);
			}
			else if (param.getRowCallbackHandler() != null) {
				RowCallbackHandler rch = param.getRowCallbackHandler();
				(new RowCallbackHandlerResultSetExtractor(rch)).extractData(rsToUse);
				returnedResults.put(param.getName(), "ResultSet returned from stored procedure was processed");
			}
			else if (param.getResultSetExtractor() != null) {
				Object result = param.getResultSetExtractor().extractData(rsToUse);
				returnedResults.put(param.getName(), result);
			}
		}
		finally {
			JdbcUtils.closeResultSet(rs);
		}
		return returnedResults;
	}


	//-------------------------------------------------------------------------
	// Implementation hooks and helper methods
	//-------------------------------------------------------------------------

	/**
	 * Create a new RowMapper for reading columns as key-value pairs.
	 * @return the RowMapper to use
	 * @see ColumnMapRowMapper
	 */
	protected RowMapper getColumnMapRowMapper() {
		return new ColumnMapRowMapper();
	}

	/**
	 * Create a new RowMapper for reading result objects from a single column.
	 * @param requiredType the type that each result object is expected to match
	 * @return the RowMapper to use
	 * @see SingleColumnRowMapper
	 */
	@SuppressWarnings("unchecked")
	protected RowMapper getSingleColumnRowMapper(Class requiredType) {
		return new SingleColumnRowMapper(requiredType);
	}

	/**
	 * Create a Map instance to be used as results map.
	 * <p>If "isResultsMapCaseInsensitive" has been set to true, a linked case-insensitive Map
	 * will be created if possible, else a plain HashMap (see Spring's CollectionFactory).
	 * @return the results Map instance
	 * @see #setResultsMapCaseInsensitive
	 * @see org.springframework.core.CollectionFactory#createLinkedCaseInsensitiveMapIfPossible
	 */
	@SuppressWarnings("unchecked")
	protected Map createResultsMap() {
		if (isResultsMapCaseInsensitive()) {
			return CollectionFactory.createLinkedCaseInsensitiveMapIfPossible(10);
		}
		else {
			return new LinkedHashMap();
		}
	}

	/**
	 * Prepare the given JDBC Statement (or PreparedStatement or CallableStatement),
	 * applying statement settings such as fetch size, max rows, and query timeout.
	 * @param stmt the JDBC Statement to prepare
	 * @throws SQLException if thrown by JDBC API
	 * @see #setFetchSize
	 * @see #setMaxRows
	 * @see #setQueryTimeout
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#applyTransactionTimeout
	 */
	protected void applyStatementSettings(DataSource dataSource, Statement stmt) throws SQLException {
		int fetchSize = getFetchSize();
		if (fetchSize > 0) {
			stmt.setFetchSize(fetchSize);
		}
		int maxRows = getMaxRows();
		if (maxRows > 0) {
			stmt.setMaxRows(maxRows);
		}
		DataSourceUtils.applyTimeout(stmt, dataSource, getQueryTimeout());
	}

	/**
	 * Throw an SQLWarningException if we're not ignoring warnings,
	 * else log the warnings (at debug level).
	 * @param stmt the current JDBC statement
	 * @throws SQLWarningException if not ignoring warnings
	 * @see org.springframework.jdbc.SQLWarningException
	 */
	protected void handleWarnings(Statement stmt) throws SQLException {
		if (isIgnoreWarnings()) {
			if (ApiLogger.isTraceEnabled()) {
				SQLWarning warningToLog = stmt.getWarnings();
				while (warningToLog != null) {
					ApiLogger.trace("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '" +
							warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]");
					warningToLog = warningToLog.getNextWarning();
				}
			}
		}
		else {
			handleWarnings(stmt.getWarnings());
		}
	}

	/**
	 * Throw an SQLWarningException if encountering an actual warning.
	 * @param warning the warnings object from the current statement.
	 * May be <code>null</code>, in which case this method does nothing.
	 * @throws SQLWarningException in case of an actual warning to be raised
	 */
	protected void handleWarnings(SQLWarning warning) throws SQLWarningException {
		if (warning != null) {
			throw new SQLWarningException("Warning not ignored", warning);
		}
	}

	/**
	 * Determine SQL from potential provider object.
	 * @param sqlProvider object that's potentially a SqlProvider
	 * @return the SQL string, or <code>null</code>
	 * @see SqlProvider
	 */
	private static String getSql(Object sqlProvider) {
		if (sqlProvider instanceof SqlProvider) {
			return ((SqlProvider) sqlProvider).getSql();
		}
		else {
			return null;
		}
	}


	/**
	 * Invocation handler that suppresses close calls on JDBC COnnections.
	 * Also prepares returned Statement (Prepared/CallbackStatement) objects.
	 * @see java.sql.Connection#close()
	 */
	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Connection target;
		private DataSource dataSource;

		public CloseSuppressingInvocationHandler(DataSource dataSource, Connection target) {
			this.target = target;
			this.dataSource = dataSource;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// Invocation on ConnectionProxy interface coming in...

			if (method.getName().equals("getTargetConnection")) {
				// Handle getTargetConnection method: return underlying Connection.
				return this.target;
			}
			else if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
			}
			else if (method.getName().equals("hashCode")) {
				// Use hashCode of PersistenceManager proxy.
				return new Integer(System.identityHashCode(proxy));
			}
			else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			}

			// Invoke method on target Connection.
			try {
				Object retVal = method.invoke(this.target, args);

				// If return value is a JDBC Statement, apply statement settings
				// (fetch size, max rows, transaction timeout).
				if (retVal instanceof Statement) {
					applyStatementSettings(dataSource, ((Statement) retVal));
				}

				return retVal;
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}


	/**
	 * Simple adapter for PreparedStatementCreator, allowing to use a plain SQL statement.
	 */
	private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

		private final String sql;

		public SimplePreparedStatementCreator(String sql) {
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
		}

		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(this.sql);
		}

		public String getSql() {
			return this.sql;
		}
	}

	/**
	 * Adapter to enable use of a RowCallbackHandler inside a ResultSetExtractor.
	 * <p>Uses a regular ResultSet, so we have to be careful when using it:
	 * We don't use it for navigating since this could lead to unpredictable consequences.
	 */
	private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor {

		private final RowCallbackHandler rch;

		public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
			this.rch = rch;
		}

		public Object extractData(ResultSet rs) throws SQLException {
			while (rs.next()) {
				this.rch.processRow(rs);
			}
			return null;
		}
	}
	
}

