/**
 * 
 */
package cc.tooyoung.common.mcq;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cc.tooyoung.common.cache.driver.VikaCacheClient;

/**
 * 
 * @author yangwm Jun 7, 2012 3:50:25 PM
 */
public class BaseWriterTest {
    
    //@Test
    public void testWriteMsg() {
        String[] ips = new String[] {
                "testmcq",
                "testmcq",
                "testmcq",
                "testmcq",
            };
        
        for (int i = 0; i < ips.length; i++) {
            BaseWriter baseWriter = getBaseWriter("tag", ips, 22201);
            baseWriter.writeMsg("{\"type\":\"tagStatus\",\"uid\":1752935322,\"status_id\":3344487624452112,\"tagName\":\"视频\",\"time\":1341903959000}");
        }
    }
    //@Test
    public void testWriteMsgWithHashKey() {
        String[] ips = new String[] {
                "testmcq",
                "testmcq",
                "testmcq",
                "testmcq",
            };
        
        for (int i = 0; i < ips.length; i++) {
            BaseWriter baseWriter = getBaseWriter("weibotag", ips, 22201);
            baseWriter.writeMsg(175293532211111L + i, "{\"type\":\"tagStatus\",\"uid\":1752935322,\"status_id\":3344487624452112,\"tagName\":\"视频\",\"time\":1341903959000}");
            
            baseWriter.writeMsg(-175293532211111L + i, "{\"type\":\"tagStatus\",\"uid\":1752935322,\"status_id\":3344487624452112,\"tagName\":\"视频\",\"time\":1341903959000}");
        }
    }
    private BaseWriter getBaseWriter(String writeKey, String[] ips, int port) {
        List<VikaCacheClient> mcqWriters = new ArrayList<VikaCacheClient>();
        for (String ip : ips) {
            VikaCacheClient vikaCacheClient = new VikaCacheClient();
            vikaCacheClient.setMinSpareConnections(2);
            vikaCacheClient.setMaxSpareConnections(5);
            vikaCacheClient.setCompressEnable(false);
            vikaCacheClient.setServerPort(ip + ":" + port);
            vikaCacheClient.init();
            mcqWriters.add(vikaCacheClient);
        }
        
        McqBaseWriter mcqBaseWriter = new McqBaseWriter();
        mcqBaseWriter.setWriteKey(writeKey);
        mcqBaseWriter.setMcqWriters(mcqWriters);
        
        return mcqBaseWriter;
    }

    //@Test
    public void testWriteMsgByDistinct() {
        String[] ips = new String[] {
                "testmcq",
                "testmcq",
                "testmcq",
                "testmcq",
            };
        String msg = "消息大小测试en:";
        for (int i = 0; i < 490; i++) {
            msg += "1";
        }
        
        for (int i = 0; i < ips.length; i++) {
            BaseWriter baseWriter = getBaseWriter("test", ips, 22201, 22202);
            
            baseWriter.writeMsg(msg);
            
            baseWriter.writeMsg("大于512，" + msg);
        }
    }
    private BaseWriter getBaseWriter(String writeKey, String[] ips, int port, int port2) {
        List<VikaCacheClient> mcqWriters = new ArrayList<VikaCacheClient>();
        for (String ip : ips) {
            VikaCacheClient vikaCacheClient = new VikaCacheClient();
            vikaCacheClient.setMinSpareConnections(2);
            vikaCacheClient.setMaxSpareConnections(5);
            vikaCacheClient.setCompressEnable(false);
            vikaCacheClient.setServerPort(ip + ":" + port);
            vikaCacheClient.init();
            mcqWriters.add(vikaCacheClient);
        }
        
        List<VikaCacheClient> mcq512bWriters = new ArrayList<VikaCacheClient>();
        for (String ip : ips) {
            VikaCacheClient vikaCacheClient2 = new VikaCacheClient();
            vikaCacheClient2.setMinSpareConnections(2);
            vikaCacheClient2.setMaxSpareConnections(5);
            vikaCacheClient2.setCompressEnable(false);
            vikaCacheClient2.setServerPort(ip + ":" + port2);
            vikaCacheClient2.init();
            mcq512bWriters.add(vikaCacheClient2);
        }
        
        McqBaseWriter mcqBaseWriter = new McqBaseWriter();
        mcqBaseWriter.setWriteKey(writeKey);
        mcqBaseWriter.setMcqWriters(mcqWriters);
        mcqBaseWriter.setDistinctBySize(true);
        //mcqBaseWriter.setDistinctBoundary(512);
        mcqBaseWriter.setMcq512BWriters(mcq512bWriters);
        
        return mcqBaseWriter;
    }
    
}
