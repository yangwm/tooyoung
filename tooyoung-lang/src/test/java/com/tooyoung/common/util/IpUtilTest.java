/**
 * 
 */
package com.tooyoung.common.util;

import org.junit.Test;

/**
 * 
 * @author yangwm May 20, 2013 6:49:09 PM
 */
public class IpUtilTest {

	@Test
	public void testIsIp(){
		org.junit.Assert.assertTrue(IpUtil.isIp("127.0.0.1"));
		org.junit.Assert.assertTrue(IpUtil.isIp("10.75.0.60"));
		org.junit.Assert.assertTrue(IpUtil.isIp("192.168.1.1"));
		org.junit.Assert.assertTrue(IpUtil.isIp("175.41.9.194"));
	}
	
	@Test
	public void testIsIpFalse(){
		org.junit.Assert.assertFalse(IpUtil.isIp("aaa"));
		org.junit.Assert.assertFalse(IpUtil.isIp("1000.75.0.60"));
		org.junit.Assert.assertFalse(IpUtil.isIp("192.168.1"));
		org.junit.Assert.assertFalse(IpUtil.isIp("175.41.9.a"));
	}
	
	@Test
	public void testIPToInt(){
		org.junit.Assert.assertEquals(2130706433,IpUtil.ipToInt("127.0.0.1"));
		org.junit.Assert.assertEquals(172687420,IpUtil.ipToInt("10.75.0.60"));
		org.junit.Assert.assertEquals(-1062731519,IpUtil.ipToInt("192.168.1.1"));
		org.junit.Assert.assertEquals(-1356265022,IpUtil.ipToInt("175.41.9.194"));
	}

	@Test
	public void testGetLocalIp(){
		String localIp = IpUtil.getLocalIp();
		System.out.println(localIp);
		org.junit.Assert.assertNotNull(localIp);
	}
	
	@Test
	public void testGetRandomPort(){
		int port = IpUtil.ramdomAvailablePort();
		System.out.println(port);
		org.junit.Assert.assertTrue(port > 0 && port <65535);
	}

}
