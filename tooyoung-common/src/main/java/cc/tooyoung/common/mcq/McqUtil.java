package cc.tooyoung.common.mcq;

import java.util.concurrent.atomic.AtomicInteger;

import cc.tooyoung.common.util.ApiLogger;


public class McqUtil {
    
    public static AtomicInteger processorId = new AtomicInteger(0);
    
    /**
     * 系统是否启动成功，启动成功方可开始处理消息
     */
    private volatile static boolean systemInitSuccess = false;

    /**
     * 等待系统初始化成功
     * 如果systemInitSuccess为true，或者已经尝试3min，则开始执行读操作
     */
    public static void waitForInit(String mcqProcessName) {
        int total = 0;
        String msg = null;
        try {
            while (!systemInitSuccess && total++ < 90) {
                safeSleep(2000); // sleep 1s
                msg = new StringBuilder(64).append(mcqProcessName + " wait for system init! systemInitSuccess:")
                                .append(systemInitSuccess).append("\tcount:").append(total).toString();
                ApiLogger.info(msg);
                System.out.println(msg);
            }
            safeSleep(10 * 1000); // sleep 3s
        } catch (Exception e) {
            ApiLogger.error("Error:when waitForInit", e);
        }
    }
    
    public static void safeSleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {          
        }
    }

    /**
     * 设置系统初始化成功状态
     */
    public static void setSystemInitSuccess() {
        systemInitSuccess = true;
    }
    
}
