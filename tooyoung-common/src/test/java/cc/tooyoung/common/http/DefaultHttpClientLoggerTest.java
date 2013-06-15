/**
 * 
 */
package cc.tooyoung.common.http;

import junit.framework.Assert;

import org.junit.Test;

import cc.tooyoung.common.util.Util;

/**
 *
 */
public class DefaultHttpClientLoggerTest {

	@Test
	public void testReplacePwd(){
		//http://ilogin.sina.com.cn/api/chksso.php?
		String text = "entry=openapi&user=aaa&pw=bbb&ip=220.181.136.230&m=73089dbf803dadfa621207ec0e4e6934&appid=13815";
		String test = DefaultHttpClientAceessLog.replacePwd(text);
		System.out.println(test);
		Assert.assertEquals("entry=openapi&user=aaa&pw=***&ip=220.181.136.230&m=73089dbf803dadfa621207ec0e4e6934&appid=13815", test);
	
		String s = "pw=%23*^$#";
		test = DefaultHttpClientAceessLog.replacePwd(s);
		System.out.println(test);
		Assert.assertEquals("pw=***",test);
		
		s = "pw="+Util.urlEncoder("%23*^$#&>@#V", "utf-8");
		test = DefaultHttpClientAceessLog.replacePwd(s);
		System.out.println(test);
		Assert.assertEquals("pw=***",test);
	}
}
