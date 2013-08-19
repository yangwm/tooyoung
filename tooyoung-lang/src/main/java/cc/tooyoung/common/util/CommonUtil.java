package cc.tooyoung.common.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 
 * @author yangwm Jul 30, 2013 12:47:55 AM
 */
public class CommonUtil {

    /**
     * count debug 
     */
    public static AtomicBoolean commonDebug = new AtomicBoolean(false);
    public static boolean isDebugEnabled() {
        return ApiLogger.isDebugEnabled() && commonDebug.get();
    }
    
    /**
     * 消息本身格式不正确，不需要重试
     */
    public static final int MQ_PROCESS_ABORT = -1;
    /**
     * 消息处理失败，需要重试
     */
    public static final int MQ_PROCESS_RETRY = 0;
    /**
     * 消息处理成功
     */
    public static final int MQ_PROCESS_SUCCESS = 1;
    
    
	private static Random random = new Random(); 

	public static void safeSleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			//eat the exception, for i am not care it
		}
	}

	public static int nextInt(){
		return random.nextInt();
	}
	
	public static int nextInt(int seed){
		return random.nextInt(seed);
	}

	public static void main(String[] args){		
		int rand = nextInt(256);
		System.out.println(rand);
	}
}
