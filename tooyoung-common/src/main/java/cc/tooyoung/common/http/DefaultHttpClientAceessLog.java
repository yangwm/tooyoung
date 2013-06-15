package cc.tooyoung.common.http;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.tooyoung.common.http.ApiHttpClient.AccessLog;

/**
 * 
 *
 */
public class DefaultHttpClientAceessLog implements ApiHttpClient.AccessLog{
	private static final Logger access = Logger.getLogger("httpclientaccess");

	public void accessLog(long time, String method, int status, int len,
			String url, String post, String ret) {
		if (post != null && post.length() > 200) {
			post = post.substring(0, 200);
			post.replaceAll("\n", "");
			post = post + "...";
		}
		if (ret != null) {
			ret = ret.trim();
			ret = ret.replaceAll("\n", "");
		}
		if(!StringUtils.isBlank(post)&&url.startsWith("http://ilogin.sina.com.cn")){
			post = replacePwd(post);
		}
		access.info(format("%s %s %s %s %s %s %s", time, method, status, len, url,
				isEmpty(post) ? "-" : post,isEmpty(ret) ? "-" : ret));
	}
	
	static String replacePwd(String text){
		text = text.replaceFirst("pw=[^&]*", "pw=***");
		return text;
	}

}
