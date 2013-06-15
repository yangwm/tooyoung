package cc.tooyoung.common.http;

import static cc.tooyoung.common.util.ApiLogger.debug;
import static cc.tooyoung.common.util.ApiLogger.isDebugEnabled;
import static cc.tooyoung.common.util.ApiLogger.warn;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;

import cc.tooyoung.common.concurrent.StandardThreadExecutor;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.Assert;
import cc.tooyoung.common.util.ByteArrayPart;
import cc.tooyoung.common.util.Util;



public class ApacheHttpClient implements ApiHttpClient{
	
	//private static final Log log = LogFactory.getLog("mblogapi");
	
	private MultiThreadedHttpConnectionManager connectionManager;
	private HttpClient client;
	
	private int maxSize;
	
	private String proxyHostPort;
	private int soTimeOut;
	
	private ExecutorService httpPool;
	
	public ApacheHttpClient() {
		// change timeout to 2s avoid block thread-pool (Tim)
		this(150, 2000, 2000, 1024 * 1024);
	}
	
	public ApacheHttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs, int maxSize) {
		this(maxConPerHost,conTimeOutMs,soTimeOutMs,maxSize,1,300);
	}
	
  	public ApacheHttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs, int maxSize,int minThread,int maxThread) {
  		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setMaxTotalConnections(600);//这个值要小于tomcat线程池是800
		params.setDefaultMaxConnectionsPerHost(maxConPerHost);
		params.setConnectionTimeout(conTimeOutMs);
		params.setSoTimeout(soTimeOutMs);
		this.soTimeOut=soTimeOutMs;

		HttpClientParams clientParams = new HttpClientParams();
  		//忽略cookie 避免 Cookie rejected 警告
  		clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
  		client = new HttpClient(clientParams,connectionManager);
		this.maxSize = maxSize;
		httpPool = new StandardThreadExecutor(minThread, maxThread);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				httpPool.shutdown();
				connectionManager.shutdown();
			}
  		}));
  	}
  	
  	/**
  	 * 
  	 * @param url
  	 * @return
  	 * @deprecated plesase use {@link #get(String)}
  	 */
  	@Deprecated
  	public String getURL(String url) {
  		if(HttpManager.isBlockResource(url)){
			debug("getURL blockResource url="+url);
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String result = null;
		long t = System.currentTimeMillis();
		try {
			getURL(url, out);
			result = new String(out.toByteArray(), "utf-8");
			if (isDebugEnabled()) {
				if (result.length() > 500) {
					debug("getURL:" + url + ", time:"
							+ (System.currentTimeMillis() - t) + ", result:"
							+ result.substring(0, 500));
				} else {
					debug("getURL:" + url + ", time:"
							+ (System.currentTimeMillis() - t) + ", result:"
							+ result);
				}
			}
		} catch (Exception e) {
			warn("getURL error, URL:" + url, e);
		}
		return result;
  	}
  	/**
  	 * Get URL with nio.
  	 * @param url
  	 * @param bb
  	 */
  	@Deprecated
  	public void getURL(String url, ByteBuffer bb){
  		long start = System.currentTimeMillis();
		HttpMethod get = new GetMethod(url);
		int i=0;
		try {
		    addRemoteInvokerHeader(get);
			client.executeMethod(get);
			int code = get.getStatusCode();
			if (code != 200) {
				warn("getURL:" + url + ",return code:" + code);
			}
			get.getResponseBodyAsString();
			InputStream in = get.getResponseBodyAsStream();
			ReadableByteChannel rc = Channels.newChannel(in);
			i = rc.read(bb);
		} catch (Exception ex) {
			warn("HTTP GET " + url + " RETURN " + ex.getMessage());
		} finally {
			bb.flip();
			get.releaseConnection();
			accessLog(System.currentTimeMillis() - start, "GET",
					get.getStatusLine() != null ? get.getStatusCode() : -1, i,
					url,"", null);
		}
  	}
  	@Deprecated
  	public int getURL(String url, OutputStream out) {
  		long start = System.currentTimeMillis();
		int readLen = 0;
		HttpMethod get = new GetMethod(url);
		try {
		    addRemoteInvokerHeader(get);
			client.executeMethod(get);
			InputStream in = get.getResponseBodyAsStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
				readLen += len;
				if (readLen > maxSize){
					break;
				}
			}
			in.close();
		} catch (Exception ex) {
			warn("HTTP GET " + url + " RETURN " + ex.getMessage());
		} finally {
			get.releaseConnection();
			accessLog(System.currentTimeMillis() - start, "GET",
					get.getStatusLine() != null ? get.getStatusCode() : -1,
					readLen, url,"", null);
		}
		return readLen;
  	}
  	private String mapToString(Map<String, ?> nameValues){
		StringBuffer sb=new StringBuffer();
		if(nameValues==null){ 
			return sb.toString();
		}
		for(Map.Entry<String, ?> entry:nameValues.entrySet()){
			if(entry.getValue() instanceof String){
				sb.append(entry.getKey()+"="+entry.getValue()+"&");
			}else if(entry.getValue() instanceof String[]){
				String[] values=(String[]) entry.getValue();
				for(String value:values){
					sb.append(entry.getKey()+"="+value+"&");
				}
			}
		}
		Util.trim(sb,'&');
		return sb.toString();
	}
  	@Deprecated
  	public String requestURL(String url, Map<String, ?> nameValues) {
  		if(HttpManager.isBlockResource(url)){
			debug("requestURL blockResource url="+url);
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String result = null;
		try {
			long t = System.currentTimeMillis();
			requestURL(url, nameValues, out);
			result = new String(out.toByteArray(), "utf-8");
			if (isDebugEnabled()) {
				if (result.length() > 500) {
					debug("getURL:" + url + ", time:"
							+ (System.currentTimeMillis() - t) + ", result:"
							+ result.substring(0, 500));
				} else {
					debug("getURL:" + url + ", time:"
							+ (System.currentTimeMillis() - t) + ", result:"
							+ result);
				}
			}
		} catch (Exception e) {
			warn("requestURL error, URL:" + url, e);
		}
		return result;
  	}
  	@Deprecated
  	public int requestURL(String url, Map<String, ?> nameValues, OutputStream out) {
  		long start = System.currentTimeMillis();
		int readLen = 0;
		PostMethod post = new PostMethod(url);
		HttpMethodParams params = new HttpMethodParams();
		params.setContentCharset("utf-8");
		post.setParams(params);
		try {
			if (nameValues != null && !nameValues.isEmpty()) {
				List<NameValuePair> list = new ArrayList<NameValuePair>(
						nameValues.size());
				for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
					if (entry.getKey() != null && !entry.getKey().isEmpty()){
						list.add(new NameValuePair(entry.getKey(), entry
								.getValue().toString()));
					}
					else{
						post.setRequestEntity(new StringRequestEntity(entry
								.getValue().toString(), "text/xml", "utf-8"));
					}
				}
				if (!list.isEmpty()){
					post.setRequestBody(list.toArray(new NameValuePair[list
							.size()]));
				}
			}
			addRemoteInvokerHeader(post);
			client.executeMethod(post);
			InputStream in = post.getResponseBodyAsStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
				readLen += len;
				if (readLen > maxSize){
					break;
				}
			}
			in.close();
		} catch (Exception ex) {
			warn("HTTP GET " + url + " RETURN " + ex.getMessage());
		} finally {
			post.releaseConnection();
			accessLog(System.currentTimeMillis() - start, "POST",
					post.getStatusLine() != null ? post.getStatusCode() : -1,
					readLen, url,"", mapToString(nameValues));
		}
		return readLen;
  	}
  	@Deprecated
  	public String multURL_UTF8(String url, Map<String, Object> nameValues) {
		long start = System.currentTimeMillis();
		if(HttpManager.isBlockResource(url)){
			debug("multURL_UTF8 blockResource url="+url);
			return "";
		}
		String retr = "";
		PostMethod post = new PostMethod(url);
		String response=null;
		try {
			Part[] parts = new Part[nameValues.size()];
			if (nameValues != null && !nameValues.isEmpty()) {
				int i = 0;
				for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
					if (entry.getValue() instanceof ByteArrayPart) {
						ByteArrayPart data = (ByteArrayPart) entry.getValue();
						parts[i++] = data;
						continue;
					}
					if (entry.getValue() instanceof FileItem) {
						FileItem item = (FileItem) entry.getValue();
						String contentType = item.getContentType();
						String uid = (String) nameValues.get("uid");
						debug("ApacheHttpClient pic upload contentType "
										+ contentType + ". uid:" + uid);
						if (contentType == null) {
							warn("ApacheHttpClient pic upload contentType is null. uid:"
											+ uid);
						}
						if ("application/octet-stream".equals(contentType)) {
							contentType = "image/png";
						}
						parts[i++] = new ByteArrayPart(item.get(),
								entry.getKey(), contentType);
					} else {
						parts[i++] = new StringPart(entry.getKey(),
								entry.getValue().toString(),"utf-8");
					}
				}
			}
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));
			addRemoteInvokerHeader(post);
			client.executeMethod(post);
			response = post.getResponseBodyAsString();

			return response;
		} catch (Exception ex) {
			warn("HTTP multURL " + url + " RETURN " + ex.getMessage());
		} finally {
			post.releaseConnection();
			accessLog(System.currentTimeMillis() - start, "POST",
					post.getStatusLine() != null ? post.getStatusCode() : -1,
					response == null ? 0 : response.length(), url,"",
					mapToString(nameValues));
		}

		return retr;
	}
	
	public void setProxyHostPort(String proxyHostPort) {
		ApiLogger.info("setProxyHostPort:" + proxyHostPort);
		
		String host = proxyHostPort;
		int port = 80;
		int pos = proxyHostPort.indexOf(':');
		if (pos > 0) {
			host = proxyHostPort.substring(0, pos);
			port = Integer.parseInt(proxyHostPort.substring(pos + 1).trim());
		}
        client.getHostConfiguration().setProxy(host, port);
	}
	

	public String getProxyHostPort() {
		return proxyHostPort;
	}

	public HttpClient getClient() {
		return client;
	}
	@Deprecated
	public String requestURL(String url, Map<String, String> nameValues, Map<String,String> headers) {
  		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
  		String result = null;
  		try {
                        long t = System.currentTimeMillis();
                        requestURL(url, nameValues,headers, out);
                        result = new String(out.toByteArray(), "utf-8");
                        ApiLogger.debug("POST URL:" + url + ", result:" + result + ", time:" + (System.currentTimeMillis() - t));
		} catch (Exception e) {
		}
		
		return result;
  	}
	@Deprecated
  	public int requestURL(String url, Map<String, ?> nameValues, Map<String,?> headers, OutputStream out) {
  		int readLen = 0;
		PostMethod post = new PostMethod(url);
		try {
			if (nameValues != null && !nameValues.isEmpty()) {
				List<NameValuePair> list = new ArrayList<NameValuePair>(
						nameValues.size());
				for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
					if (entry.getKey() != null && !entry.getKey().isEmpty()){
						list.add(new NameValuePair(entry.getKey(), entry
								.getValue().toString()));
					}
					else{
						post.setRequestEntity(new StringRequestEntity(entry
								.getValue().toString(), "text/xml", "utf-8"));
					}
				}
				if (!list.isEmpty()){
					post.setRequestBody(list.toArray(new NameValuePair[list
							.size()]));
				}
			}

			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, ?> entry : headers.entrySet()) {
					post.setRequestHeader(entry.getKey(), entry.getValue()
							.toString());
				}
			}
			addRemoteInvokerHeader(post);
			client.executeMethod(post);
			InputStream in = post.getResponseBodyAsStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
				readLen += len;
				if (readLen > maxSize){
					break;
				}
			}
			in.close();
		} catch (Exception ex) {
			warn("HTTP GET " + url + " RETURN " + ex.getMessage());
		} finally {
			post.releaseConnection();
		}
		return readLen;
  	}
	
  	
  	
//----------------------------------------------------------------------------------------------

  	
 
  	private final static String DEFAULT_CHARSET="utf-8";
  	private AccessLog accessLog = new DefaultHttpClientAceessLog();
	private static final URLCodec urlCodec = new URLCodec("utf-8");
	public void setAccessLog(AccessLog accessLog) {
		this.accessLog = accessLog;
	}
  	@Override
	public String get(String url) {
		return get(url,DEFAULT_CHARSET);
	}
  	public String get(String url,Map<String,String> headers){
  		return get(url,headers,DEFAULT_CHARSET);
  	}

	@Override
	public String get(String url, String charset) {
		return get(url,null,charset);
	}
	public String get(String url,Map<String,String> headers, String charset) {
		if (HttpManager.isBlockResource(url)) {
			debug("getURL blockResource url=" + url);
			return "";
		}
		HttpMethod get = new GetMethod(url);
		HttpMethodParams params = new HttpMethodParams();
		params.setContentCharset(charset);
		params.setUriCharset(charset);
		get.setParams(params);
		addHeader(get,headers);
		return executeMethod(url,get,null,charset);
	}
	public String getAsync(final String url){
		return getAsync(url, soTimeOut);
	}
	public String getAsync(final String url, final long timeout){
		Future<String>  future=httpPool.submit(new Callable<String>() {
			public String call() throws Exception {
				
				return get(url);
			}
		});
		try{
			return future.get(timeout, TimeUnit.MILLISECONDS);
		}catch(Exception e){
			warn("getAsync error url:"+url+" msg:"+e.getMessage());
			return "";
		}
	}
	public Future<String> getAsyncFuture(final String url){
		Future<String>  future=httpPool.submit(new Callable<String>() {
			public String call() throws Exception {
				
				return get(url);
			}
		});
		return future;
	}
	public String postAsync(final String url,final Map<String, ?> nameValues){
		Future<String>  future=httpPool.submit(new Callable<String>() {
			public String call() throws Exception {
				return post(url,nameValues);
			}
		});
		try{
			return future.get(this.soTimeOut, TimeUnit.MILLISECONDS);
		}catch(Exception e){
			warn(format("getAsync error url:%s post:%s msg:%s",url,mapToString(nameValues),e.getMessage()));
			return "";
		}
	}
	public Future<String> postAsyncFuture(final String url,final Map<String, ?> nameValues){
		Future<String>  future=httpPool.submit(new Callable<String>() {
			public String call() throws Exception {
				return post(url,nameValues);
			}
		});
		return future;
	}
	public byte[] getByte(String url){
		if (HttpManager.isBlockResource(url)) {
			debug("getURL blockResource url=" + url);
			return new byte[]{};
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		long start = System.currentTimeMillis();
		HttpMethod get = new GetMethod(url);
		int len = 0;
		try {
			len = doExecuteMethod(get, out);
			return out.toByteArray();
		} catch (ApiHttpClientExcpetion e){
			return new byte[]{};
		}finally {
			accessLog(System.currentTimeMillis() - start, "GET",
					get.getStatusLine() != null ? get.getStatusCode() : -1,
					len, url,"", null);
		}
	}
	@Override
	public String post(String url, Map<String, ?> nameValues) {
		return post(url,nameValues,DEFAULT_CHARSET);
	}

	@Override
	public String post(String url, Map<String, ?> nameValues, String charset) {
		return post(url, nameValues, null, charset);
	}
	public String post(String url, Map<String, ?> nameValues,Map<String,String> headers, String charset){
		if(HttpManager.isBlockResource(url)){
			debug("requestURL blockResource url="+url);
			return "";
		}
		PostMethod post = new PostMethod(url);
		HttpMethodParams params = new HttpMethodParams();
		params.setContentCharset(charset);
		post.setParams(params);
		addHeader(post,headers);
		if (nameValues != null && !nameValues.isEmpty()) {
			List<NameValuePair> list = new ArrayList<NameValuePair>(
					nameValues.size());
			for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
				if (entry.getKey() != null && !entry.getKey().isEmpty()){
					list.add(new NameValuePair(entry.getKey(), entry
							.getValue().toString()));
				}
				else{
					try {
						post.setRequestEntity(new StringRequestEntity(entry
								.getValue().toString(), "text/xml", "utf-8"));
					} catch (UnsupportedEncodingException e) {
					}
				}
			}
			if (!list.isEmpty()){
				post.setRequestBody(list.toArray(new NameValuePair[list.size()]));
			}
		}
		return executeMethod(url,post,mapToString(nameValues),charset);
	}
	private static void addHeader(HttpMethod method,Map<String,String> headers){
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				method.setRequestHeader(entry.getKey(), entry.getValue());
			}
		}
	}
	public String postMulti(String url, Map<String, Object> nameValues){
		return postMulti(url,nameValues,DEFAULT_CHARSET);
	}
	public String postMulti(String url, Map<String, Object> nameValues,String charset) {
		if(HttpManager.isBlockResource(url)){
			debug("multURLblockResource url="+url);
			return "";
		}
		PostMethod post = new PostMethod(url);
		Part[] parts = new Part[nameValues.size()];
		if (nameValues != null && !nameValues.isEmpty()) {
			int i = 0;
			for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
				if (entry.getValue() instanceof ByteArrayPart) {
					ByteArrayPart data = (ByteArrayPart) entry.getValue();
					parts[i++] = data;
					continue;
				}
				if (entry.getValue() instanceof FileItem) {
					FileItem item = (FileItem) entry.getValue();
					String contentType = item.getContentType();
					if ("application/octet-stream".equals(contentType)) {
						contentType = "image/png";
					}
					parts[i++] = new ByteArrayPart(item.get(), entry.getKey(),
							contentType);
				} else {
					parts[i++] = new StringPart(entry.getKey(), entry
							.getValue().toString(), "utf-8");
				}
			}
		}
		post.setRequestEntity(new MultipartRequestEntity(parts, post
				.getParams()));
		return executeMethod(url,post,mapToString(nameValues),charset);
	}
	
	public String postMulti(String url, InputStream in){
		PostMethod post = new PostMethod(url);
		post.setRequestEntity(new InputStreamRequestEntity(in));
		return executeMethod(url,post,null,DEFAULT_CHARSET);
	}
	
	public String postMulti(String url, byte[] buf){
		return this.postMulti(url, new ByteArrayInputStream(buf));
	}
	
	private byte[] executeMethodBytes(String url,HttpMethod method,ByteArrayOutputStream out,String charset){
		long start = System.currentTimeMillis();
		int len=0;
		try {
			len=doExecuteMethod(method, out);
			return out.toByteArray();
		} catch (ApiHttpClientExcpetion e){
			return new byte[]{};
		} finally{
			accessLog(System.currentTimeMillis() - start, method.getName(),
					method.getStatusLine() != null ? method.getStatusCode() : -1,
					len, url,method.getQueryString(), null,"-");
		}
	}
	private String executeMethod(String url,HttpMethod method,String postString,String charset){
		String result=null;
		long start = System.currentTimeMillis();
		int len=0;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			len=doExecuteMethod(method, out);
			result = new String(out.toByteArray(), charset);
			return result;
		} catch (UnsupportedEncodingException e) {
			warn(format("ApacheHttpClient.executeMethod UnsupportedEncodingException url:%s charset:%s",url,charset),e);
			return "";
		} catch (ApiHttpClientExcpetion e){
			return "";
		} finally{
			accessLog(System.currentTimeMillis() - start, method.getName(),
					method.getStatusLine() != null ? method.getStatusCode() : -1,
					len, url,method.getQueryString(), postString,result);
		}
	}
	private int doExecuteMethod(HttpMethod httpMethod,OutputStream out) throws ApiHttpClientExcpetion{
		long start=System.currentTimeMillis();
		int readLen = 0;
		try {
		    addRemoteInvokerHeader(httpMethod);
			client.executeMethod(httpMethod);
			if(System.currentTimeMillis()-start>this.soTimeOut){
				throw new ReadTimeOutException(format(
						"executeMethod so timeout time:%s soTimeOut:%s",
						(System.currentTimeMillis() - start), soTimeOut));
			}
			InputStream in = httpMethod.getResponseBodyAsStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				if(System.currentTimeMillis()-start>this.soTimeOut){
					throw new ReadTimeOutException(format(
							"read so timeout time:%s soTimeOut:%s",
							(System.currentTimeMillis() - start), soTimeOut));
				}
				out.write(b, 0, len);
				readLen += len;
				if (readLen > maxSize){
					throw new SizeException(
							format("size too big size:%s maxSize:%s", readLen,
									maxSize));
				}
			}
			in.close();
		} catch(ApiHttpClientExcpetion ex){
			warn(format("ApiHttpClientExcpetion url:%s message:%s", getHttpMethodURL(httpMethod),
					ex.getMessage()));
			throw ex;
		} catch (Exception ex) {
			warn(format("ApacheHttpClient.doExecuteMethod error! msg:%s",ex.getMessage()));
		} finally{
			httpMethod.releaseConnection();
		}
		return readLen;
	}
	private String getHttpMethodURL(HttpMethod httpMethod){
		try {
			return httpMethod.getURI().toString();
		} catch (URIException e) {
			return "";
		}
	}
	public <T> T get(String url, ResultConvert<T> c){
		String ret=get(url);
		return c.convert(url, null ,ret);
	}
	public <T> T get(String url,String charset,ResultConvert<T> c){
		String ret=get(url,charset);
		return c.convert(url, null ,ret);
	}
	public <T> T post(String url,Map<String, ?> nameValues, ResultConvert<T> c){
		String ret=post(url,nameValues);
		return c.convert(url, mapToString(nameValues) ,ret);
	}
	public <T> T post(String url, Map<String, ?> nameValues,String charset, ResultConvert<T> c){
		String ret=post(url, nameValues,charset);
		return c.convert(url, mapToString(nameValues),ret);
	}
	public <T> T postMulti(String url, Map<String, Object> nameValues,String charset, ResultConvert<T> c){
		String ret=postMulti(url,nameValues,charset);
		return c.convert(url, mapToString(nameValues) ,ret);
	}
	public RequestBuilder buildGet(String url){
		HttpMethod get=new GetMethod(url);
		HttpClientRequestBuilder ret=new HttpClientRequestBuilder(url,get,HttpManager.isBlockResource(url));
		return ret;
	}
	public RequestBuilder buildPost(String url){
		PostMethod post = new PostMethod(url);
		HttpClientRequestBuilder ret=new HttpClientRequestBuilder(url,post,HttpManager.isBlockResource(url));
		return ret;
	}
	public class HttpClientRequestBuilder implements RequestBuilder{
		private String url;
		private HttpMethod method;
		private boolean isBlock=false;
		private Map<String,String[]> queryParam=new HashMap<String,String[]>();
		private Map<String,String[]> bodyStringParam=new LinkedHashMap<String,String[]>(16);
		private Map<String,Object> bodyBinParam=new LinkedHashMap<String,Object>(16);
		private String charset;
		public HttpClientRequestBuilder(String url,HttpMethod method,boolean isBlock){
			this.url=url;
			this.method=method;
			this.isBlock=isBlock;
			HttpMethodParams params = new HttpMethodParams();
			params.setContentCharset(DEFAULT_CHARSET);
			params.setUriCharset(DEFAULT_CHARSET);
			method.setParams(params);
			charset = DEFAULT_CHARSET;
			if(isBlock){
				debug("blockResource url="+url);
			}
		}
		@Override
		public RequestBuilder withCharset(String charset){
			this.method.getParams().setContentCharset(charset);
			this.method.getParams().setUriCharset(charset);
			this.charset = charset;
			return this;
		}
		@Override
		public RequestBuilder withParam(Map<String, ?> param) {
			for(Map.Entry<String,?> entry:param.entrySet()){
				withParam(entry.getKey(),entry.getValue());
			}
			return this;
		}
		@Override
		public RequestBuilder withParam(boolean condition,String key, Object value) {
			if(condition){
				return withParam(key, value);
			}
			return this;
		}
		@Override
		public RequestBuilder withParam(String key, Object value) {
			boolean isQueryParam=isQueryParam();
			Map<String,String[]> param=isQueryParam?queryParam:bodyStringParam;
			if(value instanceof String){
				addParameter(key, (String) value,param);
			}else if(value instanceof String[]){
				addParameter(key, (String[]) value,param);
			}else if(value.getClass().isPrimitive()){
				addParameter(key,value.toString(),param);
			}else if(value.getClass().isArray()){
				int len=Array.getLength(value);
				for(int i=0;i<len;i++){
					withParam(key,Array.get(value, i));
				}
			}else if(value instanceof Collection){
				Iterator iter=((Collection) value).iterator();
				while(iter.hasNext()){
					withParam(key,iter.next());
				}
			}else{
				if(isQueryParam){
					warn(format(
							"HttpClientRequestBuilder.withParam unsupport url:%s type:%s",
							url, value.getClass().getSimpleName()));
					addParameter(key, value.toString(),queryParam);
				}else{
					bodyBinParam.put(key, value);
				}
			}
			return this;
		}
		public RequestBuilder withHeader(boolean condition,String key,String value){
			if(condition){
				return withHeader(key,value);
			}
			return this;
		}
		public RequestBuilder withHeader(String key,String value){
			method.setRequestHeader(key,value);
			return this;
		}
		@Override
		public RequestBuilder withHeader(Map<String, String> header) {
			if(header!=null){
				for(Map.Entry<String, String> entry:header.entrySet())
					method.addRequestHeader(entry.getKey(), entry.getValue());
			}
			return this;
		}
		@Override
		public <T> T execute(ResultConvert<T> convert) {
			return convert.convert(url, mapToString(bodyStringParam) ,execute());
		}
		@Override
		public String execute() {
			if(isBlock){
				return "";
			}
			setQuery();
			setBody();
			String post=null;
			if(bodyStringParam.size()>0){
				post=mapToString(bodyStringParam);
			}
			return executeMethod(url, method, post, this.charset);
		}
		@Override
		public byte[] executeByte() {
			if(isBlock){
				return new byte[]{};
			}
			setQuery();
			setBody();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			return executeMethodBytes(url, method, out, this.charset);
		}
		public Future<String> executeAsync(){
			Future<String>  future=httpPool.submit(new Callable<String>() {
				public String call() throws Exception {
					return execute();
				}
			});
			return future;
		}
		public String executeAsyncString(){
			Future<String>  future=httpPool.submit(new Callable<String>() {
				public String call() throws Exception {
					return execute();
				}
			});
			try{
				return future.get(ApacheHttpClient.this.soTimeOut, TimeUnit.MILLISECONDS);
			}catch(Exception e){
				warn(format("getAsync error url:%s post:%s msg:%s",url,mapToString(bodyStringParam),e.getMessage()));
				return "";
			}
		}
		private void setQuery(){
			if(queryParam.size()==0){
				return;
			}
			StringBuffer sb=new StringBuffer();
			for(Map.Entry<String, String[]> entry:queryParam.entrySet()){
					for(String item:entry.getValue()){
						sb.append(urlEncode(entry.getKey(),this.charset)+"="+urlEncode(item,this.charset)+"&");
					}
			}
			Util.trim(sb, '&');
			method.setQueryString(sb.toString());
		}
		private void setBody(){
			if (bodyBinParam.size() == 0 && bodyStringParam.size() == 0){
				return;
			}
			boolean multi=bodyBinParam.size()>0;
			if(multi){
				setMultiBody();
			}else{
				setStringBody();
			}
		}
		private void setStringBody() {
			PostMethod postMethod = (PostMethod) method;
			List<NameValuePair> list = new ArrayList<NameValuePair>(
					bodyStringParam.size());
			for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
				String[] values=entry.getValue();
				for(String value:values){
					list.add(new NameValuePair(entry.getKey(), value));
				}
			}
			if (!list.isEmpty()){
				postMethod.setRequestBody(list.toArray(new NameValuePair[list
						.size()]));
			}
		}
		private void setMultiBody(){
			PostMethod postMethod = (PostMethod) method;
			List<Part> partList=new ArrayList<Part>();
			for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
				for(String value:entry.getValue()){
					partList.add(new StringPart(entry.getKey(), value, "utf-8"));
				}
			}
			for(Map.Entry<String, Object> entry: bodyBinParam.entrySet()){
				if (entry.getValue() instanceof ByteArrayPart) {
					ByteArrayPart data = (ByteArrayPart) entry.getValue();
					partList.add(data);
				} else if (entry.getValue() instanceof FileItem) {
					FileItem item = (FileItem) entry.getValue();
					String contentType = item.getContentType();
					if ("application/octet-stream".equals(contentType)) {
						contentType = "image/png";
					}
					partList.add(new ByteArrayPart(item.get(), entry.getKey(),
							contentType));
				} else {
					warn(format(
							"HttpClientRequestBuilder.setMultiBody unsupport url:%s type:%s",
							url, entry.getValue().getClass().getSimpleName()));
					partList.add(new StringPart(entry.getKey(), entry
							.getValue().toString(), "utf-8"));
				}
			}
			postMethod.setRequestEntity(new MultipartRequestEntity(partList
					.toArray(new Part[] {}), postMethod.getParams()));
		}
		public void addParameter(String name, String value,Map<String,String[]> paramMap) {
			addParameter(name, new String[] {value},paramMap);
		}

		public void addParameter(String name, String[] values,Map<String,String[]> paramMap) {
			Assert.notNull(name, "Parameter name must not be null");
			String[] oldArr = (String[]) paramMap.get(name);
			if (oldArr != null) {
				String[] newArr = new String[oldArr.length + values.length];
				System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
				System.arraycopy(values, 0, newArr, oldArr.length, values.length);
				paramMap.put(name, newArr);
			}
			else {
				paramMap.put(name, values);
			}
		}
		private void addParameters(Map params) {
			Assert.notNull(params, "Parameter map must not be null");
			for (Iterator it = params.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				Assert.isInstanceOf(String.class, key, 
						"Parameter map key must be of type [" + String.class.getName() + "]");
				Object value = params.get(key);
				if (value instanceof String) {
					this.addParameter((String) key, (String) value,queryParam);
				}
				else if (value instanceof String[]) {
					this.addParameter((String) key, (String[]) value,queryParam);
				}
				else {
					throw new IllegalArgumentException("Parameter map value must be single value " +
							" or array of type [" + String.class.getName() + "]");
				}
			}
		}
		private boolean isQueryParam(){
			return method instanceof GetMethod || method instanceof DeleteMethod;
		}
	}
	private static String urlEncode(String str,String charset) {
		try {
			return urlCodec.encode(str,charset);
		}catch (UnsupportedEncodingException e) {
			return str;
		}
	}
	private void accessLog(long time, String method, int status, int len,
			String uri,String queryString, String post) {
		accessLog(time, method, status, len, uri,queryString, post,"-");
	}
	private void accessLog(long time, String method, int status, int len,
			String uri,String queryString, String post,String ret) {
		String url=null;
		if(StringUtils.isEmpty(queryString)){
			url=uri;
		} else if(uri.contains("?")){
			url=uri.substring(0,uri.indexOf("?")+1)+queryString;
		} else{
			url=uri+"?"+queryString;
		}
		if(time > soTimeOut){
			ApiLogger.fire("HTTP " + uri + " Error:" + time);
		}
		if(accessLog!=null){
			try{
				accessLog.accessLog(time, method, status, len, url, post, ret);
			}catch(Exception e){
				warn("error accessLog", e);
			}
		}
	}
	public class ApiHttpClientExcpetion extends Exception{
		public ApiHttpClientExcpetion(String msg){
			super(msg);
		}
	}
	public class ReadTimeOutException extends ApiHttpClientExcpetion{
		public ReadTimeOutException(String msg){
			super(msg);
		}
	}
	public class SizeException extends ApiHttpClientExcpetion {
		public SizeException(String msg){
			super(msg);
		}
	}
	private void addRemoteInvokerHeader(HttpMethod httpMethod) {
        httpMethod.setRequestHeader("X-Remote-API-Invoker", "openapi");
    }
}