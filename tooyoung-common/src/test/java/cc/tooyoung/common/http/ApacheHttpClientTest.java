package cc.tooyoung.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cc.tooyoung.common.util.ApiLogger;

/**
 *
 */
public class ApacheHttpClientTest {
	private static final String TEST_HEADER_PREFIX="for_test_";
	HttpServer server;
	int port= 0;
	static String getTestHeader(Headers header){
		StringBuffer sb=new StringBuffer();
		Iterator<String> keys=header.keySet().iterator();
		while(keys.hasNext()){
			String key=keys.next();
			if(key.toLowerCase().startsWith(TEST_HEADER_PREFIX)){
				List<String> values=header.get(key);
				for(String value:values){
					sb.append("header:"+key+""+value+"\n");
				}
			}
		}
		return sb.toString();
	}
	@Before
	public void before() throws IOException{
		port = RandomUtils.nextInt(10000);
		port = port +1000;
		server = HttpServer.create(new InetSocketAddress("localhost",port), 0);
		server.setExecutor(null);
		server.createContext("/test", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange ex) throws IOException {
				OutputStream out = ex.getResponseBody();
				StringBuffer sb=new StringBuffer();
				String ret="test";
				Headers header=ex.getResponseHeaders();
				Headers requestHeader=ex.getRequestHeaders();
				String url=ex.getRequestURI().toString();
				String headerStr=getTestHeader(requestHeader);
				ApiLogger.debug("headerStr:"+headerStr);
				sb.append(ret+"\n");
				sb.append(headerStr+"\n");
				sb.append(""+url+"\n");
				
				ex.sendResponseHeaders(200, sb.length());
				out.write(sb.toString().getBytes());
				out.flush();
				out.close();
			}
		});
		
		
		server.createContext("/slow", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange ex) throws IOException {
				ex.sendResponseHeaders(200, 10);
				OutputStream out = ex.getResponseBody();
				for(int i=0;i<10;i++){
					try {
						out.write("a".getBytes());
						out.flush();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				out.close();
			}
		});
		server.createContext("/convert", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange ex) throws IOException {
				ex.sendResponseHeaders(200, 10);
				OutputStream out = ex.getResponseBody();
				String aJson="{\"key\":1,\"key2\":2}";
				out.write(aJson.getBytes());
				out.close();
			}
		});
		
		server.createContext("/postbyte", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange ex) throws IOException {
				ex.sendResponseHeaders(200, 10);
				InputStream in = ex.getRequestBody();
				OutputStream out = ex.getResponseBody();
				IOUtils.copy(in, out);
				out.close();
			}
		});
		server.start();
		ApiLogger.debug("server start at port:"+port);
	}

	@Test
	public void testRequestUrl(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.get("http://localhost:"+port+"/test"+"?a=1&b=2");
		ApiLogger.debug(result);
		Assert.assertTrue(!StringUtils.isBlank(result));
	}
	
	@Test
	public void testGet(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.get("http://localhost:"+port+"/test");
		ApiLogger.debug(result);
		Assert.assertTrue(!StringUtils.isBlank(result));
	}
	@Test
	public void testPost(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.post("http://localhost:"+port+"/test",new HashMap<String,String>());
		ApiLogger.debug(result);
		Assert.assertTrue(!StringUtils.isBlank(result));
	}
	
	
	@Test
	public void testGetSlow(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.get("http://localhost:"+port+"/slow");
		ApiLogger.debug(result);
		Assert.assertTrue(StringUtils.isBlank(result));
	}
	@Test
	public void testPostSlow(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.post("http://localhost:"+port+"/slow",new HashMap<String,String>());
		ApiLogger.debug(result);
		Assert.assertTrue(StringUtils.isBlank(result));
	}
	@Test
	public void testGetAsyncSlow(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String result = client.getAsync("http://localhost:"+port+"/slow");
		ApiLogger.debug(result);
		Assert.assertTrue(StringUtils.isBlank(result));
	}
	@Test
	public void testGetConvert(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String json = "";
		/*
		JsonWrapper json = client.get("http://localhost:"+port+"/convert", new ResultConvert<JsonWrapper>() {

			@Override
			public JsonWrapper convert(String url, String post, String result) {
				try{
					return new JsonWrapper(result);
				}catch(Exception e){
					Assert.fail("wrong json format json String:"+result);
					return null;
				}
			}
		});
		*/
		ApiLogger.debug(json.toString());
		Assert.assertTrue(StringUtils.isBlank(json.toString()));
	}
	@Test
	public void testBuildGet(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String headvalue="headvalue";
		List<Integer> ids=new ArrayList<Integer>();
		ids.add(11);
		ids.add(12);
		String result = client.buildGet("http://localhost:" + port + "/test")
				.withHeader(TEST_HEADER_PREFIX + "ahead", headvalue)
				.withParam("uid", "1").withParam("uid", "2")
				.withParam("uid2", new String[] { "3", "4" })
				.withParam("uid3", 3l)
				.withParam("uid4", new int[] { 1, 2 })
				.withParam("uid5", ids)
				.withParam("content", "中文").execute();
		ApiLogger.debug(result);
		Assert.assertTrue(StringUtils.contains(result, headvalue));
	}
	@Test
	public void testBuildPost(){
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		String headvalue="headvalue";
		String result=client.buildPost("http://localhost:" + port + "/test?a=1")
				.withHeader(TEST_HEADER_PREFIX+"ahead", headvalue).withParam("uid", "1")
				.withParam("uid", "2").withParam("uid2", new String[]{"3","4"})
				.withParam("content", "中文").execute();
		ApiLogger.debug(result);
		Assert.assertTrue(StringUtils.contains(result, headvalue));
	}
	
	
	@Test
	public void testPostByteArray() throws UnsupportedEncodingException{
		ApiHttpClient client = new ApacheHttpClient(100, 1000, 1000, 1024*100);
		byte[] bytes = "test".getBytes("UTF-8");
		String result=client.postMulti("http://localhost:" + port + "/postbyte?a=1",bytes);
		ApiLogger.debug(result);
		Assert.assertTrue(Arrays.equals(bytes, result.getBytes("UTF-8")));
	}
	
	@After
	public void after(){
		server.stop(1);
	}
}
