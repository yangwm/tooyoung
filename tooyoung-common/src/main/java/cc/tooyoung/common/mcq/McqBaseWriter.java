/**
 * 
 */
package cc.tooyoung.common.mcq;

import java.util.List;
import java.util.Random;

import cc.tooyoung.common.CommonConst;
import cc.tooyoung.common.cache.driver.VikaCacheClient;
import cc.tooyoung.common.stat.StatLog;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.Util;


/**
 * mcq base writer 
 * 
 * @author yangwm Aug 21, 2011 10:03:15 PM
 */
public class McqBaseWriter implements BaseWriter {
	
    protected String writeKey;
    protected List<VikaCacheClient> mcqWriters;
    
    // 是否按照value大小拆分写不同队列，默认不拆分
    protected boolean distinctBySize = false;
    protected int distinctBoundary= CommonConst.MCQ_512B_BOUNDARY;
    // 512Byte端口mcq写入List
    protected List<VikaCacheClient> mcq512BWriters;
    
    private boolean logEnable = true;
    
    protected Random random = new Random();
    protected int getRandomInt() {
        return random.nextInt(10000);
    }
    
    @Override
    public void writeMsg(String msg) {
        writeMsg(getRandomInt(), msg);
    }
    @Override
    public void writeMsg(long hashKey, String msg) {
        writeMsg(hashKey, writeKey, msg, Util.toBytes(msg).length);
    }
    protected void writeMsg(String key, String msg) {
        writeMsg(getRandomInt(), key, msg, Util.toBytes(msg).length);
    }
    
    @Override
    public void writeMsg(byte[] msg) {
        writeMsg(getRandomInt(), msg);
    }
    @Override
    public void writeMsg(long hashKey, byte[] msg) {
        writeMsg(hashKey, writeKey, msg, msg.length);
    }
//    protected void writeMsg(String key, byte[] msg) {
//        writeMsg(getRandomInt(), key, msg, msg.length);
//    }    
    
    private void writeMsg(long hashKey, String key, Object msg, int msgSize){
        if (distinctBySize && mcq512BWriters != null && mcq512BWriters.size() > 0) {
            if (msg != null && msgSize < distinctBoundary) {
                writeMsg(mcq512BWriters, hashKey, key, msg);
            } else {
                writeMsg(mcqWriters, hashKey, key, msg);
            }
            return;
        }
        writeMsg(mcqWriters, hashKey, key, msg);
    }
    private void writeMsg(List<VikaCacheClient> writers, long hashKey, String key, Object msg) {
        if(writers == null || writers.size() == 0){
            return;
        }
        if (hashKey < 0) {
            hashKey = -hashKey; // why, Because (-5 % 16) will be -5 (not 0)  
        }
        
        /*
         * 1、对每条消息轮询所有的mcq，如果处理成功则直接返回。
         * 2、如果处理失败，则尝试写入下一个mcq。
         * 3、如果所有的mcq均写入失败，则不做处理。
         */
        boolean writeRs = false;
        for(int i = 0; i < writers.size(); i++){
            int index = (int)((i + hashKey) % writers.size());
            VikaCacheClient mqWriter = writers.get(index);
            
            try{
                if(mqWriter.set(key, msg)){
                    writeRs = true; 
                    StatLog.inc(getMQWriteKey(mqWriter.getServerPort(), key));
                    if(logEnable){
                        ApiLogger.info("mcq=" + mqWriter.getServerPort() + ", key=" + key + ", msg=" + msg);
                    }
                    break;
                }
            }catch(Exception e){
                ApiLogger.warn(new StringBuilder(128).append("Warn: save msg to one mq false [try next], key=").append(key).append(", mq=").append(mqWriter.getServerPort()).append(",msg").append(msg), e);
            }
            StatLog.inc(getMQWriteErrorKey(mqWriter.getServerPort(), key));
            ApiLogger.info(new StringBuilder(128).append("Info: save msg to mq false, key=").append(key).append(",mq=").append(mqWriter.getServerPort()).append(",msg").append(msg));
        }
        if(!writeRs){
            ApiLogger.error(new StringBuilder(128).append("Write mcq false, key=").append(key).append(", msg=").append(msg));
            throw new IllegalArgumentException(new StringBuilder(128).append("Write mcq false, key=").append(key).append(", msg=").append(msg).toString());
        }
    }

    private String getMQWriteErrorKey(String serverPort, String key){
        return getStatMQWriteErrorFlag() + "_" + serverPort + "_" + key;
    }
            
    private String getMQWriteKey(String serverPort, String key){
        return "write_mq_" + serverPort + "_" + key;
    }

    protected String getStatMQWriteErrorFlag() {        
        return "err_write_" + writeKey;
    }
    
    public String getWriteKey() {
        return writeKey;
    }
    public void setWriteKey(String writeKey) {
        this.writeKey = writeKey;
    }
    
    public void setMcqWriters(List<VikaCacheClient> mcqWriters) {
        this.mcqWriters = mcqWriters;
    }

    public boolean isDistinctBySize() {
		return distinctBySize;
	}

	public void setDistinctBySize(boolean distinctBySize) {
		this.distinctBySize = distinctBySize;
	}
    
    public int getDistinctBoundary() {
        return distinctBoundary;
    }

    public void setDistinctBoundary(int distinctBoundary) {
        this.distinctBoundary = distinctBoundary;
    }

    public List<VikaCacheClient> getMcq512BWriters() {
        return mcq512BWriters;
    }

    public void setMcq512BWriters(List<VikaCacheClient> mcq512bWriters) {
        mcq512BWriters = mcq512bWriters;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }

}
