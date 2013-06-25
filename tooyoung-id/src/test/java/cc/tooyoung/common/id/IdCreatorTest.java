/**
 * 
 */
package cc.tooyoung.common.id;

import org.junit.Test;

import cc.tooyoung.common.cache.driver.NaiveMemcacheClient;
import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm May 16, 2011 7:13:35 PM
 */
public class IdCreatorTest {
    
    @Test
    public void testGenerateIdForUuid() {
        IdCreator idCreator = createIdCreator("testuuid:5001,testuuid:5001");
        long t1 = System.currentTimeMillis();
        
        int count = 10;
        for(int i = 0; i < count; i++){
            long id = idCreator.generateId(2);
            ApiLogger.debug("IdCreatorTest testGenerateIdForUuid id:" + id); // UuidHelperTest.checkValid(id, true);
        }
        
        long t2 = System.currentTimeMillis();
        ApiLogger.debug(String.format("count=%s,time=%sms", count, (t2 - t1)));
    }
    
    @Test
    public void testGenerateIdForUid() {
        IdCreator idCreator = createIdCreator("testuid:5101,testuid:5101");
        long t1 = System.currentTimeMillis();
        
        int count = 10;
        for(int i = 0; i < count; i++){
            long id = idCreator.generateId(2);
            ApiLogger.debug("IdCreatorTest testGenerateIdForUid id:" + id); // UidHelperTest.checkValid(id, true);
        }
        
        long t2 = System.currentTimeMillis();
        ApiLogger.debug(String.format("count=%s,time=%sms", count, (t2 - t1)));
    }

    private IdCreator createIdCreator(String idGenerateHost){
        IdCreator idCreator = new IdCreator();
        
        NaiveMemcacheClient idGenerateClient = new NaiveMemcacheClient();
        idGenerateClient.setServerPort(idGenerateHost);
        idGenerateClient.setPrimitiveAsString(true);
        idGenerateClient.init();
        idCreator.setIdGenerateClient(idGenerateClient);

        return idCreator;
    }

}
