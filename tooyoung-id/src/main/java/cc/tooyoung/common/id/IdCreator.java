/**
 * 
 */
package cc.tooyoung.common.id;

import java.util.Random;

import cc.tooyoung.common.cache.driver.NaiveMemcacheClient;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.CommonUtil;
import cc.tooyoung.common.util.Util;

/**
 * Id Creator
 * 
 * @author yangwm May 29, 2013 2:37:58 PM
 */
public class IdCreator implements IdCreate {
    
    public final static int RETRY_TIMES = 5;
    public final static Random randomGenerator = new Random();
    
    private NaiveMemcacheClient idGenerateClient;


    /**
     * 根据bizFlag获取id 
     * 
     * @param bizFlag
     * @return
     */
    public long generateId(int bizFlagValue){
    	return getNextId(bizFlagValue);
    }
    
    /**
     * 从发号器获取id
     * @see 如果bizFlag为null，则返回默认id，否则返回非默认id
     * @param bizFlag
     * @return
     */
    private long getNextId(int bizFlagValue){
    	for(int i = 0; i < RETRY_TIMES; i++){
            long nextId = 0;
            try {
                String idKey = bizFlagValue + "id" + randomGenerator.nextInt();
                String id = (String) idGenerateClient.get(idKey);
//                if (ApiLogger.isDebugEnabled()) {
//                    ApiLogger.debug("nextId idGenerateClient:" + idGenerateClient.getServerPort() + ", idKey:" + idKey + ", id:" + id);
//                }
                nextId = Util.convertLong(id);
            } catch (Exception e) {
                ApiLogger.error("Error: in idGenerateClient get");
            }
            if (nextId > 0) {
            	return nextId;
            }
            CommonUtil.safeSleep(2 * i);
            ApiLogger.warn("Warn - retry create id, idGenerateClient:" + idGenerateClient.getServerPort() + ", tryTime:" + i + ", nextId=" + nextId);
        }
        
        // FIXME 重连  
        throw new IdCreateException("Error: false when get id from idGenerateClient:" + idGenerateClient.getServerPort());
    }

    // getter setter 

    public NaiveMemcacheClient getIdGenerateClient() {
        return idGenerateClient;
    }
    public void setIdGenerateClient(NaiveMemcacheClient idGenerateClient) {
        this.idGenerateClient = idGenerateClient;
    }

    
}
