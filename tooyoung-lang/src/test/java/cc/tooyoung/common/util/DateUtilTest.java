/**
 * 
 */
package cc.tooyoung.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

/**
 * Date Util Test
 * 
 * @author yangwm Mar 31, 2012 2:30:17 PM
 */
public class DateUtilTest {
    
    /**
     * Thu Jan 01 00:00:00 CST 2009, 1230739200
     * Mon Jun 01 00:00:00 CST 2009, 1243785600
     * Fri Jan 01 00:00:00 CST 2010, 1262275200
     * Sat Jan 01 00:00:00 CST 2011, 1293811200
     * Tue Jan 01 00:00:00 CST 2013, 1356969600
     * Thu Jan 01 00:00:00 CST 2015, 1420041600
     */
    @Test
    public void testFormateDateTime() {
        assertEquals("2011-11-25 13:12:01", DateUtil.formateDateTime(new Date(1322197921282L)));
        
        Date currentTime = DateUtil.parseDateTime("2009-01-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2009-06-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2010-01-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2011-01-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2011-06-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2013-01-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
        
        currentTime = DateUtil.parseDateTime("2015-01-01 00:00:00", null);
        System.out.println(currentTime + ", " + (currentTime.getTime() / 1000));
    }
    @Test
    public void testParseDateTime() {
        System.out.println(new Date(515483463000L));
        System.out.println(new Date(1356969600000L));
        assertEquals(new Date(1325390268000L), DateUtil.parseDateTime("2012-01-01 11:57:48", null));
    }
    
    @Test
    public void testGetYearMonth() {
        Date date = DateUtil.parseDateTime("2012-01-01 11:57:48", null);
        assertEquals("12_01", DateUtil.getYearMonth(date));
        date = DateUtil.parseDateTime("2012-12-01 00:00:00", null);
        ApiLogger.debug(date + ", " + date.getTime());
    }
    
    @Test
    public void testGetYearMonthDay() {
        assertEquals("11_11", DateUtil.getYearMonth(new Date(1322197921282L)));
    }
    
    @Test
    public void testFormateYearMonthDay() {
        assertEquals("2011-11-25", DateUtil.formateYearMonthDay(new Date(1322197921282L)));
    }
    @Test
    public void testParseYearMonthDay() {
        assertEquals(new Date(1325347200000L), DateUtil.parseYearMonthDay("2012-01-01", null));
        Date date = DateUtil.parseYearMonthDay("2012-12-01", null);
        ApiLogger.debug(date + ", " + date.getTime());
        ApiLogger.debug(new Date(1000000000000L) + ", " + new Date(1000000000000L).getTime());
        ApiLogger.debug(new Date(1000L * 60 * 43200) + ", " + new Date(1000L * 60 * 43200).getTime() + ", " + (1000L * 60 * 43200));
    }
    
    @Test
    public void testGetFirstDayInMonth() {
        assertEquals(new Date(1320076800282L), DateUtil.getFirstDayInMonth(new Date(1322197921282L)));
    }

    @Test
    public void testGsCurrentMonth(){
        assertEquals(true, DateUtil.isCurrentMonth(new Date()));
        assertEquals(false, DateUtil.isCurrentMonth(new Date(1322197921282L)));
    }
    
}
