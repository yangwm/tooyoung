/**
 * 
 */
package com.tooyoung.common.mcq;

/**
 * Base Writer(implements mcq or others) 
 * 
 * @author yangwm Dec 23, 2011 10:21:10 AM
 */
public interface BaseWriter {
    
    /**
     * return writeKey 
     * 
     */
    String getWriteKey();
    
    /**
     * write a string msg 
     * 
     * @param msg
     */
    void writeMsg(String msg);
    /**
     * write a string msg 
     * 
     * @param hashKey 
     * @param msg
     */
    void writeMsg(long hashKey, String msg);
    
    /**
     * write a byte[] msg 
     * 
     * @param msg
     */
    void writeMsg(byte[] msg);
    /**
     * write a byte[] msg 
     * 
     * @param hashKey 
     * 
     * @param hashKey
     * @param msg
     */
    void writeMsg(long hashKey, byte[] msg);

}
