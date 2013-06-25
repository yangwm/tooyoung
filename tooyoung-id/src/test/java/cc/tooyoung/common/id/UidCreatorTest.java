/**
 * 
 */
package cc.tooyoung.common.id;

import org.junit.Test;

import cc.tooyoung.common.TestBase;
import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * 
 * @author yangwm Jun 25, 2013 4:57:07 PM
 */
public class UidCreatorTest extends TestBase {
    
    @Test 
    public void testGenerateId() {
        IdCreator idCreator = getIdCreator();
        long t1 = System.currentTimeMillis();
        
        int count = 100;
        for(int i = 0; i < count; i++){
            long id = idCreator.generateId(2);
            ApiLogger.debug("UidCreatorTest testGenerateId id:" + id); // UidHelperTest.checkValid(id, true);
        }
        
        long t2 = System.currentTimeMillis();
        ApiLogger.debug(String.format("UidCreatorTest count=%s,time=%sms", count, (t2 - t1)));
    }

    private IdCreator getIdCreator(){
        IdCreator idCreator = (IdCreator)ctx.getBean("uidCreator");
        return idCreator;
    }
}
