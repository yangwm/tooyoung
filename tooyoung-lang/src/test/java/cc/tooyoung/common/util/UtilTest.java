package cc.tooyoung.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author yangwm May 21, 2013 4:05:36 PM
 */
public class UtilTest {
	
	@Test
	public void testConvertInt() {
        assertEquals(0, Util.convertInt(""));
        assertEquals(0, Util.convertInt("-"));
        assertEquals(12443, Util.convertInt("12443"));
        assertEquals(-12443, Util.convertInt("-12443"));
        assertEquals(Integer.MAX_VALUE, Util.convertInt(String.valueOf(Integer.MAX_VALUE)));
        assertEquals(Integer.MIN_VALUE, Util.convertInt(String.valueOf(Integer.MIN_VALUE)));
	}

	@Test
	public void testConvertLong() {
	    assertEquals(0L, Util.convertLong(""));
	    assertEquals(0L, Util.convertLong("-"));
        assertEquals(12443, Util.convertLong("12443"));
        assertEquals(-12443, Util.convertLong("-12443"));
        assertEquals(Long.MAX_VALUE, Util.convertLong(String.valueOf(Long.MAX_VALUE)));
        assertEquals(Long.MIN_VALUE, Util.convertLong(String.valueOf(Long.MIN_VALUE)));
	}

	@Test
	public void testParseLong() {
		assertEquals(12443l,Util.convertLong("12443"));
	}

	@Test
	public void testUrlDecoder() {
		assertEquals(",",Util.urlDecoder("%2C", "UTF-8"));
	}

	@Test
	public void testHtmlEscapeOnce() {
	    String src = "&amp;<span class=\"java\"/>";
	    String target = Util.htmlEscapeOnce(src);
	    assertEquals("&amp;amp;&lt;span class=&quot;java&quot;/&gt;", target);
	}
	
	@Test
    public void testHtmlUnEscapeOnce() {
        String src = "&amp;amp;&lt;span class=&quot;java&quot;/&gt;";
        String target = Util.htmlUnEscapeOnce(src);
        assertEquals("&amp;<span class=\"java\"/>", target);
    }
}
