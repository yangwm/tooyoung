/**
 * 
 */
package com.tooyoung.web.abc;

import java.util.Random;

import org.apache.log4j.MDC;

/**
 * 
 */
public class LogUtils {

	public final static String HEADER_LOGID = "LOGID";
	public final static String HEADER_CLIENTIP = "clientip";

	public final static String LOG_REQUEST_IP = "request.ip";
	public final static String LOG_REQUEST_LOGID = "request.logid";
	public final static String LOG_REQUEST_URI = "request.uri";
	public final static String LOG_REQUEST_UID = "request.uid";

	private final static Random random = new Random();

	public static String getLogid() {

		return (String) MDC.get(LOG_REQUEST_LOGID);
	}

	public static String genLogid() {
		Long logid = (System.currentTimeMillis() << 22) + random.nextInt(1 << 23);

		return logid.toString();
	}

}
