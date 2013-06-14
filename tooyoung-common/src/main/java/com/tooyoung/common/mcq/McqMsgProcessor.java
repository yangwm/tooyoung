/**
 * 
 */
package com.tooyoung.common.mcq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.tooyoung.common.cache.driver.VikaCacheClient;
import com.tooyoung.common.stat.StatLog;
import com.tooyoung.common.util.CommonUtil;
import com.tooyoung.common.util.ApiLogger;


/**
 * mcq msg processor
 * 
 * @author yangwm Nov 4, 2011 3:03:14 PM
 */
public class McqMsgProcessor implements StartReadingAble{

    private String readKey;
    private List<VikaCacheClient> mcqReaders;

    //读取的线程数
    private int readThreadCountEachMcq = 3;
    
    //连续读取的数量
    private int readCountOnce = 100;
    
    //连续读取若干数量 or 没有读取到后的等待时间间隔
    private int waitTimeOnce = 100;   
    
    private List<Thread> readThreads = new ArrayList<Thread>();

    /**
     * 错误队列 
     */
    private McqBaseWriter errorMsgWriter;
    
    private MsgHandler msgHandler;
    private String processorName;
    private boolean recvLogEnable = true;
    
    
    public void startReading(){
        if (msgHandler == null) {
            throw new RuntimeException("msgHandler must be not null!!!");
        }
        processorName = "McqMsgProcessor[" + msgHandler.getClass().getSimpleName() + "]";
        
        for(VikaCacheClient mcqr : mcqReaders){              
            int i = 0;
            while(i++ < readThreadCountEachMcq){
                Thread t = createReadThread(mcqr);
                t.start();
                readThreads.add(t);
            }               
        }
    }

    protected Thread createReadThread(final VikaCacheClient mqr){
        Thread t = new Thread("thread_" + McqUtil.processorId.addAndGet(1) + "_mq_" + mqr.getServerPort()){
            @Override
            public void run() {             
                readFrmMQ(mqr);
            }
        };
        t.setDaemon(true);      
        return t;
    }
    
    protected void readFrmMQ(VikaCacheClient mqReader){
        // wait a moment for system init.
        McqUtil.waitForInit(processorName);

        String portInfo = new StringBuilder(64).append("KEY:").append(getReadKey())
                .append("\tServer:").append(mqReader.getServerPort()).toString();
        ApiLogger.info("Start mq reader!" + portInfo);
        AtomicInteger continueReadCount = new AtomicInteger(0);
        while(true){
            try {
                String msg = null;      
                while(McqBaseManager.IS_ALL_READ.get() && (msg = (String) mqReader.get(getReadKey())) != null){                    
                    StatLog.inc(getStatMQReadFlag());
                    StatLog.inc(getStatMQReadStatFlag());
                    if(ApiLogger.isTraceEnabled()){
                        StatLog.inc(getMQReadDataKey(mqReader.getServerPort(), getReadKey()));
                    }
                    
                    if (recvLogEnable) {
                        ApiLogger.info(processorName + " recvLog readKey:" + readKey + " msg:" + msg);
                    }
                    
                    int result = CommonUtil.MQ_PROCESS_ABORT;
                    try {
                    	/** TODO yangwm fix */ 
                        String msgJson = msg;//new JsonWrapper(msg);

                        result = msgHandler.handleMsq(getReadKey(), msg, msgJson);        
                        
                        if(continueReadCount.addAndGet(1) % readCountOnce == 0){                            
                            McqUtil.safeSleep(waitTimeOnce);
                            continueReadCount.set(0);
                            //StatLog.inc(getMQReadSleepKey(mqReader.getServerPort(), getReadKey()), waitTimeOnce);
                        }
                    } catch (Exception e) {
                        result = CommonUtil.MQ_PROCESS_RETRY;
                        ApiLogger.warn(new StringBuilder(128).append("Error: processing the msg frm mq error, ").append(portInfo).append(", msg=").append(msg), e);
                    }
                    
                    // 将处理不完整的消息写入失败队列 
                    if (result == CommonUtil.MQ_PROCESS_RETRY) {
                        saveErrorMsg(msg);
                    } else if (result == CommonUtil.MQ_PROCESS_ABORT) {
                        ApiLogger.warn(new StringBuilder(256).append(processorName + " Abort msg:").append(msg));
                    }
                }
                
                if (!McqBaseManager.IS_ALL_READ.get()) {
                    ApiLogger.info(processorName + " is alive but not read message.");
                }
                
                McqUtil.safeSleep(waitTimeOnce);
                StatLog.inc(getStatMQReadStatFlag());               
                
                //should response thread interrupted
                if(Thread.interrupted()){
                    ApiLogger.warn(new StringBuilder(32).append("Thread interrupted :").append(Thread.currentThread().getName()));
                    break;
                }
            } catch (Exception e) {
                ApiLogger.error(new StringBuilder("Error: when read mq. key:").append(getReadKey()), e);
            }
        }
    }

    /**
     * 如果msg处理失败，再写回mq，供后续恢复处理
     * @param msg
     */
    protected void saveErrorMsg(String msg){
        if(msg == null){
            return;
        }
        if(errorMsgWriter == null){
            ApiLogger.error(processorName + " process false and ignore msg:" + msg);
            return;
        }
        errorMsgWriter.writeMsg(msg);
    }

    public void setMcqReaders(List<VikaCacheClient> mcqReaders) {
        this.mcqReaders = mcqReaders;
        /** TODO yangwm fix , need?? , but code is not beatiful  
        for(VikaCacheClient mcqr : mcqReaders){
            mcqr.getClient().setPrimitiveAsString(true);                
        }
        */
    }

    public void setReadThreadCountEachMcq(int readThreadCountEachMcq) {
        this.readThreadCountEachMcq = readThreadCountEachMcq;
    }
    public void setReadCountOnce(int readCountOnce) {
        this.readCountOnce = readCountOnce;
    }
    public void setWaitTimeOnce(int waitTimeOnce) {
        this.waitTimeOnce = waitTimeOnce;
    }
    public String getReadKey() {
        return readKey;
    }
    private String getMQReadDataKey(String serverPort, String key){
        return "read_mq_data_" + serverPort + "_" + key;
    }
    protected String getStatMQReadFlag() {      
        return "all_mq_read_MSG_" + readKey;
    }
    protected String getStatMQReadStatFlag() {
        return "all_mq_read_stat_" + readKey;
    }
    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }
    public void setReadThreads(List<Thread> readThreads) {
        this.readThreads = readThreads;
    } 
    public void setMsgHandler(MsgHandler msgHandler) {
        this.msgHandler = msgHandler;
    }
    public void setErrorMsgWriter(McqBaseWriter errorMsgWriter) {
        this.errorMsgWriter = errorMsgWriter;
    }
    public void setRecvLogEnable(boolean recvLogEnable) {
        this.recvLogEnable = recvLogEnable;
    }
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }
    public List<VikaCacheClient> getMcqReaders() {
        return mcqReaders;
    }
    public int getReadThreadCountEachMcq() {
        return readThreadCountEachMcq;
    }
    public int getReadCountOnce() {
        return readCountOnce;
    }
    public int getWaitTimeOnce() {
        return waitTimeOnce;
    }
    public List<Thread> getReadThreads() {
        return readThreads;
    }
    public McqBaseWriter getErrorMsgWriter() {
        return errorMsgWriter;
    }
    public MsgHandler getMsgHandler() {
        return msgHandler;
    }
    public boolean isRecvLogEnable() {
        return recvLogEnable;
    }
    public String getProcessorName() {
        return processorName;
    }
    
}
