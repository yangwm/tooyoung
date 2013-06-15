/**
 * 
 */
package cc.tooyoung.common.stat;

import java.util.LinkedList;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * 
 * @author yangwm Jul 10, 2012 6:42:45 PM
 */
public class UseTimeStasticsMonitorTest {
    
    public static final UseTimeStasticsMonitor stasticsMonitor = new UseTimeStasticsMonitor("testMonitor");
    public static final boolean hit = false;
    public static final boolean debug = false;
    public static final int slowTime = 50;
    
    @Test 
    public void test() {
        LinkedList<Long> stamps = stasticsMonitor.start(null, debug);
        stasticsMonitor.mark(stamps, debug);
        stasticsMonitor.end(stamps, "test", hit, debug, slowTime);
        ApiLogger.debug(stamps);
    }

}
