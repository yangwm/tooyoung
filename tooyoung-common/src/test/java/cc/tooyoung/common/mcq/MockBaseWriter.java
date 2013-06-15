/**
 * 
 */
package cc.tooyoung.common.mcq;

import cc.tooyoung.common.util.ApiLogger;

/**
 * Mock Base Writer
 * 
 * @author yangwm Feb 29, 2012 5:53:53 PM
 */
public class MockBaseWriter implements BaseWriter {

    @Override
    public String getWriteKey() {
        return "mock_key";
    }
    
    @Override
    public void writeMsg(String msg) {
        ApiLogger.info("MockBaseWriter writeKey:" + getWriteKey() + ", msg:" + msg);
    }
    @Override
    public void writeMsg(long hashKey, String msg) {
        ApiLogger.info("MockBaseWriter hashKey:" + hashKey + ", writeKey:" + getWriteKey() + ", msg:" + msg);
    }

    @Override
    public void writeMsg(byte[] msg) {
        ApiLogger.info("MockBaseWriter writeKey:" + getWriteKey() + ", msg:" + msg);
    }
    @Override
    public void writeMsg(long hashKey, byte[] msg) {
        ApiLogger.info("MockBaseWriter hashKey:" + hashKey + ", writeKey:" + getWriteKey() + ", msg:" + msg);
    }

}
