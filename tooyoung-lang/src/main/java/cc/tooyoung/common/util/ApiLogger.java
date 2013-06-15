package cc.tooyoung.common.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author yangwm May 18, 2013 4:42:08 PM
 */
public class ApiLogger {
	
	public static long MC_FIRE_TIME=200; //MC操作超时
	
	public static long DB_FIRE_TIME=500; //DB操作超时
	
	public static long REDIS_FIRE_TIME=300; //Redis操作超时

	private static Logger log = Logger.getLogger("api");
	private static Logger infoLog = Logger.getLogger("info");
	private static Logger warnLog = Logger.getLogger("warn");
	private static Logger errorLog = Logger.getLogger("error");
	
	private static Logger fireLog = Logger.getLogger("fire");

	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                LogManager.shutdown();              
            }
        });
	}

	public static boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public static boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public static void trace(Object msg) {
		log.trace(msg);
	}

	public static void debug(Object msg) {
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
	}
	
	public static void fire(Object msg) {
		if (fireLog.isInfoEnabled()) {
			fireLog.info(msg);
		}
	}

	public static void info(Object msg) {
		if (infoLog.isInfoEnabled()) {
			infoLog.info(msg);
		}
	}

	public static void warn(Object msg) {
		warnLog.warn(msg);
	}

	public static void warn(Object msg, Throwable e) {
		warnLog.warn(msg, e);
	}

	public static void error(Object msg) {
		errorLog.error(msg);
	}

	public static void error(Object msg, Throwable e) {
		errorLog.error(msg, e);
	}

}
