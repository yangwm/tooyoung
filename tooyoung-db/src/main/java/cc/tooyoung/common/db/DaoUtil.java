package cc.tooyoung.common.db;

import java.util.ArrayList;
import java.util.Collection;

public class DaoUtil {

	/**
	 * 
	 * @param sql
	 * @param paramsSize
	 * @return
	 */
	public static String createMutiGetEncodedSql(String sql, int paramsSize){
	    StringBuilder sqlBuf = new StringBuilder().append(sql);
	    sqlBuf.append("( convert(_latin1 ? using utf8)");
	    for (int i = 1; i < paramsSize; i++){
		sqlBuf.append(", convert(_latin1 ? using utf8)");
	    }
	    return sqlBuf.append(")").toString();
	}
	
	public static String createMutiGetSql(String sql, int paramsSize){
		StringBuilder sqlBuf = new StringBuilder().append(sql).append("( ?");
		for(int i = 1; i < paramsSize; i++){
			sqlBuf.append(", ?");
		}
		return sqlBuf.append(")").toString();		
	}
	
	/**
	 * expend sql such as:<b>select * from table where uid=? and type in (?) and
	 * vflag in (?) order by date</b> to <b>select * from table where uid=? and
	 * type in (?,?,?) and vflag in (?,?,?) order by date</b>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static String expendMultiGetSql(String sql, Object[] params) {
		int lastQuoteIndex = -1;
		for (int i = 0; i < params.length; i++) {
			lastQuoteIndex = sql.indexOf('?', lastQuoteIndex + 1);
			Object param = params[i];
			if (param instanceof Object[]) {
				Object[] nestArrayParam = (Object[]) param;
				String multipleGetStatement = multiGetPrepareStatement(nestArrayParam.length);
				sql = sql.substring(0, lastQuoteIndex) + multipleGetStatement
						+ sql.substring(lastQuoteIndex + 1);
				lastQuoteIndex += multipleGetStatement.length();
			}
		}
		return sql;
	}
	
	/**
	 * expend nest param such as: Object[] {1234, new Object[] {1234, 1234}, new
	 * Object[] {1234, 1234}} to: Object[] {1234, 1234, 1234, 1234, 1234}
	 * 
	 * @param params
	 * @return
	 */
	public static Object[] expendMultiGetParams(Object[] params) {
		Collection<Object> expended = new ArrayList<Object>();
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (param instanceof Object[]) {
				Object[] nestArrayParam = (Object[]) param;
				for (int j = 0; j < nestArrayParam.length; j++) {
					expended.add(nestArrayParam[j]);
				}
			} else {
				expended.add(param);
			}
		}
		return expended.toArray(new Object[expended.size()]);
	}
	
	private static String multiGetPrepareStatement(int paramCount) {
		StringBuilder buf = new StringBuilder();
		buf.append('?');
		for (int i = 1; i < paramCount; i++) {
			buf.append(',').append('?');
		}
		return buf.toString();
	}
	   
    public static String createMultiInsertSql(String sqlPrefix, String sqlSuffix, int valuesSize) {
        StringBuilder sb = new StringBuilder(sqlPrefix).append(sqlSuffix);
        for (int i = 1; i < valuesSize; i++) {
            sb.append(", ");
            sb.append(sqlSuffix);
        }
        return sb.toString();
    }
    
    public static String buildSql(String rawSql, String db, String table){
        String dbTable = db + "." + table;
        String result = rawSql.replace("$dbTable$", dbTable);
        return result;
    }
    
}
