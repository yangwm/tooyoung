package cc.tooyoung.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;


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

	private static ThreadLocal<CRC32> crc32Provider = new ThreadLocal<CRC32>(){
		@Override
		protected CRC32 initialValue() {
			return new CRC32();
		}
	};

	public static long getCrc32(byte[] b) {
        CRC32 crc = crc32Provider.get();
        crc.reset();
        crc.update(b);
        return crc.getValue();
    }
	public static long getCrc32(String str) {
        try {
            return getCrc32(str.getBytes("utf-8"));   
        } catch (UnsupportedEncodingException e) {
            ApiLogger.warn(new StringBuilder(64).append("Error: getCrc32, str=").append(str), e);
            return -1;
        }
    }

	public static String getAttentionHash(long uid, int tblCount){		
		int hash = getHash4split(uid, tblCount);
		String hex = Long.toHexString(hash);
		if(hex.length() == 1){
			hex = "0" + hex;
		}
		return hex;
		
	}
	
	public static int getHash4split(long id, int splitCount){
		try {
			long h = getCrc32(String.valueOf(id).getBytes("utf-8"));
			if(h < 0){
				h = -1 * h;
			}
			int hash = (int)(h / splitCount % splitCount);			
			return hash;
		} catch (UnsupportedEncodingException e) {
			ApiLogger.warn(new StringBuilder(64).append("Error: when hash4split, id=").append(id).append(", splitCount=").append(splitCount), e);
			return -1;
		}
	}
	
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
		int hash = getHash4split(10506, 256);
		System.out.println(hash);
	}
}
