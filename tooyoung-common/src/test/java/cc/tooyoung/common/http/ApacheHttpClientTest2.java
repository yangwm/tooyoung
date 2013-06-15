package cc.tooyoung.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import cc.tooyoung.common.util.ByteArrayPart;

/**
 * 
 * @author yongkun
 *
 */
public class ApacheHttpClientTest2 {
	ApacheHttpClient client = new ApacheHttpClient(100, 8000, 8000, 1024*100);
	String source="3741904135";

	@Test
	public void testBaidu(){
		String str = client.buildGet("http://www.baidu.com/s").withCharset("gbk").withParam("wd", "娴嬭瘯").execute();
		System.out.println(str);
		Assert.assertNotNull(str);
	}

	@Test
	public void testWeiboApi() throws Exception{
		String str = client.buildGet("http://api.weibo.com/2/statuses/user_timeline.json?uid=1734528095&source=2975945008").withCharset("utf-8").execute();
		System.out.println(str);
		Assert.assertNotNull(str);
//		JsonWrapper json = new JsonWrapper(str);
//		JsonNode node = json.getJsonNode("statuses");
//		JsonNode status = node.getElementValue(0);
//		Assert.assertNotNull(status);
//		String domain = status.getFieldValue("user").getFieldValue("domain").getTextValue();
//		Assert.assertEquals("jkl00", domain);
	}
	
}
