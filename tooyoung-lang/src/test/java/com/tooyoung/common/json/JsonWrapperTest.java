/**
 * 
 */
package com.tooyoung.common.json;

import java.util.Arrays;

import org.junit.Test;

import com.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm May 21, 2013 8:18:42 PM
 */
public class JsonWrapperTest {
    
    @Test
    public void test() throws Exception {
        JsonWrapper jsonWrapper = new JsonWrapper("{\"uid\":1750715731,\"name\":\"yangwm\",\"optional\":[\"image\",\"create_at\",\"update_at\"],\"indexs\":[1001,1002]}");
        
        ApiLogger.debug(jsonWrapper.getString("uid"));
        ApiLogger.debug(jsonWrapper.getLong("uid"));
        
        ApiLogger.debug(jsonWrapper.getString("name"));
        ApiLogger.debug(jsonWrapper.getLong("name")); // not long 
        
        ApiLogger.debug(Arrays.toString(jsonWrapper.getStringArr("optional")));
        ApiLogger.debug(Arrays.toString(jsonWrapper.getLongArr("optional"))); // not long[] 
        
        ApiLogger.debug(Arrays.toString(jsonWrapper.getStringArr("indexs")));
        ApiLogger.debug(Arrays.toString(jsonWrapper.getLongArr("indexs")));
    }

}
