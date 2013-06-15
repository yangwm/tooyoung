package com.tooyoung.common.db;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tooyoung.common.util.ApiLogger;

/**
 * 
 * 
 * @author yangwm May 21, 2013 4:55:46 PM
 */
public class DaoUtilTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long[] uids = new long[] { 1477356933L, 201100828135254110L, 1732849265, 27005000, 1750715731, 1821155363, 1821176901 };
        for (long uid : uids) {
            ApiLogger.debug("uid:" + uid);
        }
    }


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateMutiGetSql() {
        String sql = "select a,b from table where a in";
        int paramsSize = 10;
        String result = DaoUtil.createMutiGetSql(sql, paramsSize);
        int count = 0;
        char[] chs = result.toCharArray();
        for (char ch : chs) {
            if (ch == '?') {
                count++;
            }
        }
        if (paramsSize <= 0) {
            Assert.assertEquals(1, count);
        } else {
            Assert.assertEquals(paramsSize, count);
        }

	}
	
    @Test
    public void testCreateMultiInsertSql() {
        int paramsSize = 1;
        String result = DaoUtil.createMultiInsertSql("insert into test values", "(?, ?, ?, now())", paramsSize);
        System.out.println("testCreateMultiInsertSql paramsSize:" + paramsSize + ", result:" + result);
        Assert.assertEquals("insert into test values(?, ?, ?, now())", result);

        paramsSize = 3;
        result = DaoUtil.createMultiInsertSql("insert ignore into test values", "(?, ?, ?, now())", paramsSize);
        System.out.println("testCreateMultiInsertSql paramsSize:" + paramsSize + ", result:" + result);
        Assert.assertEquals("insert ignore into test values(?, ?, ?, now()), (?, ?, ?, now()), (?, ?, ?, now())", result);
    }

	@Test
	public void testMultiGetSql() {
		String sql = "select * from table where uid=? and type in (?) order by date";
		Object[] params = new Object[] { 12345, new Object[] { 1234, 1234 } };
		
		String rs = DaoUtil.expendMultiGetSql(sql, params);
		Assert.assertEquals("select * from table where uid=? and type in (?,?) order by date", rs);
		
		sql = "select * from table where uid=? and type in (?) and vflag in (?) order by date";
		params = new Object[] {123, new Object[] {12,12}, new Object[]{12,12}};
		rs = DaoUtil.expendMultiGetSql(sql, params);
		Assert.assertEquals("select * from table where uid=? and type in (?,?) and vflag in (?,?) order by date", rs);
		
		params = new Object[] {123, new Object[] {12}, new Object[]{12}};
		rs = DaoUtil.expendMultiGetSql(sql, params);
		Assert.assertEquals("select * from table where uid=? and type in (?) and vflag in (?) order by date", rs);
		
		params = new Object[] {123, 12, 12};
		rs = DaoUtil.expendMultiGetSql(sql, params);
		Assert.assertEquals("select * from table where uid=? and type in (?) and vflag in (?) order by date", rs);
		
		sql = "select * from table where uid=?";
		rs = DaoUtil.expendMultiGetSql(sql, params);
		Assert.assertEquals("select * from table where uid=?", rs);
	}
}
