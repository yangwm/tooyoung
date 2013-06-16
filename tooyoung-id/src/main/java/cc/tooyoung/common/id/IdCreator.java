/**
 * 
 */
package cc.tooyoung.common.id;

import java.util.Random;

import cc.tooyoung.common.cache.driver.NaiveMemcacheClient;
import cc.tooyoung.common.id.UuidConst.BizFlag;
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
     * 根据bizFlag获取uuid 
     * 
     * @param bizFlag
     * @return
     */
    public long generateId(BizFlag bizFlag){
    	return getNextId(bizFlag);
    }
    
    /**
     * 从发号器获取uuid
     * @see 如果bizFlag为null，则返回默认uuid，否则返回非默认uuid
     * @param bizFlag
     * @return
     */
    private long getNextId(BizFlag bizFlag){
    	for(int i = 0; i < RETRY_TIMES; i++){
            long nextId = 0;
            try {
                String uuidKey = "id" + randomGenerator.nextInt();
                if (bizFlag != null) {
                    uuidKey = bizFlag.getValue() + uuidKey;
                }
                String uuid = (String) idGenerateClient.get(uuidKey);
//                if (ApiLogger.isDebugEnabled()) {
//                    ApiLogger.debug("nextId idGenerateClient:" + idGenerateClient.getServerPort() + ", uuidKey:" + uuidKey + ", uuid:" + uuid);
//                }
                nextId = Util.convertLong(uuid);
            } catch (Exception e) {
                ApiLogger.error("Error: in idGenerateClient get");
            }
            if (UuidHelper.isValidId(nextId)) {
            	return nextId;
            }
            CommonUtil.safeSleep(2 * i);
            ApiLogger.warn("Warn - retry create id, idGenerateClient:" + idGenerateClient.getServerPort() + ", tryTime:" + i + ", nextId=" + nextId);
        }
        
        // FIXME 重连  
        throw new IdCreateException("Error: false when get uuid from idGenerateClient:" + idGenerateClient.getServerPort());
    }

    // getter setter 

    public NaiveMemcacheClient getIdGenerateClient() {
        return idGenerateClient;
    }
    public void setIdGenerateClient(NaiveMemcacheClient idGenerateClient) {
        this.idGenerateClient = idGenerateClient;
    }

    
}
