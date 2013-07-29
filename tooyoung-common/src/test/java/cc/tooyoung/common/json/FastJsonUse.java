/**
 * 
 */
package cc.tooyoung.common.json;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author yangwm Jul 18, 2013 4:51:08 PM
 */
public class FastJsonUse {

    @Test
    public void testToJson() {
        TestJson testJson = new TestJson(15, "15");
        String testJsonJson = JSON.toJSONString(testJson);
        ApiLogger.debug("testJsonJson=" + testJsonJson);
    }
    @Test
    public void testParseJson() {
        String testJsonJson = "{\"db\":15,\"table\":\"15\"}";
        TestJson testJson = JSON.parseObject(testJsonJson, TestJson.class);
        ApiLogger.debug("testJson=" + testJson.toString());
    }
    
    @Test
    public void testToJsonObject() {
        TestJson testJson = new TestJson(15, "15");
        JSONObject testJsonJsonObject = (JSONObject) JSON.toJSON(testJson);
        ApiLogger.debug("testJsonJsonObject=" + testJsonJsonObject);
    }
    @Test
    public void testParseJsonObject() {
        String testJsonJson = "{\"db\":15,\"table\":\"15\"}";
        JSONObject testJsonObject = JSON.parseObject(testJsonJson);
        ApiLogger.debug("testJsonObject=" + testJsonObject.toString());
    }

    @Test
    public void testWriteClassName() {
        TestJson testJson = new TestJson(15, "15");
        testJson.setId(5555555555555555555L);
        String testJsonJson = JSON.toJSONString(testJson, SerializerFeature.WriteClassName);
        ApiLogger.debug("testJsonJson=" + testJsonJson);
        
        testJson = (TestJson) JSON.parse(testJsonJson);
        ApiLogger.debug("testJson=" + testJson.toString());
    }
    
    @Test
    public void testSort() {
        TestJson testJson = new TestJson(15, "15");
        String testJsonJson = JSON.toJSONString(testJson, SerializerFeature.SortField);
        ApiLogger.debug("testJsonJson=" + testJsonJson);
    }
    
}
