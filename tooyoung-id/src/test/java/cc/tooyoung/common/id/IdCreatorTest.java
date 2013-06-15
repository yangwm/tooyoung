/**
 * 
 */
package cc.tooyoung.common.id;

import org.junit.Test;

import cc.tooyoung.common.cache.driver.VikaCacheClient;
import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm May 16, 2011 7:13:35 PM
 */
public class IdCreatorTest {
    
    //@Test
    public void testGenerateIdForUuid() {
        IdCreator idCreator = createIdFactory("test1.weibao:5001,test2.weibao:5001");
        long t1 = System.currentTimeMillis();
        
        int count = 100;
        for(int i = 0; i < count; i++){
            long id = idCreator.generateId(UuidConst.BizFlag.api);
            UuidHelperTest.checkValid(id, true);
        }
        
        long t2 = System.currentTimeMillis();
        ApiLogger.debug(String.format("count=%s,time=%sms", count, (t2 - t1)));
    }

    private IdCreator createIdFactory(String idGenerateHost){
        IdCreator idCreator = new IdCreator();
        
        VikaCacheClient idGenerateClient = new VikaCacheClient();
        idGenerateClient.setServerPort(idGenerateHost);
        idGenerateClient.setPrimitiveAsString(true);
        idGenerateClient.init();
        idCreator.setIdGenerateClient(idGenerateClient);

        return idCreator;
    }

}
