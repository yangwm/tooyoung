/**
 * 
 */
package cc.tooyoung.common.cache;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import cc.tooyoung.common.cache.driver.NaiveMemcacheClient;
import cc.tooyoung.common.collection.SortedVector;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.memcache.naive.CasValue;

/**
 * MemCached Client Test
 * 
 * @author yangwm Feb 23, 2012 5:32:32 PM
 */
public class MemCachedClientTest {
    
    //@Test
    public void testSetExpire() {
        String key = "yangwm.testSet.se";
        String val = "abc";
        MemcacheClient memcacheClient = getMemcacheClient();
        /*
         * if expire time超过30天，会值导致set不进去为null   
         */
        boolean result = memcacheClient.set(key, val, new Date(1000L * 60 * 1440 * 30));
        Assert.assertTrue(result);

        val = (String) memcacheClient.get(key);
        ApiLogger.debug("testSetExpire val:" + val);
        Assert.assertEquals("abc", val);
    }
    
    //@Test
    public void testSetInvalidExpire() {
        String key = "yangwm.testSet.sie";
        String val = "abc";
        MemcacheClient memcacheClient = getMemcacheClient();
        /*
         * if expire time超过30天，会值导致set不进去为null   
         */
        boolean result = memcacheClient.set(key, val, new Date(1000L * 60 * 1440 * 30 + 1000L));
        Assert.assertTrue(result);

        val = (String) memcacheClient.get(key);
        ApiLogger.debug("testSetErrorExpire val:" + val);
        Assert.assertEquals(null, val);
    }
    
    //@Test
    public void testGetSet() {
        String key = "yangwm.testGetSet";
        MemcacheClient memcacheClient = getMemcacheClient();
        
        /*
         * SortedVector 更改implements VectorInterface, Iterable, Serializable不影响序列化数据  
         */
        SortedVector val = (SortedVector) memcacheClient.get(key);
        if (val == null) {
            val = new SortedVector(new long[]{3, 4, 5});
        }
        val.add(val.getActualItems()[val.getLen() - 1] + 1);
        ApiLogger.debug("testGetSet val:" + val);
        boolean result = memcacheClient.set(key, val);
        
        Assert.assertTrue(result);
        //delete(key);
    }
    
    //@Test
    public void testCasSample() {
        String key = "yangwm.testCas";
        String val = "yangwm.test.value";
        
        boolean result = casSample(key, val);
        
        Assert.assertTrue(result);
        delete(key);
    }
    
    public boolean casSample(String key, String val) {
        MemcacheClient memcacheClient = getMemcacheClient();

        int tryTime = 0;
        boolean success = false;
        while(!success && tryTime++ < 2) {
            CasValue<Object> valueCas = memcacheClient.gets(key);
            if (valueCas != null) {
                String value = (String)valueCas.getValue() + tryTime;
                valueCas.setValue(value);
                success = memcacheClient.cas(key, valueCas);
            } else {
                /*
                 * do nothing, or can use add, or can use set, but can't use cas 
                 */
                //success = true; // do nothing 
                success = memcacheClient.add(key, val);
            }
            
            if (success == false) {
                // log info 
            }
        }
        if (success == false) {
            /*
             * can use delete or can use set 
             */
            success = memcacheClient.delete(key);
            
            /*
             * or can use set 
             *
            CasValue<Object> valueCas = memcacheClient.gets(key);
            if (valueCas != null) {
                String value = (String)valueCas.getValue() + tryTime;
                valueCas.setValue(value);
                success = memcacheClient.set(key, valueCas.getValue());
            } else {
                success = true; // do nothing 
            }
             */
        }
        if (success == false) {
            // log warn 
        }
        
        return success;
    }
    
    //@Test
    public void testCasIsFalse() {
        MemcacheClient memcacheClient = getMemcacheClient();
        
        String key = "yangwm.test";
        String value = "yangwm.test.value";
        memcacheClient.set(key, "yangwm.test.value");
        
        CasValue<Object> valueCas = memcacheClient.gets(key);
        for (int i = 0; i < 5; i++) {
            valueCas.setValue((String)valueCas.getValue() + i);
            boolean result = memcacheClient.cas(key, valueCas);
            if (i % 2 == 0) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
                valueCas = memcacheClient.gets(key); // last time update false, so need get again   
            }
            
            value = (String)memcacheClient.get(key);
            System.out.println("value:" + value + ", valueCas.getValue():" + valueCas.getValue());
            Assert.assertEquals(value, valueCas.getValue());
        }
        
        delete(key);
    }

    //@Test
    public void testCasIsTrue() {
        MemcacheClient memcacheClient = getMemcacheClient();
        
        String key = "yangwm.test";
        String value = "yangwm.test.value";
        memcacheClient.set(key, value);
        
        for (int i = 0; i < 5; i++) {
            long id = i * 10;
            
            CasValue<Object> valueCas = memcacheClient.gets(key);
            valueCas.setValue((String)valueCas.getValue() + id);
            boolean result = memcacheClient.cas(key, valueCas);
            Assert.assertTrue(result);
            
            value = (String)memcacheClient.get(key);
            //System.out.println("after add id:" + id + ", value:" + value + ", valueCas.getValue():" + valueCas.getValue());
            Assert.assertEquals(value, valueCas.getValue());
        }
        
        delete(key);
    }

    private boolean delete(String key) {
        MemcacheClient memcacheClient = getMemcacheClient();
        return memcacheClient.delete(key);
    }
    
    private MemcacheClient getMemcacheClient() {
        NaiveMemcacheClient vikaCacheClient = new NaiveMemcacheClient();
        vikaCacheClient.setMinSpareConnections(2);
        vikaCacheClient.setMaxSpareConnections(15);
        vikaCacheClient.setConsistentHashEnable(false);
        vikaCacheClient.setFailover(true);
        vikaCacheClient.setServerPort("testmc:11211");
        vikaCacheClient.init();
        return vikaCacheClient;
    }
    
}
